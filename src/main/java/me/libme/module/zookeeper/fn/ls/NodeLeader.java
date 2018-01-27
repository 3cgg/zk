package me.libme.module.zookeeper.fn.ls;

import me.libme.kernel._c.util.Assert;
import me.libme.module.zookeeper.ZooKeeperConnector;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class NodeLeader implements Serializable{
	
	private static final Logger logger= LoggerFactory.getLogger(NodeLeader.class);

	private final LeaderBuilder leaderBuilder;

	private final LeaderConfig conf;

	private final ZooKeeperConnector.ZookeeperExecutor executor;

	private final LeaderLatch leaderLatch;

	private final NodeMeta nodeMeta;

	/**
	 * never used in other case
	 */
	private final Object lockLeaderLatch=new Object();

	private Executor joinExecutor;

	private boolean start=false;


	private String leaderPath(){
		return conf.getBasePath()+"/leader-latch";
	}

	private String name(){
		return conf.getName();
	}



	private NodeLeader(LeaderBuilder leaderBuilder) {
		this.leaderBuilder=leaderBuilder;
		this.conf=leaderBuilder.conf;
		this.nodeMeta=new NodeMeta();
		nodeMeta.setName(conf.getName());

		this.executor = leaderBuilder.executor;
		this.joinExecutor=Executors.newFixedThreadPool(1,r->new Thread(r, "["+name()+"]node-leader-join"));

		leaderLatch=new LeaderLatch(executor.backend(),
				leaderPath());
		leaderLatch.addListener(new LeaderLatchListener() {
			
			@Override
			public synchronized void notLeader() {

				logger.info(Thread.currentThread().getName()+" lose leader .... ");
				boolean ok=true;

				try{
					if(leaderLatch.hasLeadership())
						leaderLatch.close();
				}catch (Exception e){
					logger.error(e.getMessage(),e);
					ok=false;
				}

				for(CloseResource closeResource:leaderBuilder.closeResources){
					try {
						logger.info("attempt to close "+closeResource.name());
						closeResource.close(NodeLeader.this);
					}catch (Exception e){
						ok=false;
						logger.error(e.getMessage(),e);
					}
				}
				if(!ok){
					logger.error("as resource cannot be closed safely, exit VM.");
					System.exit(-1);
				}
				joinLeader();
			}
			
			@Override
			public synchronized void isLeader() {
				logger.info(Thread.currentThread().getName()+" is leader .... ");
				try {
					for (OpenResource openResource : leaderBuilder.openResources) {
						logger.info("attempt to open "+openResource.name());
						openResource.open(NodeLeader.this);
					}
				}catch (Throwable e){
					logger.error("as resource cannot be open safely, exit VM.",e);
					System.exit(-1);
				}

			}
		}, Executors.newFixedThreadPool(1,r->new Thread(r, "["+name()+"]leader-status-changed-listener")));


	}


	public void start(){
		if(!start)
			joinLeader();
	}


	public static LeaderBuilder builder(){
		return new LeaderBuilder();
	}



	private void joinLeader(){
		joinExecutor.execute(()->startLeader());
	}

	
	/**
	 * attempt to latch leader.
	 */
	private void startLeader(){
		
		try {
			logger.info(Thread.currentThread().getName()+" attempt to join leadership .... ");
			leaderLatch.start();
			while(true){
				try{
					leaderLatch.await();
					break;
				}catch (InterruptedException e) {
					continue;
				}catch (EOFException e) {
					logger.error(e.getMessage(),e);
				}catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
			logger.info(Thread.currentThread().getName()+" got leader.... ");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally {

		}
	}





	public static class LeaderBuilder{


		private LeaderConfig conf=new LeaderConfig();

		private ZooKeeperConnector.ZookeeperExecutor executor;

		private List<OpenResource> openResources=new ArrayList<>();

		private List<CloseResource> closeResources=new ArrayList<>();


		public LeaderBuilder conf(LeaderConfig conf) {
			this.conf = conf;
			return this;
		}

		public LeaderBuilder executor(ZooKeeperConnector.ZookeeperExecutor executor) {
			this.executor = executor;
			return this;
		}

		public LeaderBuilder addOpenResource(OpenResource openResource){
			openResources.add(openResource);
			return this;
		}

		public LeaderBuilder addCloseResource(CloseResource closeResource){
			closeResources.add(closeResource);
			return this;
		}

		public LeaderBuilder name(String name){
			conf.setName(name);
			return this;
		}

		public NodeLeader build(){
			Assert.notNull(conf.getName(),"Name cannot be null/empty");
			NodeLeader nodeLeader=new NodeLeader(this);
			return nodeLeader;
		}



	}

	public String getLeaderName(){
		return conf.getName();
	}

	public NodeMeta getNodeMeta(){
		return nodeMeta;
	}



}
