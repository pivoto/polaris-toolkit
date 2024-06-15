package io.polaris.core.concurrent.pool;

import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import org.junit.jupiter.api.Test;

class PooledExecutorTest {
	final ILogger log = ILoggers.of(getClass());

	@Test
	public void test01() {
		PooledExecutor<Object> pooledExecutor = new PooledExecutor<>();
		try {
			pooledExecutor.setOpenStatistics(true);
			/*pooledExecutor.addConsumer(10, data -> {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
				log.debug("handle data: {}", data);
			});*/
			pooledExecutor.setRejectConsumer(records -> {
				log.error("err: {}, size: {}, data: {}", records.getError(), records.getRecords().size(), records.getRecords());
			});
			pooledExecutor.addConsumer(4, new TransactionConsumer0());
			pooledExecutor.start();
			for (int i = 0; i < 100; i++) {
				pooledExecutor.offer("data-" + i);
			}

		} finally {
			pooledExecutor.shutdown();
		}
		RunnableStatistics statistics = pooledExecutor.runnableStatistics();
		log.info("total: {}, success: {}, error: {}", statistics.getTotal(), statistics.getSuccess(), statistics.getError());
	}

	@Test
	public void test99() {
		int ringBufferSize = 1000;

		int size = 1 << 4;
		for (; size < ringBufferSize; size <<= 1) {
		}

		System.out.println(Long.toBinaryString(size));
		System.out.println(Long.toBinaryString(ringBufferSize));

		System.out.println(Math.log10(size) / Math.log10(2));
		System.out.println(Math.log10(ringBufferSize) / Math.log10(2));

	}

	class TransactionConsumer0 implements TransactionConsumer<Object, Object> {

		@Override
		public int commitCount() {
			return 10;
		}

		@Override
		public Object openResource() throws Throwable {
			log.debug("open");
			return "resource";
		}

		@Override
		public void processData(Object resource, Object data) throws Throwable {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
			log.debug("handle data: {}", data);
			if (Math.random() < 0.1) throw new SecurityException();
		}

		@Override
		public void commitResource(Object resource) throws Throwable {
			log.debug("commit");
		}

		@Override
		public void rollbackResource(Object resource) throws Throwable {
			log.debug("rollback");
		}

		@Override
		public void closeResource(Object resource) {
			log.debug("close");
		}
	}
}
