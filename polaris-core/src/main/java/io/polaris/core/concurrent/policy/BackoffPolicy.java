package io.polaris.core.concurrent.policy;

/**
 * @author Qt
 * @since Jun 05, 2025
 */
public interface BackoffPolicy {

	boolean hasNext();

	long next();

}
