package io.polaris.core.datacarrier.consumer;

import io.polaris.core.datacarrier.buffer.BufferChannel;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Qt
 * @since 1.8
 */
public class ConsumeDriver<T> implements IConsumerDriver<T> {
	private boolean running;
	private final ConsumerThread<T>[] threads;
	private final BufferChannel<T> channel;
	private final ReentrantLock lock;

	@SuppressWarnings("unchecked")
	private ConsumeDriver(BufferChannel<T> channel, int num) {
		this.running = false;
		this.channel = channel;
		this.threads = new ConsumerThread[num];
		this.lock = new ReentrantLock();
	}

	public ConsumeDriver(String name, BufferChannel<T> channel, IConsumer<T> consumer, int num, long thinkTime) {
		this(channel, num);
		for (int i = 0; i < num; i++) {
			threads[i] = new ConsumerThread<T>("DataCarrier." + name + ".Consumer." + i + "", consumer, thinkTime);
			threads[i].setDaemon(true);
		}
	}


	@Override
	public void begin(BufferChannel<T> channel) {
		if (running) {
			return;
		}
		lock.lock();
		try {
			this.allocateBuffer2Thread();
			for (ConsumerThread<T> thread : threads) {
				thread.start();
			}
			running = true;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isRunning(BufferChannel<T> channel) {
		return running;
	}

	private void allocateBuffer2Thread() {
		int bufferCount = this.channel.getBufferCount();
		for (int idx = 0; idx < bufferCount; idx++) {
			int consumerIndex = idx % threads.length;
			threads[consumerIndex].addSource(channel.getBuffer(idx));
		}
	}

	@Override
	public void close(BufferChannel<T> channel) {
		lock.lock();
		try {
			this.running = false;
			for (ConsumerThread<T> thread : threads) {
				thread.shutdown();
			}
		} finally {
			lock.unlock();
		}
	}
}
