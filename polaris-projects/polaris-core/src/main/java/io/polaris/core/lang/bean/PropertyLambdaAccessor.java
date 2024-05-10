package io.polaris.core.lang.bean;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.Function;

import io.polaris.core.asm.reflect.BeanLambdaAccess;

/**
 * @author Qt
 * @since  Apr 12, 2024
 */
class PropertyLambdaAccessor implements PropertyAccessor {
	private final BeanLambdaAccess<?> access;
	private final Type type;
	private final Function<Object, Object> getter;
	private final BiConsumer<Object, Object> setter;

	PropertyLambdaAccessor(BeanLambdaAccess<?> access, Type type, Function<Object, Object> getter, BiConsumer<Object, Object> setter) {
		this.access = access;
		this.type = type;
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public Type type() {
		return type;
	}

	@Override
	public boolean hasSetter() {
		return setter != null;
	}

	@Override
	public boolean hasGetter() {
		return getter != null;
	}

	@Override
	public Object get(Object bean) {
		return getter.apply(bean);
	}

	@Override
	public void set(Object bean, Object val) {
		setter.accept(bean, val);
	}
}
