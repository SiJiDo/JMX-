package test;

public interface HelloMBean {
	public void sayhello();
	public int add(int x, int y);
	public String getName();
	public int getCacheSize();
	public void setCacheSize(int size);
}
