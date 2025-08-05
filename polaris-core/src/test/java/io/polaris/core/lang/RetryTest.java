package io.polaris.core.lang;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import io.polaris.core.concurrent.Schedules;
import io.polaris.core.io.Consoles;
import io.polaris.core.junit.Slow;
import io.polaris.core.random.Randoms;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slow
class RetryTest {
	static ScheduledExecutorService executor;

	@BeforeAll
	static void beforeAll() {
		executor = Schedules.create(1);
	}

	@AfterAll
	static void afterAll() {
		Schedules.shutdown(executor);
	}


	@Test
	void testRetryRunnable() {
		AtomicInteger count = new AtomicInteger(0);
		Runnable runnable = () -> {
			Consoles.log("count:{}", count.get());
			if (count.incrementAndGet() < Randoms.randomInt(3, 10)) {
				throw new RuntimeException("retry");
			}
		};
		Retry.doRetry(runnable, 10, 100);
	}

	@Test
	void testRetryCallable() throws Exception {
		AtomicInteger count = new AtomicInteger(0);
		Callable<Integer> callable = () -> {
			Consoles.log("count:{}", count.get());
			if (count.incrementAndGet() < Randoms.randomInt(3, 10)) {
				throw new RuntimeException("retry");
			}
			return count.get();
		};
		Integer rs = Retry.doRetry(callable, 10, 100);
		Consoles.log("rs:{}", rs);
	}

	@Test
	void testRetryPooledRunnable() throws Exception {
		AtomicInteger count = new AtomicInteger(0);
		Runnable runnable = () -> {
			Consoles.log("count:{}", count.get());
			if (count.incrementAndGet() < Randoms.randomInt(3, 10)) {
				throw new RuntimeException("retry");
			}
		};
		Future<?> future = Retry.doRetry(executor, runnable, 10, 100);
		Consoles.log("rs:{}", future.get());
	}

	@Test
	void testRetryPooledCallable() throws Exception {
		AtomicInteger count = new AtomicInteger(0);
		Callable<Integer> callable = () -> {
			Consoles.log("count:{}", count.get());
			if (count.incrementAndGet() < Randoms.randomInt(3, 10)) {
				throw new RuntimeException("retry");
			}
			return count.get();
		};
		Future<Integer> future = Retry.doRetry(executor, callable, 10, 100);
		Consoles.log("rs:{}", future.get());
	}
}
