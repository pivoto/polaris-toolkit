package io.polaris.core.data.consumer;

import io.polaris.core.data.buffer.BufferChannel;
import io.polaris.core.data.buffer.QueueBuffer;
import io.polaris.core.tuple.Tuple2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
public class BulkConsumerThread<T> extends Thread {
	private volatile boolean running = true;
	private volatile List<Tuple2<BufferChannel<T>, DataConsumer<T>>> bulkConsumers;
	private volatile long size;
	private final long thinkTime;

	public BulkConsumerThread(String name, long thinkTime) {
		super(name);
		this.bulkConsumers = new ArrayList<>();
		this.thinkTime = thinkTime;
	}

	BulkConsumerThread<T> copy() {
		BulkConsumerThread<T> thread = new BulkConsumerThread<>(getName(), thinkTime);
		thread.bulkConsumers = this.bulkConsumers;
		thread.size = this.size;
		return thread;
	}

	public void addBulk(BufferChannel<T> channel, DataConsumer<T> consumer) {
		Tuple2<BufferChannel<T>, DataConsumer<T>> group = new Tuple2<>(channel, consumer);
		// 重建列表防止添加时正在消费中
		List<Tuple2<BufferChannel<T>, DataConsumer<T>>> newList = new ArrayList<>();
		newList.addAll(bulkConsumers);
		newList.add(group);
		bulkConsumers = newList;
		size += channel.size();
	}

	public long size() {
		return size;
	}


	@Override
	public void run() {
		running = true;
		final List<T> consumeList = new ArrayList<>(2000);
		while (running) {
			boolean hasData = false;
			for (Tuple2<BufferChannel<T>, DataConsumer<T>> target : bulkConsumers) {
				boolean consume = consume(target, consumeList);
				hasData = hasData || consume;
			}

			if (!hasData) {
				try {
					Thread.sleep(thinkTime);
				} catch (InterruptedException e) {
				}
			}
		}

		// 余量消费
		for (Tuple2<BufferChannel<T>, DataConsumer<T>> target : bulkConsumers) {
			consume(target, consumeList);
			target.getSecond().onExit();
		}
	}

	private boolean consume(Tuple2<BufferChannel<T>, DataConsumer<T>> target, List<T> consumeList) {
		for (int i = 0; i < target.getFirst().getBufferCount(); i++) {
			QueueBuffer<T> buffer = target.getFirst().getBuffer(i);
			buffer.drainTo(consumeList);
		}

		if (!consumeList.isEmpty()) {
			try {
				target.getSecond().consume(consumeList);
			} catch (Throwable t) {
				target.getSecond().onError(consumeList, t);
			} finally {
				consumeList.clear();
			}
			return true;
		}
		return false;
	}

	public void shutdown() {
		running = false;
	}


}
