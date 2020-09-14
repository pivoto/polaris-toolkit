package io.polaris.core.datacarrier.consumer;

import io.polaris.core.datacarrier.buffer.BufferChannel;

/**
 * @author Qt
 * @since 1.8
 */
public interface IConsumerDriver<T> {
	boolean isRunning(BufferChannel<T> channel);

	void begin(BufferChannel<T> channel);

	void close(BufferChannel<T> channel);
}
