package io.polaris.core.lang.bean;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8,  Aug 05, 2023
 */
public class BeanMetadataImpl implements BeanMetadata{
	@Override
	public Map<String, Type> types() {
		return BeanMetadatas.getPropertyTypes(BeanMetadatas.class);
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
