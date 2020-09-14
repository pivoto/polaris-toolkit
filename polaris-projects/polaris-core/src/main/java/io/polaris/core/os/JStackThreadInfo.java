package io.polaris.core.os;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Qt
 * @since 1.8
 */
@Getter
@Setter
@ToString
public class JStackThreadInfo {
	private String threadName;
	private long threadId;
	private boolean daemon;
	private int priority;
	private int osPriority;
	private String tid;
	private String nid;
	private int pid;
	private String stackTrace;
	private String stackTraceHeader;
}
