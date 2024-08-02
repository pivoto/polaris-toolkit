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

/**
 * @author Qt
 * @since Aug 01, 2024
 */
public class MultiBatchDataCollector<K, E> {

	private final Map<K, BatchDataCollector<E>> collectors = new ConcurrentHashMap<>();
	private final int maxStoreSize;
	private final long maxStoreNanos;
	private ScheduledExecutorService scheduler;


	public MultiBatchDataCollector(int maxStoreSize, long maxStoreTime, TimeUnit timeUnit) {
		this(maxStoreSize, timeUnit.toNanos(maxStoreTime));
	}

	public MultiBatchDataCollector(int maxStoreSize, long maxStoreNanos) {
		Arguments.isTrue(maxStoreSize > 0, "maxStoreSize must be greater than 0");
		Arguments.isTrue(maxStoreNanos > 0, "maxStoreNanos must be greater than 0");
		this.maxStoreSize = maxStoreSize;
		this.maxStoreNanos = maxStoreNanos;
	}

	public long getMaxStoreNanos() {
		return maxStoreNanos;
	}

	public int getMaxStoreSize() {
		return maxStoreSize;
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
				new PooledThreadFactory("MultiBatchDataCollector"));
			this.scheduler = scheduler;
			scheduler.scheduleAtFixedRate(() -> {
				for (Map.Entry<K, BatchDataCollector<E>> entry : collectors.entrySet()) {
					BatchDataCollector<E> collector = entry.getValue();
					if (collector.getConsumer() != null) {
						collector.flush();
					}
				}
			}, maxStoreNanos, maxStoreNanos, TimeUnit.NANOSECONDS);
			return true;
		}
	}

	private BatchDataCollector<E> getCollector(K key, Consumer<List<E>> consumer) {
		return collectors.computeIfAbsent(key, k -> new BatchDataCollector<>(maxStoreSize, maxStoreNanos, consumer));
	}

	private BatchDataCollector<E> getCollector(K key) {
		return Objects.requireNonNull(collectors.get(key));
	}

	public boolean register(K key, Consumer<List<E>> consumer) {
		return register(key, new BatchDataCollector<>(maxStoreSize, maxStoreNanos, consumer));
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
