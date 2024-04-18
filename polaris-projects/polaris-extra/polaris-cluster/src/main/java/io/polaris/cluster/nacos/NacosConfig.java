package io.polaris.cluster.nacos;

import io.polaris.core.string.Strings;
import lombok.Getter;
import lombok.Setter;

import com.alibaba.nacos.api.common.Constants;

/**
 * @author Qt
 * @since 1.8,  Apr 17, 2024
 */
@Setter
@Getter
public class NacosConfig {
	private String serviceName;
	// public
	private String namespace = Constants.DEFAULT_NAMESPACE_ID;
	// DEFAULT_GROUP
	private String groupName = Constants.DEFAULT_GROUP;
	// DEFAULT
	private String clusterName = Constants.DEFAULT_CLUSTER_NAME;
	private String hostPort;
	private String username;
	private String password;
	private String accessKey;
	private String secretKey;


	public String getHostPort() {
		return Strings.coalesce(hostPort, "localhost:8848");
	}


}
