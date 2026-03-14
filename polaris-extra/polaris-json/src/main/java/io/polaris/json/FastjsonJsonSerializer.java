package io.polaris.json;

import java.lang.reflect.Type;

import io.polaris.core.json.JsonSerializer;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.service.SpiName;
import io.polaris.core.service.SpiOrder;

/**
 * @author Qt
 * @since Feb 04, 2024
 */
@SpiName("fastjson")
@SpiOrder(10)
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
