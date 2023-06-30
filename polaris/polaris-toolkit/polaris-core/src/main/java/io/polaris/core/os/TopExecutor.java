package io.polaris.core.os;

import io.polaris.core.regex.Patterns;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Qt
 * @since 1.8
 */
public class TopExecutor {

	public static final String REGEXP_TOP_PID = "(\\d+)\\s+" +//PID
		"(\\w+)\\s+" +//USER
		"(\\d+)\\s+" +//PR  --  Priority
		"(\\d+)\\s+" +//NI  --  Nice Value
		"([\\d.kmg]+)\\s+" +//VIRT  --  Virtual Memory Size (KiB)
		"([\\d.kmg]+)\\s+" +//RES  --  Resident Memory Size (KiB)
		"([\\d.kmg]+)\\s+" +//SHR  --  Shared Memory Size (KiB)
		"([\\w]+)\\s+" +//S  --  Process Status
		"([\\d.kmg]+)\\s+" +//%CPU  --  CPU Usage
		"([\\d.kmg]+)\\s+" +//%MEM  --  Memory Usage (RES)
		"([\\d.kmg]+)" +//TIME+  --  CPU Time, hundredths
		"";
	public static final String REGEXP_TOP_SWAP = "Swap:\\s*([\\dkmg]+)\\s*total,\\s*([\\dkmg]+)\\s*used,\\s*([\\dkmg]+)\\s*free,\\s*([\\dkmg]+)\\s*cached";
	public static final String REGEXP_TOP_MEM = "Mem:\\s*([\\dkmg]+)\\s*total,\\s*([\\dkmg]+)\\s*used,\\s*([\\dkmg]+)\\s*free,\\s*([\\dkmg]+)\\s*buffers";
	public static final String REGEXP_TOP_CPU = "Cpu\\(s\\):\\s*([\\d.]+)%us,\\s*([\\d.]+)%sy,\\s*([\\d.]+)%ni,\\s*([\\d.]+)%id,\\s*([\\d.]+)%wa,\\s*([\\d.]+)%hi,\\s*([\\d.]+)%si,\\s*([\\d.]+)%st";
	public static final String REGEXP_TOP_TASK = "Tasks:\\s*(\\d+) total,\\s*(\\d+) running,\\s*(\\d+) sleeping,\\s*(\\d+) stopped,\\s*(\\d+) zombie";

	private static volatile TopExecutor instance;
	private volatile boolean topThreadStarted = false;
	private volatile boolean topThreadRunning = true;
	private volatile Process topProcess;
	private volatile TopResult topResult;
	private volatile boolean collectTopSimple = true;

	public static TopExecutor getInstance() {
		if (instance == null) {
			synchronized (TopExecutor.class) {
				if (instance == null) {
					instance = new TopExecutor();
				}
			}
		}
		return instance;
	}

	private TopExecutor() {
		start();
		Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
	}

	public TopExecutor simple(boolean simple) {
		this.collectTopSimple = simple;
		return this;
	}

	public TopResult getTopResult() {
		return topResult;
	}

	public boolean isStarted() {
		return topThreadStarted;
	}

	public TopExecutor stop() {
		if (!topThreadStarted) {
			return this;
		}
		try {
			this.topThreadRunning = false;
			if (this.topProcess != null) {
				this.topProcess.destroy();
			}
		} catch (Throwable ignore) {
		}
		return this;
	}

	public TopExecutor start() {
		if (topThreadStarted) {
			return this;
		}
		startTopThread();
		return this;
	}

	private void startTopThread() {
		if (OsType.WINDOWS == OS.getOsType()) {
			return;
		}
		Thread topThread = new Thread(() -> {
			String cmd = "top -b -d 5 -n 2 -H -p " + OS.getPid();
			String[] cmdArray = cmd.split(" +");
			while (topThreadRunning) {
				Process process = null;
				try {
					process = topProcess = Runtime.getRuntime().exec(cmdArray);
					InputStream stdin = process.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(stdin, StandardCharsets.UTF_8));

					TopResult[] topResult = new TopResult[]{new TopResult()};
					for (String line = br.readLine(); line != null; line = br.readLine()) {
						//System.out.println(line);
						line = line.trim();
						parseTopResult(topResult, line);
					}
					process.waitFor();
				} catch (Throwable ignore) {
				} finally {
					if (process != null) {
						try {
							process.destroy();
						} catch (Exception ignore) {
						}
					}
				}
			}
			topThreadStarted = false;
		});
		topThread.setDaemon(true);
		topThread.setName("daemon-top-thread-p" + OS.getPid());
		topThreadStarted = true;
		topThread.start();
	}

	private double toByteDoubleVal(String byteStr) {
		if (byteStr.endsWith("b") || byteStr.endsWith("B")) {
			return toByteDoubleVal(byteStr.substring(0, byteStr.length() - 1));
		}
		if (byteStr.endsWith("k") || byteStr.endsWith("K")) {
			return 1024L * toByteDoubleVal(byteStr.substring(0, byteStr.length() - 1));
		}
		if (byteStr.endsWith("m") || byteStr.endsWith("M")) {
			return 1024L * 1024L * toByteDoubleVal(byteStr.substring(0, byteStr.length() - 1));
		}
		if (byteStr.endsWith("g") || byteStr.endsWith("G")) {
			return 1024L * 1024L * 1024L * toByteDoubleVal(byteStr.substring(0, byteStr.length() - 1));
		}
		return Double.parseDouble(byteStr);
	}

	private long toByteVal(String byteStr) {
		return (long) toByteDoubleVal(byteStr);
	}

	private void parseTopResult(TopResult[] topResultRef, String line) {

		/*
		top - 09:26:34 up 1187 days, 16:30,  1 user,  load average: 16.60, 17.65, 16.84
		Tasks: 744 total,   0 running, 744 sleeping,   0 stopped,   0 zombie
		Cpu(s):  9.1%us,  0.7%sy,  0.0%ni, 90.1%id,  0.1%wa,  0.0%hi,  0.1%si,  0.0%st
		Mem:  41342388k total, 40671972k used,   670416k free,   228012k buffers
		Swap:  8208380k total,  2376800k used,  5831580k free,  2660372k cached

		   PID USER      PR  NI  VIRT  RES  SHR S %CPU %MEM    TIME+  COMMAND
		  7683 web       20   0 10.9g 983m 8004 S  1.9  2.4 721:53.63 java
		118618 web       20   0 10.9g 983m 8004 S  1.9  2.4  17:53.34 java
		...
		...
 		*/
		TopResult topResult = topResultRef[0];
		if (topResult == null) {
			topResult = topResultRef[0] = new TopResult();
		}

		try {
			if (line.startsWith("top - ")) {
				// 新一批监控结果
				topResult = topResultRef[0] = new TopResult();
				if (collectTopSimple) {
					topResult.setSimpleData(new TopResult.SimpleData());
					topResult.setFullData(null);
				} else {
					topResult.setSimpleData(null);
					topResult.setFullData(new TopResult.FullData());
				}
				int i = line.indexOf("load average: ");
				String loadAverage = line.substring(i + "load average: ".length());
				String[] arr = loadAverage.split(",");
				String loadAverage1 = arr[0].trim();
				String loadAverage5 = arr[1].trim();
				String loadAverage15 = arr[2].trim();
				if (!topResult.isSimple()) {
					topResult.getFullData().setLoadAverage1(Double.parseDouble(loadAverage1));
					topResult.getFullData().setLoadAverage5(Double.parseDouble(loadAverage5));
					topResult.getFullData().setLoadAverage15(Double.parseDouble(loadAverage15));
				}
			} else if (line.startsWith("Tasks:")) {
				if (!topResult.isSimple()) {
					Pattern pattern = Patterns.getPattern(REGEXP_TOP_TASK);
					Matcher matcher = pattern.matcher(line);
					if (matcher.find()) {
						topResult.getFullData().setTaskTotal(Integer.parseInt(matcher.group(1)));
						topResult.getFullData().setTaskRunningTotal(Integer.parseInt(matcher.group(2)));
						topResult.getFullData().setTaskSleepingTotal(Integer.parseInt(matcher.group(3)));
						topResult.getFullData().setTaskStoppedTotal(Integer.parseInt(matcher.group(4)));
						topResult.getFullData().setTaskZombieTotal(Integer.parseInt(matcher.group(5)));
					}
				}
			} else if (line.startsWith("Cpu(s):")) {
				if (!topResult.isSimple()) {
					Pattern pattern = Patterns.getPattern(REGEXP_TOP_CPU);
					Matcher matcher = pattern.matcher(line);
					if (matcher.find()) {
						topResult.getFullData().setCpuUnnicedUser(Double.parseDouble(matcher.group(1)));
						topResult.getFullData().setCpuSystem(Double.parseDouble(matcher.group(2)));
						topResult.getFullData().setCpuNicedUser(Double.parseDouble(matcher.group(3)));
						topResult.getFullData().setCpuIdle(Double.parseDouble(matcher.group(4)));
						topResult.getFullData().setCpuIoWait(Double.parseDouble(matcher.group(5)));
						topResult.getFullData().setCpuHardInterrupts(Double.parseDouble(matcher.group(6)));
						topResult.getFullData().setCpuSoftInterrupts(Double.parseDouble(matcher.group(7)));
					}
				}
			} else if (line.startsWith("Mem:")) {
				if (!topResult.isSimple()) {
					Pattern pattern = Patterns.getPattern(REGEXP_TOP_MEM);
					Matcher matcher = pattern.matcher(line);
					if (matcher.find()) {
						topResult.getFullData().setMemTotal(toByteVal(matcher.group(1)));
						topResult.getFullData().setMemUsed(toByteVal(matcher.group(2)));
						topResult.getFullData().setMemFree(toByteVal(matcher.group(3)));
						topResult.getFullData().setMemBuffers(toByteVal(matcher.group(4)));
					}
				}
			} else if (line.startsWith("Swap:")) {
				if (!topResult.isSimple()) {
					Pattern pattern = Patterns.getPattern(REGEXP_TOP_SWAP);
					Matcher matcher = pattern.matcher(line);
					if (matcher.find()) {
						topResult.getFullData().setSwapTotal(toByteVal(matcher.group(1)));
						topResult.getFullData().setSwapUsed(toByteVal(matcher.group(2)));
						topResult.getFullData().setSwapFree(toByteVal(matcher.group(3)));
						topResult.getFullData().setSwapCached(toByteVal(matcher.group(4)));
					}
				}
			} else if (line.length() == 0) {
				if (!topResult.isSimple()) {
					if (!topResult.getFullData().getProcessResults().isEmpty()) {
						// 本轮采集完成
						//System.out.println("本轮采集完成 "+new Date());
						TopExecutor.this.topResult = topResult;
					}
				} else {
					if (!topResult.getSimpleData().getThreadCpuUsage().isEmpty()) {
						// 本轮采集完成
						//System.out.println("本轮采集完成 "+new Date());
						TopExecutor.this.topResult = topResult;
					}
				}
			} else {
				// PID USER      PR  NI  VIRT  RES  SHR S %CPU %MEM    TIME+  COMMAN
				if (line.length() > 0 && !line.startsWith("PID")) {
					Pattern pattern = Patterns.getPattern(REGEXP_TOP_PID);
					Matcher matcher = pattern.matcher(line);
					if (!topResult.isSimple()) {
						if (matcher.find()) {
							TopResult.TopProcessResult processResult = new TopResult.TopProcessResult();
							String pid = matcher.group(1);
							processResult.setPid(Integer.parseInt(pid));
							String user = matcher.group(2);
							processResult.setUser(user);
							String priority = matcher.group(3);
							processResult.setPriority(Integer.parseInt(priority));
							String nice = matcher.group(4);
							processResult.setNice(Integer.parseInt(nice));
							String virtualMemory = matcher.group(5);
							processResult.setVirtualMemory(toByteVal(virtualMemory));
							String residentMemory = matcher.group(6);
							processResult.setResidentMemory(toByteVal(residentMemory));
							String sharedMemory = matcher.group(7);
							processResult.setSharedMemory(toByteVal(sharedMemory));
							String status = matcher.group(8);
							processResult.setStatus(status);
							String cpuUsage = matcher.group(9);
							processResult.setCpuUsage(Double.parseDouble(cpuUsage));
							String memUsage = matcher.group(10);
							processResult.setMemUsage(Double.parseDouble(memUsage));
							topResult.getFullData().getProcessResults().add(processResult);
						}
					} else {
						if (matcher.find()) {
							String pid = matcher.group(1);
							String cpuUsage = matcher.group(9);
							topResult.getSimpleData().getThreadCpuUsage().put(Integer.parseInt(pid), Double.parseDouble(cpuUsage));
						}
					}
				}
			}
		} catch (Exception ignore) {
		}
	}
}
