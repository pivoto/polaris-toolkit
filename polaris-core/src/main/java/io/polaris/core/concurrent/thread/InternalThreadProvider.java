package io.polaris.core.concurrent.thread;

import java.util.function.Supplier;

import io.polaris.core.concurrent.ThreadLocalSpec;
import io.polaris.core.spi.SpiDefault;

/**
 * @author Qt
 * @since May 17, 2026
 */
@SpiDefault
public class InternalThreadProvider implements ThreadProvider {
	@Override
	public Thread newThread(Runnable target, String name) {
		return new InternalThread(target, name);
	}

	@Override
	public Thread newThread(ThreadGroup threadGroup, Runnable target, String name) {
		return new InternalThread(threadGroup, target, name);
	}

	@Override
	public <T> ThreadLocalSpec<T> newThreadLocal() {
		return new InternalThreadLocal<>();
	}

	@Override
	public <T> ThreadLocalSpec<T> newThreadLocal(Supplier<? extends T> supplier) {
		return InternalThreadLocal.ofInitial(supplier);
	}

	@Override
	public <T> ThreadLocalSpec<T> newThreadLocal(String name) {
		return new InternalThreadLocal<>(name);
	}

	@Override
	public <T> ThreadLocalSpec<T> newThreadLocal(String name, Supplier<? extends T> supplier) {
		return InternalThreadLocal.ofInitial(name, supplier);
	}

}
