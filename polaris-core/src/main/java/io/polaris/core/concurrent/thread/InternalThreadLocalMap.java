package io.polaris.core.concurrent.thread;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Qt
 */
public class InternalThreadLocalMap {
	// Reference: https://hg.openjdk.java.net/jdk8/jdk8/jdk/file/tip/src/share/classes/java/util/ArrayList.java#l229
	private static final int ARRAY_LIST_CAPACITY_MAX_SIZE = Integer.MAX_VALUE - 8;
	private static final int ARRAY_LIST_CAPACITY_EXPAND_THRESHOLD = 1 << 30;

	private static final AtomicInteger NEXT_INDEX = new AtomicInteger();
	private static ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = new ThreadLocal<>();
	static final Object UNSET = new Object();
	private Object[] indexedVariables;

	public static InternalThreadLocalMap getIfSet() {
		Thread thread = Thread.currentThread();
		if (thread instanceof InternalThreadSpec) {
			return ((InternalThreadSpec) thread).threadLocalMap();
		}
		return slowThreadLocalMap.get();
	}

	public static InternalThreadLocalMap get() {
		Thread thread = Thread.currentThread();
		if (thread instanceof InternalThreadSpec) {
			return fastGet((InternalThreadSpec) thread);
		}
		return slowGet();
	}

	public static InternalThreadLocalMap getAndRemove() {
		try {
			return getIfSet();
		} finally {
			remove();
		}
	}

	public static void set(InternalThreadLocalMap internalThreadLocalMap) {
		Thread thread = Thread.currentThread();
		if (thread instanceof InternalThreadSpec) {
			((InternalThreadSpec) thread).setThreadLocalMap(internalThreadLocalMap);
		}
		slowThreadLocalMap.set(internalThreadLocalMap);
	}
	public static void remove() {
		Thread thread = Thread.currentThread();
		if (thread instanceof InternalThreadSpec) {
			((InternalThreadSpec) thread).setThreadLocalMap(null);
		} else {
			slowThreadLocalMap.remove();
		}
	}
	public static void destroy() {
		slowThreadLocalMap = null;
	}

	public static int nextVariableIndex() {
		int index = NEXT_INDEX.getAndIncrement();
		if (index >= ARRAY_LIST_CAPACITY_MAX_SIZE || index < 0) {
			NEXT_INDEX.set(ARRAY_LIST_CAPACITY_MAX_SIZE);
			throw new IllegalStateException("Too many thread-local indexed variables");
		}
		return index;
	}

	public static int lastVariableIndex() {
		return NEXT_INDEX.get() - 1;
	}

	private InternalThreadLocalMap() {
		indexedVariables = newIndexedVariableTable();
	}

	public Object indexedVariable(int index) {
		Object[] lookup = indexedVariables;
		return index < lookup.length ? lookup[index] : UNSET;
	}

	/**
	 * @return {@code true} if and only if a new thread-local variable has been created
	 */
	public boolean setIndexedVariable(int index, Object value) {
		Object[] lookup = indexedVariables;
		if (index < lookup.length) {
			Object oldValue = lookup[index];
			lookup[index] = value;
			return oldValue == UNSET;
		} else {
			expandIndexedVariableTableAndSet(index, value);
			return true;
		}
	}
	public Object removeIndexedVariable(int index) {
		Object[] lookup = indexedVariables;
		if (index < lookup.length) {
			Object v = lookup[index];
			lookup[index] = UNSET;
			return v;
		} else {
			return UNSET;
		}
	}
	public int size() {
		int count = 0;
		for (Object o : indexedVariables) {
			if (o != UNSET) {
				++count;
			}
		}
		return count - 1;
	}
	private static Object[] newIndexedVariableTable() {
		int variableIndex = NEXT_INDEX.get();
		int newCapacity = variableIndex < 32 ? 32 : newCapacity(variableIndex);
		Object[] array = new Object[newCapacity];
		Arrays.fill(array, UNSET);
		return array;
	}

	private static int newCapacity(int index) {
		int newCapacity;
		if (index < ARRAY_LIST_CAPACITY_EXPAND_THRESHOLD) {
			newCapacity = index;
			newCapacity |= newCapacity >>>  1;
			newCapacity |= newCapacity >>>  2;
			newCapacity |= newCapacity >>>  4;
			newCapacity |= newCapacity >>>  8;
			newCapacity |= newCapacity >>> 16;
			newCapacity ++;
		} else {
			newCapacity = ARRAY_LIST_CAPACITY_MAX_SIZE;
		}
		return newCapacity;
	}

	private static InternalThreadLocalMap fastGet(InternalThreadSpec thread) {
		InternalThreadLocalMap threadLocalMap = thread.threadLocalMap();
		if (threadLocalMap == null) {
			thread.setThreadLocalMap(threadLocalMap = new InternalThreadLocalMap());
		}
		return threadLocalMap;
	}

	private static InternalThreadLocalMap slowGet() {
		ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = InternalThreadLocalMap.slowThreadLocalMap;
		InternalThreadLocalMap ret = slowThreadLocalMap.get();
		if (ret == null) {
			ret = new InternalThreadLocalMap();
			slowThreadLocalMap.set(ret);
		}
		return ret;
	}

	private void expandIndexedVariableTableAndSet(int index, Object value) {
		Object[] oldArray = indexedVariables;
		final int oldCapacity = oldArray.length;
		int newCapacity = newCapacity(index);
		Object[] newArray = Arrays.copyOf(oldArray, newCapacity);
		Arrays.fill(newArray, oldCapacity, newArray.length, UNSET);
		newArray[index] = value;
		indexedVariables = newArray;
	}

}
