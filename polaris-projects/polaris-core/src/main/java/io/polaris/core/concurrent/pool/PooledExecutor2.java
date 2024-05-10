package io.polaris.core.concurrent.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qt
 * @since  Apr 23, 2024
 */

public class PooledExecutor2<E> {
	private static final ILogger log = ILoggers.of(PooledExecutor2.class);
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
	private RunnableStatistics statistics;
	private AtomicReference<Consumer<ErrorRecords<E>>> rejectConsumerRef = new AtomicReference();

	private List<Executor<E>> executors = new ArrayList<>();

	public void setRejectConsumer(Consumer<ErrorRecords<E>> rejectConsumer) {
		this.rejectConsumerRef.set(rejectConsumer);
	}

	public void addConsumer(int count, Consumer<E> consumer) {
		for (int k = 0; k < count; k++) {
			Executor<E> executor = new Executor<>();
			Runnable runnable = RunnableDelegates.createDelegate(executor, consumer,this.rejectConsumerRef);
			executor.setRunnable(runnable);
			executors.add(executor);
		}
	}

	public <Resource> void addConsumer(int count, TransactionConsumer<E, Resource> consumer) {
		for (int k = 0; k < count; k++) {
			Executor<E> executor = new Executor<>();
			Runnable runnable = RunnableDelegates.createDelegate(executor, consumer,this.rejectConsumerRef);
			executor.setRunnable(runnable);
			executors.add(executor);
		}
	}

	public void start() {
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

	public void offer(int i, Iterable<E> datas) {
		Executor<E> executor = executors.get(i);
		for (E data : datas) {
			executor.offer(data);
		}
	}

	public void offer(int i, E... datas) {
		Executor<E> executor = executors.get(i);
		for (E data : datas) {
			executor.offer(data);
		}
	}

	public void offer(int i, E data) {
		Executor<E> executor = executors.get(i);
		executor.offer(data);
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


	class Executor<E> implements RunnableState<E> {
		private volatile boolean running = false;
		private BlockingQueue<E> blockingQueue;
		private Lock awaitLock = new ReentrantLock();
		private AtomicInteger activeCount = new AtomicInteger();
		private Condition awaitCondition = awaitLock.newCondition();
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

