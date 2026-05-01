package io.polaris.json;

import java.util.function.Supplier;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;

/**
 * @author Qt
 * @since Feb 04, 2024
 */
public abstract class BaseJacksonSerializer<T> extends JsonSerializer<T> {

	protected final JavaType javaType;
	protected final Class<T> rawClass;

	@SuppressWarnings("unchecked")
	public BaseJacksonSerializer(Annotated annotated) {
		if (annotated == null) {
			this.javaType = null;
			this.rawClass = null;
		} else {
			JavaType javaType = annotated.getType();
			Class<?> rawClass = annotated.getRawType();
			if (rawClass == void.class) {
				if (annotated instanceof AnnotatedWithParams) {
					int count = ((AnnotatedWithParams) annotated).getParameterCount();
					if (count == 1) {
						javaType = ((AnnotatedWithParams) annotated).getParameterType(0);
						rawClass = ((AnnotatedWithParams) annotated).getRawParameterType(0);
					}
				}
			}
			this.javaType = javaType;
			this.rawClass = (Class<T>) rawClass;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> handledType() {
		if (javaType != null) {
			return (Class<T>) javaType.getRawClass();
		}
		return rawClass;
	}

	@SuppressWarnings("unchecked")
	protected T createBean(Supplier<T> supplier) {
		try {
			Class<?> rawClass = handledType();
			if (rawClass != null) {
				return (T) rawClass.newInstance();
			}
			return supplier.get();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	protected T createBean() {
		try {
			Class<?> rawClass = handledType();
			if (rawClass != null) {
				return (T) rawClass.newInstance();
			}
			return null;
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}

