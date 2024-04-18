package io.polaris.json;

import com.alibaba.fastjson2.reader.ObjectReader;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public abstract class BaseFastjsonObjectReader<T> implements ObjectReader<T> {
	protected Class<T> typeClass;

	public BaseFastjsonObjectReader() {
	}

	public BaseFastjsonObjectReader(Class<T> typeClass) {
		this.typeClass = typeClass;
	}

	protected T createBean(Type type, Supplier<T> supplier) {
		T bean = createBean(type);
		if (bean == null) {
			bean = supplier.get();
		}
		return bean;
	}

	protected T createBean(Type type) {
		T dto = null;
		if (type != null) {
			if (type instanceof ParameterizedType) {
				type = ((ParameterizedType) type).getRawType();
			}
			if (type instanceof Class) {
				try {
					dto = (T) ((Class<?>) type).newInstance();
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
		if (dto == null) {
			if (this.typeClass != null) {
				try {
					dto = (T) (typeClass).newInstance();
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
		return dto;
	}


}
