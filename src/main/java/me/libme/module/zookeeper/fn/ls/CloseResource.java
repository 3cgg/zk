package me.libme.module.zookeeper.fn.ls;

import java.io.IOException;

/**
 * Created by J on 2018/1/27.
 */
public interface CloseResource extends Nameable{

    void close(NodeLeader nodeLeader) throws IOException;
}
