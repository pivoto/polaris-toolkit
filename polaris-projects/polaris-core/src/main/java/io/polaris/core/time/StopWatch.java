package io.polaris.core.time;

import javax.annotation.concurrent.NotThreadSafe;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Qt
 * @since 1.8,  Dec 25, 2023
 */
@NotThreadSafe
public class StopWatch {
	private final String id;
	private final List<Task> taskList = new LinkedList<>();
	private long totalTimeNanos;
	private int taskCount;
	private long startTimeNanos;
	private String current;

	public StopWatch() {
		this("");
	}

	public StopWatch(String id) {
		this.id = id;
	}

	public void start() throws IllegalStateException {
		start("");
	}

	public void start(String taskName) throws IllegalStateException {
		if (isRunning()) {
			throw new IllegalStateException("It's already running");
		}
		this.current = taskName;
		this.startTimeNanos = System.nanoTime();
	}

	public void stop() throws IllegalStateException {
		if (this.current == null) {
			throw new IllegalStateException("It's not running");
		}
		long lastTime = System.nanoTime() - this.startTimeNanos;
		this.totalTimeNanos += lastTime;
		this.taskList.add(new Task(this.current, lastTime));
		++this.taskCount;
		this.current = null;
	}

	public boolean isRunning() {
		return (this.current != null);
	}

	public String id() {
		return this.id;
	}

	public long totalTimeNanos() {
		return this.totalTimeNanos;
	}

	public long totalTimeMillis() {
		return TimeUnit.NANOSECONDS.toMillis(this.totalTimeNanos);
	}

	public double totalTimeSeconds() {
		return this.totalTimeNanos / 1_000_000_000.0;
	}

	public int taskCount() {
		return this.taskCount;
	}

	public Task[] getTasks() {
		return this.taskList.toArray(new Task[0]);
	}

	public String shortSummary() {
		return "StopWatch '" + this.id + "': running time = " + totalTimeNanos() + " ns";
	}

	public String prettyPrint() {
		StringBuilder sb = new StringBuilder(shortSummary());
		sb.append('\n');
		sb.append("---------------------------------------------\n");
		sb.append("ns         %     Task name\n");
		sb.append("---------------------------------------------\n");
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMinimumIntegerDigits(9);
		nf.setGroupingUsed(false);
		NumberFormat pf = NumberFormat.getPercentInstance();
		pf.setMinimumIntegerDigits(3);
		pf.setGroupingUsed(false);
		for (Task task : getTasks()) {
			sb.append(nf.format(task.timeNanos())).append("  ");
			sb.append(pf.format((double) task.timeNanos() / totalTimeNanos())).append("  ");
			sb.append(task.taskName()).append("\n");
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(shortSummary());
		for (Task task : getTasks()) {
			sb.append("; [").append(task.taskName()).append("] took ").append(task.timeNanos()).append(" ns");
			long percent = Math.round(100.0 * task.timeNanos() / totalTimeNanos());
			sb.append(" = ").append(percent).append("%");
		}
		return sb.toString();
	}


	public static final class Task {
		private final String taskName;
		private final long timeNanos;

		Task(String taskName, long timeNanos) {
			this.taskName = taskName;
			this.timeNanos = timeNanos;
		}

		public String taskName() {
			return taskName;
		}

		public long timeNanos() {
			return timeNanos;
		}

		public long timeMillis() {
			return TimeUnit.NANOSECONDS.toMillis(this.timeNanos);
		}

		public double timeSeconds() {
			return this.timeNanos / 1_000_000_000.0;
		}
	}

}
