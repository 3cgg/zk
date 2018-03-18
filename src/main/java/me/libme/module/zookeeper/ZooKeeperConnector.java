package me.libme.module.zookeeper;

import me.libme.kernel._c.util.JStringUtils;
import me.libme.module.zookeeper.fn.ls.NodeLeader;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("serial")
public class ZooKeeperConnector implements Serializable {

	private static final Logger logger= LoggerFactory.getLogger(ZooKeeperConnector.class);

	private ZooKeeperConfig zooKeeperConfig;
	
	public ZooKeeperConnector(ZooKeeperConfig zooKeeperConfig) {
		this.zooKeeperConfig=zooKeeperConfig;
	}
	
	public ZookeeperExecutor connect(){
		return new ZookeeperExecutor() {
			@Override
			protected ZooKeeperConfig zooKeeperConfigProvide() {
				return zooKeeperConfig;
			}
		};
	}
	
	public abstract class ZookeeperExecutor implements Serializable{


		private CuratorFramework curatorFramework;
		
		protected abstract ZooKeeperConfig zooKeeperConfigProvide();
		
		public ZookeeperExecutor() {
			ZooKeeperConfig zooKeeperConfig=zooKeeperConfigProvide();

			logger.info("ZK Connection String : "+zooKeeperConfig.getConnectString());
			logger.info("ZK Connection Namespace : "+zooKeeperConfig.getNamespace());
	        CuratorFramework client = CuratorFrameworkFactory.builder()
	                .connectString(zooKeeperConfig.getConnectString())
	                .retryPolicy(zooKeeperConfig.getRetryPolicy())
	                .namespace(zooKeeperConfig.getNamespace())
	                .build();
	        client.start();
	        curatorFramework=client;
		}
		
		public String createPath(String path,CreateMode createMode){
			return createPath(path, new byte[]{}, createMode);
		}
		
		public String createPath(String path){
			return createPath(path,new byte[]{},CreateMode.PERSISTENT);
		}
		
		public String createEphSequencePath(String path){
			return createPath(path,new byte[]{},CreateMode.EPHEMERAL_SEQUENTIAL);
		}
		
		public String createPath(String path,String data){
			return createPath(path, JStringUtils.utf8(data));
		}
		
		public String createPath(String path,byte[] data){
			return createPath(path, data, CreateMode.PERSISTENT);
		}
		
		public String createPath(String path,String data,CreateMode createMode){
			return createPath(path, JStringUtils.utf8(data), createMode);
		}
		
		public String createPath(String path,byte[] data,CreateMode createMode){
			try{
				return curatorFramework.create()
				.creatingParentsIfNeeded()
				.withMode(createMode)
				.withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
				.forPath(path,data);
			}catch (Exception e) {
				throw new CustomZooKeeperException(e);
			}
		}
		
		
		public void setPath(String path,byte[] data){
			try{
				curatorFramework.setData()
				.forPath(path,data);
			}catch (Exception e) {
				throw new CustomZooKeeperException(e);
			}
		}
		
		public void setPath(String path,String data){
			setPath(path,JStringUtils.utf8(data));
		}
		
		public void deletePath(String path){
			try{
				curatorFramework.delete()
				.deletingChildrenIfNeeded()
				.forPath(path);
			}catch (Exception e) {
				throw new CustomZooKeeperException(e);
			}
		}
		
		public byte[] getPath(String path){
			try{
				return curatorFramework.getData()
				.forPath(path);
			}catch (Exception e) {
				throw new CustomZooKeeperException(e);
			}
		}
		
		public List<String> getChildren(String path){
			try{
				List<String> child=curatorFramework.getChildren().forPath(path);
				if(child==null){
					child=Collections.EMPTY_LIST;
				}
				return child;
			}catch (Exception e) {
				throw new CustomZooKeeperException(e);
			}
		}
		
		public NodeCache watchPath(final String path,final ZooNodeCallback nodeCallback,ExecutorService executor){

			try{
				final NodeCache nodeCache = new NodeCache(curatorFramework, path, false);
				nodeCache.start(true);
				nodeCache.getListenable().addListener(new NodeCacheListener() {
					
					@Override
					public void nodeChanged() throws Exception {
						try{
							ZooNode node=new ZooNode();
							node.setPath(path);
							ChildData childData= nodeCache.getCurrentData();
							if(childData==null) return ;
							node.setData(childData.getData());
							nodeCallback.call(node);
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, executor);
				return nodeCache;
			}catch (Exception e) {
				throw new CustomZooKeeperException(e);
			}
		}
		
		public NodeCache watchPath(final String path,final ZooNodeCallback nodeCallback){
			ExecutorService pool = Executors.newFixedThreadPool(1);
			return watchPath(path, nodeCallback, pool);
		}
		
		
		public PathChildrenCache watchChildrenPath(final String path,final ZooNodeChildrenCallback nodeChildrenCallback,PathChildrenCacheEvent.Type... types){
			ExecutorService pool = Executors.newFixedThreadPool(1);
			return watchChildrenPath(path, nodeChildrenCallback, pool, types);
		}
		
		
		public PathChildrenCache watchChildrenPath(final String path,final ZooNodeChildrenCallback nodeChildrenCallback,ExecutorService executor,PathChildrenCacheEvent.Type... types){
			try{
				final Type[] _types;
				if(types.length==0){
					_types=new PathChildrenCacheEvent.Type[]{PathChildrenCacheEvent.Type.CHILD_ADDED,
							PathChildrenCacheEvent.Type.CHILD_REMOVED};
				}
				else{
					_types=types;
				}
				final PathChildrenCache childrenCache=
						new PathChildrenCache(curatorFramework, path,false,false, executor);
						
				childrenCache.start(StartMode.NORMAL);
				childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
					
					@Override
					public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
						
						try{
							boolean done=false;
							for(Type type:_types){
								if(type==event.getType()){
									done=true;
									break;
								}
							}
							if(!done) return;
							
							List<ChildData> childDatas= childrenCache.getCurrentData();
							List<ZooNode> nodes=new ArrayList<>();
							for(ChildData childData:childDatas){
								ZooNode node=new ZooNode();
								node.setPath(childData.getPath());
								node.setData(childData.getData());
								nodes.add(node);
							}
							nodeChildrenCallback.call(nodes);
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, executor);
				return childrenCache;
			}catch (Exception e) {
				throw new CustomZooKeeperException(e);
			}
		}
		
		
		public boolean exists(final String path){
			try{
				return curatorFramework.checkExists()
				.forPath(path)!=null;
			}catch (Exception e) {
				throw new CustomZooKeeperException(e);
			}
		}
		
		public CuratorFramework backend(){
			return curatorFramework;
		}
		
	}
	
	
	
}
