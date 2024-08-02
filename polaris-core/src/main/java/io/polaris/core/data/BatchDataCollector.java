package io.polaris.core.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import io.polaris.core.assertion.Arguments;
import io.polaris.core.concurrent.PooledThreadFactory;

/**
 * @author Qt
 * @since Aug 01, 2024
 */
public class BatchDataCollector<E> {

	private final AtomicLong lastTime;
	private final ArrayBlockingQueue<E> buffer;
	private final int maxStoreSize;
	private final long maxStoreNanos;
	private final Consumer<List<E>> consumer;
	private ScheduledExecutorService scheduler;

	public BatchDataCollector(int maxStoreSize, long maxStoreTime, TimeUnit timeUnit, Consumer<List<E>> consumer) {
		this(maxStoreSize, timeUnit.toNanos(maxStoreTime), consumer);
	}

	public BatchDataCollector(int maxStoreSize, long maxStoreNanos, Consumer<List<E>> consumer) {
		Arguments.isTrue(maxStoreSize > 0, "maxStoreSize must be greater than 0");
		Arguments.isTrue(maxStoreNanos > 0, "maxStoreNanos must be greater than 0");
		this.maxStoreNanos = maxStoreNanos;
		this.maxStoreSize = maxStoreSize;
		this.consumer = consumer;
		this.buffer = new ArrayBlockingQueue<>(maxStoreSize);
		this.lastTime = new AtomicLong(System.nanoTime());
	}

	public int getMaxStoreSize() {
		return maxStoreSize;
	}

	public long getMaxStoreNanos() {
		return maxStoreNanos;
	}

	public Consumer<List<E>> getConsumer() {
		return consumer;
	}

	public boolean startScheduler() {
		if (this.consumer == null) {
			return false;
		}
		if (this.scheduler != null) {
			return false;
		}
		synchronized (this) {
			if (this.scheduler != null) {
				return false;
			}
			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1,
				new PooledThreadFactory("BatchDataCollector"));
			this.scheduler = scheduler;
			scheduler.scheduleAtFixedRate(this::flush, maxStoreNanos, maxStoreNanos, TimeUnit.NANOSECONDS);
			return true;
		}
	}

	public void collect(E datum) {
		collect(Collections.singletonList(datum), consumer);
	}

	public void collect(E datum, Consumer<List<E>> consumer) {
		collect(Collections.singletonList(datum), consumer);
	}

	public void collect(Iterable<E> data) {
		collect(data, consumer);
	}

	public void collect(Iterable<E> data, Consumer<List<E>> consumer) {
		if (consumer == null) {
			throw new IllegalStateException("数据消费器不能为空");
		}
		for (E datum : data) {
			while (!buffer.offer(datum)) {
				// 队列容量已满
				flush(consumer);
			}
		}
		long now = System.nanoTime();
		boolean expired = now - lastTime.get() > maxStoreNanos;
		if (expired) {
			// 时间跨度超限
			flush(consumer);
		}
	}

	public void flush() {
		flush(consumer);
	}

	public void flush(Consumer<List<E>> consumer) {
		if (consumer == null) {
			throw new IllegalStateException("数据消费器不能为空");
		}
		int size = buffer.size();
		List<E> list = new ArrayList<>(size);
		try {
			buffer.drainTo(list);
			if (!list.isEmpty()) {
				consumer.accept(list);
			}
		} finally {
			lastTime.set(System.nanoTime());
		}
	}


}
