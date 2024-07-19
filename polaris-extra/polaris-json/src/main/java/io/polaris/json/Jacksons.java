package io.polaris.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import java.util.TimeZone;

import io.polaris.core.lang.TypeRef;
import io.polaris.core.service.StatefulServiceLoader;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author Qt
 * @since  Feb 04, 2024
 */
@Slf4j
public class Jacksons {
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

		// 使用系统默认时区
		mapper.setTimeZone(TimeZone.getDefault());
		mapper.setLocale(Locale.getDefault());

		// JsonInclude
		//mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);

		SimpleModule simpleModule = new SimpleModule();
		// number -> String
		simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
		simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
		simpleModule.addSerializer(BigDecimal.class, ToStringSerializer.instance);
		simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
		mapper.registerModule(simpleModule);
		mapper.setHandlerInstantiator(new CustomHandlerInstantiator());

		for (IJacksonCustomizer customizer : StatefulServiceLoader.load(IJacksonCustomizer.class)) {
			try {
				customizer.customize(mapper);
			} catch (Throwable e) {
				log.error("", e);
			}
		}
		return mapper;
	}

	public static byte[] toJsonBytes(Object target) {
		try {
			// UTF-8
			return MAPPER.writeValueAsBytes(target);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
		/*String json = toJsonString(target);
		return json == null ? new byte[0] : json.getBytes(StandardCharsets.UTF_8);*/
	}

	public static String toJsonString(Object target) {
		try {
			return MAPPER.writeValueAsString(target);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String toJsonStringOrNull(Object target) {
		try {
			return MAPPER.writeValueAsString(target);
		} catch (Throwable e) {
			log.error("json序列化失败", e);
			return null;
		}
	}

	public static String toJsonPrettyString(Object target) {
		try {
			return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(target);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String toJsonPrettyStringOrNull(Object target) {
		try {
			return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(target);
		} catch (Throwable e) {
			log.error("json序列化失败", e);
			return null;
		}
	}

	public static String toJsonOrJavaString(Object target) {
		try {
			return MAPPER.writeValueAsString(target);
		} catch (Throwable e) {
			return target == null ? null : target.toString();
		}
	}

	public static JsonNode toJsonTree(Object target) {
		try {
			return MAPPER.valueToTree(target);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static JsonNode toJsonTreeOrNull(Object target) {
		try {
			return MAPPER.valueToTree(target);
		} catch (Throwable e) {
			log.error("json序列化失败", e);
			return null;
		}
	}

	public static <T> T toJavaObject(String json, TypeReference<T> type) {
		try {
			return MAPPER.readValue(json, type);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static <T> T toJavaObjectOrNull(String json, TypeReference<T> type) {
		try {
			return MAPPER.readValue(json, type);
		} catch (IOException e) {
			log.error("读取json失败,json:{}", json, e);
			return null;
		}
	}


	public static <T> T toJavaObject(String json, TypeRef<T> type) {
		return toJavaObject(json, type.getType());
	}

	public static <T> T toJavaObjectOrNull(String json, TypeRef<T> type) {
		return toJavaObjectOrNull(json, type.getType());
	}

	public static <T> T toJavaObject(String json, Type type) {
		try {
			if (type instanceof io.polaris.core.lang.JavaType) {
				return toJavaObject(json, ((io.polaris.core.lang.JavaType<?>) type).getRawType());
			}
			return MAPPER.readValue(json, MAPPER.constructType(type));
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static <T> T toJavaObjectOrNull(String json, Type type) {
		try {
			if (type instanceof io.polaris.core.lang.JavaType) {
				return toJavaObjectOrNull(json, ((io.polaris.core.lang.JavaType<?>) type).getRawType());
			}
			return MAPPER.readValue(json, MAPPER.constructType(type));
		} catch (IOException e) {
			log.error("读取json失败,json:{}", json, e);
			return null;
		}
	}


	public static <T> T toJavaObject(String json, com.fasterxml.jackson.databind.JavaType type) {
		try {
			return MAPPER.readValue(json, type);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static <T> T toJavaObjectOrNull(String json, com.fasterxml.jackson.databind.JavaType type) {
		try {
			return MAPPER.readValue(json, type);
		} catch (IOException e) {
			log.error("读取json失败,json:{}", json, e);
			return null;
		}
	}

	public static <T> T toJavaObject(String json, Class<T> clazz) {
		try {
			return MAPPER.readValue(json, clazz);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static <T> T toJavaObjectOrNull(String json, Class<T> clazz) {
		try {
			return MAPPER.readValue(json, clazz);
		} catch (IOException e) {
			log.error("读取json失败,json:{}", json, e);
			return null;
		}
	}

	public static <T> T toJavaObject(byte[] json, TypeReference<T> type) {
		try {
			return MAPPER.readValue(json, type);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static <T> T toJavaObjectOrNull(byte[] json, TypeReference<T> type) {
		try {
			return MAPPER.readValue(json, type);
		} catch (IOException e) {
			log.error("读取json失败,json:{}", json, e);
			return null;
		}
	}


	public static <T> T toJavaObject(byte[] json, TypeRef<T> type) {
		return toJavaObject(json, type.getType());
	}

	public static <T> T toJavaObjectOrNull(byte[] json, TypeRef<T> type) {
		return toJavaObjectOrNull(json, type.getType());
	}

	public static <T> T toJavaObject(byte[] json, Type type) {
		try {
			if (type instanceof io.polaris.core.lang.JavaType) {
				return toJavaObject(json, ((io.polaris.core.lang.JavaType<?>) type).getRawType());
			}
			return MAPPER.readValue(json, MAPPER.constructType(type));
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static <T> T toJavaObjectOrNull(byte[] json, Type type) {
		try {
			if (type instanceof io.polaris.core.lang.JavaType) {
				return toJavaObjectOrNull(json, ((io.polaris.core.lang.JavaType<?>) type).getRawType());
			}
			return MAPPER.readValue(json, MAPPER.constructType(type));
		} catch (IOException e) {
			log.error("读取json失败,json:{}", json, e);
			return null;
		}
	}

	public static <T> T toJavaObject(byte[] bytes, Class<T> clazz) {
		try {
			return MAPPER.readValue(bytes, clazz);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static <T> T toJavaObjectOrNull(byte[] bytes, Class<T> clazz) {
		try {
			return MAPPER.readValue(bytes, clazz);
		} catch (IOException e) {
			log.error("读取json失败,json:{}", new String(bytes), e);
			return null;
		}
	}

	public static <T> T toJavaObject(byte[] bytes, JavaType javaType) {
		try {
			return MAPPER.readValue(bytes, javaType);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static <T> T toJavaObjectOrNull(byte[] bytes, JavaType javaType) {
		try {
			return MAPPER.readValue(bytes, javaType);
		} catch (IOException e) {
			log.error("读取json失败,json:{}", new String(bytes), e);
			return null;
		}
	}

	public static JsonNode toJsonTree(String json) {
		try {
			return MAPPER.readTree(json);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static JsonNode toJsonTreeOrNull(String json) {
		try {
			return MAPPER.readTree(json);
		} catch (IOException e) {
			log.error("读取json失败,json:{}", json, e);
			return null;
		}
	}

	public static JsonNode toJsonTree(byte[] bytes) {
		try {
			return MAPPER.readTree(bytes);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static JsonNode toJsonTreeOrNull(byte[] bytes) {
		try {
			return MAPPER.readTree(bytes);
		} catch (IOException e) {
			log.error("读取json失败,json:{}", new String(bytes), e);
			return null;
		}
	}

	public static <T> Type toType(TypeReference<T> type) {
		return type.getType();
	}

}
