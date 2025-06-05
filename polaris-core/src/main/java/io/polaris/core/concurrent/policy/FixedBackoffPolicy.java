package io.polaris.core.concurrent.policy;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qt
 * @since Jun 05, 2025
 */
public class FixedBackoffPolicy implements BackoffPolicy {
	public static final long DEFAULT_INTERVAL = 5000;
	public static final long UNLIMITED_ATTEMPTS = Long.MAX_VALUE;

	@Setter
	@Getter
	private long maxAttempts = UNLIMITED_ATTEMPTS;
	@Setter
	@Getter
	private long interval = DEFAULT_INTERVAL;
	private long attempts = 0;

	public FixedBackoffPolicy() {
	}

	public FixedBackoffPolicy(long interval) {
		this.interval = interval;
	}

	public FixedBackoffPolicy(long maxAttempts, long interval) {
		this.maxAttempts = maxAttempts;
		this.interval = interval;
	}

	@Override
	public boolean hasNext() {
		return attempts < maxAttempts;
	}

	@Override
	public long next() {
		if (!hasNext()) {
			throw new IllegalStateException("No more attempts available");
		}
		attempts++;
		return interval;
	}

	public long attempts() {
		return attempts;
	}
}
