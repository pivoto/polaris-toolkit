package io.polaris.core.json;

import java.lang.reflect.Type;

import io.polaris.core.lang.TypeRef;
import io.polaris.core.spi.Spi;
import io.polaris.core.spi.SpiLoadable;
import io.polaris.core.spi.StatefulSpiLoadable;

/**
 * @author Qt
 */
@Spi("JSON序列化器")
public interface JsonSerializer extends StatefulSpiLoadable {

	String serialize(Object value);

	<T> T deserialize(String json, Type type);

	default <T> T deserialize(String json, Class<? extends T> type) {
		return deserialize(json, (Type) type);
	}

	default <T> T deserialize(String json, TypeRef<T> type) {
		return deserialize(json, type.getType());
	}


}
