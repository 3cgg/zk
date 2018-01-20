package test.me.libme.module.zookeeper

import me.libme.kernel._c.util.{CliParams, JStringUtils}
import me.libme.module.zookeeper.{ZooKeeperConfig, ZooKeeperConnector}

import scala.collection.JavaConversions
import scalalg.me.libme.module.zookeeper.ZooKeeperCliParam

/**
  * Created by J on 2018/1/11.
  */
object TestZooKeeper {



  def main(args: Array[String]): Unit = {

    val zooKeeperConfig = new ZooKeeperConfig

    val cliParams=new CliParams(args)
      .append(ZooKeeperCliParam.__CONNECT_STRING__,"one.3cgg.rec:2181")
      .append(ZooKeeperCliParam.__NAME_SPACE__,"lib_me_test")

    zooKeeperConfig.setConnectString(ZooKeeperCliParam.connectString(cliParams))
    zooKeeperConfig.setNamespace(ZooKeeperCliParam.namespace(cliParams))

    val executor = new ZooKeeperConnector(zooKeeperConfig).connect;

    // do crud

    val path="b"

    executor.createPath(path,"data:a")

    val data=executor.getPath(path)

    println(new String(data,"utf-8"))

    executor.setPath(path,"data:another-a")

    val adata=executor.getPath(path)

    println(new String(adata,"utf-8"))

    val child=path+"/child_path"

    val r=executor.createPath(child)

    val exitsChild=executor.exists(child)

    println("exists : " +exitsChild)

    JavaConversions.asScalaBuffer(executor.getChildren(path))
        .foreach(cpath=>{

          val _data=executor.getPath(path+"/"+cpath)
          println(JStringUtils.utf8(_data))
        })

    executor.deletePath(path)

    println("==========================OK=====================================")

  }




}
