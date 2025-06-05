package io.polaris.core.concurrent.policy;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Qt
 * @since Jun 05, 2025
 */
public class CallerNewsRejectedPolicy implements RejectedExecutionHandler {

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		Thread thread = executor.getThreadFactory().newThread(r);
		thread.start();
	}
}
