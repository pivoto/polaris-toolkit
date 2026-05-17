package io.polaris.core.concurrent.thread;

import java.util.function.Supplier;

import io.polaris.core.concurrent.ThreadLocalSpec;
import io.polaris.core.spi.Spi;
import io.polaris.core.spi.StatefulSpiLoadable;

/**
 * @author Qt
 * @since May 17, 2026
 */
@Spi
public interface ThreadProvider extends StatefulSpiLoadable {

	Thread newThread(Runnable target, String name);

	Thread newThread(ThreadGroup threadGroup, Runnable target, String name);

	<T> ThreadLocalSpec<T> newThreadLocal();

	<T> ThreadLocalSpec<T> newThreadLocal(Supplier<? extends T> supplier);

	<T> ThreadLocalSpec<T> newThreadLocal(String name);

	<T> ThreadLocalSpec<T> newThreadLocal(String name, Supplier<? extends T> supplier);
}
