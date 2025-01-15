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
import io.polaris.core.concurrent.Schedules;
import io.polaris.core.io.Consoles;

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
	private final boolean withShutdownHook;
	private volatile boolean running = true;
	private volatile ScheduledExecutorService scheduler;

	public BatchDataCollector(int maxStoreSize, long maxStoreTime, TimeUnit timeUnit, Consumer<List<E>> consumer) {
		this(maxStoreSize, timeUnit.toNanos(maxStoreTime), consumer, true);
	}

	public BatchDataCollector(int maxStoreSize, long maxStoreTime, TimeUnit timeUnit, Consumer<List<E>> consumer, boolean withShutdownHook) {
		this(maxStoreSize, timeUnit.toNanos(maxStoreTime), consumer, withShutdownHook);
	}

	public BatchDataCollector(int maxStoreSize, long maxStoreNanos, Consumer<List<E>> consumer, boolean withShutdownHook) {
		Arguments.isTrue(maxStoreSize > 0, "maxStoreSize must be greater than 0");
		Arguments.isTrue(maxStoreNanos > 0, "maxStoreNanos must be greater than 0");
		this.maxStoreNanos = maxStoreNanos;
		this.maxStoreSize = maxStoreSize;
		this.consumer = consumer;
		this.withShutdownHook = withShutdownHook;
		this.buffer = new ArrayBlockingQueue<>(maxStoreSize);
		this.lastTime = new AtomicLong(System.nanoTime());
		if (withShutdownHook) {
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					this.running = false;
					this.flush();
				} catch (Exception ignored) {
				}
			}));
		}
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
			final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1,
				new PooledThreadFactory(BatchDataCollector.class.getSimpleName()));
			this.scheduler = scheduler;
			scheduler.scheduleAtFixedRate(this::tryFlush, maxStoreNanos, maxStoreNanos, TimeUnit.NANOSECONDS);
			if (withShutdownHook) {
				Runtime.getRuntime().addShutdownHook(new Thread(() -> {
					try {
						this.scheduler = null;
						Schedules.shutdown(scheduler);
					} catch (Exception ignored) {
					}
				}));
			}
			return true;
		}
	}

	public boolean stopScheduler() {
		if (this.scheduler == null) {
			return false;
		}
		synchronized (this) {
			if (this.scheduler == null) {
				return false;
			}
			ScheduledExecutorService scheduler = this.scheduler;
			this.scheduler = null;
			Schedules.shutdown(scheduler);
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
			throw new IllegalStateException("数据处理器不能为空");
		}
		for (E datum : data) {
			while (!buffer.offer(datum)) {
				// 队列容量已满，立即处理数据
				flush(consumer);
			}
		}
		if (!running) {
			// 数据收集器已停止，直接处理数据
			flush(consumer);
			return;
		}
		long now = System.nanoTime();
		boolean expired = now - lastTime.get() > maxStoreNanos;
		if (expired) {
			// 时间跨度超限，立即处理数据
			flush(consumer);
		}
	}

	public void tryFlush() {
		tryFlush(consumer);
	}

	public boolean tryFlush(Consumer<List<E>> consumer) {
		if (buffer.size() >= maxStoreSize) {
			// 队列容量已满，立即处理数据
			flush(consumer);
			return true;
		}
		long now = System.nanoTime();
		boolean expired = now - lastTime.get() > maxStoreNanos;
		if (expired) {
			// 时间跨度超限，立即处理数据
			flush(consumer);
			return true;
		}
		return false;
	}

	public void flush() {
		flush(consumer);
	}

	public void flush(Consumer<List<E>> consumer) {
		if (consumer == null) {
			throw new IllegalStateException("数据消费器不能为空");
		}
		lastTime.set(System.nanoTime());
		int size = buffer.size();
		List<E> list = new ArrayList<>(size);
		try {
			buffer.drainTo(list);
			if (!list.isEmpty()) {
				consumer.accept(list);
			}
		} finally {
		}
	}


}
