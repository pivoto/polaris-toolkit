package io.polaris.core.clock;

import java.util.LinkedHashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.polaris.core.time.StopWatch;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Qt
 * @since 1.8,  May 20, 2024
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClockTest {
	public static final int REPEAT = 4;
	int threadCount = 100000;


	@RepeatedTest(REPEAT)
	@Order(1)
	void test01() throws Exception {
		CountDownLatch subDownLatch = new CountDownLatch(threadCount);
		CountDownLatch mainCountDownLatch = new CountDownLatch(1);
		// 单线程循环获取
		long singleStartTime = System.nanoTime();
		// 主线程开始继续执行
		mainCountDownLatch.countDown();
		for (int i = 0; i < threadCount; i++) {
			try {
				// 主线程先阻塞等待
				mainCountDownLatch.await();
				System.currentTimeMillis();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				subDownLatch.countDown();
			}
		}
		// 子线程等待线程池中其他活跃线程执行完
		subDownLatch.await();
		long singleDeltaTime = System.nanoTime() - singleStartTime;
		System.out.println("单线程A" + threadCount + "次耗时：" + singleDeltaTime + "ns");
	}

	@RepeatedTest(REPEAT)
	@Order(3)
	void test02() throws Exception {
		// 使用线程池，多线程并发获取
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * REPEAT,
			60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(threadCount));

		CountDownLatch subDownLatch = new CountDownLatch(threadCount);
		CountDownLatch mainCountDownLatch = new CountDownLatch(1);
		for (int i = 0; i < threadCount; i++) {
			threadPoolExecutor.execute(() -> {
				try {
					// 主线程先阻塞等待
					mainCountDownLatch.await();
					System.currentTimeMillis();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					subDownLatch.countDown();
				}
			});
		}
		long startTime = System.nanoTime();
		// 主线程开始继续执行
		mainCountDownLatch.countDown();
		// 子线程等待线程池中其他活跃线程执行完
		subDownLatch.await();
		long deltaTime = System.nanoTime() - startTime;
		System.out.println("多线程A" + threadCount + "次耗时：" + deltaTime + "ns");
	}

	@RepeatedTest(REPEAT)
	@Order(2)
	void test03() throws Exception {
		// 使用线程池，多线程并发获取
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * REPEAT,
			60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(threadCount));


		CountDownLatch subDownLatch = new CountDownLatch(threadCount);
		CountDownLatch mainCountDownLatch = new CountDownLatch(1);
		for (int i = 0; i < threadCount; i++) {
			threadPoolExecutor.execute(() -> {
				try {
					// 主线程先阻塞等待
					mainCountDownLatch.await();
					TimeMillisClock.currentTimeMillis();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					subDownLatch.countDown();
				}
			});
		}
		long startTime = System.nanoTime();
		// 主线程开始继续执行
		mainCountDownLatch.countDown();
		// 子线程等待线程池中其他活跃线程执行完
		subDownLatch.await();
		long deltaTime = System.nanoTime() - startTime;
		System.out.println("多线程B" + threadCount + "次耗时：" + deltaTime + "ns");
	}


	@Test
	void test04() throws InterruptedException {
		{
			//100个线程各执行一次
			CountDownLatch wait = new CountDownLatch(1);
			CountDownLatch threadLatch = new CountDownLatch(100);
			for (int i = 0; i < 100; i++) {
				new Thread(() -> {
					try {
						StopWatch watch = new StopWatch();
						//先阻塞住所有线程
						wait.await();
						watch.start();
						for (int j = 0; j < 100; j++) {
							System.currentTimeMillis();
						}
						watch.stop();
						System.out.print(watch.totalTimeNanos()  + ",");
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						threadLatch.countDown();
					}
				}).start();
			}
			//暂停1s保证线程创建完成
			Thread.sleep(1000);
			wait.countDown();
			threadLatch.await();
			System.out.println();
			System.out.println("============================");
		}
		{
			//100个线程各执行一次
			CountDownLatch wait = new CountDownLatch(1);
			CountDownLatch threadLatch = new CountDownLatch(100);
			for (int i = 0; i < 100; i++) {
				new Thread(() -> {
					try {
						StopWatch watch = new StopWatch();
						//先阻塞住所有线程
						wait.await();
						watch.start();
						for (int j = 0; j < 100; j++) {
							TimeMillisClock.currentTimeMillis();
						}
						watch.stop();
						System.out.print(watch.totalTimeNanos() + ",");
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						threadLatch.countDown();
					}
				}).start();
			}
			//暂停1s保证线程创建完成
			Thread.sleep(1000);
			wait.countDown();
			threadLatch.await();
			System.out.println();
			System.out.println("============================");
		}
	}

	@Test
	void test05() {
		CacheClock.currentTimeMillis();
		TimeMillisClock.currentTimeMillis();
		int num = 10_000_000;
		System.out.print("单线程" + num + "次System.currentTimeMillis调用总耗时：    ");
		System.out.println(singleThreadTest(() -> {
			long l = System.currentTimeMillis();
		}, num));
		System.out.print("单线程" + num + "次CacheClock.currentTimeMillis调用总耗时：");
		System.out.println(singleThreadTest(() -> {
			long l = CacheClock.currentTimeMillis();
		}, num));
		System.out.print("并发" + num + "次System.currentTimeMillis调用总耗时：      ");
		System.out.println(concurrentTest(() -> {
			long l = System.currentTimeMillis();
		}, num));

		System.out.print("并发" + num + "次CacheClock.currentTimeMillis调用总耗时：  ");
		System.out.println(concurrentTest(() -> {
			long l = CacheClock.currentTimeMillis();
		}, num));
//		System.out.print("并发" + num + "次CacheClock.currentTimeMillis调用总耗时：  ");
//		System.out.println(concurrentTest(() -> {
//			long l = TimeMillisClock.currentTimeMillis();
//		}, num));
	}


	@Test
	void test06() throws InterruptedException {
		for (int i = 0; i < 100; i++) {
			System.out.println(TimeMillisClock.currentTimeMillis());
			Thread.sleep(5);
		}
	}

	@Test
	void test07() throws InterruptedException {
		for (int i = 0; i < 100; i++) {
			System.out.println(System.currentTimeMillis());
			;
			Thread.sleep(5);
		}
	}

	/**
	 * 单线程测试
	 *
	 * @return
	 */
	private static long singleThreadTest(Runnable runnable, int num) {
		long sum = 0;
		for (int i = 0; i < num; i++) {
			long begin = System.nanoTime();
			runnable.run();
			long end = System.nanoTime();
			sum += end - begin;
		}
		return sum;
	}

	/**
	 * 并发测试
	 *
	 * @return
	 */
	private static long concurrentTest(Runnable runnable, int num) {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(200, 200, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(num));
		long[] sum = new long[]{0};
		//闭锁基于CAS实现，并不适合当前的计算密集型场景，可能导致等待时间较长
		CountDownLatch countDownLatch = new CountDownLatch(num);
		for (int i = 0; i < num; i++) {
			threadPoolExecutor.submit(() -> {
				long begin = System.nanoTime();
				runnable.run();
				long end = System.nanoTime();
				//计算复杂型场景更适合使用悲观锁
				synchronized (ClockTest.class) {
					sum[0] += end - begin;
				}
				countDownLatch.countDown();
			});
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		threadPoolExecutor.shutdown();
		return sum[0];
	}


	/**
	 * 缓存时钟，缓存System.currentTimeMillis()的值，每隔20ms更新一次
	 */
	public static class CacheClock {
		private static volatile long timeMilis;

		static {
			ScheduledExecutorService timer = new ScheduledThreadPoolExecutor(1);
			//每秒更新毫秒缓存
			timer.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					timeMilis = System.currentTimeMillis();
				}
			}, 0, 1, TimeUnit.MILLISECONDS);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				timer.shutdown();
			}));
		}

		public static long currentTimeMillis() {
			return timeMilis;
		}
	}


	@Test
	void test08() {
		LinkedHashSet<Long> set = new LinkedHashSet<>();
		for (int i = 0; i < 10000000; i++) {
			set.add(TimeMillisClock.currentTimeMillis());
		}
		for (Long v : set) {
			System.out.println(v);
		}
	}
}
