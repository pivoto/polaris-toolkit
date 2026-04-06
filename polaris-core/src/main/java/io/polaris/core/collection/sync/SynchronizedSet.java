package io.polaris.core.collection.sync;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Qt
 * @since Apr 05, 2026
 */
public class SynchronizedSet<E> implements Set<E> {
	private final Set<E> raw;
	private final ReentrantReadWriteLock lock;
	private final ReentrantReadWriteLock.ReadLock readLock;
	private final ReentrantReadWriteLock.WriteLock writeLock;

	public SynchronizedSet(Set<E> raw) {
		this.raw = Objects.requireNonNull(raw);
		this.lock = new ReentrantReadWriteLock();
		this.readLock = lock.readLock();
		this.writeLock = lock.writeLock();
	}

	public SynchronizedSet(Set<E> raw, ReentrantReadWriteLock lock) {
		this.raw = Objects.requireNonNull(raw);
		this.lock = Objects.requireNonNull(lock);
		this.readLock = lock.readLock();
		this.writeLock = lock.writeLock();
	}

	@Override
	public int size() {
		readLock.lock();
		try {return raw.size();} finally {readLock.unlock();}
	}

	@Override
	public boolean isEmpty() {
		readLock.lock();
		try {return raw.isEmpty();} finally {readLock.unlock();}
	}

	@Override
	public boolean contains(Object o) {
		readLock.lock();
		try {return raw.contains(o);} finally {readLock.unlock();}
	}

	@Override
	public Iterator<E> iterator() {
		readLock.lock();
		try {return new SynchronizedIterator<>(raw.iterator(), lock);} finally {readLock.unlock();}
	}

	@Override
	public Object[] toArray() {
		readLock.lock();
		try {return raw.toArray();} finally {readLock.unlock();}
	}

	@Override
	public <T> T[] toArray(T[] a) {
		readLock.lock();
		try {return raw.toArray(a);} finally {readLock.unlock();}
	}

	@Override
	public boolean add(E e) {
		writeLock.lock();
		try {return raw.add(e);} finally {writeLock.unlock();}
	}

	@Override
	public boolean remove(Object o) {
		writeLock.lock();
		try {return raw.remove(o);} finally {writeLock.unlock();}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		readLock.lock();
		try {return raw.containsAll(c);} finally {readLock.unlock();}
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		writeLock.lock();
		try {return raw.addAll(c);} finally {writeLock.unlock();}
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		writeLock.lock();
		try {return raw.retainAll(c);} finally {writeLock.unlock();}
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		writeLock.lock();
		try {return raw.removeAll(c);} finally {writeLock.unlock();}
	}

	@Override
	public void clear() {
		writeLock.lock();
		try {raw.clear();} finally {writeLock.unlock();}
	}
}
