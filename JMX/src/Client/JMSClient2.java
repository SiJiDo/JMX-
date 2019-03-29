package Client;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JMSClient2 {
    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        String port = "9999";
        String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi";

        JMXServiceURL jmxServiceURL = new JMXServiceURL(url);
        JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL);
        MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();

        ObjectName objectName = new ObjectName("test:type=Hello");
        System.out.println("==========" + objectName + "===================");
        MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);

        //获取Attributes的属性信息
        System.out.println("[Attributes]");
        for(MBeanAttributeInfo mBeanAttributeInfo : mBeanInfo.getAttributes())
            try {
                Object value = mBeanAttributeInfo.isReadable() ? mBeanServerConnection.getAttribute(objectName, mBeanAttributeInfo.getName()) : "";
                System.out.println(mBeanAttributeInfo.getName() + " : " + value);
            } catch (Exception e) {
                e.getMessage();
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
        Object cacheSize = mBeanServerConnection.getAttribute(objectName, "CacheSize");
        System.out.println("Now the size is:" + cacheSize);
        mBeanServerConnection.setAttribute(objectName, new Attribute("CacheSize", 100));
        cacheSize = mBeanServerConnection.getAttribute(objectName, "CacheSize");
        System.out.println("Now the size is:" + cacheSize);
        mBeanServerConnection.invoke(objectName, "sayhello", null, null);
        Object result = mBeanServerConnection.invoke(objectName,"add", new Object[]{cacheSize,cacheSize}, new String[]{"int", "int"});
        System.out.println(result);

    }
}


