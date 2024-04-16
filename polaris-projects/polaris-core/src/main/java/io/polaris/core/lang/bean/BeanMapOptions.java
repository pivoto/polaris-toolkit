package io.polaris.core.lang.bean;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.polaris.core.converter.Converters;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Qt
 * @since 1.8,  Apr 12, 2024
 */
@Accessors(chain = true, fluent = true)
@Setter
@Getter
public class BeanMapOptions {
	public static final BeanAccessMode DEFAULT_MODE = BeanAccessMode.INDEXED;
	public static final BiFunction<Type, Object, Object> DEFAULT_CONVERTER = Converters::convert;

	private BeanAccessMode mode = DEFAULT_MODE;
	private boolean includeFields = true;
	private boolean enableConverter = true;
	private boolean enableFallback = true;
	private boolean ignoreUnknownKeys = true;
	private boolean warnUnknownKeys = false;
	private boolean enableDefaultConverter = false;
	private BiFunction<Type, Object, Object> converter = DEFAULT_CONVERTER;
	private Function<String, Object> fallbackGetter;
	private BiConsumer<String, Object> fallbackSetter;

	public BeanMapOptions() {
	}

	public static BeanMapOptions newOptions() {
		return new BeanMapOptions();
	}

}
