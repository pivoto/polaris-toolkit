package io.polaris.core.lang.bean.impl;

import io.polaris.core.lang.bean.BeanPropertyBuilder;
import io.polaris.core.lang.bean.Beans;

/**
 * @author Qt
 * @since 1.8,  Dec 28, 2023
 */
public class StdBeanPropertyBuilder<T> extends AbstractBeanPropertyBuilder<T> implements BeanPropertyBuilder<T> {
	private T dest;

	public StdBeanPropertyBuilder(T dest) {
		this.dest = dest;
	}

	public StdBeanPropertyBuilder(Class<T> clazz) {
		try {
			this.dest = clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public StdBeanPropertyBuilder(Object orig, T dest) {
		this(dest);
		from(orig);
	}

	public void exec(Seriation seriation) {
		Object orig = seriation.orig;
		if (orig != null) {
			Object val = Beans.getPathProperty(orig, seriation.origProperty);
			if (val != null || !seriation.ignoredNull) {
				Beans.setPathProperty(dest, seriation.destProperty, val);
			}
		} else {
			if (seriation.propertyValue != null || !seriation.ignoredNull) {
				Beans.setPathProperty(dest, seriation.destProperty, seriation.propertyValue);
			}
		}
	}

	@Override
	public T done() {
		exec();
		return dest;
	}
}
