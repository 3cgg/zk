package me.libme.module.zookeeper.fn.ls;

/**
 * Created by J on 2018/11/15.
 */
public class Node {

    private String name;

    private String ip;

    private String hostName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}
