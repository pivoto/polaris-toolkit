package io.polaris.core.data.consumer;

import io.polaris.core.data.buffer.IQueueBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
public class ConsumerThread<T> extends Thread {
	private volatile boolean running = false;
	private final IConsumer<T> consumer;
	private final List<IQueueBuffer<T>> queueBuffers = new ArrayList<>(1);
	private final long thinkTime;

	ConsumerThread(String name, IConsumer<T> consumer, long thinkTime) {
		super(name);
		this.consumer = consumer;
		this.thinkTime = thinkTime;
	}

	void addSource(IQueueBuffer<T> sourceBuffer) {
		queueBuffers.add(sourceBuffer);
	}

	@Override
	public void run() {
		running = true;
		final List<T> consumeList = new ArrayList<T>(1500);
		while (running) {
			if (!consume(consumeList)) {
				try {
					Thread.sleep(thinkTime);
				} catch (InterruptedException e) {
				}
			}
		}
		consume(consumeList);
		consumer.onExit();
	}

	private boolean consume(List<T> consumeList) {
		for (IQueueBuffer<T> sourceBuffer : queueBuffers) {
			sourceBuffer.drainTo(consumeList);
		}
		if (!consumeList.isEmpty()) {
			try {
				consumer.consume(consumeList);
			} catch (Throwable t) {
				consumer.onError(consumeList, t);
			} finally {
				consumeList.clear();
			}
			return true;
		}
		return false;
	}

	void shutdown() {
		running = false;
	}

}
