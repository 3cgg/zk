package me.libme.module.zookeeper.fn.ls;

/**
 * Created by J on 2018/1/27.
 */
public interface OpenResource extends Nameable {

    void open(NodeLeader nodeLeader) throws Exception;

}
