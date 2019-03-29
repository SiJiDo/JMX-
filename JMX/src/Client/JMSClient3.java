package Client;

import test.Hello;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JMSClient3 {
    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        String port = "9999";
        String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi";

        JMXServiceURL jmxServiceURL = new JMXServiceURL(url);
        JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL);
        MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();
       // System.out.println(JMXConnector.CREDENTIALS);
        HelloMBean helloMBean = JMX.newMBeanProxy(mBeanServerConnection,new ObjectName("test:type=Hello"),HelloMBean.class, true);
        helloMBean.sayhello();
    }
}
