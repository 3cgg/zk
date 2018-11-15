package test.me.libme.module.zookeeper

import me.libme.kernel._c.util.CliParams
import me.libme.module.zookeeper.fn.ls.{LeaderConfig, LeaderNodeRegister, NodeLeader}
import me.libme.module.zookeeper.{ZooKeeperConfig, ZooKeeperConnector}

/**
  * Created by J on 2018/1/27.
  */
object TestLeader {


  def main(args: Array[String]): Unit = {


    val zooKeeperConfig = new ZooKeeperConfig

    val cliParams=new CliParams(args)
      .append(ZooKeeperCliParam.__CONNECT_STRING__,"one.3cgg.rec:2181")
      .append(ZooKeeperCliParam.__NAME_SPACE__,"lib_me_test")

    zooKeeperConfig.setConnectString(ZooKeeperCliParam.connectString(cliParams))
    zooKeeperConfig.setNamespace(ZooKeeperCliParam.namespace(cliParams))

    val executor = new ZooKeeperConnector(zooKeeperConfig).connect;

    val conf:LeaderConfig=new LeaderConfig
    conf.setBasePath("/coordinator")
    conf.setName("Leader-Test")
    val leaderNodeRegister:LeaderNodeRegister=new LeaderNodeRegister("Leader Register","/leader-info",executor);


    val nodeLeader:NodeLeader=NodeLeader.builder()
        .conf(conf)
        .executor(executor)
        .addOpenResource(leaderNodeRegister)
        .addCloseResource(leaderNodeRegister)
        .build()

    nodeLeader.start()

    println(nodeLeader)


  }


}
