package io.polaris.concurrent.zookeeper;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.polaris.core.date.Dates;
import io.polaris.core.io.IO;
import io.polaris.core.os.OS;
import io.polaris.core.string.Strings;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

/**
 * @author Qt
 * @since 1.8,  Apr 23, 2024
 */
@Slf4j
@Getter
public class KeepAliveClient {
	/** Zookeeper连接配置 */
	private final ConnProps props;
	private final String namespace;
	/** Zookeeper注册路径 */
	private final String regPath;
	/** 主机IP+虚拟机进程号 */
	private final String vmProcessUid;
	/** 虚拟机启动时间 */
	private final long vmStartTime;
	private CuratorFramework client;

	public KeepAliveClient(ConnProps props, String regPath, Collection<String> hostPatterns) {
		this(props, null, regPath, hostPatterns);
	}

	public KeepAliveClient(ConnProps props, String regPath) {
		this(props, null, regPath, null);
	}

	public KeepAliveClient(ConnProps props, String namespace, String regPath) {
		this(props, namespace, regPath, null);
	}

	public KeepAliveClient(ConnProps props, String namespace, String regPath, Collection<String> hostPatterns) {
		this.props = props;
		this.namespace = namespace;
		if (regPath.startsWith("/")) {
			this.regPath = regPath;
		} else {
			this.regPath = "/" + regPath;
		}
		if (hostPatterns != null) {
			String[] regex = hostPatterns.toArray(new String[0]);
			vmProcessUid = OS.getPriorOrFirstIp(regex) + "#" + OS.getPid();
		} else {
			vmProcessUid = OS.getFirstIp() + "#" + OS.getPid();
		}
		vmStartTime = OS.getVmStartTime();
	}

	public void startup() {
		log.info("初始化连接...");
		CuratorFramework client = ZkClientCache.getClient(props);
		if (Strings.isNotBlank(namespace)) {
			this.client = client.usingNamespace(namespace);
		} else {
			this.client = client;
		}
	}

	public boolean registerNode() {
		return registerNode(vmProcessUid);
	}

	public boolean registerNode(String node) {
		CuratorFramework zkClient = this.client;
		boolean regSuccess = false;
		int tryTimes = 3;
		String path = this.regPath + "/" + node;
		for (int i = 0; i < tryTimes; i++) {
			try {
				log.info("namespace:{}, state:{}", zkClient.getNamespace(),zkClient.getState());
				log.info("注册临时节点{}...",path);
				zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
				zkClient.setData().forPath(path,
					(vmProcessUid + "@" + Dates.YYYY_MM_DD_HH_MM_SS_SSS.format(Instant.now())).getBytes());
				regSuccess = true;
				break;
			} catch (Exception e) {
				log.debug("通信异常", e);
			}
		}
		if (!regSuccess) {
			log.error("注册节点{}失败!", path);
		}
		return regSuccess;
	}

	/**
	 * 获取所有执行端注册节点
	 *
	 * @return
	 * @throws Exception
	 */
	public List<String> getAllRegisterNodes() throws Exception {
		List<String> list = client.getChildren().forPath(regPath);
		return Collections.unmodifiableList(list);
	}

	public void shutdown() {
		CuratorFramework zkClient = this.client;
		IO.close(zkClient);
		this.client = null;
	}
}
