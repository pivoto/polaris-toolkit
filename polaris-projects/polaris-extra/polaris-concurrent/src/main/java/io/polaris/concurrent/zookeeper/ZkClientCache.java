package io.polaris.concurrent.zookeeper;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
 * @since 1.8,  Apr 23, 2024
 */
@Slf4j
public class ZkClientCache {
	private static final Map<ConnProps, CuratorFramework> factories = new ConcurrentHashMap<>();

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.warn("运行JVM虚拟机停止钩子: 关闭Zookeeper客户端连接");
			closeAll();
		}));
	}

	public static CuratorFramework getClient(ConnProps props) {
		String address = props.getAddress();
		int retry = props.getRetry();
		if (Strings.isBlank(address)) {
			throw new IllegalArgumentException(address);
		}
		log.debug("获取客户端连接. address: {}", address);
		CuratorFramework client = factories.get(props);
		if (client == null || client.getState() != CuratorFrameworkState.STARTED) {
			synchronized (factories) {
				client = factories.get(props);
				if (client == null || client.getState() != CuratorFrameworkState.STARTED) {
					client = CuratorFrameworkFactory.newClient(address,
						/*60 * 1000, 60 * 1000,*/
						retryPolicy(retry));
					client.start();
					log.info("初始化客户端连接并启动. address: {}", address);
					factories.put(props, client);
				}
			}
		}
		return client;
	}

	public static RetryPolicy retryPolicy(int retry) {
		if (retry < 0) {
			return retryForeverPolicy();
		} else if (retry == 0) {
			return new RetryOneTime(100);
		} else {
			return new ExponentialBackoffRetry(100, retry);
		}
	}

	public static RetryPolicy retryForeverPolicy() {
		return new RetryForever(2000);
	}

	public static void closeAll() {
		Set<Map.Entry<ConnProps, CuratorFramework>> set = factories.entrySet();
		for (Iterator<Map.Entry<ConnProps, CuratorFramework>> it = set.iterator(); it.hasNext(); ) {
			Map.Entry<ConnProps, CuratorFramework> next = it.next();
			try {
				next.getValue().close();
			} catch (Throwable e) {
				log.error("", e);
			}
			it.remove();
		}
	}

}
