package io.polaris.core.datacarrier.consumer;

import io.polaris.core.datacarrier.buffer.BufferChannel;

/**
 * @author Qt
 * @since 1.8
 */
public interface IBulkConsumerDriver<T> extends IConsumerDriver<T> {
	void add(String name, BufferChannel<T> channel, IConsumer<T> consumer);
}
