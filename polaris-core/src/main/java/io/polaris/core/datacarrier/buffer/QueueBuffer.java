package io.polaris.core.datacarrier.buffer;

import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
public class QueueBuffer<T> implements IQueueBuffer<T> {
	private final Object[] buffer;
	private final AtomicRangeInteger index;
	private final boolean override;

	QueueBuffer(int bufferSize, boolean override) {
		this.buffer = new Object[bufferSize];
		this.override = override;
		this.index = new AtomicRangeInteger(0, bufferSize);
	}

	@Override
	public boolean push(T data) {
		int i = index.getAndIncrement();
		if (buffer[i] != null) {
			if (!override) {
				return false;
			}
		}
		buffer[i] = data;
		return true;
	}


	@Override
	public int size() {
		return buffer.length;
	}

	@Override
	public void drainTo(List<T> consumeList) {
		this.drainTo(consumeList, 0, buffer.length);
	}

	void drainTo(List<T> consumeList, int start, int end) {
		for (int i = start; i < end; i++) {
			if (buffer[i] != null) {
				consumeList.add((T) buffer[i]);
				buffer[i] = null;
			}
		}
	}

}
