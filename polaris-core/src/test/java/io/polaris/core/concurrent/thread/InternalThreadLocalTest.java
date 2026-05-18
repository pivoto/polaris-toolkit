package io.polaris.core.concurrent.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import io.polaris.core.concurrent.Executors;
import io.polaris.core.concurrent.Threads;
import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since May 18, 2026
 */
public class InternalThreadLocalTest {
	static InternalThreadLocal<Boolean> local = new InternalThreadLocal<>();

	@Test
	void test01() {
		ThreadFactory factory = InternalThread::new;
		ThreadPoolExecutor pool = Executors.create(4, factory);
		Runnable task = () -> {
			Boolean b = local.get();
			Consoles.log("local: {}", b);
			if (b == null) {
				local.set(true);
				try {
					Consoles.log("set local: {}", true);
					Threads.sleep(100L);
					Consoles.log("get local: {}", local.get());
					Consoles.log("over.");
				} finally {
					local.remove();
				}
			}
		};
		for (int i = 0; i < 10; i++) {
			pool.execute(task);
		}

		Threads.sleep(5000L);
		Executors.shutdown(pool);
	}
}
