package io.polaris.core.json;

import java.lang.reflect.Type;

import io.polaris.core.lang.TypeRef;
import io.polaris.core.spi.SpiLoadable;

/**
 * @author Qt
 */
public interface JsonSerializer extends SpiLoadable {

	String serialize(Object value);

	<T> T deserialize(String json, Type type);

	default <T> T deserialize(String json, Class<? extends T> type) {
		return deserialize(json, (Type) type);
	}

	default <T> T deserialize(String json, TypeRef<T> type) {
		return deserialize(json, type.getType());
	}


}
