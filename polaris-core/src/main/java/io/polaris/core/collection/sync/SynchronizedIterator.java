package io.polaris.core.collection.sync;

import java.util.Iterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Qt
 * @since Apr 05, 2026
 */
public class SynchronizedIterator<E> implements Iterator<E> {
	private final Iterator<E> raw;
	private final ReentrantReadWriteLock lock;

	public SynchronizedIterator(Iterator<E> raw, ReentrantReadWriteLock lock) {
		this.raw = raw;
		this.lock = lock;
	}

	@Override
	public boolean hasNext() {
		lock.readLock().lock();
		try {return raw.hasNext();} finally {lock.readLock().unlock();}
	}

	@Override
	public E next() {
		lock.readLock().lock();
		try {return raw.next();} finally {lock.readLock().unlock();}
	}

	@Override
	public void remove() {
		lock.writeLock().lock();
		try {raw.remove();} finally {lock.writeLock().unlock();}
	}
}
