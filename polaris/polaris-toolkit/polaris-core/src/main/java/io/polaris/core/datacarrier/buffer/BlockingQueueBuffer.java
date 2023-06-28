package io.polaris.core.datacarrier.buffer;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Qt
 * @since 1.8
 */
public class BlockingQueueBuffer<T> implements IQueueBuffer<T> {
	private final BlockingQueue<T> queue;
	private final int size;

	BlockingQueueBuffer(int bufferSize) {
		this.queue = new ArrayBlockingQueue<T>(bufferSize);
		this.size = bufferSize;
	}

	@Override
	public boolean push(T data) {
		//only BufferStrategy.BLOCKING
		try {
			queue.put(data);
		} catch (InterruptedException e) {
			// Ignore the error
			return false;
		}
		return true;
	}

	@Override
	public void drainTo(List<T> consumeList) {
		queue.drainTo(consumeList);
	}

	@Override
	public int size() {
		return size;
	}

}
