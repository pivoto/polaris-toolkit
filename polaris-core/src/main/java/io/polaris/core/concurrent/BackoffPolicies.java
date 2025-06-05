package io.polaris.core.concurrent;

import io.polaris.core.concurrent.policy.BackoffPolicy;
import io.polaris.core.concurrent.policy.ExponentialBackoffPolicy;
import io.polaris.core.concurrent.policy.FixedBackoffPolicy;
import io.polaris.core.concurrent.policy.NoBackoffPolicy;

/**
 * @author Qt
 * @since Jun 05, 2025
 */
public class BackoffPolicies {

	private static final NoBackoffPolicy NO_BACKOFF_POLICY = new NoBackoffPolicy();

	public static BackoffPolicy noBackoff() {
		return NO_BACKOFF_POLICY;
	}

	public static BackoffPolicy fixedBackoff(long interval) {
		return new FixedBackoffPolicy(interval);
	}

	public static BackoffPolicy fixedBackoff(long maxAttempts, long interval) {
		return new FixedBackoffPolicy(maxAttempts, interval);
	}


	public static BackoffPolicy exponentialBackoff() {
		return new ExponentialBackoffPolicy();
	}

	public static BackoffPolicy exponentialBackoff(long initialInterval) {
		return new ExponentialBackoffPolicy(initialInterval);
	}

	public static BackoffPolicy exponentialBackoff(long initialInterval, double multiplier) {
		return new ExponentialBackoffPolicy(initialInterval, multiplier);
	}

	public static BackoffPolicy exponentialBackoff(long initialInterval, double multiplier, long maxInterval) {
		return new ExponentialBackoffPolicy(initialInterval, multiplier, maxInterval);
	}

	public static BackoffPolicy exponentialBackoff(long maxAttempts, long initialInterval) {
		return new ExponentialBackoffPolicy(maxAttempts, initialInterval);
	}


	public static BackoffPolicy exponentialBackoff(long maxAttempts, long initialInterval, double multiplier) {
		return new ExponentialBackoffPolicy(maxAttempts, initialInterval, multiplier);
	}

	public static BackoffPolicy exponentialBackoff(long maxAttempts, long initialInterval, double multiplier, long maxInterval) {
		return new ExponentialBackoffPolicy(maxAttempts, initialInterval, multiplier, maxInterval);
	}
}
