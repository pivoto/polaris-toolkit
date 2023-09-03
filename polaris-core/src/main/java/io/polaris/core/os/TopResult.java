package io.polaris.core.os;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("PMD")
@Getter
@Setter
@ToString
public class TopResult {

	private SimpleData simpleData;
	private FullData fullData;

	public boolean isSimple() {
		return simpleData != null;
	}


	@Getter
	@Setter
	@ToString
	public static class SimpleData {
		private Map<Integer, Double> threadCpuUsage = new HashMap<>(1024);
	}

	@Getter
	@Setter
	@ToString
	public static class FullData {

		private double loadAverage1;
		private double loadAverage5;
		private double loadAverage15;
		private List<TopProcessResult> processResults = new ArrayList<>();

		private int taskTotal;
		private int taskRunningTotal;
		private int taskSleepingTotal;
		private int taskStoppedTotal;
		private int taskZombieTotal;

		private double cpuUnnicedUser;
		private double cpuSystem;
		private double cpuNicedUser;
		private double cpuIdle;
		private double cpuIoWait;
		private double cpuHardInterrupts;
		private double cpuSoftInterrupts;

		private long memTotal;
		private long memUsed;
		private long memFree;
		private long memBuffers;
		private long swapTotal;
		private long swapUsed;
		private long swapFree;
		private long swapCached;
	}

	@Getter
	@Setter
	@ToString
	public static class TopProcessResult {
		private int pid;
		private String user;
		private int priority;
		private int nice;
		private long virtualMemory;
		private long residentMemory;
		private long sharedMemory;
		private String status;
		private double cpuUsage;
		private double memUsage;

	}
}

