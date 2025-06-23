package io.polaris.core.concurrent.queue;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qt
 * @since Feb 14, 2025
 */
public class MemorySafeLinkedBlockingQueue<E> extends LinkedBlockingQueue<E> {

	private static final long serialVersionUID = 8032578371739960142L;

	public static int THE_256_MB = 256 * 1024 * 1024;

	@Setter
	@Getter
	private int limitedFreeMemory;

	public MemorySafeLinkedBlockingQueue() {
		this(Math.min(THE_256_MB, (int) (Runtime.getRuntime().maxMemory() / 8)));
	}

	public MemorySafeLinkedBlockingQueue(int limitedFreeMemory) {
		super(Integer.MAX_VALUE);
		this.limitedFreeMemory = limitedFreeMemory;
	}


	public boolean hasRemainedMemory() {
		return MemoryLimitCalculator.maxAvailable() > limitedFreeMemory;
	}

	@Override
	public void put(final E e) throws InterruptedException {
		if (hasRemainedMemory()) {
			super.put(e);
		} else {
			throw new InterruptedException("no free memory");
		}
	}

	@Override
	public boolean offer(final E e, final long timeout, final TimeUnit unit) throws InterruptedException {
		if (!hasRemainedMemory()) {
			return false;
		}
		return super.offer(e, timeout, unit);
	}

	@Override
	public boolean offer(final E e) {
		if (!hasRemainedMemory()) {
			return false;
		}
		return super.offer(e);
	}
}
