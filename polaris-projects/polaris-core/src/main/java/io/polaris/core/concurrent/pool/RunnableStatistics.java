package io.polaris.core.concurrent.pool;

import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;

/**
 * @author Qt
 * @since 1.8,  Apr 23, 2024
 */
public class RunnableStatistics {

	@Getter
	private final AtomicLong total = new AtomicLong(0);
	@Getter
	private final AtomicLong success = new AtomicLong(0);
	@Getter
	private final AtomicLong error = new AtomicLong(0);
	private final int errorLimit;

	public RunnableStatistics() {
		this.errorLimit = -1;
	}

	public RunnableStatistics(int errorLimit) {
		this.errorLimit = errorLimit == 0 ? 1 : errorLimit;
	}

	public boolean isExceedErrorLimit() {
		return errorLimit > 0 && error.get() >= errorLimit;
	}
}
