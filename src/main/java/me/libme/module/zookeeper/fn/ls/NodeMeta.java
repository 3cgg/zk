package me.libme.module.zookeeper.fn.ls;


import me.libme.kernel._c._m.JModel;

public class NodeMeta implements JModel {

	/**
	 * the node name
	 */
	private String name;

	 /**
	 * the node machine host
	 */
	private String hostName;

	/**
	 * the node machine host
	 */
	private String ip;
	
	/**
	 * the node processor id
	 */
	private int pid;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
