package me.libme.module.zookeeper;

import me.libme.kernel._c.util.JStringUtils;

public class ZooNode{

	private String path;
	
	private byte[] data;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public byte[] getData() {
		return data;
	}
	
	public byte[] getDataAsPossible(ZooKeeperConnector.ZookeeperExecutor executor) {
		byte[] bytes=getData();
		if(bytes==null){
			bytes=executor.getPath(getPath());
		}
		return bytes;
	}
	

	public void setData(byte[] data) {
		this.data = data;
	}
	
	@Deprecated
	public String getStringData(){
		byte[] bytes=getData();
		if(bytes==null){
			return null;
		}
		return JStringUtils.utf8(getData());
	}
	

}
