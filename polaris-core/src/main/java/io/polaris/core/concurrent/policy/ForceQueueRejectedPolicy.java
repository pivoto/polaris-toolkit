package io.polaris.core.concurrent.policy;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import io.polaris.core.concurrent.Threads;

/**
 * @author Qt
 * @since Jun 05, 2025
 */
public class ForceQueueRejectedPolicy implements RejectedExecutionHandler {

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		try {
			executor.getQueue().put(r);
		} catch (InterruptedException e) {
			throw new RejectedExecutionException(e);
		}
	}
}
