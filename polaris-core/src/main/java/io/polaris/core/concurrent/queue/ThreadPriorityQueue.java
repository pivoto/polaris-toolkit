package io.polaris.core.concurrent.queue;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author Qt
 * @since Jan 25, 2026
 */
public class ThreadPriorityQueue<E> implements BlockingQueue<E> {
	private final BlockingQueue<E> raw;
	private ThreadPoolExecutor executor;

	private ThreadPriorityQueue(BlockingQueue<E> raw) {
		this.raw = raw;
	}

	public static <E> ThreadPriorityQueue<E> create(BlockingQueue<E> raw) {
		return new ThreadPriorityQueue<>(raw);
	}

	public void bind(ThreadPoolExecutor executor) {
		this.executor = executor;
	}

	@Override
	public boolean add(E e) {
		if (executor != null) {
			int poolSize = executor.getPoolSize();
			// 存在空闲线程，可以直接入队由空闲线程处理
			if (executor.getActiveCount() < poolSize) {
				return raw.add(e);
			}
			// 线程池未达最大容量，优先扩容创建新线程而非入队
			if (poolSize < executor.getMaximumPoolSize()) {
				return false;
			}
		}
		return raw.add(e);
	}

	@Override
	public boolean offer(E e) {
		if (executor != null) {
			int poolSize = executor.getPoolSize();
			// 存在空闲线程，可以直接入队由空闲线程处理
			if (executor.getActiveCount() < poolSize) {
				return raw.offer(e);
			}
			// 线程池未达最大容量，优先扩容创建新线程而非入队
			if (poolSize < executor.getMaximumPoolSize()) {
				return false;
			}
		}
		return raw.offer(e);
	}

	@Override
	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		if (executor != null) {
			int poolSize = executor.getPoolSize();
			// 存在空闲线程，可以直接入队由空闲线程处理
			if (executor.getActiveCount() < poolSize) {
				return raw.offer(e, timeout, unit);
			}
			// 线程池未达最大容量，优先扩容创建新线程而非入队
			if (poolSize < executor.getMaximumPoolSize()) {
				return false;
			}
		}
		return raw.offer(e, timeout, unit);
	}

	@Override
	public void put(E e) throws InterruptedException {
		raw.put(e);
	}

	@Override
	public E take() throws InterruptedException {
		return raw.take();
	}

	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		return raw.poll(timeout, unit);
	}

	@Override
	public int remainingCapacity() {
		return raw.remainingCapacity();
	}

	@Override
	public boolean remove(Object o) {
		return raw.remove(o);
	}

	@Override
	public boolean contains(Object o) {
		return raw.contains(o);
	}

	@Override
	public int drainTo(Collection<? super E> c) {
		return raw.drainTo(c);
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		return raw.drainTo(c, maxElements);
	}

	@Override
	public E remove() {
		return raw.remove();
	}

	@Override
	public E poll() {
		return raw.poll();
	}

	@Override
	public E element() {
		return raw.element();
	}

	@Override
	public E peek() {
		return raw.peek();
	}

	@Override
	public int size() {
		return raw.size();
	}

	@Override
	public boolean isEmpty() {
		return raw.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return raw.iterator();
	}

	@Override
	public Object[] toArray() {
		return raw.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return raw.toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return raw.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return raw.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return raw.removeAll(c);
	}

	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		return raw.removeIf(filter);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return raw.retainAll(c);
	}

	@Override
	public void clear() {
		raw.clear();
	}

	@Override
	public Spliterator<E> spliterator() {
		return raw.spliterator();
	}

	@Override
	public Stream<E> stream() {
		return raw.stream();
	}

	@Override
	public Stream<E> parallelStream() {
		return raw.parallelStream();
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		raw.forEach(action);
	}
}
