package io.polaris.core.data;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import io.polaris.core.random.Randoms;
import org.junit.jupiter.api.Test;

class MultiBatchDataCollectorTest {
	@Test
	void test01() throws InterruptedException {
		MultiBatchDataCollector<String, Object> multiCollector = new MultiBatchDataCollector<>(100, 10, TimeUnit.MILLISECONDS);
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
				try {
					TimeUnit.NANOSECONDS.sleep(1);
				} catch (InterruptedException ignored) {
				}
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
}
