package io.polaris.core.data;

import io.polaris.core.data.buffer.BufferChannel;
import io.polaris.core.data.consumer.DefaultConsumerDriver;
import io.polaris.core.data.consumer.IConsumer;
import io.polaris.core.data.buffer.BufferStrategy;
import io.polaris.core.data.consumer.IBulkConsumerDriver;
import io.polaris.core.data.consumer.ConsumerDriver;
import io.polaris.core.data.partition.IDataPartitioner;
import io.polaris.core.data.partition.SimpleRollingPartitioner;

/**
 * @author Qt
 * @since 1.8
 */
public class DataCarrier<T> {
	private BufferChannel<T> channel;
	private ConsumerDriver<T> driver;
	private String name;

	public DataCarrier(int bufferCount, int bufferSize) {
		this("DEFAULT", bufferCount, bufferSize);
	}

	public DataCarrier(String name, int bufferCount, int bufferSize) {
		this(name, bufferCount, bufferSize, BufferStrategy.BLOCKING);
	}

	public DataCarrier(int bufferCount, int bufferSize, BufferStrategy strategy) {
		this("DEFAULT", bufferCount, bufferSize, strategy);
	}

	public DataCarrier(String name, int bufferCount, int bufferSize, BufferStrategy strategy) {
		this(name, bufferCount, bufferSize, strategy, new SimpleRollingPartitioner<T>());
	}


	public DataCarrier(String name, int bufferCount, int bufferSize, BufferStrategy strategy,IDataPartitioner<T> partitioner) {
		this.name = name;
		channel = new BufferChannel<>(bufferCount, bufferSize, partitioner, strategy);
	}

	public boolean produce(T data) {
		if (driver != null) {
			if (!driver.isRunning(channel)) {
				return false;
			}
		}
		return this.channel.produce(data);
	}

	public DataCarrier<T> consume(IConsumer<T> consumer, int num) {
		return this.consume(consumer, num, 20);
	}

	public DataCarrier<T> consume(IConsumer<T> consumer, int num, long thinkTime) {
		if (driver != null) {
			driver.close(channel);
		}
		driver = new DefaultConsumerDriver<T>(this.name, this.channel, consumer, num, thinkTime);
		driver.begin(channel);
		return this;
	}

	public DataCarrier<T> consume(IBulkConsumerDriver<T> consumerDriver, IConsumer<T> consumer) {
		driver = consumerDriver;
		consumerDriver.add(channel, consumer);
		driver.begin(channel);
		return this;
	}

	public DataCarrier<T> setPartitioner(IDataPartitioner<T> dataPartitioner) {
		this.channel.setPartitioner(dataPartitioner);
		return this;
	}

	public DataCarrier<T> setMaxRetryCount(final int maxRetryCount) {
		this.channel.setMaxRetryCount(maxRetryCount);
		return this;
	}

	public BufferChannel<T> getChannel() {
		return channel;
	}

	public void shutdown() {
		if (driver != null) {
			driver.close(channel);
		}
	}

}
