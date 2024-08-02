package io.polaris.core.data.consumer;

import io.polaris.core.data.buffer.BufferChannel;

/**
 * @author Qt
 * @since 1.8
 */
public interface IBulkConsumerDriver<T> extends IConsumerDriver<T> {
	void add(BufferChannel<T> channel, IConsumer<T> consumer);
}
