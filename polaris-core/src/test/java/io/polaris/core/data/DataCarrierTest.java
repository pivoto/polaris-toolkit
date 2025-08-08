package io.polaris.core.data;

import io.polaris.core.data.consumer.DefaultBulkConsumerDriver;
import io.polaris.core.data.consumer.DataConsumer;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since 1.8
 */
public class DataCarrierTest {
	private static final Logger log = Loggers.of(DataCarrierTest.class);

	@Test
	public void testBlockingProduce() throws IllegalAccessException {
		final DataCarrier<SampleData> carrier = new DataCarrier<>(3, 100);

		for (int i = 0; i < 200; i++) {
			carrier.produce(new SampleData().setName("d" + i));
		}

		long time1 = System.currentTimeMillis();
		DataConsumer<SampleData> consumer = data -> log.info("{} consume: {}", System.currentTimeMillis(), data.size());
		carrier.consume(consumer, 3);
		carrier.produce(new SampleData().setName("blocking-data"));

		for (int i = 0; i < 800; i++) {
			carrier.produce(new SampleData().setName("d" + i));
		}

		long time2 = System.currentTimeMillis();
		log.info("exec time : {}ms", time2 - time1);
	}

	@Test
	public void testBlockingProduce2() throws Exception {
		DataConsumer<SampleData> consumer = data -> log.info("{} consume: {}", System.currentTimeMillis(), data.size());

		final DataCarrier<SampleData> carrier = new DataCarrier<>(3, 100);
		DefaultBulkConsumerDriver<SampleData> bulkConsumeDriver = new DefaultBulkConsumerDriver<>("test", 3, 20);
		carrier.consume(bulkConsumeDriver, consumer);

		long time1 = System.currentTimeMillis();
		for (int i = 0; i < 200; i++) {
			carrier.produce(new SampleData().setName("d" + i));
		}
		carrier.produce(new SampleData().setName("blocking-data"));
		for (int i = 0; i < 800; i++) {
			carrier.produce(new SampleData().setName("d" + i));
		}

		long time2 = System.currentTimeMillis();
		log.info("exec time : {}ms", time2 - time1);
	}
	@Test
	public void testBlockingProduce3() throws Exception {
		DataConsumer<SampleData> consumer = data -> log.info("{} consume: {}", System.currentTimeMillis(), data.size());

		final DataCarrier<SampleData> carrier = new DataCarrier<>(3, 100);
		final DataCarrier<SampleData> carrier2 = new DataCarrier<>(3, 100);
		DefaultBulkConsumerDriver<SampleData> bulkConsumeDriver = new DefaultBulkConsumerDriver<>("test", 3, 20);
		carrier.consume(bulkConsumeDriver, consumer);
		carrier2.consume(bulkConsumeDriver, consumer);

		long time1 = System.currentTimeMillis();
		for (int i = 0; i < 200; i++) {
			carrier.produce(new SampleData().setName("d" + i));
			carrier2.produce(new SampleData().setName("d" + i));
		}
		carrier.produce(new SampleData().setName("blocking-data"));
		carrier2.produce(new SampleData().setName("blocking-data"));
		for (int i = 0; i < 800; i++) {
			carrier.produce(new SampleData().setName("d" + i));
			carrier2.produce(new SampleData().setName("d" + i));
		}

		long time2 = System.currentTimeMillis();
		log.info("exec time : {}ms", time2 - time1);
	}
}
