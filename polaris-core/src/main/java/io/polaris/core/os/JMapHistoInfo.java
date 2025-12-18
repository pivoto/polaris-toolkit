package io.polaris.core.os;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * @author Qt
 */
@Getter
@Setter
@ToString
public class JMapHistoInfo {
	private int num;
	private int instances;
	private long bytes;
	private String className;
}
