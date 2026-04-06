package io.polaris.core.collection.sync;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.UnaryOperator;

/**
 * @author Qt
 * @since Apr 05, 2026
 */
public class SynchronizedList<E> implements List<E> {
	private final List<E> raw;
	private final ReentrantReadWriteLock lock;
	private final ReentrantReadWriteLock.ReadLock readLock;
	private final ReentrantReadWriteLock.WriteLock writeLock;

	public SynchronizedList(List<E> raw) {
		this.raw = Objects.requireNonNull(raw);
		this.lock = new ReentrantReadWriteLock();
		this.readLock = lock.readLock();
		this.writeLock = lock.writeLock();
	}

	public SynchronizedList(List<E> raw, ReentrantReadWriteLock lock) {
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
	public boolean addAll(int index, Collection<? extends E> c) {
		writeLock.lock();
		try {return raw.addAll(index, c);} finally {writeLock.unlock();}
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		writeLock.lock();
		try {return raw.removeAll(c);} finally {writeLock.unlock();}
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		writeLock.lock();
		try {return raw.retainAll(c);} finally {writeLock.unlock();}
	}

	@Override
	public void replaceAll(UnaryOperator<E> operator) {
		writeLock.lock();
		try {raw.replaceAll(operator);} finally {writeLock.unlock();}
	}

	@Override
	public void sort(Comparator<? super E> c) {
		writeLock.lock();
		try {raw.sort(c);} finally {writeLock.unlock();}
	}

	@Override
	public void clear() {
		writeLock.lock();
		try {raw.clear();} finally {writeLock.unlock();}
	}

	@Override
	public E get(int index) {
		readLock.lock();
		try {return raw.get(index);} finally {readLock.unlock();}
	}

	@Override
	public E set(int index, E element) {
		writeLock.lock();
		try {return raw.set(index, element);} finally {writeLock.unlock();}
	}

	@Override
	public void add(int index, E element) {
		writeLock.lock();
		try {raw.add(index, element);} finally {writeLock.unlock();}
	}

	@Override
	public E remove(int index) {
		writeLock.lock();
		try {return raw.remove(index);} finally {writeLock.unlock();}
	}

	@Override
	public int indexOf(Object o) {
		readLock.lock();
		try {return raw.indexOf(o);} finally {readLock.unlock();}
	}

	@Override
	public int lastIndexOf(Object o) {
		readLock.lock();
		try {return raw.lastIndexOf(o);} finally {readLock.unlock();}
	}

	@Override
	public ListIterator<E> listIterator() {
		readLock.lock();
		try {return new SynchronizedListIterator<>(raw.listIterator(), lock);} finally {readLock.unlock();}
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		readLock.lock();
		try {return new SynchronizedListIterator<>(raw.listIterator(index), lock);} finally {readLock.unlock();}
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		readLock.lock();
		try {return new SynchronizedList<>(raw.subList(fromIndex, toIndex));} finally {readLock.unlock();}
	}

}
