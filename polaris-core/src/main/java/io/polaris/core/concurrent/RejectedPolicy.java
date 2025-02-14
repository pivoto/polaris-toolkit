package io.polaris.core.concurrent;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Qt
 * @since Feb 14, 2025
 */
public enum RejectedPolicy {

	/** 处理程序遭到拒绝将抛出RejectedExecutionException */
	ABORT(new ThreadPoolExecutor.AbortPolicy()),
	/** 放弃当前任务 */
	DISCARD(new ThreadPoolExecutor.DiscardPolicy()),
	/** 如果执行程序尚未关闭，则位于工作队列头部的任务将被删除，然后重试执行程序（如果再次失败，则重复此过程） */
	DISCARD_OLDEST(new ThreadPoolExecutor.DiscardOldestPolicy()),
	/** 由主线程来直接执行 */
	CALLER_RUNS(new ThreadPoolExecutor.CallerRunsPolicy());

	private RejectedExecutionHandler policy;

	private RejectedPolicy(RejectedExecutionHandler handler) {
		this.policy = handler;
	}

	public RejectedExecutionHandler policy() {
		return this.policy;
	}
}
