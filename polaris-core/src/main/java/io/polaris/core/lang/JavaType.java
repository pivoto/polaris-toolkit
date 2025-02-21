package io.polaris.core.lang;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;

/**
 * @author Qt
 * @since 1.8
 */
public class JavaType<T> implements Type {
	private static class CacheHolder {
		private static final Map<Type, JavaType> cache = Collections.synchronizedMap(new WeakHashMap<>());
	}

	private final Class<T> clazz;
	private final Type type;
	private Map<TypeVariable<?>, Type> typeVariableMap;

	protected JavaType(Type type) {
		this.type = type;
		this.clazz = (Class<T>) Types.getClass(type);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Nonnull
	public static <T> JavaType<T> of(@Nonnull Type type) {
		if (type instanceof JavaType) {
			return of(((JavaType<?>) type).getRawType());
		}
		Map<Type, JavaType> cache = CacheHolder.cache;
		JavaType<T> javaType = cache.get(type);
		if (javaType != null) {
			return javaType;
		}
		synchronized (cache) {
			javaType = cache.get(type);
			if (javaType == null) {
				cache.put(type, javaType = new JavaType<>(type));
			}
		}
		return javaType;
	}

	@Nonnull
	public static <T> JavaType<T> of(@Nonnull Class<T> clazz) {
		return JavaType.of((Type) clazz);
	}

	@Nonnull
	public static <T> JavaType<T> of(@Nonnull TypeRef<T> type) {
		return JavaType.of((Type) type.getType());
	}

	@Nonnull
	public static <T> JavaType<T> of(@Nonnull String typeName) throws ClassNotFoundException {
		return JavaType.of((Type) TypeRefs.getType(typeName));
	}

	public Class<T> getRawClass() {
		return clazz;
	}

	public Type getRawType() {
		return type;
	}

	@Nonnull
	public Type[] getTypeArguments() {
		return Types.getTypeArguments(type);
	}

	public boolean isInstance(Object obj) {
		return clazz.isInstance(obj);
	}

	public boolean isEnum() {
		return clazz.isEnum();
	}

	public boolean isArray() {
		return clazz.isArray();
	}

	public T cast(Object obj) {
		return clazz.cast(obj);
	}

	@Nonnull
	public Map<TypeVariable<?>, Type> getTypeVariableMap() {
		if (typeVariableMap == null) {
			typeVariableMap = Types.getTypeVariableMap(type);
		}
		return typeVariableMap;
	}

	@Nonnull
	public Type getActualType(TypeVariable<?> typeVariable) {
		return getTypeVariableMap().getOrDefault(typeVariable, Object.class);
	}


	@Nonnull
	public Type getActualType(Class<?> genericClass, int typeParameterIndex) {
		TypeVariable<? extends Class<?>>[] typeParameters = genericClass.getTypeParameters();
		if (typeParameters.length <= typeParameterIndex) {
			throw new IndexOutOfBoundsException();
		}
		TypeVariable<?> variable = typeParameters[typeParameterIndex];
		return getActualType(variable);
	}

	public Type getArrayComponentType() {
		if (type instanceof GenericArrayType) {
			return ((GenericArrayType) type).getGenericComponentType();
		} else {
			return getRawClass().getComponentType();
		}
	}

	@Override
	public String getTypeName() {
		return type instanceof Class ? ((Class<?>) type).getCanonicalName() : type.getTypeName();
	}

	@Override
	public String toString() {
		return "JavaType:" + getTypeName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		JavaType<?> javaType = (JavaType<?>) o;
		return Objects.equals(type, javaType.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type);
	}
}
