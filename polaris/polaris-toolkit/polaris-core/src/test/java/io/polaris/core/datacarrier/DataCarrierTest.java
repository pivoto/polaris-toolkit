package io.polaris.core.datacarrier;

import io.polaris.core.datacarrier.buffer.BufferChannel;
import io.polaris.core.datacarrier.buffer.IQueueBuffer;
import io.polaris.core.datacarrier.consumer.IConsumer;
import io.polaris.core.datacarrier.consumer.BulkConsumeDriver;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
@Slf4j
public class DataCarrierTest {

	@Test
	public void testBlockingProduce() throws IllegalAccessException {
		final DataCarrier<SampleData> carrier = new DataCarrier<>(2, 100);

		for (int i = 0; i < 200; i++) {
			carrier.produce(new SampleData().setName("d" + i));
		}

		long time1 = System.currentTimeMillis();
		IConsumer<SampleData> consumer = data -> log.info("{} consume: {}", System.currentTimeMillis(), data.size());
		carrier.consume(consumer, 2);
		carrier.produce(new SampleData().setName("blocking-data"));

		for (int i = 0; i < 800; i++) {
			carrier.produce(new SampleData().setName("d" + i));
		}

		long time2 = System.currentTimeMillis();
		log.info("exec time : {}ms", time2 - time1);
	}

	@Test
	public void testBlockingProduce2() throws Exception {
		IConsumer<SampleData> consumer = data -> log.info("{} consume: {}", System.currentTimeMillis(), data.size());

		final DataCarrier<SampleData> carrier = new DataCarrier<>(2, 100);
		carrier.consume(new BulkConsumeDriver<SampleData>("test", 3, 20), consumer);

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
}
