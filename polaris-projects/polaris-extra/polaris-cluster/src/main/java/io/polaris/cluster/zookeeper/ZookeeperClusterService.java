package io.polaris.cluster.zookeeper;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import io.polaris.core.cluster.ClusterService;
import io.polaris.core.cluster.InstanceNode;
import io.polaris.core.cluster.InstanceQueryException;
import io.polaris.core.cluster.InstanceRegisterException;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.service.ServiceName;
import io.polaris.core.string.Strings;
import io.polaris.json.Jacksons;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import com.google.common.collect.Lists;

/**
 * @author Qt
 * @since  Apr 17, 2024
 */
@SuppressWarnings("ALL")
@Slf4j
@ServiceName("zookeeper")
public class ZookeeperClusterService implements ClusterService {
	private static final String DEFAULT_NAMESPACE = "/public";
	@Setter
	@Getter
	private ZookeeperConfig config;
	private CuratorFramework client;
	private ServiceDiscovery<InstanceNode> serviceDiscovery;
	private ServiceCache<InstanceNode> serviceCache;
	private volatile InstanceNode selfInstance;
	private volatile boolean started = false;

	public ZookeeperClusterService() {
		this(null);
	}

	public ZookeeperClusterService(ZookeeperConfig config) {
		this.config = config;
		Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
	}

	private void doStart() {
		if (!started) {
			doStop();
			if (config == null) {
				throw new IllegalStateException("config is null");
			}
			RetryPolicy retryPolicy = new ExponentialBackoffRetry(config.getBaseSleepTimeMs(), config.getMaxRetries());

			CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
				.retryPolicy(retryPolicy)
				.connectString(config.getHostPort());
			if (config.isEnableAcl()) {
				String authInfo = config.getAuth();
				if ("digest".equals(config.getSchema())) {
					try {
						authInfo = DigestAuthenticationProvider.generateDigest(authInfo);
					} catch (NoSuchAlgorithmException e) {
						throw new IllegalStateException(e.getMessage(), e);
					}
				} else {
					throw new IllegalStateException("Support digest schema only.");
				}
				final List<ACL> acls = Lists.newArrayList();
				acls.add(new ACL(ZooDefs.Perms.ALL, new Id(config.getSchema(), authInfo)));
				acls.add(new ACL(ZooDefs.Perms.READ, ZooDefs.Ids.ANYONE_ID_UNSAFE));

				ACLProvider provider = new ACLProvider() {
					@Override
					public List<ACL> getDefaultAcl() {
						return acls;
					}

					@Override
					public List<ACL> getAclForPath(String s) {
						return acls;
					}
				};
				builder.aclProvider(provider);
				builder.authorization(config.getSchema(), config.getAuth().getBytes());
			}
			this.client = builder.build();
			String path = (Strings.isBlank(config.getNamespace()) ? DEFAULT_NAMESPACE :
				this.config.getNamespace());

			this.serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceNode.class)
				.client(client)
				.basePath(path)
				.watchInstances(true)
				.serializer(new InstanceSerializer() {
					@Override
					public byte[] serialize(ServiceInstance instance) throws Exception {
						return Jacksons.toJsonBytes(instance);
					}

					@Override
					public ServiceInstance deserialize(byte[] bytes) throws Exception {
						return Jacksons.toJavaObject(bytes, new TypeRef<ServiceInstance<InstanceNode>>() {});
					}
				})
				.build();
			try {
				this.client.start();
				this.client.blockUntilConnected();
				this.serviceDiscovery.start();
				this.serviceCache = serviceDiscovery.serviceCacheBuilder().name(config.getServiceName()).build();
				this.serviceCache.start();
				this.started = true;
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
	}

	private void doStop() {
		try {
			if (serviceCache != null) {
				serviceCache.close();
			}
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
		}
		try {
			if (serviceDiscovery != null) {
				serviceDiscovery.close();
			}
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
		}
		try {
			if (client != null) {
				CuratorFrameworkState state = client.getState();
				if (state == null || state != CuratorFrameworkState.STOPPED) {
					client.close();
				}
			}
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
		}
		started = false;
		client = null;
		serviceDiscovery = null;
		serviceCache = null;
	}

	private void prepare() {
		if (!started) {
			doStart();
		}
	}

	public void start() {
		doStart();
	}

	public void stop() {
		doStop();
	}


	@Override
	public synchronized void register(InstanceNode instanceNode) throws InstanceRegisterException {
		prepare();
		instanceNode.check();
		try {
			ServiceInstance<InstanceNode> thisInstance
				= ServiceInstance.<InstanceNode>builder()
				.name(config.getServiceName())
				//.id(UUID.randomUUID().toString())
				.id(instanceNode.getId())
				.address(instanceNode.getHost())
				.port(instanceNode.getPort())
				.payload(instanceNode)
				.build();
			serviceDiscovery.registerService(thisInstance);
			this.selfInstance = instanceNode;
		} catch (Throwable e) {
			throw new InstanceRegisterException(e.getMessage(), e);
		}
	}

	@Override
	public List<InstanceNode> query() {
		prepare();
		List<InstanceNode> list = new ArrayList<>(20);
		try {
			List<ServiceInstance<InstanceNode>> serviceInstances = serviceCache.getInstances();
			boolean hasSelf = false;
			for (ServiceInstance<InstanceNode> serviceInstance : serviceInstances) {
				InstanceNode instance = serviceInstance.getPayload();
				if (instance.equals(selfInstance)) {
					instance.setSelf(true);
					hasSelf = true;
				} else {
					instance.setSelf(false);
				}
				list.add(instance);
			}
			if (selfInstance != null && !hasSelf) {
				list.add(selfInstance);
			}
		} catch (Throwable e) {
			throw new InstanceQueryException(e.getMessage(), e);
		}
		return list;
	}

}
