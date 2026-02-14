package io.polaris.cluster.nacos;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.polaris.core.cluster.InstanceNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class NacosClusterServiceTest {

	@Test
	void test01() throws InterruptedException {
		NacosClusterService service = new NacosClusterService();
		NacosConfig config = new NacosConfig();
		config.setHostPort("127.0.0.1:8848");
		config.setNamespace("public");
		config.setServiceName("polaris-service");
		service.setConfig(config);
		// nacos 不支持同客户端面注册多实例
		for (int i = 0; i < 5; i++) {
			InstanceNode instanceNode = new InstanceNode();
			instanceNode.setDefault();
//			instanceNode.setAddress("1.1.1."+(i+1));
			instanceNode.setPort(0);
//			instanceNode.setPort(3000 + i);
			service.register(instanceNode);
		}

		TimeUnit.SECONDS.sleep(3);
		for (int i = 0; i < 10; i++) {
			List<InstanceNode> list = service.query();
			log.info("list: {}", list);
			TimeUnit.SECONDS.sleep(3);
		}
		TimeUnit.SECONDS.sleep(3);
	}
}
