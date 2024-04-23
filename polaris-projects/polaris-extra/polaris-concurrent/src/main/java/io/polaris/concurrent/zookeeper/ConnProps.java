package io.polaris.concurrent.zookeeper;

import java.util.Objects;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qt
 * @since 1.8,  Apr 24, 2024
 */
@Getter
public class ConnProps {
	private final String address;
	private final int retry;

	public ConnProps(String address) {
		this(address, -1);
	}

	public ConnProps(String address, int retry) {
		this.address = address;
		this.retry = retry;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ConnProps connProps = (ConnProps) o;
		return retry == connProps.retry && Objects.equals(address, connProps.address);
	}

	@Override
	public int hashCode() {
		return Objects.hash(address, retry);
	}
}
