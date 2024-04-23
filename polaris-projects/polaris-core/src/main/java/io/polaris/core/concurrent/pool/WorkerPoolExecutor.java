package io.polaris.core.concurrent.pool;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;

/**
 * @author Qt
 * @since 1.8,  Apr 23, 2024
 */
public class WorkerPoolExecutor extends ThreadPoolExecutor {
	private static final ILogger log = ILoggers.of(WorkerPoolExecutor.class);
	public static final int CORE_NUM = Runtime.getRuntime().availableProcessors();
	public static final float MAXIMUM_FACTOR = 8;
	public static final float EXTEND_MAXIMUM_FACTOR = 100;
	public static final int KEEP_ALIVE_TIME = 1000;
	public static final int MAX_TRY_TIME = Integer.MAX_VALUE;
	private int maxTryTime = MAX_TRY_TIME;
	private Lock lock = new ReentrantLock();
	private Condition threadPoolCond = lock.newCondition();

	public WorkerPoolExecutor(int poolSize) {
		this(poolSize, (int) (poolSize * MAXIMUM_FACTOR), KEEP_ALIVE_TIME);
	}

	public WorkerPoolExecutor(int poolSize, int maximumPoolSize) {
		this(poolSize, maximumPoolSize, KEEP_ALIVE_TIME);
	}

	public WorkerPoolExecutor(int poolSize, int maximumPoolSize, long keepAliveTime) {
		super(poolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
			new SynchronousQueue<>(true), Executors.defaultThreadFactory());
		this.allowCoreThreadTimeOut(true);
	}

	public WorkerPoolExecutor() {
		this(CORE_NUM);
	}

	public void shutdownQuietly() {
		try {
			this.awaitTermination(5000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
		}
		try {
			if (!this.isShutdown()) {
				this.shutdownNow();
				if (!this.isShutdown()) {
					this.shutdown();
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}

	public void setMaxTryTime(int maxTryTime) {
		this.maxTryTime = maxTryTime;
	}

	/**
	 * 扩张线程池大小(增加大小为cpu核心数)
	 */
	public void extend() {
		if (this.getMaximumPoolSize() < CORE_NUM * EXTEND_MAXIMUM_FACTOR) {
			this.setMaximumPoolSize(this.getMaximumPoolSize() + CORE_NUM);
			this.setCorePoolSize(this.getCorePoolSize() + CORE_NUM);
		}
	}

	public boolean executeAwait(Runnable task) {
		Runnable delegate = () -> {
			try {
				task.run();
			} finally {
				notifyForThreadPoolAccess();
			}
		};
		for (int i = 0; i <= maxTryTime; i++) {
			try {
				super.execute(delegate);
				return true;
			} catch (RejectedExecutionException e) {
				waitForThreadPoolAccess();
			}
		}
		return false;
	}

	void waitForThreadPoolAccess() {
		lock.lock();
		try {
			threadPoolCond.await(100, TimeUnit.MICROSECONDS);
		} catch (InterruptedException ex) {
		} finally {
			lock.unlock();
		}
	}

	void notifyForThreadPoolAccess() {
		lock.lock();
		try {
			threadPoolCond.signalAll();
		} finally {
			lock.unlock();
		}
	}

}
