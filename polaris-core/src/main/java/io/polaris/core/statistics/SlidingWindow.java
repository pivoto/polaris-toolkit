package io.polaris.core.statistics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import io.polaris.core.concurrent.PooledThreadFactory;

/**
 * 滑动窗口统计工具
 *
 * @author Qt
 * @since Aug 01, 2024
 */
public class SlidingWindow<S extends EventStatistics<S>> {
	private final int maxDuration;
	private final int windowMills;
	private final Deque<Item> queue;
	private final Supplier<S> supplier;
	private final ReentrantLock queueLock = new ReentrantLock();
	private final ReentrantLock itemLock = new ReentrantLock();
	private final ReentrantLock summaryLock = new ReentrantLock();
	private final S summary;
	private ScheduledExecutorService scheduler;


	public SlidingWindow(int windowSize, int windowTime, TimeUnit timeUnit, Supplier<S> supplier) {
		this(windowSize, (int) timeUnit.toMillis(windowTime), supplier);
	}

	public SlidingWindow(int windowSize, int windowMills, Supplier<S> supplier) {
		this.windowMills = windowMills;
		this.queue = new ArrayDeque<>(windowSize);
		this.supplier = supplier;
		this.maxDuration = windowSize * windowMills;
		this.summary = supplier.get();
	}

	public boolean startScheduler() {
		if (this.scheduler != null) {
			return false;
		}
		synchronized (this) {
			if (this.scheduler != null) {
				return false;
			}
			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1,
				new PooledThreadFactory("SlidingWindow"));
			this.scheduler = scheduler;
			scheduler.scheduleAtFixedRate(() -> {
				long currentTime = System.currentTimeMillis();
				long timestamp = (currentTime / windowMills) * windowMills;
				cleanup(timestamp);
			}, maxDuration, maxDuration, TimeUnit.NANOSECONDS);
			return true;
		}
	}

	private void cleanup(long timestamp) {
		queueLock.lock();
		try {
			for (Item first = this.queue.peekFirst(); first != null && first.timestamp < timestamp - maxDuration; first = this.queue.peekFirst()) {
				this.queue.pollFirst();
				summaryLock.lock();
				try {
					summary.minus(first.statistics);
				} finally {
					summaryLock.unlock();
				}
			}
		} finally {
			queueLock.unlock();
		}
	}

	public void emit(long currentTime, Object event) {
		long timestamp = (currentTime / windowMills) * windowMills;
		Item item = this.queue.peekLast();
		if (item == null || timestamp > item.timestamp) {
			queueLock.lock();
			try {
				item = this.queue.peekLast();
				if (item == null || timestamp > item.timestamp) {
					item = new Item(timestamp, this.supplier.get());
					while (!this.queue.offerLast(item)) {
						Item first = this.queue.pollFirst();
						if (first != null) {
							summaryLock.lock();
							try {
								summary.minus(first.statistics);
							} finally {
								summaryLock.unlock();
							}
						}
					}
					cleanup(timestamp);
				}
			} finally {
				queueLock.unlock();
			}
		}
		itemLock.lock();
		try {
			item.receive(event);
		} finally {
			itemLock.unlock();
		}
		summaryLock.lock();
		try {
			summary.receive(event);
		} finally {
			summaryLock.unlock();
		}
	}

	public S summary() {
		return summary.clone();
	}

	public List<S> get() {
		List<S> list = new ArrayList<>();
		for (Item item : this.queue) {
			list.add(item.statistics);
		}
		return list;
	}


	private class Item {
		private final long timestamp;
		private final S statistics;

		private Item(long timestamp, S statistics) {
			this.timestamp = timestamp;
			this.statistics = statistics;
		}

		public void receive(Object event) {
			this.statistics.receive(event);
		}

	}
}
