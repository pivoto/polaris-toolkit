package io.polaris.core.data.consumer;

import io.polaris.core.data.buffer.BufferChannel;

/**
 * @author Qt
 */
public interface BulkConsumerDriver<T> extends ConsumerDriver<T> {

	void add(BufferChannel<T> channel, DataConsumer<T> consumer);

}
