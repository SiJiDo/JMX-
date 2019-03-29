package Client;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;
import java.util.Map;

public class JMSClient5 {
    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        String port = "9999";
        String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi";

        Map<String, Object> allowed = new HashMap<>();
        allowed.put(JMXConnector.CREDENTIALS, new String[]{"admin", "password"});

        JMXServiceURL jmxServiceURL = new JMXServiceURL(url);
        JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, allowed);
        MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();

        ObjectName objectName = new ObjectName("test:type=Hello");
        mBeanServerConnection.invoke(objectName, "sayhello", null, null);
    }
}
