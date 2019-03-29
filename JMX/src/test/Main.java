package test;

import java.lang.management.ManagementFactory;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class Main {

	public static void main(String[] args) throws Exception {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName mbsname = new ObjectName("test:type=Hello");
		Hello mbean = new Hello();
		mbs.registerMBean(mbean, mbsname);

		new Thread(new Runnable(){
			public void run() {
				Random random = new Random();
				while(true){
					try {
						TimeUnit.SECONDS.sleep(random.nextInt(10));
					}
					catch (Exception e){}
					mbean.setCacheSize(random.nextInt(10) + mbean.getCacheSize());
				}
			}
		}).start();

        Thread.sleep(Long.MAX_VALUE);
	}
}
