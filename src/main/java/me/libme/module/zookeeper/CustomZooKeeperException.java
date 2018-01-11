package me.libme.module.zookeeper;

@SuppressWarnings("serial")
public class CustomZooKeeperException extends RuntimeException{
	
	public CustomZooKeeperException(Exception e) {
		super(e);
	}
	
	public CustomZooKeeperException(String message,Exception e) {
		super(message,e);
	}
	
	public CustomZooKeeperException(String message) {
		super(message);
	}
}