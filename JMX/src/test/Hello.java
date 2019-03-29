package test;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

public class Hello extends NotificationBroadcasterSupport implements HelloMBean {
	private String name = "sijidou";
	private static final int DEFAULT_CACHE_SIZE = 200;
	private int cacheSize = DEFAULT_CACHE_SIZE;
	private long sequenceNumber = 0;

	public MBeanNotificationInfo[] getNotificationInfo() {
		String[] types = new String[]{
				AttributeChangeNotification.ATTRIBUTE_CHANGE
		};
		String name = AttributeChangeNotification.class.getName();
		String description = "An attribute of this MBean has changed";
		MBeanNotificationInfo mBeanNotificationInfo = new MBeanNotificationInfo(types, name, description);
		return new MBeanNotificationInfo[]{mBeanNotificationInfo};
	}

	@Override
	public void sayhello() {
		System.out.println("hello, world"); 
	}

	@Override
	public int add(int x, int y) {
		return x + y;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getCacheSize() {
		return this.cacheSize;
	}

	@Override
	public synchronized void setCacheSize(int size) {
		int oldSize = this.cacheSize;
		this.cacheSize = size;
		System.out.println("now the cachesize is "+ Integer.toString(this.cacheSize));
		Notification n = new AttributeChangeNotification(	this, sequenceNumber++,
																	System.currentTimeMillis(),
													         "cacheSize change",
													"CacheSize",
													 "int",
				                                                   oldSize,
														           this.cacheSize);
		sendNotification(n);
	}

}
