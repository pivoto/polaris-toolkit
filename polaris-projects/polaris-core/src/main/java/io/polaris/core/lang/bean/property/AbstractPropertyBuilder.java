package io.polaris.core.lang.bean.property;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import io.polaris.core.lang.bean.PropertyAccessor;
import io.polaris.core.lang.bean.Beans;

/**
 * @author Qt
 * @since 1.8,  Dec 28, 2023
 */
public abstract class AbstractPropertyBuilder<T> implements PropertyBuilder<T> {
	private final Deque<Operation> actionQueue = new ArrayDeque<>();
	protected Object lastOrig;
	protected boolean ignoredNull = true;

	private Operation newOperation(String origProperty, String destProperty, Object value) {
		Operation operation = new Operation();
		if (origProperty != null) {// 需要从orig对象取值
			operation.orig = lastOrig;
			operation.origProperty = origProperty;
		}
		operation.destProperty = destProperty;
		operation.propertyValue = value;
		operation.ignoredNull = ignoredNull;
		return operation;
	}

	@Override
	public PropertyBuilder<T> from(Object orig) {
		lastOrig = orig;
		return this;
	}

	@Override
	public PropertyBuilder<T> ignoreNull(boolean ignored) {
		ignoredNull = ignored;
		return this;
	}

	@Override
	public PropertyBuilder<T> set(String destProperty, Object value) {
		Operation operation = newOperation(null, destProperty, value);
		actionQueue.add(operation);
		return this;
	}

	@Override
	public PropertyBuilder<T> mapAll() {
		if (lastOrig != null) {
			mapAll(lastOrig.getClass());
		}
		return this;
	}

	@Override
	public PropertyBuilder<T> mapAll(Class<?> clazz) {
		Map<String, PropertyAccessor> metadata = Beans.getIndexedPropertyAccessors(clazz);
		for (String name : metadata.keySet()) {
			map(name, name);
		}
		return this;
	}

	@Override
	public PropertyBuilder<T> map(String origProperty, String destProperty) {
		if (lastOrig != null) {
			Operation operation = newOperation(origProperty, destProperty, null);
			actionQueue.add(operation);
		}
		return this;
	}

	@Override
	public PropertyBuilder<T> exec() {
		Operation operation = null;
		while ((operation = actionQueue.poll()) != null) {
			exec(operation);
		}
		return this;
	}

	protected abstract void exec(Operation operation);


	/**
	 * @author Qt
	 * @since 1.8,  Dec 28, 2023
	 */
	protected static class Operation {
		Object orig;
		String origProperty;
		String destProperty;
		Object propertyValue;
		boolean ignoredNull = true;
	}
}
