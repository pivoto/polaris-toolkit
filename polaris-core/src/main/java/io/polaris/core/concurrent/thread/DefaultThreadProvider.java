package io.polaris.core.concurrent.thread;

import java.util.function.Supplier;

import io.polaris.core.concurrent.ThreadLocalSpec;
import io.polaris.core.spi.SpiDefault;

/**
 * @author Qt
 * @since May 17, 2026
 */
@SpiDefault(Integer.MAX_VALUE)
public class DefaultThreadProvider implements ThreadProvider {
	@Override
	public Thread newThread(Runnable target, String name) {
		return new Thread(target, name);
	}

	@Override
	public Thread newThread(ThreadGroup threadGroup, Runnable target, String name) {
		return new Thread(threadGroup, target, name);
	}

	@Override
	public <T> ThreadLocalSpec<T> newThreadLocal() {
		return new JdkThreadLocal<>();
	}

	@Override
	public <T> ThreadLocalSpec<T> newThreadLocal(Supplier<? extends T> supplier) {
		return JdkThreadLocal.ofInitial(supplier);
	}

	@Override
	public <T> ThreadLocalSpec<T> newThreadLocal(String name) {
		return new JdkThreadLocal<>(name);
	}

	@Override
	public <T> ThreadLocalSpec<T> newThreadLocal(String name, Supplier<? extends T> supplier) {
		return JdkThreadLocal.ofInitial(name, supplier);
	}

}
