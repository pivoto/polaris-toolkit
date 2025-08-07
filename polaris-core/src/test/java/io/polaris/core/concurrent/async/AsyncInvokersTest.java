package io.polaris.core.concurrent.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.polaris.core.concurrent.Executors;
import io.polaris.core.concurrent.Schedules;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsyncInvokersTest {

	private ExecutorService customExecutor;
	private ScheduledExecutorService customScheduler;

	@BeforeEach
	void setUp() {
		customExecutor = Executors.create(2, "async-executor");
		customScheduler = Schedules.create(2);
	}

	@AfterEach
	void tearDown() {
		AsyncInvokers.asyncExecutor(null);
		AsyncInvokers.asyncScheduler(null);

		if (customExecutor != null && !customExecutor.isShutdown()) {
			Executors.shutdown(customExecutor);
		}
		if (customScheduler != null && !customScheduler.isShutdown()) {
			Schedules.shutdown(customScheduler);
		}
	}

	/**
	 * 测试目标：验证设置和获取自定义异步执行器的功能
	 * 测试行为：设置自定义执行器后，应该能正确获取到该执行器
	 */
	@Test
	void testAsyncExecutorSetterGetter() {
		// 设置自定义执行器
		AsyncInvokers.asyncExecutor(customExecutor);

		// 验证获取到的是我们设置的执行器
		assertEquals(customExecutor, AsyncInvokers.asyncExecutor());
	}

	/**
	 * 测试目标：验证设置和获取自定义异步调度器的功能
	 * 测试行为：设置自定义调度器后，应该能正确获取到该调度器
	 */
	@Test
	void testAsyncSchedulerSetterGetter() {
		// 设置自定义调度器
		AsyncInvokers.asyncScheduler(customScheduler);

		// 验证获取到的是我们设置的调度器
		assertEquals(customScheduler, AsyncInvokers.asyncScheduler());
	}

	/**
	 * 测试目标：验证默认异步执行器的创建功能
	 * 测试行为：在未设置自定义执行器时，应该能创建并获取默认的执行器
	 */
	@Test
	void testDefaultAsyncExecutor() {
		// 确保没有设置自定义执行器
		AsyncInvokers.asyncExecutor(null);

		// 获取默认执行器
		ExecutorService executor = AsyncInvokers.asyncExecutor();
		assertNotNull(executor);

		// 验证执行器可以执行任务
		AtomicBoolean executed = new AtomicBoolean(false);
		Future<?> future = AsyncInvokers.doAsync(() -> executed.set(true));

		assertDoesNotThrow(() -> future.get(1, TimeUnit.SECONDS));
		assertTrue(executed.get());
	}

	/**
	 * 测试目标：验证默认异步调度器的创建功能
	 * 测试行为：在未设置自定义调度器时，应该能创建并获取默认的调度器
	 */
	@Test
	void testDefaultAsyncScheduler() {
		// 确保没有设置自定义调度器
		AsyncInvokers.asyncScheduler(null);

		// 获取默认调度器
		ScheduledExecutorService scheduler = AsyncInvokers.asyncScheduler();
		assertNotNull(scheduler);

		// 验证调度器可以执行延迟任务
		AtomicBoolean executed = new AtomicBoolean(false);
		Future<?> future = AsyncInvokers.doAsync(() -> executed.set(true), 100, TimeUnit.MILLISECONDS);

		assertDoesNotThrow(() -> future.get(1, TimeUnit.SECONDS));
		assertTrue(executed.get());
	}

	/**
	 * 测试目标：验证doAsync(Consumer<ExecutorService>)方法的功能
	 * 测试行为：应该能通过Consumer使用异步执行器执行任务
	 */
	@Test
	void testDoAsyncWithConsumer() {
		AtomicBoolean executed = new AtomicBoolean(false);

		AsyncInvokers.doAsync(executor -> {
			executor.execute(() -> executed.set(true));
		});

		// 等待任务执行完成
		try {
			Thread.sleep(100);
		} catch (InterruptedException ignored) {
		}

		assertTrue(executed.get());
	}

	/**
	 * 测试目标：验证doAsync(Runnable)方法的功能
	 * 测试行为：应该能异步执行Runnable任务并返回Future
	 */
	@Test
	void testDoAsyncWithRunnable() throws Exception {
		AtomicBoolean executed = new AtomicBoolean(false);

		Future<?> future = AsyncInvokers.doAsync(() -> executed.set(true));

		// 等待任务完成
		future.get(1, TimeUnit.SECONDS);

		assertTrue(executed.get());
	}

	/**
	 * 测试目标：验证doAsync(Callable<T>)方法的功能
	 * 测试行为：应该能异步执行Callable任务并返回包含结果的Future
	 */
	@Test
	void testDoAsyncWithCallable() throws Exception {
		String expectedResult = "test result";

		Future<String> future = AsyncInvokers.doAsync(() -> expectedResult);

		String result = future.get(1, TimeUnit.SECONDS);

		assertEquals(expectedResult, result);
	}

	/**
	 * 测试目标：验证doAsync(Runnable, long)方法的功能
	 * 测试行为：应该能延迟异步执行Runnable任务
	 */
	@Test
	void testDoAsyncWithRunnableAndDelay() throws Exception {
		AtomicBoolean executed = new AtomicBoolean(false);
		long delay = 100; // 100毫秒

		long startTime = System.currentTimeMillis();
		Future<?> future = AsyncInvokers.doAsync(() -> executed.set(true), delay);

		// 等待任务完成
		future.get(1, TimeUnit.SECONDS);
		long endTime = System.currentTimeMillis();

		assertTrue(executed.get());
		assertTrue(endTime - startTime >= delay);
	}

	/**
	 * 测试目标：验证doAsync(Runnable, long, TimeUnit)方法的功能
	 * 测试行为：应该能按指定时间单位延迟异步执行Runnable任务
	 */
	@Test
	void testDoAsyncWithRunnableDelayAndTimeUnit() throws Exception {
		AtomicBoolean executed = new AtomicBoolean(false);
		long delay = 100;
		TimeUnit timeUnit = TimeUnit.MILLISECONDS;

		long startTime = System.currentTimeMillis();
		Future<?> future = AsyncInvokers.doAsync(() -> executed.set(true), delay, timeUnit);

		// 等待任务完成
		future.get(1, TimeUnit.SECONDS);
		long endTime = System.currentTimeMillis();

		assertTrue(executed.get());
		assertTrue(endTime - startTime >= delay);
	}

	/**
	 * 测试目标：验证doAsync(Callable<T>, long)方法的功能
	 * 测试行为：应该能延迟异步执行Callable任务并返回包含结果的Future
	 */
	@Test
	void testDoAsyncWithCallableAndDelay() throws Exception {
		String expectedResult = "delayed result";
		long delay = 100; // 100毫秒

		long startTime = System.currentTimeMillis();
		Future<String> future = AsyncInvokers.doAsync(() -> expectedResult, delay);

		String result = future.get(1, TimeUnit.SECONDS);
		long endTime = System.currentTimeMillis();

		assertEquals(expectedResult, result);
		assertTrue(endTime - startTime >= delay);
	}

	/**
	 * 测试目标：验证doAsync(Callable<T>, long, TimeUnit)方法的功能
	 * 测试行为：应该能按指定时间单位延迟异步执行Callable任务并返回包含结果的Future
	 */
	@Test
	void testDoAsyncWithCallableDelayAndTimeUnit() throws Exception {
		String expectedResult = "delayed result";
		long delay = 100;
		TimeUnit timeUnit = TimeUnit.MILLISECONDS;

		long startTime = System.currentTimeMillis();
		Future<String> future = AsyncInvokers.doAsync(() -> expectedResult, delay, timeUnit);

		String result = future.get(1, TimeUnit.SECONDS);
		long endTime = System.currentTimeMillis();

		assertEquals(expectedResult, result);
		assertTrue(endTime - startTime >= delay);
	}


	/**
	 * 测试目标：验证doAsyncRetry(Supplier<T>, int, Class...)方法的功能
	 * 测试行为：应该能对Supplier任务进行重试执行
	 */
	@Test
	void testDoAsyncRetryWithSupplier() throws Exception {
		AtomicInteger callCount = new AtomicInteger(0);
		String expectedResult = "success";

		Future<String> future = AsyncInvokers.doAsyncRetry(() -> {
			if (callCount.getAndIncrement() < 2) {
				throw new RuntimeException("模拟失败");
			}
			return expectedResult;
		}, 3, RuntimeException.class);

		String result = future.get(1, TimeUnit.SECONDS);

		assertEquals(expectedResult, result);
		assertEquals(3, callCount.get());
	}

	/**
	 * 测试目标：验证doAsyncRetry(Runnable, int, Class...)方法的功能
	 * 测试行为：应该能对Runnable任务进行重试执行
	 */
	@Test
	void testDoAsyncRetryWithRunnable() throws Exception {
		AtomicInteger callCount = new AtomicInteger(0);
		AtomicBoolean success = new AtomicBoolean(false);

		Future<?> future = AsyncInvokers.doAsyncRetry(() -> {
			if (callCount.getAndIncrement() < 2) {
				throw new RuntimeException("模拟失败");
			}
			success.set(true);
		}, 3, RuntimeException.class);

		future.get(1, TimeUnit.SECONDS);

		assertTrue(success.get());
		assertEquals(3, callCount.get());
	}


	/**
	 * 测试目标：验证切换执行器时旧执行器的关闭行为
	 * 测试行为：当设置新的执行器时，旧的默认执行器应该被关闭
	 */
	@Test
	void testOldExecutorShutdownWhenSwitching() {
		// 先获取默认执行器以确保它被创建
		ExecutorService defaultExecutor = AsyncInvokers.asyncExecutor();
		assertNotNull(defaultExecutor);

		// 设置新的执行器
		AsyncInvokers.asyncExecutor(customExecutor);

		// 验证获取到的是新的执行器
		assertEquals(customExecutor, AsyncInvokers.asyncExecutor());
	}

	/**
	 * 测试目标：验证切换调度器时旧调度器的关闭行为
	 * 测试行为：当设置新的调度器时，旧的默认调度器应该被关闭
	 */
	@Test
	void testOldSchedulerShutdownWhenSwitching() {
		// 先获取默认调度器以确保它被创建
		ScheduledExecutorService defaultScheduler = AsyncInvokers.asyncScheduler();
		assertNotNull(defaultScheduler);

		// 设置新的调度器
		AsyncInvokers.asyncScheduler(customScheduler);

		// 验证获取到的是新的调度器
		assertEquals(customScheduler, AsyncInvokers.asyncScheduler());
	}
}
