package io.polaris.core.json;

import java.lang.reflect.Type;
import java.util.List;

import javax.annotation.Nullable;

import io.polaris.core.lang.TypeRef;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.service.Service;
import io.polaris.core.service.ServiceLoader;
import io.polaris.core.service.SpiLoaders;
import io.polaris.core.service.StatefulServiceLoader;
import io.polaris.core.tuple.ValueRef;

/**
 * @author Qt
 * @since  Feb 04, 2024
 */
public class Jsons {
	private static final Logger log = Loggers.of(Jsons.class);
	private static volatile ValueRef<JsonSerializer> ref;

	@Nullable
	public static JsonSerializer getJsonSerializer() {
		if (ref != null) {
			return ref.get();
		}
		synchronized (Jsons.class) {
			if (ref != null) {
				return ref.get();
			}
			ref = new ValueRef<>(loadJsonSerializer());
			return ref.get();
		}
	}

	@Nullable
	private static JsonSerializer loadJsonSerializer() {
		try {
			ServiceLoader<JsonSerializer> serviceLoader = SpiLoaders.loadStateful(JsonSerializer.class).serviceLoader();
			List<Service<JsonSerializer>> providers = serviceLoader.getProviders();
			for (Service<JsonSerializer> provider : providers) {
				try {
					JsonSerializer jsonSerializer = provider.getSingleton();
					return jsonSerializer;
				} catch (Throwable e) {
					log.error(e, "Failed to load json serializer");
				}
			}
		} catch (Throwable e) {
			log.error(e, "Failed to load json serializer");
		}
		return null;
	}

	public static String serialize(Object value) {
		JsonSerializer jsonSerializer = getJsonSerializer();
		if (jsonSerializer != null) {
			return jsonSerializer.serialize(value);
		}
		throw new UnsupportedOperationException();
	}

	public static <T> T deserialize(String json, Type type) {
		JsonSerializer jsonSerializer = getJsonSerializer();
		if (jsonSerializer != null) {
			return jsonSerializer.deserialize(json, type);
		}
		throw new UnsupportedOperationException();
	}

	public static <T> T deserialize(String json, Class<? extends T> type) {
		JsonSerializer jsonSerializer = getJsonSerializer();
		if (jsonSerializer != null) {
			return jsonSerializer.deserialize(json, type);
		}
		throw new UnsupportedOperationException();
	}

	public static <T> T deserialize(String json, TypeRef<T> type) {
		JsonSerializer jsonSerializer = getJsonSerializer();
		if (jsonSerializer != null) {
			return jsonSerializer.deserialize(json, type);
		}
		throw new UnsupportedOperationException();
	}

}
