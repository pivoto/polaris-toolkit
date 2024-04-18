package io.polaris.json;

import com.alibaba.fastjson2.*;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.service.StatefulServiceLoader;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("SpellCheckingInspection")
@Slf4j
public class Fastjsons {

	private static final JSONReader.Feature[] readerFeatures = {
		JSONReader.Feature.SupportSmartMatch,
		JSONReader.Feature.AllowUnQuotedFieldNames,
		JSONReader.Feature.NonZeroNumberCastToBooleanAsTrue,
	};


	private static final JSONWriter.Feature[] writerFeatures = {
		JSONWriter.Feature.WriteMapNullValue,
		JSONWriter.Feature.IgnoreErrorGetter,
		JSONWriter.Feature.WriteLongAsString,
		JSONWriter.Feature.WriteBigDecimalAsPlain,
		JSONWriter.Feature.WriteEnumsUsingName,
	};
	private static final JSONWriter.Feature[] writerPrettyFeatures = {
		JSONWriter.Feature.WriteMapNullValue,
		JSONWriter.Feature.IgnoreErrorGetter,
		JSONWriter.Feature.WriteLongAsString,
		JSONWriter.Feature.WriteBigDecimalAsPlain,
		JSONWriter.Feature.WriteEnumsUsingName,
		JSONWriter.Feature.PrettyFormat
	};

	static {
		for (IFastjsonCustomizer customizer : StatefulServiceLoader.load(IFastjsonCustomizer.class)) {
			try {
				customizer.customize();
			} catch (Throwable e) {
				log.error("" , e);
			}
		}
	}

	public static JSONReader.Feature[] getDefaultReaderFeatures() {
		return readerFeatures;
	}

	public static JSONWriter.Feature[] getDefaultWriterFeatures() {
		return writerFeatures;
	}


	public static byte[] toJsonBytes(Object target) {
		try {
			// UTF-8
			return JSON.toJSONBytes(target, writerFeatures);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
		/*String json = toJsonString(target);
		return json == null ? new byte[0] : json.getBytes(StandardCharsets.UTF_8);*/
	}

	public static String toJsonString(Object target) {
		try {
			return JSON.toJSONString(target, writerFeatures);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String toJsonStringOrNull(Object target) {
		try {
			return JSON.toJSONString(target, writerFeatures);
		} catch (Throwable e) {
			log.error("json序列化失败" , e);
			return null;
		}
	}

	public static String toJsonPrettyString(Object target) {
		try {
			return JSON.toJSONString(target, writerPrettyFeatures);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String toJsonPrettyStringOrNull(Object target) {
		try {
			return JSON.toJSONString(target, writerPrettyFeatures);
		} catch (Throwable e) {
			log.error("json序列化失败" , e);
			return null;
		}
	}

	public static String toJsonOrJavaString(Object target) {
		try {
			return JSON.toJSONString(target, writerFeatures);
		} catch (Throwable e) {
			return target == null ? null : target.toString();
		}
	}

	public static Object toJsonObject(Object obj) {
		return JSON.toJSON(obj, writerFeatures);
	}

	public static Object toJson(String text) {
		return JSON.parse(text, readerFeatures);
	}

	public static JSONObject toJsonObject(String text) {
		return JSON.parseObject(text, readerFeatures);
	}

	public static JSONArray toJsonList(String text) {
		return JSON.parseArray(text, readerFeatures);
	}


	public static <T> T toJavaObject(String json, TypeRef<T> type) {
		return toJavaObject(json, type.getType());
	}

	public static <T> T toJavaObject(String text, Type type) {
		if (type instanceof JavaType) {
			return toJavaObject(text, ((JavaType<?>) type).getRawType());
		}
		return JSON.parseObject(text, type, readerFeatures);
	}

	public static <T> T toJavaObject(String text, Class<T> clazz) {
		return JSON.parseObject(text, clazz, readerFeatures);
	}

	public static <T> T toJavaObject(String text, TypeReference<T> clazz) {
		return JSON.parseObject(text, clazz, readerFeatures);
	}

	public static <T> List<T> toJavaList(String text, Type type) {
		if (type instanceof JavaType) {
			return toJavaList(text, ((JavaType<?>) type).getRawType());
		}
		return JSON.parseArray(text, type, readerFeatures);
	}

	public static <T> List<T> toJavaList(String text, Class<T> clazz) {
		return JSON.parseArray(text, clazz, readerFeatures);
	}

	public static <T> Type toType(TypeReference<T> type) {
		return type.getType();
	}
}
