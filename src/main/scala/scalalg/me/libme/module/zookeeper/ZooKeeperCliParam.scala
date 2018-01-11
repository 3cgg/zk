package scalalg.me.libme.module.zookeeper

import me.libme.kernel._c.util.CliParams

/**
  * Created by J on 2018/1/11.
  */
object ZooKeeperCliParam {


  val __NAME_SPACE__ :String="--zk.namespace"

  val __CONNECT_STRING__ :String="--zk.connectString"



  def connectString(cliParam: CliParams):String={
    cliParam.getString(__CONNECT_STRING__)
  }

  def namespace(cliParam: CliParams):String={
    cliParam.getString(__NAME_SPACE__)
  }




}
