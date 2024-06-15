package io.polaris.concurrent.zookeeper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.polaris.core.env.GlobalStdEnv;
import io.polaris.core.random.Randoms;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.junit.jupiter.api.Test;

@Slf4j
class KeepAliveClientTest {
	// 20.4.16.6:2181,20.4.16.9:2181,20.4.16.24:2181
	static String zkAddress = GlobalStdEnv.getOrDefaultIfBlank("zk.address", "127.0.0.1:2181");

	@Test
	void test01() throws Exception {
		CuratorFramework client = ZkClientCache.getClient(new ConnProps(zkAddress));

		CountDownLatch latch = new CountDownLatch(10);

		Runnable r = () -> {
			KeepAliveClient kac = new KeepAliveClient(new ConnProps(zkAddress), "abc/def", "a1");
			try {
				kac.startup();
				String node = kac.getVmProcessUid() + "@" + Randoms.randomString(5);
				log.info("node: {}", node);
				kac.registerNode(node);
				TimeUnit.SECONDS.sleep(1);
				log.info("all: {}", kac.getAllRegisterNodes());
				TimeUnit.SECONDS.sleep(3);
			} catch (Exception e) {
				log.error("", e);
			} finally {
				latch.countDown();
			}
		};

		for (int i = 0; i < latch.getCount(); i++) {
			new Thread(r).start();
		}
		latch.await();

		ZkClientCache.closeAll();
	}
}
