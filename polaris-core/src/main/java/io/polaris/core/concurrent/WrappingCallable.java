package io.polaris.core.concurrent;

import java.util.concurrent.Callable;

/**
 * @author Qt
 * @since Jan 24, 2025
 */
public interface WrappingCallable<V> extends WrappingTask, Callable<V> {
}
