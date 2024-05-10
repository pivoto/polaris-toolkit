package io.polaris.core.lang.bean;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Qt
 * @since  Aug 05, 2023
 */
public class BeanMetadataV1Impl implements BeanMetadataV1 {
	@Override
	public Map<String, Type> types() {
		return BeanMetadatasV1.getPropertyTypes(BeanMetadatasV1.class);
	}

	@Override
	public Map<String, Function<Object, Object>> getters() {
		return null;
	}

	@Override
	public Map<String, BiConsumer<Object, Object>> setters() {
		return null;
	}
}
