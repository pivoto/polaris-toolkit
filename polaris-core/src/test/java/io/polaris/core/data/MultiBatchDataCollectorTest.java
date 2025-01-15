package io.polaris.core.data;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import io.polaris.core.concurrent.PooledThreadFactory;
import io.polaris.core.random.Randoms;
import io.polaris.core.time.Dates;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

class MultiBatchDataCollectorTest {
	@Test
	void test01() throws InterruptedException {
		MultiBatchDataCollector<String, Object> multiCollector = new MultiBatchDataCollector<>(100, 1000, TimeUnit.MILLISECONDS);
		multiCollector.startScheduler();
		AtomicLong key1Count = new AtomicLong(0);
		AtomicLong key2Count = new AtomicLong(0);
		multiCollector.register("key1", (List<Object> data) -> {
			key1Count.addAndGet(data.size());
			System.out.println(Thread.currentThread().getName() + " key1 消费量：" + data.size() + " data:" + data);
		});
		multiCollector.register("key2", (List<Object> data) -> {
			key2Count.addAndGet(data.size());
			System.out.println(Thread.currentThread().getName() + " key2 消费量：" + data.size() + " data:" + data);
		});

		int COUNT = 3;
		CountDownLatch latch = new CountDownLatch(COUNT);
		Runnable task = () -> {
			int count = Randoms.randomInt(200, 300);
			System.out.println(Thread.currentThread().getName() + " 预生产量：" + count);
			for (int i = 0; i < count; i++) {
//				try {
//					TimeUnit.NANOSECONDS.sleep(1);
//				} catch (InterruptedException ignored) {
//				}
				multiCollector.collect("key1", i);
				multiCollector.collect("key2", i);
			}
			System.out.println(Thread.currentThread().getName() + " 生产量：" + count);
			latch.countDown();
		};
		for (int i = 0; i < COUNT; i++) {
			Thread thread = new Thread(task);
			thread.start();
		}
		System.out.println("end generated");
		latch.await();
		multiCollector.flush("key1");
		multiCollector.flush("key2");
		System.out.println("end flush");
		System.out.println("key1 count:" + key1Count.get() + " key2 count:" + key2Count.get());
	}

	@Test
	void test02() throws InterruptedException {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2,
			new PooledThreadFactory("Test"));
		@RequiredArgsConstructor
		class Task implements Runnable {
			private final String name;

			@Override
			public void run() {
				System.out.println(name + " " + Thread.currentThread().getName() + " Start " + Dates.nowStr());
				try {
				Thread.sleep(2000);
				} catch (Exception ignored) {
				}
				System.out.println(name + " " + Thread.currentThread().getName() + " End " + Dates.nowStr());
			}
		}


		scheduler.scheduleAtFixedRate(new Task("task1"), 1000, 1000, TimeUnit.MILLISECONDS);
		scheduler.scheduleAtFixedRate(new Task("task2"), 1000, 1000, TimeUnit.MILLISECONDS);


		Thread.sleep(60000);
	}
}
