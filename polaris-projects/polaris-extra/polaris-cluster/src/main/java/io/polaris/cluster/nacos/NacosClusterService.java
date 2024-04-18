package io.polaris.cluster.nacos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import io.polaris.core.cluster.ClusterService;
import io.polaris.core.cluster.InstanceNode;
import io.polaris.core.cluster.InstanceQueryException;
import io.polaris.core.cluster.InstanceRegisterException;
import io.polaris.core.lang.bean.BeanMap;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.service.ServiceName;
import io.polaris.core.string.Strings;
import io.polaris.json.Jacksons;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.common.utils.CollectionUtils;

/**
 * @author Qt
 * @since 1.8,  Apr 17, 2024
 */
@SuppressWarnings("ALL")
@Slf4j
@ServiceName("nacos")
public class NacosClusterService implements ClusterService {
	@Setter
	@Getter
	private NacosConfig config;
	private NamingService namingService;
	private volatile InstanceNode selfInstance;
	private volatile boolean started = false;

	public NacosClusterService() {
		this(null);
	}

	public NacosClusterService(NacosConfig config) {
		this.config = config;
		Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
	}

	private void doStart() {
		if (!started) {
			doStop();
			if (config == null) {
				throw new IllegalStateException("config is null");
			}
			try {
				Properties properties = new Properties();
				properties.put(PropertyKeyConst.SERVER_ADDR, config.getHostPort());
				properties.put(PropertyKeyConst.NAMESPACE, config.getNamespace());
				if (Strings.isNotEmpty(config.getUsername())) {
					properties.put(PropertyKeyConst.USERNAME, config.getUsername());
					properties.put(PropertyKeyConst.PASSWORD, config.getPassword());
				} else if (Strings.isNotEmpty(config.getAccessKey())) {
					properties.put(PropertyKeyConst.ACCESS_KEY, config.getAccessKey());
					properties.put(PropertyKeyConst.SECRET_KEY, config.getSecretKey());
				}
				namingService = NamingFactory.createNamingService(properties);
				this.started = true;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
	}

	private void doStop() {
		try {
			namingService.shutDown();
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
		}
		started = false;
		namingService = null;
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
	public void register(InstanceNode instanceNode) throws InstanceRegisterException {
		prepare();
		instanceNode.check();
		try {
			String host = instanceNode.getHost();
			int port = instanceNode.getPort();
			Instance instance = new Instance();
			//instance.setInstanceId(instanceNode.getId());
			instance.setIp(host);
			instance.setPort(port);
			instance.setClusterName(config.getClusterName());
			BeanMap<InstanceNode> beanMap = Beans.newBeanMap(instanceNode);
			Map<String, String> metadata = new HashMap<>();
			beanMap.forEach((k, v) -> {
				if (v != null) {
					metadata.put(k, Jacksons.toJsonString(v));
				}
			});
			instance.setMetadata(metadata);
			if (Strings.isNotBlank(config.getGroupName())) {
				this.namingService.registerInstance(config.getServiceName(), config.getGroupName(), instance);
			} else {
				this.namingService.registerInstance(config.getServiceName(), instance);
			}
			this.selfInstance = instanceNode;
		} catch (Throwable e) {
			throw new InstanceRegisterException(e.getMessage(), e);
		}
	}

	@Override
	public List<InstanceNode> query() {
		prepare();
		List<InstanceNode> list = new ArrayList<>();
		try {
			boolean hasSelf = false;
			List<Instance> instances = null;
			if (Strings.isNotBlank(config.getGroupName())) {
				instances = namingService.selectInstances(config.getServiceName(), config.getGroupName(), true);
			} else {
				instances = namingService.selectInstances(config.getServiceName(), true);
			}

			if (CollectionUtils.isNotEmpty(instances)) {
				for (Instance instance : instances) {
					InstanceNode instanceNode = new InstanceNode();
					Map<String, String> metadata = instance.getMetadata();
					if (metadata != null) {
						BeanMap<InstanceNode> beanMap = Beans.newBeanMap(instanceNode);
						metadata.forEach((k, v) -> {
							try {
								if (beanMap.containsKey(k)) {
									beanMap.put(k, Jacksons.toJavaObject(v, beanMap.getType(k)));
								}
							} catch (Exception ignored) {
							}
						});
					}
					//instanceNode.setId(instance.getInstanceId());
					instanceNode.setHost(instance.getIp());
					instanceNode.setPort(instance.getPort());
					if (instanceNode.equals(selfInstance)) {
						instanceNode.setSelf(true);
						hasSelf = true;
					} else {
						instanceNode.setSelf(false);
					}
					list.add(instanceNode);
				}
				if (selfInstance != null && !hasSelf) {
					list.add(selfInstance);
				}
			}
		} catch (Throwable e) {
			throw new InstanceQueryException(e.getMessage());
		}
		return list;
	}
}
