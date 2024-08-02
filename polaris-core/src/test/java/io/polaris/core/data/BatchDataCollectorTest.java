package io.polaris.core.data;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.polaris.core.random.Randoms;
import org.junit.jupiter.api.Test;

class BatchDataCollectorTest {
	@Test
	void test01() throws InterruptedException {
		BatchDataCollector<Object> collector = new BatchDataCollector<>(100, 10, TimeUnit.MILLISECONDS, (List<Object> data) -> {
			System.out.println(Thread.currentThread().getName() + " 消费量：" + data.size() + " data:" + data);
		});
		collector.startScheduler();

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
				collector.collect(i);
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
		collector.flush();
		System.out.println("end flush");
	}
}

