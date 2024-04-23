package io.polaris.concurrent.pool;

import io.polaris.core.concurrent.pool.RunnableStatistics;
import io.polaris.core.concurrent.pool.TransactionConsumer;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DisruptorPooledExecutorTest {
	final ILogger log = ILoggers.of(getClass());

	@Test
	public void test01() {
		DisruptorPooledExecutor<Object> pooledExecutor = new DisruptorPooledExecutor<>();
		try {
			pooledExecutor.setOpenStatistics(true);
			/*pooledExecutor.addConsumer(10, data -> {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
				log.debug("handle data: {}", data);
			});*/
			pooledExecutor.addConsumer(10, new TransactionConsumer0());
			pooledExecutor.start();
			for (int i = 0; i < 1000; i++) {
				pooledExecutor.offer("data-" + i);
			}

		} finally {
			pooledExecutor.shutdown();
		}
		RunnableStatistics statistics = pooledExecutor.runnableStatistics();
		log.info("total: {}, success: {}, error: {}", statistics.getTotal(), statistics.getSuccess(), statistics.getError());


	}

	class TransactionConsumer0 implements TransactionConsumer<Object, Object> {

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
