package io.polaris.json;

import java.lang.reflect.Type;

import io.polaris.core.json.JsonSerializer;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.service.ServiceName;

/**
 * @author Qt
 * @since 1.8,  Feb 04, 2024
 */
@ServiceName("jackson")
public class JacksonJsonSerializer implements JsonSerializer {
	@Override
	public String serialize(Object value) {
		return Jacksons.toJsonString(value);
	}

	@Override
	public <T> T deserialize(String json, Type type) {
		return Jacksons.toJavaObject(json, type);
	}

	@Override
	public <T> T deserialize(String json, Class<? extends T> type) {
		return Jacksons.toJavaObject(json, type);
	}

	@Override
	public <T> T deserialize(String json, TypeRef<T> type) {
		return Jacksons.toJavaObject(json, type);
	}
}
