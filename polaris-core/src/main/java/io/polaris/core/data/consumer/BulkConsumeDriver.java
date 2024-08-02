package io.polaris.core.data.consumer;

import io.polaris.core.data.buffer.BufferChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
public class BulkConsumeDriver<T> implements IBulkConsumerDriver<T> {
	private final List<BulkConsumerThread<T>> threads;
	private volatile boolean running = false;

	public BulkConsumeDriver(String name, int size, long thinkTime) {
		threads = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			BulkConsumerThread<T> thread = new BulkConsumerThread<T>("DataCarrier." + name + ".BulkConsumer." + i + "", thinkTime);
			thread.setDaemon(true);
			threads.add(thread);
		}
	}

	@Override
	synchronized public void add(BufferChannel<T> channel, IConsumer<T> consumer) {
		BulkConsumerThread<T> thread = getLowestPayload();
		thread.addBulk(channel, consumer);
	}

	private BulkConsumerThread<T> getLowestPayload() {
		BulkConsumerThread<T> winner = threads.get(0);
		for (int i = 1; i < threads.size(); i++) {
			BulkConsumerThread<T> thread = threads.get(i);
			if (thread.size() < winner.size()) {
				winner = thread;
			}
		}
		return winner;
	}

	@Override
	public boolean isRunning(BufferChannel<T> channel) {
		return running;
	}

	@Override
	public void begin(BufferChannel<T> channel) {
		if (running) {
			return;
		}
		for (BulkConsumerThread<T> thread : threads) {
			thread.start();
		}
		running = true;
	}

	@Override
	public void close(BufferChannel<T> channel) {
		for (BulkConsumerThread<T> thread : threads) {
			thread.shutdown();
		}
	}
}
