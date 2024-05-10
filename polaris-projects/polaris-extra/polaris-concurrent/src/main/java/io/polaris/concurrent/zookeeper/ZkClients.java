package io.polaris.concurrent.zookeeper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.polaris.core.env.GlobalStdEnv;
import io.polaris.core.string.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.retry.RetryOneTime;

/**
 * @author Qt
 * @since  Apr 23, 2024
 */
@Slf4j
public class ZkClients {
	public static final String ZOOKEEPER_NAMESPACE = "system.zookeeper.namespace";
	public static final String ZOOKEEPER_ADDRESS = "system.zookeeper.address";
	public static final String CURATOR_RETRY = "system.zookeeper.curator.retry";
	private static final Map<String, CuratorFramework> factories = new ConcurrentHashMap<>();

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.warn("运行JVM虚拟机停止钩子: ZkClients.close()");
			factories.forEach((k, v) -> v.close());
		}));
	}

	public static CuratorFramework getClient(String zkAddress) {
		if (Strings.isBlank(zkAddress)) {
			throw new IllegalArgumentException(zkAddress);
		}
		log.debug("获取 ZK 客户端连接. zkAddress: {}", zkAddress);
		CuratorFramework client = factories.get(zkAddress);
		if (client == null || client.getState() != CuratorFrameworkState.STARTED) {
			synchronized (factories) {
				client = factories.get(zkAddress);
				if (client == null || client.getState() != CuratorFrameworkState.STARTED) {
					client = CuratorFrameworkFactory.newClient(zkAddress,
						/*60 * 1000, 60 * 1000,*/
						getRetryPolicy());
					client.start();
					log.info("初始化 ZK 客户端连接并启动. zkAddress: {}", zkAddress);
					factories.put(zkAddress, client);
				}
			}
		}
		return client;
	}

	public static CuratorFramework getClient() {
		return getClient(getAddress());
	}

	public static String getAddress() {
		return GlobalStdEnv.get(ZOOKEEPER_ADDRESS);
	}

	public static String getNamespace() {
		return GlobalStdEnv.get(ZOOKEEPER_NAMESPACE);
	}

	public static RetryPolicy getRetryPolicy() {
		try {
			int retry = Integer.parseInt(GlobalStdEnv.get(CURATOR_RETRY, "3"));
			return retryPolicy(retry);
		} catch (Exception e) {
			return maxRetryPolicy();
		}
	}

	public static RetryPolicy retryPolicy(int retry) {
		if (retry <= 0) {
			return new RetryOneTime(100);
		} else {
			return new ExponentialBackoffRetry(100, retry);
		}
	}

	public static RetryPolicy maxRetryPolicy() {
		return new ExponentialBackoffRetry(100, 29);
	}

	public static RetryPolicy retryForeverPolicy() {
		return new RetryForever(2000);
	}

}
