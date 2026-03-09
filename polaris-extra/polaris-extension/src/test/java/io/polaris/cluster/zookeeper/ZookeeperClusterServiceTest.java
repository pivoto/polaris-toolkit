package io.polaris.cluster.zookeeper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.polaris.core.cluster.InstanceNode;
import io.polaris.core.lang.TypeRef;
import io.polaris.json.Jacksons;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ZookeeperClusterServiceTest {

	@Test
	void test00() throws Exception {
		InstanceNode instanceNode = new InstanceNode();
		org.apache.curator.x.discovery.ServiceInstance<InstanceNode> instance = org.apache.curator.x.discovery.ServiceInstance.<InstanceNode>builder()
			.name("test")
//			.id(UUID.randomUUID().toString())
			.id(instanceNode.getId())
			.address(instanceNode.getHost())
			.port(instanceNode.getPort())
			.payload(instanceNode)
			.build();

		log.info("serialize: {}",Jacksons.toJsonString(instance));
		log.info("deserialize: {}",Jacksons.toJavaObject(Jacksons.toJsonString(instance), new TypeRef<org.apache.curator.x.discovery.ServiceInstance<InstanceNode>>() {}));
	}

	@Test
	void test01() throws InterruptedException {
		ZookeeperClusterService service = new ZookeeperClusterService();
		ZookeeperConfig config = new ZookeeperConfig();
		config.setHostPort("127.0.0.1:2181");
		config.setNamespace("public/cluster/polaris");
		config.setServiceName("polaris-service");
		service.setConfig(config);
		for (int i = 0; i < 5; i++) {
			InstanceNode instanceNode = new InstanceNode();
			instanceNode.setDefault();
			instanceNode.setPort(3000+i);
			service.register(instanceNode);
		}

		TimeUnit.SECONDS.sleep(3);
		for (int i = 0; i < 3; i++) {
			List<InstanceNode> list = service.query();
			log.info("list: {}", list);
			TimeUnit.SECONDS.sleep(3);
		}
		TimeUnit.SECONDS.sleep(3);
	}
}
