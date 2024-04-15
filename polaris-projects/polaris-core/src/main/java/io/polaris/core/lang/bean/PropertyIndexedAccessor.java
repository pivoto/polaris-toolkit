package io.polaris.core.lang.bean;

import java.lang.reflect.Type;

import io.polaris.core.asm.reflect.BeanAccess;

/**
 * @author Qt
 * @since 1.8,  Apr 12, 2024
 */
class PropertyIndexedAccessor implements PropertyAccessor {
	private final BeanAccess<?> access;
	private final Type type;
	private final int getter;
	private final int setter;

	PropertyIndexedAccessor(BeanAccess<?> access, Type type, int getter, int setter) {
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
		return setter >= 0;
	}

	@Override
	public boolean hasGetter() {
		return getter >= 0;
	}

	@Override
	public Object get(Object bean) {
		return access.getIndexProperty(bean, getter);
	}

	@Override
	public void set(Object bean, Object val) {
		access.setIndexProperty(bean, setter, val);
	}
}
