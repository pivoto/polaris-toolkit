package io.polaris.json;

import java.lang.reflect.Constructor;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;

/**
 * @author Qt
 * @since 1.8,  Feb 04, 2024
 */
public class CustomHandlerInstantiator extends HandlerInstantiator {
	@Override
	public JsonDeserializer<?> deserializerInstance(DeserializationConfig config, Annotated annotated, Class<?> deserClass) {
		return (JsonDeserializer<?>) create(annotated, deserClass);
	}

	private Object create(Annotated annotated, Class<?> clazz) {
		try {
			Constructor<?> c = clazz.getConstructor(Annotated.class);
			return c.newInstance(annotated);
		} catch (Exception ignored) {
		}
		try {
			JavaType type = annotated.getType();
			Constructor<?> c = clazz.getConstructor(JavaType.class);
			return c.newInstance(type);
		} catch (Exception ignored) {
		}
		try {
			Class<?> rawType = annotated.getRawType();
			Constructor<?> c = clazz.getConstructor(Class.class);
			return c.newInstance(rawType);
		} catch (Exception ignored) {
		}
		try {
			Constructor<?> c = clazz.getConstructor();
			return c.newInstance();
		} catch (Exception ignored) {
		}
		return null;
	}

	@Override
	public KeyDeserializer keyDeserializerInstance(DeserializationConfig config, Annotated annotated, Class<?> keyDeserClass) {
		return (KeyDeserializer) create(annotated, keyDeserClass);
	}

	@Override
	public JsonSerializer<?> serializerInstance(SerializationConfig config, Annotated annotated, Class<?> serClass) {
		return (JsonSerializer<?>) create(annotated, serClass);
	}

	@Override
	public TypeResolverBuilder<?> typeResolverBuilderInstance(MapperConfig<?> config, Annotated annotated, Class<?> builderClass) {
		return (TypeResolverBuilder<?>) create(annotated, builderClass);
	}

	@Override
	public TypeIdResolver typeIdResolverInstance(MapperConfig<?> config, Annotated annotated, Class<?> resolverClass) {
		return (TypeIdResolver) create(annotated, resolverClass);
	}
}
