package me.libme.module.zookeeper;

import me.libme.module.zookeeper.config.yaml.YamlZKConfig;

/**
 * Created by J on 2018/9/29.
 */
public class ZKExecutor {

    private static ZooKeeperConnector.ZookeeperExecutor executor;


    public static ZooKeeperConnector.ZookeeperExecutor defaultExecutor(){

        if(executor==null){
            synchronized (ZKExecutor.class){
                if(executor==null){
                    ZooKeeperConfig zooKeeperConfig=new YamlZKConfig(Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream("application-cpp-zk.yml")).find();
                    ZKExecutor.executor=executor(zooKeeperConfig);
                }
            }
        }
        return executor;
    }


    public static ZooKeeperConnector.ZookeeperExecutor executor(ZooKeeperConfig zooKeeperConfig){
        return new ZooKeeperConnector(zooKeeperConfig)
                .connect();
    }


}
