package io.polaris.core.apm;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.polaris.core.concurrent.Schedules;
import io.polaris.core.concurrent.Threads;
import io.polaris.core.string.Strings;

public class StackTraceTimeEstimator {


	// 存储每个代码行的执行次数和总耗时
	private static final Map<String, ExecutionData> lineExecutionData = new LinkedHashMap<>();
	// 采样间隔，单位为毫秒
	private static final long SAMPLING_INTERVAL = 10;
	// 记录程序开始时间
	private static final long startTime = System.currentTimeMillis();

	public static void main(String[] args) throws InterruptedException {
		// 创建一个示例线程
		AtomicBoolean start = new AtomicBoolean(true);
		Thread targetThread = new Thread(() -> {
			System.out.println(Strings.repeat('-',80));
			for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
				System.out.println(ste);
			}
			System.out.println(Strings.repeat('-',80));
			while (start.get()) {
				// 模拟一些工作
				doSomeWork();
			}
			System.out.println(Strings.repeat('-',80));
			for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
				System.out.println(ste);
			}
			System.out.println(Strings.repeat('-',80));
		});
		targetThread.start();

		// 创建定时任务，每10毫秒获取一次堆栈信息
		ScheduledExecutorService scheduler = Schedules.single();
		scheduler.scheduleAtFixedRate(new StackTraceSampler(targetThread), 0, 10, TimeUnit.MILLISECONDS);

		// 模拟程序运行一段时间
		Threads.sleep(1000);

		// 停止定时任务
		start.set(false);
		Schedules.shutdown(scheduler);

		Threads.sleep(1000);
		System.out.println(Strings.repeat('-',80));
		for (StackTraceElement ste : targetThread.getStackTrace()) {
			System.out.println(ste);
		}
		System.out.println(Strings.repeat('-',80));
		// 输出估算结果
		printEstimatedTimes();
	}

	private static void doSomeWork() {
		// 模拟一些代码执行
		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// 记录异常信息，并中断当前线程
				System.err.println("Thread was interrupted: " + e.getMessage());
				Thread.currentThread().interrupt(); // 恢复中断状态
				return;
			}
			Math.sqrt(i);
		}
	}

	private static void printEstimatedTimes() {
		long totalTime = System.currentTimeMillis() - startTime;
		for (Map.Entry<String, ExecutionData> entry : lineExecutionData.entrySet()) {
			String line = entry.getKey();
			ExecutionData data = entry.getValue();
			int hits = data.getHits();
			long totalDuration = data.getTotalDuration();
			// 简单估算平均执行时间
			double averageTime = (double) totalDuration / hits;
			System.out.printf("Line: %s, Hits: %d, Total Time: %d ms, Average Time: %.3f ms%n", line, hits,totalDuration, averageTime);
		}
	}

	static class StackTraceSampler implements Runnable {
		private final Thread targetThread;

		public StackTraceSampler(Thread targetThread) {
			this.targetThread = targetThread;
		}

		@Override
		public void run() {
			StackTraceElement[] stackTrace = targetThread.getStackTrace();
			for (StackTraceElement element : stackTrace) {
				String lineKey = element.toString();// element.getClassName() + "." + element.getMethodName() + ":" + element.getLineNumber();
				lineExecutionData.computeIfAbsent(lineKey, k -> new ExecutionData()).increment();
			}
		}
	}

	static class ExecutionData {
		private int hits;
		private long totalDuration;

		public ExecutionData() {
			this.hits = 0;
			this.totalDuration = 0;
		}

		public void increment() {
			hits++;
			totalDuration += SAMPLING_INTERVAL;
		}

		public int getHits() {
			return hits;
		}

		public long getTotalDuration() {
			return totalDuration;
		}
	}
}
