package io.polaris.core.concurrent.policy;

/**
 * @author Qt
 * @since Jun 05, 2025
 */
public class NoBackoffPolicy implements BackoffPolicy {

	public NoBackoffPolicy() {
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public long next() {
		throw new IllegalStateException("No more attempts available");
	}

}
