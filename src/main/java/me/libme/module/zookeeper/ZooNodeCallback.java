package me.libme.module.zookeeper;

import java.io.Serializable;

public interface ZooNodeCallback extends Serializable {
	
	public void call(ZooNode node);
	
}