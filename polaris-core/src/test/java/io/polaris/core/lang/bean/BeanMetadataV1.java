package io.polaris.core.lang.bean;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Qt
 * @since  Aug 03, 2023
 */
public interface BeanMetadataV1 {

	Map<String, Type> types();

	Map<String, Function<Object, Object>> getters();

	Map<String, BiConsumer<Object, Object>> setters();

}
