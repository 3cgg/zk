package me.libme.module.zookeeper.fn.ls;

import me.libme.kernel._c.json.JJSON;
import me.libme.kernel._c.util.NetUtil;
import me.libme.module.zookeeper.ZooKeeperConnector;
import org.apache.zookeeper.CreateMode;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;

/**
 * Created by J on 2018/1/27.
 */
public class LeaderNodeRegister implements OpenResource,CloseResource {

    private final String name;

    private final String path;

    private final ZooKeeperConnector.ZookeeperExecutor executor;

    public LeaderNodeRegister(String name, String path,ZooKeeperConnector.ZookeeperExecutor executor) {
        this.name = name;
        this.path=path;
        this.executor = executor;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void close(NodeLeader nodeLeader) throws IOException {
        executor.deletePath(path);
    }

    @Override
    public void open(NodeLeader nodeLeader) throws Exception {

        NodeMeta nodeMeta=nodeLeader.getNodeMeta();

        InetAddress inetAddress= NetUtil.getLocalAddress();
        nodeMeta.setIp(inetAddress.getHostAddress());
        nodeMeta.setHostName(inetAddress.getHostName());
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        int indexOf = pid.indexOf('@');
        if (indexOf > 0){
            pid = pid.substring(0, indexOf);
        }
        nodeMeta.setPid(Integer.parseInt(pid));

        String string= JJSON.get().format(nodeMeta);
        executor.createPath(path,string, CreateMode.EPHEMERAL);
    }
}
