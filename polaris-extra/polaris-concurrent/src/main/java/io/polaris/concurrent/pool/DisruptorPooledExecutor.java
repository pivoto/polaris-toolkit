package io.polaris.concurrent.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import io.polaris.core.concurrent.pool.ConsumerDelegates;
import io.polaris.core.concurrent.pool.ResourceableConsumer;
import io.polaris.core.concurrent.pool.RunnableStatistics;
import io.polaris.core.concurrent.pool.RunnableStatisticsHolder;
import io.polaris.core.concurrent.pool.TransactionConsumer;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import lombok.Getter;
import lombok.Setter;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * @author Qt
 * @since  Apr 23, 2024
 */
public class DisruptorPooledExecutor<E> implements RunnableStatisticsHolder {
	private static final Logger log = Loggers.of(DisruptorPooledExecutor.class);
	/** 错误数限制 */
	@Setter
	@Getter
	private int errorLimit = -1;
	@Setter
	@Getter
	private boolean openStatistics = false;
	private volatile boolean running = false;
	private RunnableStatistics statistics;
	private List<Consumer<E>> consumers = new ArrayList<>();

	@Setter
	@Getter
	private String name;
	private int ringBufferSize = 1 << 12;
	private Disruptor<PooledEvent> disruptor;

	@Override
	public RunnableStatistics runnableStatistics() {
		return statistics;
	}

	public void setRingBufferSize(int ringBufferSize) {
		if (ringBufferSize > 0) {
			int size = 1 << 4;
			for (; size < ringBufferSize; size <<= 1) {
			}
			this.ringBufferSize = size;
		}
	}

	private void addConsumerInner(Consumer<E> consumer) {
		consumers.add(consumer);
	}

	public void addConsumer(Consumer<E> consumer) {
		addConsumer(1, consumer);
	}

	public <Resource> void addConsumer(TransactionConsumer<E, Resource> consumer) {
		addConsumer(1, consumer);
	}

	public void addConsumer(int count, Consumer<E> consumer) {
		for (int k = 0; k < count; k++) {
			Consumer<E> delegate = ConsumerDelegates.createDelegate(this, consumer);
			addConsumerInner(delegate);
		}
	}

	public <Resource> void addConsumer(int count, TransactionConsumer<E, Resource> consumer) {
		for (int k = 0; k < count; k++) {
			Consumer<E> delegate = ConsumerDelegates.createDelegate(this, consumer);
			addConsumerInner(delegate);
		}
	}

	public boolean isExceedErrorLimit() {
		return statistics != null && statistics.isExceedErrorLimit();
	}

	public void start() {
		if (running) {
			throw new IllegalStateException("正在运行中");
		}
		this.running = true;
		if (openStatistics || errorLimit >= 0) {
			this.statistics = new RunnableStatistics(errorLimit);
		}

		int size = consumers.size();
		PooledEventHandler[] handles = new PooledEventHandler[size];
		for (int i = 0; i < size; i++) {
			Consumer<E> consumer = consumers.get(i);
			if (consumer instanceof ResourceableConsumer) {
				((ResourceableConsumer<E>) consumer).open();
			}
			handles[i] = new PooledEventHandler(consumer);
		}
		ThreadFactory threadFactory = new PooledThreadFactory(name);
		final Disruptor<PooledEvent> disruptor = new Disruptor<PooledEvent>(new PooledEventFactory(), ringBufferSize, threadFactory, ProducerType.SINGLE, new SleepingWaitStrategy());
		disruptor.handleEventsWithWorkerPool(handles);
		disruptor.start();
		this.disruptor = disruptor;
	}

	public void offer(Iterable<E> datas) {
		for (E data : datas) {
			offer(data);
		}
	}

	public void offer(E... datas) {
		for (E data : datas) {
			offer(data);
		}
	}

	public void offer(E data) {
		if (!running) {
			throw new IllegalStateException("状态已停止");
		}
		if (isExceedErrorLimit()) {
			throw new IllegalStateException("处理失败数量超限(" + getErrorLimit() + ")");
		}
		disruptor.publishEvent(this::translateTo, data);
	}


	public void shutdown() {
		try {
			this.running = false;
			log.info("shutdown...");
			disruptor.shutdown();
			int size = consumers.size();
			for (int i = 0; i < size; i++) {
				Consumer<E> consumer = consumers.get(i);
				if (consumer instanceof ResourceableConsumer) {
					((ResourceableConsumer<E>) consumer).close();
				}
			}
			log.info("shutdown!");
		} catch (Exception e) {
			log.error("", e);
		}
	}

	void translateTo(PooledEvent event, long sequence, E task) {
		////event.setSequence(sequence);
		event.setData(task);
	}

	@Getter
	@Setter
	static class PooledEvent<E> {
		//private long sequence;
		private E data;
	}

	static class PooledEventHandler<E> implements WorkHandler<PooledEvent<E>> {

		private Consumer<E> consumer;

		public PooledEventHandler(Consumer<E> consumer) {
			this.consumer = consumer;
		}

		@Override
		public void onEvent(PooledEvent<E> event) throws Exception {
			consumer.accept(event.data);
		}
	}


	static class PooledEventFactory<E> implements EventFactory<PooledEvent<E>> {

		@Override
		public PooledEvent<E> newInstance() {
			return new PooledEvent<E>();
		}
	}

	static class PooledThreadFactory implements ThreadFactory {
		private static AtomicLong pool = new AtomicLong(0);
		private AtomicLong seq = new AtomicLong(0);
		private long poolId = 0;
		private String name = "disruptor-pooled";

		public PooledThreadFactory(String name) {
			this.poolId = pool.incrementAndGet();
			if (name != null && name.length() > 0) {
				this.name = name;
			}
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setName(name + (poolId > 0 ? "-" + poolId + "-" : "-") + seq.incrementAndGet());
			return t;
		}
	}
}
