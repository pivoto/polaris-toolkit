package io.polaris.core.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.polaris.core.lang.JavaType;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Qt
 * @since 1.8
 */
public class TestJsonSerializer implements JsonSerializer {
	private static final ObjectMapper MAPPER = buildObjectMapper();

	public static ObjectMapper defaultObjectMapper() {
		return MAPPER;
	}

	public static ObjectMapper buildObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.disable(SerializationFeature.FAIL_ON_SELF_REFERENCES);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);

		mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
		mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
		mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
		mapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
		mapper.enable(JsonParser.Feature.IGNORE_UNDEFINED);
		mapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
		mapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);

		// JsonInclude
		//mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);

		SimpleModule simpleModule = new SimpleModule();
		// number -> String
		simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
		simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
		simpleModule.addSerializer(BigDecimal.class, ToStringSerializer.instance);
		simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
		mapper.registerModule(simpleModule);
		return mapper;
	}

	@Override
	public String serialize(Object value) {
		try {
			return MAPPER.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new UnsupportedOperationException(e);
		}
//		return JSON.toJSONString(value);
	}

	@Override
	public <T> T deserialize(String json, Class<? extends T> type) {
		try {
			return MAPPER.readValue(json, type);
		} catch (JsonProcessingException e) {
			throw new UnsupportedOperationException(e);
		}
//		return JSON.parseObject(json, targetType);
	}

	@Override
	public <T> T deserialize(String json, Type type) {
		try {
			if (type instanceof JavaType) {
				type = ((JavaType<?>) type).getRawType();
			}
			return MAPPER.readValue(json, MAPPER.constructType(type));
		} catch (JsonProcessingException e) {
			throw new UnsupportedOperationException(e);
		}
//		return JSON.parseObject(json, targetType);
	}
}
