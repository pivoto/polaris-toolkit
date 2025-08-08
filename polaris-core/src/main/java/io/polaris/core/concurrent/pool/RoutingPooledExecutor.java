package io.polaris.core.concurrent.pool;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qt
 * @since Apr 23, 2024
 */

public class RoutingPooledExecutor<E> {
	private static final Logger log = Loggers.of(RoutingPooledExecutor.class);
	/** 数据队列大小 */
	@Setter
	@Getter
	private int queueSize = 1000;
	/** 错误数限制 */
	@Setter
	@Getter
	private int errorLimit = -1;
	@Setter
	@Getter
	private boolean openStatistics = false;
	/** 线程路由 */
	@Setter
	@Getter
	private Function<E, Integer> router = Objects::hashCode;
	private RunnableStatistics statistics;
	private final AtomicReference<Consumer<ErrorRecords<E>>> rejectConsumerRef = new AtomicReference<>();
	private final List<Executor<E>> executors = new CopyOnWriteArrayList<>();

	public void setRejectConsumer(Consumer<ErrorRecords<E>> rejectConsumer) {
		this.rejectConsumerRef.set(rejectConsumer);
	}

	public void addConsumer(Consumer<E> consumer) {
		addConsumer(1, consumer);
	}

	public <R> void addConsumer(TransactionConsumer<E, R> consumer) {
		addConsumer(1, consumer);
	}

	public void addConsumer(int count, Consumer<E> consumer) {
		if (isRunning()) {
			throw new IllegalStateException("正在运行中");
		}
		for (int k = 0; k < count; k++) {
			Executor<E> executor = new Executor<>();
			Runnable runnable = RunnableDelegates.createDelegate(executor, consumer, this.rejectConsumerRef);
			executor.setRunnable(runnable);
			executors.add(executor);
		}
	}

	public <R> void addConsumer(int count, TransactionConsumer<E, R> consumer) {
		if (isRunning()) {
			throw new IllegalStateException("正在运行中");
		}
		for (int k = 0; k < count; k++) {
			Executor<E> executor = new Executor<>();
			Runnable runnable = RunnableDelegates.createDelegate(executor, consumer, this.rejectConsumerRef);
			executor.setRunnable(runnable);
			executors.add(executor);
		}
	}

	public void start() {
		if (isRunning()) {
			throw new IllegalStateException("正在运行中");
		}
		if (executors.isEmpty()) {
			throw new IllegalArgumentException("未提供消费者");
		}
		if (openStatistics || errorLimit >= 0) {
			this.statistics = new RunnableStatistics(errorLimit);
		}
		int count = executors.size();
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				Executor<E> executor = executors.get(i);
				executor.start();
			}
		}
	}

	public boolean isRunning() {
		int count = executors.size();
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				Executor<E> executor = executors.get(i);
				if (executor.running) {
					return true;
				}
			}
		}
		return false;
	}

	public void offer(Iterable<E> datas) {
		for (E data : datas) {
			offer(data);
		}
	}

	@SafeVarargs
	public final void offer(E... datas) {
		if (datas.length == 0) {
			return;
		}
		if (datas.length == 1) {
			offer(router.apply(datas[0]), datas[0]);
			return;
		}
		for (E data : datas) {
			offer(router.apply(data), data);
		}
	}

	public void offer(E data) {
		offer(router.apply(data), data);
	}

	public void offer(int index, Iterable<E> datas) {
		Executor<E> executor = getExecutor(index);
		for (E data : datas) {
			executor.offer(data);
		}
	}

	@SafeVarargs
	public final void offer(int index, E... datas) {
		Executor<E> executor = getExecutor(index);
		for (E data : datas) {
			executor.offer(data);
		}
	}

	public void offer(int index, E data) {
		Executor<E> executor = getExecutor(index);
		executor.offer(data);
	}

	private Executor<E> getExecutor(int index) {
		index = Math.abs(index) % executors.size();
		return executors.get(index);
	}


	public void await() {
		int count = executors.size();
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				Executor<E> executor = executors.get(i);
				executor.await();
			}
		}
		if (statistics != null) {
			log.info("total: {}, success: {}, error: {}", statistics.getTotal().get(), statistics.getSuccess().get(), statistics.getError().get());
		}
	}

	public void shutdown() {
		int count = executors.size();
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				Executor<E> executor = executors.get(i);
				executor.setRunning(false);
			}
		}
		await();
		executors.clear();
	}

	public boolean isExceedErrorLimit() {
		return statistics != null && statistics.isExceedErrorLimit();
	}

	public int getExecutorCount() {
		return executors.size();
	}

	public RunnableStatistics runnableStatistics() {
		return statistics;
	}


	private class Executor<E> implements RunnableState<E> {
		private volatile boolean running = false;
		private BlockingQueue<E> blockingQueue;
		private final Lock awaitLock = new ReentrantLock();
		private final AtomicInteger activeCount = new AtomicInteger();
		private final Condition awaitCondition = awaitLock.newCondition();
		private Runnable runnable;

		private Executor() {
		}

		@Override
		public void notifyFinished() {
			awaitLock.lock();
			try {
				awaitCondition.signalAll();
			} finally {
				awaitLock.unlock();
			}
		}

		public void setRunnable(Runnable runnable) {
			this.runnable = runnable;
		}

		public void setRunning(boolean running) {
			this.running = running;
		}

		@Override
		public RunnableStatistics runnableStatistics() {
			return statistics;
		}

		@Override
		public void incrementActiveCount() {
			activeCount.incrementAndGet();
		}

		@Override
		public void decrementActiveCount() {
			activeCount.decrementAndGet();
		}

		@Override
		public boolean hasNext() {
			return running || !blockingQueue.isEmpty();
		}

		@Override
		public E next() {
			return blockingQueue.poll();
		}


		private void start() {
			if (running) {
				throw new IllegalStateException("正在运行中");
			}
			this.blockingQueue = new ArrayBlockingQueue<>(queueSize, true);
			this.running = true;
			new Thread(runnable).start();
		}

		private void await() {
			while (running || activeCount.get() > 0 /*|| !blockingQueue.isEmpty()*/) {
				awaitLock.lock();
				try {
					awaitCondition.await(100, TimeUnit.NANOSECONDS);
				} catch (InterruptedException e) {
					log.trace("", e);
				} finally {
					awaitLock.unlock();
				}
			}
		}

		private void offer(E data) {
			if (!running) {
				throw new IllegalStateException("状态已停止");
			}
			if (isExceedErrorLimit()) {
				throw new IllegalStateException("处理失败数量超限(" + getErrorLimit() + ")");
			}
			while (true) {
				try {
					boolean rs = blockingQueue.offer(data, 10, TimeUnit.NANOSECONDS);
					if (rs) {
						break;
					}
				} catch (InterruptedException e) {
				}
			}
		}


	}


}
