package me.libme.module.zookeeper.config.yaml;

import me.libme.kernel._c.yaml.YamlMapConfig;
import me.libme.module.zookeeper.ZooKeeperConfig;
import me.libme.module.zookeeper.config.ZooKeeperConfigFinder;

import java.io.InputStream;

/**
 * Created by J on 2018/9/29.
 */
public class YamlZKConfig implements ZooKeeperConfigFinder {

    private YamlMapConfig yamlMapConfig;

    public YamlZKConfig(InputStream inputStream) {
        this.yamlMapConfig = new YamlMapConfig(inputStream);
    }

    @Override
    public ZooKeeperConfig find() {
        ZooKeeperConfig zooKeeperConfig=new ZooKeeperConfig();

        String namespace=yamlMapConfig.getString("cpp.zk.namespace","cpp-storm");
        String connectString=yamlMapConfig.getString("cpp.zk.connect-string","one.3cgg.rec:2181");
        zooKeeperConfig.setConnectString(connectString);
        zooKeeperConfig.setNamespace(namespace);

        return zooKeeperConfig;
    }



























}
