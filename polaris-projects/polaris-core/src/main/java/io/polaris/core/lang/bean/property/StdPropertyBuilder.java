package io.polaris.core.lang.bean.property;

import io.polaris.core.lang.bean.Beans;

/**
 * @author Qt
 * @since  Dec 28, 2023
 */
public class StdPropertyBuilder<T> extends AbstractPropertyBuilder<T> implements PropertyBuilder<T> {
	private final T dest;

	public StdPropertyBuilder(T dest) {
		this.dest = dest;
	}

	public StdPropertyBuilder(Class<T> clazz) {
		try {
			this.dest = clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public StdPropertyBuilder(Object orig, T dest) {
		this(dest);
		from(orig);
	}

	@Override
	public void exec(Operation operation) {
		Object orig = operation.orig;
		if (orig != null) {
			Object val = Beans.getPathProperty(orig, operation.origProperty);
			if (val != null || !operation.ignoredNull) {
				Beans.setPathProperty(dest, operation.destProperty, val);
			}
		} else {
			if (operation.propertyValue != null || !operation.ignoredNull) {
				Beans.setPathProperty(dest, operation.destProperty, operation.propertyValue);
			}
		}
	}

	@Override
	public T done() {
		exec();
		return dest;
	}
}
