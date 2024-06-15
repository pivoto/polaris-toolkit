package io.polaris.core.lang.bean;

import java.lang.reflect.Type;

/**
 * @author Qt
 * @since  Apr 12, 2024
 */
public interface PropertyAccessor {

	Type type();

	boolean hasSetter();

	boolean hasGetter();

	Object get(Object bean);

	void set(Object bean, Object val);
}
