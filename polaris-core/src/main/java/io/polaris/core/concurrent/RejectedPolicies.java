package io.polaris.core.concurrent;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import io.polaris.core.concurrent.policy.CallerNewsRejectedPolicy;
import io.polaris.core.concurrent.policy.ForceQueueRejectedPolicy;
import io.polaris.core.concurrent.policy.ForceRetryRejectedPolicy;

/**
 * @author Qt
 * @since Feb 14, 2025
 */
public enum RejectedPolicies {

	/** 处理程序遭到拒绝将抛出RejectedExecutionException */
	ABORT(new ThreadPoolExecutor.AbortPolicy()),
	/** 放弃当前任务 */
	DISCARD(new ThreadPoolExecutor.DiscardPolicy()),
	/** 如果执行程序尚未关闭，则位于工作队列头部的任务将被删除，然后重试执行程序（如果再次失败，则重复此过程） */
	DISCARD_OLDEST(new ThreadPoolExecutor.DiscardOldestPolicy()),
	/** 由主线程来直接执行 */
	CALLER_RUNS(new ThreadPoolExecutor.CallerRunsPolicy()),
	/** 创建新线程直接执行 */
	CALLER_NEWS(new CallerNewsRejectedPolicy()),
	/** 当前线程池中强行重试 */
	FORCE_RETRY(new ForceRetryRejectedPolicy()),
	/** 强行加入当前线程池队列 */
	FORCE_QUEUE(new ForceQueueRejectedPolicy()),

	;

	private final RejectedExecutionHandler policy;

	private RejectedPolicies(RejectedExecutionHandler handler) {
		this.policy = handler;
	}

	public RejectedExecutionHandler policy() {
		return this.policy;
	}



}
