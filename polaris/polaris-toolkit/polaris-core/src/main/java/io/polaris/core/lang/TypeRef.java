package io.polaris.core.lang;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Qt
 * @since 1.8
 */
public abstract class TypeRef<T> {

	protected final Type type;

	protected TypeRef() {
		Type superClass = getClass().getGenericSuperclass();
		type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
	}

	public Type getType() {
		return type;
	}

}
