package io.polaris.core.lang.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8,  Aug 06, 2023
 */
@Accessors(chain = true, fluent = true)
@Setter
@Getter
public class BeanMapBuilder<T> {
	private boolean compilable = true;
	private boolean warnUnknownKeys = false;
	private boolean ignoreUnknownKeys = true;
	private T bean;
	private Class<?> beanType;
	private BiFunction<Object, Type, Object> converter;
	private Function<String, Object> fallbackGetter;
	private BiConsumer<String, Object> fallbackSetter;

	public BeanMapBuilder() {
	}

	public BeanMapBuilder(T bean) {
		this.bean = bean;
	}

	public BeanMap<T> build() {
		return new BeanMap<>(bean, beanType, converter, fallbackGetter, fallbackSetter, ignoreUnknownKeys, compilable, warnUnknownKeys);
	}


}
