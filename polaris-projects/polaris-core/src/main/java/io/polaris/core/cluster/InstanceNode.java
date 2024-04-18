package io.polaris.core.cluster;

import java.util.Map;

import io.polaris.core.consts.CharConsts;
import io.polaris.core.os.OS;
import io.polaris.core.string.Strings;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Qt
 * @since 1.8,  Apr 17, 2024
 */
@ToString
public class InstanceNode implements Comparable<InstanceNode> {

	@Setter
	private String id;
	@Getter
	@Setter
	private String host;
	@Getter
	@Setter
	private int port;
	@Getter
	@Setter
	private int pid;
	@Getter
	@Setter
	private String hostName;
	@Getter
	@Setter
	private String osName;
	@Getter
	@Setter
	private long vmStartTime;
	@Getter
	@Setter
	private Map<String, String> metadata;
	@Getter
	@Setter
	private volatile boolean self;

	public InstanceNode() {
	}

	public void setDefault() {
		this.host = OS.getFirstIp();
		this.hostName = OS.getHostName();
		this.osName = OS.getOsName();
		this.pid = OS.getPid();
		this.vmStartTime = OS.getVmStartTime();
	}

	public void check() {
		if (Strings.isBlank(id)) {
			if (Strings.isBlank(host)) {
				throw new IllegalArgumentException("address is null");
			}
			if (pid <= 0 && port <= 0) {
				throw new IllegalArgumentException("pid and port is null");
			}
		}
	}

	public String getId() {
		if (Strings.isBlank(id)) {
			if (port > 0) {
				return host + CharConsts.COLON + port;
			}
			return host + CharConsts.AT_MARK + pid;
		}
		return id;
	}

	@Override
	public int compareTo(InstanceNode o) {
		return this.getId().compareTo(o.getId());
	}


	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		return getId().equals(((InstanceNode) obj).getId());
	}

}
