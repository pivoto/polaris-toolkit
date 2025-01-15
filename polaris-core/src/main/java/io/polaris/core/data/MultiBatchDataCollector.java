package io.polaris.core.data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.polaris.core.assertion.Arguments;
import io.polaris.core.concurrent.PooledThreadFactory;
import io.polaris.core.concurrent.Schedules;

/**
 * @author Qt
 * @since Aug 01, 2024
 */
public class MultiBatchDataCollector<K, E> {

	private final Map<K, BatchDataCollector<E>> collectors = new ConcurrentHashMap<>();
	private final int maxStoreSize;
	private final long maxStoreNanos;
	private final boolean withShutdownHook;
	private volatile ScheduledExecutorService scheduler;


	public MultiBatchDataCollector(int maxStoreSize, long maxStoreTime, TimeUnit timeUnit) {
		this(maxStoreSize, timeUnit.toNanos(maxStoreTime), true);
	}

	public MultiBatchDataCollector(int maxStoreSize, long maxStoreTime, TimeUnit timeUnit, boolean withShutdownHook) {
		this(maxStoreSize, timeUnit.toNanos(maxStoreTime), withShutdownHook);
	}

	public MultiBatchDataCollector(int maxStoreSize, long maxStoreNanos, boolean withShutdownHook) {
		Arguments.isTrue(maxStoreSize > 0, "maxStoreSize must be greater than 0");
		Arguments.isTrue(maxStoreNanos > 0, "maxStoreNanos must be greater than 0");
		this.maxStoreSize = maxStoreSize;
		this.maxStoreNanos = maxStoreNanos;
		this.withShutdownHook = withShutdownHook;
	}

	public long getMaxStoreNanos() {
		return maxStoreNanos;
	}

	public int getMaxStoreSize() {
		return maxStoreSize;
	}

	public void startSchedulerSeverally() {
		collectors.forEach((k, v) -> v.startScheduler());
	}

	public void stopSchedulerSeverally() {
		collectors.forEach((k, v) -> v.stopScheduler());
	}

	public boolean startScheduler() {
		if (this.scheduler != null) {
			return false;
		}
		synchronized (this) {
			if (this.scheduler != null) {
				return false;
			}
			int poolSize = collectors.isEmpty() ? Runtime.getRuntime().availableProcessors()
				: Integer.max(collectors.size(), 4);
			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(poolSize,
				new PooledThreadFactory(MultiBatchDataCollector.class.getSimpleName()));
			this.scheduler = scheduler;
			scheduler.scheduleAtFixedRate(() -> {
				for (Map.Entry<K, BatchDataCollector<E>> entry : collectors.entrySet()) {
					BatchDataCollector<E> collector = entry.getValue();
					if (collector.getConsumer() != null) {
						collector.flush();
					}
				}
			}, maxStoreNanos, maxStoreNanos, TimeUnit.NANOSECONDS);

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


	private BatchDataCollector<E> getCollector(K key, Consumer<List<E>> consumer) {
		return collectors.computeIfAbsent(key, k -> new BatchDataCollector<>(maxStoreSize, maxStoreNanos, consumer, withShutdownHook));
	}

	private BatchDataCollector<E> getCollector(K key) {
		return Objects.requireNonNull(collectors.get(key));
	}

	public boolean register(K key, Consumer<List<E>> consumer) {
		return register(key, new BatchDataCollector<>(maxStoreSize, maxStoreNanos, consumer, withShutdownHook));
	}

	public boolean register(K key, BatchDataCollector<E> collector) {
		BatchDataCollector<E> old = collectors.putIfAbsent(key, collector);
		return old == null;
	}

	public void collect(K key, E datum) {
		collect(key, Collections.singletonList(datum));
	}

	public void collect(K key, Iterable<E> data) {
		getCollector(key).collect(data);
	}

	public void flush(K key) {
		getCollector(key).flush();
	}

	public void collect(K key, E datum, Consumer<List<E>> consumer) {
		collect(key, Collections.singletonList(datum), consumer);
	}

	public void collect(K key, Iterable<E> data, Consumer<List<E>> consumer) {
		getCollector(key, consumer).collect(data, consumer);
	}


	public void flush(K key, Consumer<List<E>> consumer) {
		getCollector(key, consumer).flush(consumer);
	}
}
