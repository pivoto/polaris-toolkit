package io.polaris.core.concurrent.thread;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import io.polaris.core.concurrent.ThreadLocalSpec;

/**
 * @author Qt
 */
public class InternalThreadLocal<V> implements ThreadLocalSpec<V> {
	private static final int VARIABLES_TO_REMOVE_INDEX = InternalThreadLocalMap.nextVariableIndex();
	private final int index;
	private final String name;

	public InternalThreadLocal(String name) {
		this.index = InternalThreadLocalMap.nextVariableIndex();
		this.name = name;
	}

	public InternalThreadLocal() {
		this("");
	}

	public static <T> ThreadLocalSpec<T> ofInitial(String name, Supplier<? extends T> supplier) {
		return new InternalThreadLocal.SuppliedThreadLocal<>(name, supplier);
	}

	public static <T> ThreadLocalSpec<T> ofInitial(Supplier<? extends T> supplier) {
		return new InternalThreadLocal.SuppliedThreadLocal<>(supplier);
	}

	@SuppressWarnings("unchecked")
	public static void removeAll() {
		InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.getIfSet();
		if (threadLocalMap == null) {
			return;
		}

		try {
			Object v = threadLocalMap.indexedVariable(VARIABLES_TO_REMOVE_INDEX);
			if (v != null && v != InternalThreadLocalMap.UNSET) {
				Set<InternalThreadLocal<?>> variablesToRemove = (Set<InternalThreadLocal<?>>) v;
				InternalThreadLocal<?>[] variablesToRemoveArray =
					variablesToRemove.toArray(new InternalThreadLocal[0]);
				for (InternalThreadLocal<?> tlv : variablesToRemoveArray) {
					tlv.remove(threadLocalMap);
				}
			}
		} finally {
			InternalThreadLocalMap.remove();
		}
	}

	public static int size() {
		InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.getIfSet();
		if (threadLocalMap == null) {
			return 0;
		} else {
			return threadLocalMap.size();
		}
	}

	public static void destroy() {
		InternalThreadLocalMap.destroy();
	}

	@SuppressWarnings("unchecked")
	private static void addToVariablesToRemove(InternalThreadLocalMap threadLocalMap, InternalThreadLocal<?> variable) {
		Object v = threadLocalMap.indexedVariable(VARIABLES_TO_REMOVE_INDEX);
		Set<InternalThreadLocal<?>> variablesToRemove;
		if (v == InternalThreadLocalMap.UNSET || v == null) {
			variablesToRemove = Collections.newSetFromMap(new IdentityHashMap<>());
			threadLocalMap.setIndexedVariable(VARIABLES_TO_REMOVE_INDEX, variablesToRemove);
		} else {
			variablesToRemove = (Set<InternalThreadLocal<?>>) v;
		}

		variablesToRemove.add(variable);
	}

	@SuppressWarnings("unchecked")
	private static void removeFromVariablesToRemove(InternalThreadLocalMap threadLocalMap, InternalThreadLocal<?> variable) {

		Object v = threadLocalMap.indexedVariable(VARIABLES_TO_REMOVE_INDEX);

		if (v == InternalThreadLocalMap.UNSET || v == null) {
			return;
		}

		Set<InternalThreadLocal<?>> variablesToRemove = (Set<InternalThreadLocal<?>>) v;
		variablesToRemove.remove(variable);
	}

	@Override
	@SuppressWarnings("unchecked")
	public final V get() {
		InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.get();
		Object v = threadLocalMap.indexedVariable(index);
		if (v != InternalThreadLocalMap.UNSET) {
			return (V) v;
		}

		return initialize(threadLocalMap);
	}

	@SuppressWarnings("unchecked")
	public final V getWithoutInitialize() {
		InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.get();
		Object v = threadLocalMap.indexedVariable(index);
		if (v != InternalThreadLocalMap.UNSET) {
			return (V) v;
		}
		return null;
	}

	private V initialize(InternalThreadLocalMap threadLocalMap) {
		V v;
		try {
			v = initialValue();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		threadLocalMap.setIndexedVariable(index, v);
		addToVariablesToRemove(threadLocalMap, this);
		return v;
	}

	@Override
	public final void set(V value) {
		if (value == null || value == InternalThreadLocalMap.UNSET) {
			remove();
		} else {
			InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.get();
			if (threadLocalMap.setIndexedVariable(index, value)) {
				addToVariablesToRemove(threadLocalMap, this);
			}
		}
	}

	@Override
	public final void remove() {
		remove(InternalThreadLocalMap.getIfSet());
	}

	@SuppressWarnings("unchecked")
	public final void remove(InternalThreadLocalMap threadLocalMap) {
		if (threadLocalMap == null) {
			return;
		}

		Object v = threadLocalMap.removeIndexedVariable(index);
		removeFromVariablesToRemove(threadLocalMap, this);

		if (v != InternalThreadLocalMap.UNSET) {
			try {
				onRemoval((V) v);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	protected V initialValue() {
		return null;
	}

	protected void onRemoval(@SuppressWarnings("unused") V value) {
	}


	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	static final class SuppliedThreadLocal<T> extends InternalThreadLocal<T> {

		private final Supplier<? extends T> supplier;

		SuppliedThreadLocal(String name, Supplier<? extends T> supplier) {
			super(name);
			this.supplier = Objects.requireNonNull(supplier);
		}

		SuppliedThreadLocal(Supplier<? extends T> supplier) {
			this.supplier = Objects.requireNonNull(supplier);
		}

		@Override
		protected T initialValue() {
			return supplier.get();
		}
	}
}
