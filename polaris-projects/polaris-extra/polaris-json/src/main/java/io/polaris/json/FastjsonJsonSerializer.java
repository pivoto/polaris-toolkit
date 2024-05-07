package io.polaris.json;

import java.lang.reflect.Type;

import io.polaris.core.json.JsonSerializer;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.service.ServiceName;
import io.polaris.core.service.ServiceOrder;

/**
 * @author Qt
 * @since 1.8,  Feb 04, 2024
 */
@ServiceName("fastjson")
@ServiceOrder(10)
public class FastjsonJsonSerializer implements JsonSerializer {
	@Override
	public String serialize(Object value) {
		return Fastjsons.toJsonString(value);
	}

	@Override
	public <T> T deserialize(String json, Type type) {
		return Fastjsons.toJavaObject(json, type);
	}

	@Override
	public <T> T deserialize(String json, Class<? extends T> type) {
		return Fastjsons.toJavaObject(json, type);
	}

	@Override
	public <T> T deserialize(String json, TypeRef<T> type) {
		return Fastjsons.toJavaObject(json, type);
	}
}
