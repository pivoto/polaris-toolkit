package io.polaris.cluster.zookeeper;

import io.polaris.core.string.Strings;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qt
 * @since 1.8,  Apr 17, 2024
 */
@Setter
@Getter
public class ZookeeperConfig {
	private String serviceName;
	private String namespace = "public";
	private String hostPort;
	private int baseSleepTimeMs;
	private int maxRetries;
	private boolean enableAcl = false;
	private String schema;
	private String auth;


	public String getHostPort() {
		return Strings.coalesce(hostPort, "localhost:2181");
	}

	public int getBaseSleepTimeMs() {
		return baseSleepTimeMs > 0 ? baseSleepTimeMs : 1000;
	}

	public int getMaxRetries() {
		return maxRetries > 0 ? maxRetries : 3;
	}

}
