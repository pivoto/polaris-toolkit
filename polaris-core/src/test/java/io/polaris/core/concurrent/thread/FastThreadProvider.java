package io.polaris.core.concurrent.thread;

import java.util.function.Supplier;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import io.polaris.core.concurrent.ThreadLocalSpec;
import io.polaris.core.spi.SpiName;
import io.polaris.core.spi.SpiOrder;

/**
 * @author Qt
 * @since May 17, 2026
 */
@SpiOrder
@SpiName("netty")
public class FastThreadProvider implements ThreadProvider {
	@Override
	public Thread newThread(Runnable target, String name) {
		return new FastThreadLocalThread(target, name);
	}

	@Override
	public Thread newThread(ThreadGroup threadGroup, Runnable target, String name) {
		return new FastThreadLocalThread(threadGroup, target, name);
	}

	@Override
	public <T> ThreadLocalSpec<T> newThreadLocal() {
		return new NettyFastThreadLocal<>();
	}

	@Override
	public <T> ThreadLocalSpec<T> newThreadLocal(Supplier<? extends T> supplier) {
		return NettyFastThreadLocal.ofInitial(supplier);
	}

	@Override
	public <T> ThreadLocalSpec<T> newThreadLocal(String name) {
		return new NettyFastThreadLocal<>(name);
	}

	@Override
	public <T> ThreadLocalSpec<T> newThreadLocal(String name, Supplier<? extends T> supplier) {
		return NettyFastThreadLocal.ofInitial(name, supplier);
	}
}
