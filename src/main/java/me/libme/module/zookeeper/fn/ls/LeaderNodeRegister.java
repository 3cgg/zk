package me.libme.module.zookeeper.fn.ls;

import me.libme.kernel._c.json.JJSON;
import me.libme.module.zookeeper.ZKExecutor;
import me.libme.module.zookeeper.ZooKeeperConfig;
import me.libme.module.zookeeper.ZooKeeperConnector;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * Created by J on 2018/1/27.
 */
public class LeaderNodeRegister implements OpenResource,CloseResource {

    private static final Logger LOGGER= LoggerFactory.getLogger(LeaderNodeRegister.class);

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

        ZooKeeperConfig zooKeeperConfig=ZKExecutor.defaultConfig();

        nodeMeta.setIp(zooKeeperConfig.getNode().getIp());
        nodeMeta.setHostName(zooKeeperConfig.getNode().getHostName());
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
