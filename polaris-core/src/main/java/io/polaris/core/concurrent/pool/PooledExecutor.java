package io.polaris.core.concurrent.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
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

public class PooledExecutor<E> implements RunnableState<E> {
	private static final ILogger log = ILoggers.of(PooledExecutor.class);
	public static final int CORE_NUM = Runtime.getRuntime().availableProcessors();
	public static final float MAXIMUM_FACTOR = 8;
	public static final int KEEP_ALIVE_TIME = 1000;
	/** 线程池核心数 */
	@Setter
	@Getter
	private int poolSize = CORE_NUM;
	/** 线程池最大数 */
	@Setter
	@Getter
	private int maximumPoolSize = CORE_NUM * 8;
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

	private volatile boolean running = false;
	private BlockingQueue<E> blockingQueue;
	private Lock awaitLock = new ReentrantLock();
	private Condition awaitCondition = awaitLock.newCondition();
	private AtomicInteger activeCount = new AtomicInteger(0);
	private ThreadPoolExecutor pool;
	private List<Runnable> consumers = new ArrayList<>();
	private RunnableStatistics statistics;
	private AtomicReference<Consumer<ErrorRecords<E>>> rejectConsumerRef = new AtomicReference();

	public static ThreadPoolExecutor newWorkerPool(int poolSize, int maximumPoolSize) {
		return newWorkerPool(poolSize, maximumPoolSize, KEEP_ALIVE_TIME);
	}

	public static ThreadPoolExecutor newWorkerPool(int poolSize) {
		return newWorkerPool(poolSize, (int) (poolSize * MAXIMUM_FACTOR), KEEP_ALIVE_TIME);
	}

	public static ThreadPoolExecutor newWorkerPool() {
		return newWorkerPool(CORE_NUM);
	}

	public static ThreadPoolExecutor newWorkerPool(int poolSize, int maximumPoolSize, long keepAliveTime) {
		ThreadPoolExecutor pool = new ThreadPoolExecutor(poolSize, maximumPoolSize, keepAliveTime,
			TimeUnit.MILLISECONDS, new SynchronousQueue<>(true), Executors.defaultThreadFactory());
		pool.allowCoreThreadTimeOut(true);
		return pool;
	}

	@Override
	public RunnableStatistics runnableStatistics() {
		return statistics;
	}

	@Override
	public boolean hasNext() {
		return running || !blockingQueue.isEmpty();
	}

	@Override
	public E next() {
		return blockingQueue.poll();
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
	public void notifyFinished() {
		awaitLock.lock();
		try {
			awaitCondition.signalAll();
		} finally {
			awaitLock.unlock();
		}
	}

	public void setRejectConsumer(Consumer<ErrorRecords<E>> rejectConsumer) {
		this.rejectConsumerRef.set(rejectConsumer);
	}

	public void addConsumer(Consumer<E> consumer) {
		addConsumer(1, consumer);
	}

	public <Resource> void addConsumer(TransactionConsumer<E, Resource> consumer) {
		addConsumer(1, consumer);
	}

	public void addConsumer(int count, Consumer<E> consumer) {
		for (int k = 0; k < count; k++) {
			Runnable delegate = RunnableDelegates.createDelegate(this, consumer,this.rejectConsumerRef);
			consumers.add(delegate);
		}
	}

	public <Resource> void addConsumer(int count, TransactionConsumer<E, Resource> consumer) {
		for (int k = 0; k < count; k++) {
			Runnable delegate = RunnableDelegates.createDelegate(this, consumer,this.rejectConsumerRef);
			consumers.add(delegate);
		}
	}

	public void start() {
		if (running) {
			throw new IllegalStateException("正在运行中");
		}
		if (consumers.isEmpty()) {
			throw new IllegalArgumentException("未提供消费者");
		}
		this.running = true;
		this.pool = newWorkerPool(poolSize, maximumPoolSize);
		if (openStatistics || errorLimit >= 0) {
			this.statistics = new RunnableStatistics(errorLimit);
		}
		this.blockingQueue = new ArrayBlockingQueue<>(queueSize, true);
		for (Runnable consumer : consumers) {
			try {
				this.pool.execute(consumer);
				continue;
			} catch (RejectedExecutionException e) {
				extend();
			}
			try {
				this.pool.execute(consumer);
				continue;
			} catch (RejectedExecutionException e) {
				break;
			}
		}
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

	public boolean isExceedErrorLimit() {
		return statistics != null && statistics.isExceedErrorLimit();
	}

	public void offer(E data) {
		if (!running) {
			throw new IllegalStateException("状态已停止");
		}
		if (isExceedErrorLimit()) {
			throw new IllegalStateException("处理失败数量超限(" + getErrorLimit() + ")");
		}
		while (true) {
			try {
				boolean rs = blockingQueue.offer(data, 1, TimeUnit.MILLISECONDS);
				if (rs) {
					break;
				}
			} catch (InterruptedException e) {
				log.trace("", e);
			}
		}
	}

	public void await() {
		while (running || activeCount.get() > 0 || !blockingQueue.isEmpty()) {
			awaitLock.lock();
			try {
				awaitCondition.await(1, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				log.trace("", e);
			} finally {
				awaitLock.unlock();
			}
		}
		if (statistics != null) {
			log.info("total: {}, success: {}, error: {}", statistics.getTotal().get(), statistics.getSuccess().get(), statistics.getError().get());
		}
	}

	public void shutdown() {
		this.running = false;
		await();
		consumers.clear();
		closePool();
	}

	private void closePool() {
		if (this.pool != null) {
			ThreadPoolExecutor workerPool = this.pool;
			this.pool = null;
			try {
				workerPool.awaitTermination(10, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
			}
			if (!workerPool.isShutdown()) {
				workerPool.shutdownNow();
				if (!workerPool.isShutdown()) {
					workerPool.shutdown();
				}
			}
			log.info("关闭线程池完成");
		}
	}

	private void extend() {
		if (pool.getMaximumPoolSize() < CORE_NUM * 100) {
			pool.setMaximumPoolSize(pool.getMaximumPoolSize() + CORE_NUM);
			pool.setCorePoolSize(pool.getCorePoolSize() + CORE_NUM);
		}
	}

}
