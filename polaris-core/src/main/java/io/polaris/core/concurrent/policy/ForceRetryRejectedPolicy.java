package io.polaris.core.concurrent.policy;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import io.polaris.core.concurrent.Threads;

/**
 * @author Qt
 * @since Jun 05, 2025
 */
public class ForceRetryRejectedPolicy implements RejectedExecutionHandler {

	private final int sleepMills;

	public ForceRetryRejectedPolicy() {
		this.sleepMills = 1000;
	}

	public ForceRetryRejectedPolicy(int sleepMills) {
		this.sleepMills = sleepMills;
	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		if (executor.isShutdown()) {
			throw new RejectedExecutionException("Task " + r.toString() +
				" rejected from shutdown " +
				executor.toString());
		}
		Threads.sleep(sleepMills);
		executor.execute(r);
	}
}
