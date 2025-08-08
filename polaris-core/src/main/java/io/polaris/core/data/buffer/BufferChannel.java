package io.polaris.core.data.buffer;

import io.polaris.core.data.partition.DataPartitioner;

/**
 * @author Qt
 * @since 1.8
 */
public class BufferChannel<T> {
	private final QueueBuffer<T>[] buffers;
	private final BufferStrategy strategy;
	private final long size;
	private int maxRetryCount = 3;
	private DataPartitioner<T> dataPartitioner;

	@SuppressWarnings({"unchecked", "PointlessArithmeticExpression"})
	public BufferChannel(int bufferCount, int bufferSize, DataPartitioner<T> partitioner, BufferStrategy strategy) {
		this.dataPartitioner = partitioner;
		this.strategy = strategy;
		buffers = new QueueBuffer[bufferCount];
		switch (strategy) {
			case BLOCKING:
				for (int i = 0; i < bufferCount; i++) {
					buffers[i] = new BlockingQueueBuffer<>(bufferSize);
				}
				break;
			case FAIL_FAST:
				for (int i = 0; i < bufferCount; i++) {
					buffers[i] = new DefaultQueueBuffer<>(bufferSize, false);
				}
				break;
			case OVERRIDE:
			default:
				for (int i = 0; i < bufferCount; i++) {
					buffers[i] = new DefaultQueueBuffer<>(bufferSize, true);
				}
				break;
		}
		size = 1L * bufferCount * bufferSize;
	}


	public boolean produce(T data) {
		int index = dataPartitioner.partition(buffers.length, data);
		int retryCountDown = 1;
		if (BufferStrategy.FAIL_FAST.equals(strategy)) {
			if (maxRetryCount > 1) {
				retryCountDown = maxRetryCount;
			}
		}
		for (; retryCountDown > 0; retryCountDown--) {
			if (buffers[index].push(data)) {
				return true;
			}
		}
		return false;
	}

	public void setMaxRetryCount(final int maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
	}

	public void setPartitioner(DataPartitioner<T> dataPartitioner) {
		this.dataPartitioner = dataPartitioner;
	}

	public int getBufferCount() {
		return this.buffers.length;
	}

	public long size() {
		return size;
	}

	public QueueBuffer<T> getBuffer(int index) {
		return this.buffers[index];
	}

}
