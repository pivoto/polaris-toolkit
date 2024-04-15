package io.polaris.core.lang.bean;

import java.lang.reflect.Type;

import io.polaris.core.asm.reflect.BeanAccess;

/**
 * @author Qt
 * @since 1.8,  Apr 12, 2024
 */
class PropertyFieldIndexedAccessor implements PropertyAccessor {
	private final BeanAccess<?> access;
	private final Type type;
	private final int index;

	PropertyFieldIndexedAccessor(BeanAccess<?> access, Type type, int index) {
		assert index >= 0;
		this.access = access;
		this.type = type;
		this.index = index;
	}

	@Override
	public Type type() {
		return type;
	}

	@Override
	public boolean hasSetter() {
		return true;
	}

	@Override
	public boolean hasGetter() {
		return true;
	}

	@Override
	public Object get(Object bean) {
		return access.getIndexField(bean, index);
	}

	@Override
	public void set(Object bean, Object val) {
		access.setIndexField(bean, index, val);
	}
}
