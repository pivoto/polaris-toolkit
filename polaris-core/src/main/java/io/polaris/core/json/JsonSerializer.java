package io.polaris.core.json;

import io.polaris.core.lang.TypeRef;
import io.polaris.core.service.ServiceLoadable;

import java.lang.reflect.Type;

/**
 * @author Qt
 * @since 1.8
 */
public interface JsonSerializer extends ServiceLoadable {

	String serialize(Object value);

	<T> T deserialize(String json, Type type);

	default <T> T deserialize(String json, Class<? extends T> type) {
		return deserialize(json, (Type) type);
	}

	default <T> T deserialize(String json, TypeRef<T> type) {
		return deserialize(json, type.getType());
	}


}
