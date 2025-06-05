package io.polaris.core.concurrent.policy;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qt
 * @since Jun 05, 2025
 */
public class ExponentialBackoffPolicy implements BackoffPolicy {
	public static final double DEFAULT_MULTIPLIER = 1.5;
	public static final long DEFAULT_INITIAL_INTERVAL = 2000L;
	public static final long DEFAULT_MAX_INTERVAL = Long.MAX_VALUE;
	public static final long UNLIMITED_ATTEMPTS = Long.MAX_VALUE;


	@Setter
	@Getter
	private long maxAttempts = UNLIMITED_ATTEMPTS;
	@Setter
	@Getter
	private long initialInterval = DEFAULT_INITIAL_INTERVAL;
	@Setter
	@Getter
	private double multiplier = DEFAULT_MULTIPLIER;
	@Setter
	@Getter
	private long maxInterval = DEFAULT_MAX_INTERVAL;

	private long interval = 0;
	private long attempts = 0;

	public ExponentialBackoffPolicy() {
	}

	public ExponentialBackoffPolicy(long initialInterval) {
		this.initialInterval = initialInterval;
	}

	public ExponentialBackoffPolicy(long initialInterval, double multiplier) {
		this.initialInterval = initialInterval;
		this.multiplier = multiplier;
	}

	public ExponentialBackoffPolicy(long initialInterval, double multiplier, long maxInterval) {
		this.initialInterval = initialInterval;
		this.multiplier = multiplier;
		this.maxInterval = maxInterval;
	}

	public ExponentialBackoffPolicy(long maxAttempts, long initialInterval) {
		this.maxAttempts = maxAttempts;
		this.initialInterval = initialInterval;
	}

	public ExponentialBackoffPolicy(long maxAttempts, long initialInterval, double multiplier) {
		this.maxAttempts = maxAttempts;
		this.initialInterval = initialInterval;
		this.multiplier = multiplier;
	}

	public ExponentialBackoffPolicy(long maxAttempts, long initialInterval, double multiplier, long maxInterval) {
		this.maxAttempts = maxAttempts;
		this.initialInterval = initialInterval;
		this.multiplier = multiplier;
		this.maxInterval = maxInterval;
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
		if (interval >= maxInterval) {
			return maxInterval;
		}
		if (interval <= 0) {
			interval = Long.min(initialInterval, maxInterval);
		} else {
			interval = (long) ((double) interval * multiplier);
		}
		return interval;
	}

	public long attempts() {
		return attempts;
	}
}
