package io.polaris.core.converter.support;

import java.lang.reflect.Type;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.jdbc.sql.VarRef;
import io.polaris.core.lang.JavaType;

/**
 * @author Qt
 * @since 1.8
 */
public class VarRefConverter<T> extends AbstractSimpleConverter<VarRef<T>> {
	private final JavaType<VarRef<T>> targetType = JavaType.of((Type) VarRef.class);

	@Override
	public JavaType<VarRef<T>> getTargetType() {
		return targetType;
	}

	@SuppressWarnings({"unchecked"})
	@Override
	protected VarRef<T> doConvert(Object value, JavaType<VarRef<T>> targetType) {
		return (VarRef<T>) VarRef.of(value);
	}
}
