package io.polaris.core.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * @author Qt
 */
public class Lists {

	public static <E> List<E> newArrayList() {
		return new ArrayList<>();
	}

	public static <E> List<E> newLinkedList() {
		return new LinkedList<>();
	}

	public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
		return new CopyOnWriteArrayList<>();
	}

	public static <E> List<E> asList(Collection<E> collection) {
		return new ArrayList<>(collection);
	}

	public static <E> List<E> asList(Enumeration<E> enumeration) {
		List<E> c = new ArrayList<>();
		while (enumeration.hasMoreElements()) {
			c.add(enumeration.nextElement());
		}
		return c;
	}

	@SafeVarargs
	public static <E> List<E> asList(E... iterable) {
		List<E> c = new ArrayList<>();
		Collections.addAll(c, iterable);
		return c;
	}


	public static <E> List<E> asList(Iterable<E> iterable) {
		List<E> c = new ArrayList<>();
		for (E e : iterable) {
			c.add(e);
		}
		return c;
	}

	public static <E> List<E> asList(Iterator<E> iterator) {
		List<E> c = new ArrayList<>();
		while (iterator.hasNext()) {
			c.add(iterator.next());
		}
		return c;
	}

	public static <E> List<E> asList(Supplier<List<E>> supplier, Enumeration<E> enumeration) {
		List<E> c = supplier.get();
		while (enumeration.hasMoreElements()) {
			c.add(enumeration.nextElement());
		}
		return c;
	}

	@SafeVarargs
	public static <E> List<E> asList(Supplier<List<E>> supplier, E... iterable) {
		List<E> c = supplier.get();
		Collections.addAll(c, iterable);
		return c;
	}


	public static <E> List<E> asList(Supplier<List<E>> supplier, Iterable<E> iterable) {
		List<E> c = supplier.get();
		for (E e : iterable) {
			c.add(e);
		}
		return c;
	}

	public static <E> List<E> asList(Supplier<List<E>> supplier, Iterator<E> iterator) {
		List<E> c = supplier.get();
		while (iterator.hasNext()) {
			c.add(iterator.next());
		}
		return c;
	}


}
