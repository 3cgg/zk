package me.libme.module.zookeeper.fn.ls;

import me.libme.kernel._c.json.JJSON;
import me.libme.kernel._c.util.NetUtil;
import me.libme.module.zookeeper.ZooKeeperConnector;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;

/**
 * Created by J on 2018/1/27.
 */
public class LeaderNodeRegister implements OpenResource,CloseResource {

    private static final Logger LOGGER= LoggerFactory.getLogger(LeaderNodeRegister.class);

    private final String name;

    private final String path;

    private Node node;

    private final ZooKeeperConnector.ZookeeperExecutor executor;

    public LeaderNodeRegister(String name, String path,ZooKeeperConnector.ZookeeperExecutor executor,Node node) {
        this.name = name;
        this.path=path;
        this.executor = executor;
        if(node==null){
            InetAddress inetAddress= NetUtil.getLocalAddress();
            this.node=new Node();
            this.node.setIp(inetAddress.getHostAddress());
            this.node.setHostName(inetAddress.getHostName());
        }else{
            this.node=node;
        }

    }

    public LeaderNodeRegister(String name, String path,ZooKeeperConnector.ZookeeperExecutor executor) {
        this(name,path,executor,null);
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
        nodeMeta.setIp(node.getIp());
        nodeMeta.setHostName(node.getHostName());
        nodeMeta.setName(node.getName());

        String pid = ManagementFactory.getRuntimeMXBean().getName();
        int indexOf = pid.indexOf('@');
        if (indexOf > 0){
            pid = pid.substring(0, indexOf);
        }
        nodeMeta.setPid(Integer.parseInt(pid));

        String string= JJSON.get().format(nodeMeta);

        if(executor.exists(path)){   // first remove path
            try{
                executor.deletePath(path);
            }catch (Exception e){
                LOGGER.warn(e.getMessage(),e);
            }
        }
        executor.createPath(path,string, CreateMode.EPHEMERAL);

    }
}
