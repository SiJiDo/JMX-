**JMX学习**
测试的源码src文件夹下test目录是服务器端代码，client目录是客户端代码

**1.JMX是什么**

个人理解是JMX是一个服务器，它能让客户端远程访问该服务器上运行的java程序的api，并且可以对该程序通过相应的函数进行增删改查

每一个运行的程序是叫做MBean(Managed Bean)

JMX是在jdk自带的库文件中有相应的功能支持类，因此不需要从网上下载jar包

jboss，weblogic等中间件的原理也是依据JMX来实现的



**2.MBean的类型**

Standard MBeans

Dynamic MBeans

Open MBeans

Model MBeans

MXBeans

MXBeans

常用就是标准MBeans和动态MBeans



**3.MBean的使用**

```
//使用工厂生产一个类对象
MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();  

//创建一个MBean的专用的Object，参数为 "路径:type=类"
ObjectName mbeanName = new ObjectName("test:type=Hello"); 

//创建一个对象实例
Hello mbean = new Hello(); 

//用MBeanServer对象实例的registerMBean方法将创建的对象和Object对象绑定起来
mbs.registerMBean(mbean, mbeanName);

//使程序长时间运行，就不会退出了
Thread.sleep(Long.MAX_VALUE);
```

可以理解为MBeanServer是一个大的服务容器，MBean(由ObjectName创建的)是一个MBean的组件，Hello类是游离于这个服务器的一个类，这段代码就是把这个游离的Hello类和MBean组件结合在一起，并加入到MBeanServer这个容器中



**4.通知**

一般一个类，包括函数和变量，而JMX是要能够被远程访问的java程序，所以有必要知道谁访问了这个远程的类，并进行了如何操作

在对远程的JMX服务器上的程序进行相应操作后，如果相应了解何时发生了操作，那么可以设置通知

操作是对MBean的对象进行修改的(ObjectName定义的对象)

ps：英文意思Notification：通知

设置通知要使Hello类继承该类

```
NotificationBroadcasterSupport
```

并重写方法getNotificationInfo()方法

```
public MBeanNotificationInfo[] getNotificationInfo() {
	String[] types = new String[]{
			AttributeChangeNotification.ATTRIBUTE_CHANGE
	};
	String name = AttributeChangeNotification.class.getName();
	String description = "An attribute of this MBean has changed";
	MBeanNotificationInfo mBeanNotificationInfo = new MBeanNotificationInfo(types, name, description);
	return new MBeanNotificationInfo[]{mBeanNotificationInfo};
}
```

这个方法的作用是，显示这个通知功能的信息，其中里面重写的个性化内容是description的内容

对应方法添加发送每条通知的信息

```
public synchronized void setCacheSize(int size) {
		int oldSize = this.cacheSize;
		this.cacheSize = size;
		System.out.println("now the cachesize is "+ Integer.toString(this.cacheSize));
		Notification n = new AttributeChangeNotification(this, sequenceNumber++, System.currentTimeMillis(), "cacheSize change", "CacheSize", "int", oldSize, this.cacheSize);
		sendNotification(n);
	}
```

这里面最主要描述的内容是n = new AttributeChangeNotification(xxxx)

第一个参数是返回本类

第二个参数是通知序列号，这里累加

第三个参数是设置时间

第四个参数是每条通知会显示的一个提示消息

第五个参数是对改变参数的名字描述

第六个参数是对改变参数的类型

第七个参数是原来的值

第八个参数是现在的值

然后即用sendNotification(n)将通知发送出去，这个方法估计是继承的NotificationBroadcasterSupport类自带的



**5.远程连接**

启动的时候用如下代码，会开到9999端口上

```
java -Dcom.sun.management.jmxremote.port=9999 ^
-Dcom.sun.management.jmxremote.authenticate=false ^
-Dcom.sun.management.jmxremote.ssl=false ^
test/Main
```

它这个链接用的协议是rmi协议，除了可用地址:端口的形式，还可以用协议

```
127.0.0.1:9999	可用连过去
service:jmx:rmi:///jndi/rmi://127.0.0.1:9999/jmxrmi 可用连过去
```



**6.自己写代码来连接**

先前的连接工具用的是jdk自带Jconsole这个软件，它有比较好的可视化界面，它形成的原理可用通过自己写java代码来实现

```
public class JMSClient {
    public static void main(String[] args) throws Exception{
        String host = "127.0.0.1";
        String port = "9999";
        String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi";

        JMXServiceURL jmxServiceURL = new JMXServiceURL(url);
        JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL);
        MBeanServerConnection connector = jmxConnector.getMBeanServerConnection();
    }
}
```

JMXServiceURL类根据url定为目标JMX服务器信息

JMXconnector类是和目标服务器建立连接

MBeanServerConnection是与目标服务器的MBeanServer建立连接

但该代码只是建立了连接，但是还没有获取数据

获取数据的方法如下

```
 Set<ObjectName> objectNames = connection.queryNames(null, null);

        for(ObjectName objectName : objectNames){
            System.out.println("==========" + objectName + "===================");
            MBeanInfo mBeanInfo = connection.getMBeanInfo(objectName);

            //获取Attributes的属性信息
            System.out.println("[Attributes]");
            for(MBeanAttributeInfo mBeanAttributeInfo : mBeanInfo.getAttributes()){
                try{
                    Object value = mBeanAttributeInfo.isReadable() ? connection.getAttribute(objectName, mBeanAttributeInfo.getName()) : "";
                    System.out.println(mBeanAttributeInfo.getName() + " : " + value);
                }
                catch(Exception e){
                    e.getMessage();
                }
            }
            //获取Operations的属性信息
            System.out.println("[Operations]");
            for(MBeanOperationInfo mBeanOperationInfo : mBeanInfo.getOperations()){
                System.out.println(mBeanOperationInfo.getName() + " : " + mBeanOperationInfo.getDescription());
            }

            //获取Notifications的属性的信息
            System.out.println("[Notifications]");
            for(MBeanNotificationInfo mBeanNotificationInfo : mBeanInfo.getNotifications()){
                System.out.println(mBeanNotificationInfo.getName() + " : " + mBeanNotificationInfo.getDescription());
            }
        }
```

它会有很多的MBean，大部分其实就是JMX服务器自己启动时候自带的

利用for循环，对每个MBean进行查找属性，查找函数，查找通知

并且这三个查找的值也可能是多对的，因此也使用for循环，唯一注意的是Attribute值有些可能是private就无法访问，所以这个地方要抛出下异常，但不能终止

原来是用connection.queryNames(null, null)来获取所以的MBean，如果要进行对某个MBean的查询可以使用

```
ObjectName = new ObjectName("test:type=Hello");
```

直接指定ObjectName的对象，当然也可用获取他们的变量或者方法，进行运算

```
//这个可以设置成员变量的值
mBeanServerConnection.setAttribute(objectName, new Attribute("CacheSize", 100));

//获取成员变量的值
Object cacheSize = mBeanServerConnection.getAttribute(objectName, "CacheSize");

//调用不带返回值的方法，不带参的方法
mBeanServerConnection.invoke(objectName, "sayhello", null, null);

//调用带返回值的方法，并获取返回值
//第一个值是MBean，第二个值是方法名，第三个值是该方法参数，第四个值是方法中参数的类型
Object result = mBeanServerConnection.invoke(objectName,"add", new Object[]{cacheSize,cacheSize}, new String[]{"int", "int"});

//输出返回值
System.out.println(result);
```

如果知道接口的话可以申请个该接口的对象来进行操控

```
//申明方法，参数分别是
//参数一：连接后的返回对象;参数二：MBean的对象;参数三：接口的类;参数四：true就对了
HelloMBean helloMBean = JMX.newMBeanProxy(mBeanServerConnection,new ObjectName("test:type=Hello"),HelloMBean.class, true);

//直接调用自己定义的对象实例的sayhello()方法，该方法会在远程的JMX服务器上执行
helloMBean.sayhello();
```



**6.设置连接的账号和密码**

启动的时候和在指定端口运行相比，多加2个参数，改变一个参数即可

```
java -Dcom.sun.management.jmxremote.port=9999 ^
-Dcom.sun.management.jmxremote.authenticate=true ^
-Dcom.sun.management.jmxremote.ssl=false ^
-Dcom.sun.management.jmxremote.access.file=./jmx.access ^
-Dcom.sun.management.jmxremote.password.file=./jmx.passwd ^
test/Main
```

-Dcom.sun.management.jmxremote.authenticate=true 设置要身份验证



-Dcom.sun.management.jmxremote.access.file=./jmx.access 设置账号文件，文件名任意

文件格式

```
user readonly
admin readwrite
```

user用户只可读，只能查看变量

admin用户可读可写，意味着能调用方法，改变变量



-Dcom.sun.management.jmxremote.password.file=./jmx.passwd 设置密码文件，文件名任意，但是要限制该文件权限

```
user user
admin passwor
```

格式为用户名[[空格]密码,要和jmx.access对应



最后在连接前多添加以个键值对的Map

```
Map<String, Object> allowed = new HashMap<>();
allowed.put(JMXConnector.CREDENTIALS, new String[]{"admin", "password"});

JMXServiceURL jmxServiceURL = new JMXServiceURL(url);
//多带了个allowed参数
JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, allowed);
MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();
```



PS：我的jmx.passwd文件权限是我本地的用户，所以如果要用该文件要修改成自己的权限

参考链接

https://blog.csdn.net/isea533/article/details/77431044

https://blog.csdn.net/isea533/article/details/77455973

https://blog.csdn.net/isea533/article/details/77600542

https://www.linuxidc.com/Linux/2015-02/113416.htm

