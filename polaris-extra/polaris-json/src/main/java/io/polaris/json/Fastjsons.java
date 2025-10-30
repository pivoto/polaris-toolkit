package io.polaris.json;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.service.SpiLoaders;
import io.polaris.core.service.StatefulServiceLoader;
import lombok.extern.slf4j.Slf4j;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.fastjson2.filter.Filter;

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
	private static final JSONWriter.Feature[] writerClassNameFeatures = {
		JSONWriter.Feature.WriteMapNullValue,
		JSONWriter.Feature.IgnoreErrorGetter,
		JSONWriter.Feature.WriteLongAsString,
		JSONWriter.Feature.WriteBigDecimalAsPlain,
		JSONWriter.Feature.WriteEnumsUsingName,
		// https://alibaba.github.io/fastjson2/autotype_cn.html
		JSONWriter.Feature.WriteClassName,
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
		for (FastjsonCustomizer customizer : SpiLoaders.loadStateful(FastjsonCustomizer.class)) {
			try {
				customizer.customize();
			} catch (Throwable e) {
				log.error("", e);
			}
		}
	}

	public static JSONWriter.Feature[] merge(JSONWriter.Feature[] standard, final JSONWriter.Feature[] features) {
		if (features.length > 0) {
			EnumSet<JSONWriter.Feature> set = EnumSet.noneOf(JSONWriter.Feature.class);
			Collections.addAll(set, features);
			Collections.addAll(set, standard);
			return set.toArray(new JSONWriter.Feature[0]);
		}
		return standard;
	}

	public static JSONReader.Feature[] merge(JSONReader.Feature[] standard, final JSONReader.Feature[] features) {
		if (features.length > 0) {
			EnumSet<JSONReader.Feature> set = EnumSet.noneOf(JSONReader.Feature.class);
			Collections.addAll(set, features);
			Collections.addAll(set, standard);
			return set.toArray(new JSONReader.Feature[0]);
		}
		return standard;
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
	}

	public static byte[] toJsonBytesWithClassName(Object target) {
		try {
			// UTF-8
			return JSON.toJSONBytes(target, writerClassNameFeatures);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static byte[] toJsonBytes(Object target, JSONWriter.Feature... features) {
		try {
			return JSON.toJSONBytes(target, merge(writerFeatures, features));
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}


	public static String toJsonString(Object target) {
		try {
			return JSON.toJSONString(target, writerFeatures);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String toJsonStringWithClassName(Object target) {
		try {
			return JSON.toJSONString(target, writerClassNameFeatures);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String toJsonString(Object target, JSONWriter.Feature... features) {
		try {
			return JSON.toJSONString(target, merge(writerFeatures, features));
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String toJsonStringOrNull(Object target) {
		try {
			return JSON.toJSONString(target, writerFeatures);
		} catch (Throwable e) {
			log.error("json序列化失败", e);
			return null;
		}
	}

	public static String toJsonStringOrNullWithClassName(Object target) {
		try {
			return JSON.toJSONString(target, writerClassNameFeatures);
		} catch (Throwable e) {
			log.error("json序列化失败", e);
			return null;
		}
	}

	public static String toJsonStringOrNull(Object target, JSONWriter.Feature... features) {
		try {
			return JSON.toJSONString(target, merge(writerFeatures, features));
		} catch (Throwable e) {
			log.error("json序列化失败", e);
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

	public static String toJsonPrettyString(Object target, JSONWriter.Feature... features) {
		try {
			return JSON.toJSONString(target, merge(writerPrettyFeatures, features));
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String toJsonPrettyStringOrNull(Object target) {
		try {
			return JSON.toJSONString(target, writerPrettyFeatures);
		} catch (Throwable e) {
			log.error("json序列化失败", e);
			return null;
		}
	}

	public static String toJsonPrettyStringOrNull(Object target, JSONWriter.Feature... features) {
		try {
			return JSON.toJSONString(target, merge(writerPrettyFeatures, features));
		} catch (Throwable e) {
			log.error("json序列化失败", e);
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

	public static String toJsonOrJavaString(Object target, JSONWriter.Feature... features) {
		try {
			return JSON.toJSONString(target, merge(writerFeatures, features));
		} catch (Throwable e) {
			return target == null ? null : target.toString();
		}
	}

	public static JSONObject toJsonObject(Object obj) {
		// fix 由于fastjson转换JSONObject对象在不同版本表现有差异，所以这里使用parseObject
		//return JSON.toJSON(obj, writerFeatures);
		return toJsonObject(toJsonString(obj));
	}

	public static JSONObject toJsonObjectWithClassName(Object obj) {
		// fix 由于fastjson转换JSONObject对象在不同版本表现有差异，所以这里使用parseObject
		//return JSON.toJSON(obj, writerClassNameFeatures);
		return toJsonObject(toJsonString(obj, writerClassNameFeatures));
	}

	public static JSONObject toJsonObject(Object obj, JSONWriter.Feature... features) {
		// fix 由于fastjson转换JSONObject对象在不同版本表现有差异，所以这里使用parseObject
		return toJsonObject(toJsonString(obj, features));
	}

	public static Object toJson(String text) {
		return JSON.parse(text, readerFeatures);
	}

	public static Object toJson(String text, JSONReader.Feature... features) {
		return JSON.parse(text, merge(readerFeatures, features));
	}

	public static JSONObject toJsonObject(String text) {
		return JSON.parseObject(text, readerFeatures);
	}

	public static JSONObject toJsonObject(String text, JSONReader.Feature... features) {
		return JSON.parseObject(text, merge(readerFeatures, features));
	}

	public static JSONArray toJsonList(String text) {
		return JSON.parseArray(text, readerFeatures);
	}

	public static JSONArray toJsonList(String text, JSONReader.Feature... features) {
		return JSON.parseArray(text, merge(readerFeatures, features));
	}


	public static <T> T toJavaObject(String json, TypeRef<T> type) {
		return toJavaObject(json, type.getType());
	}

	public static <T> T toJavaObject(String json, TypeRef<T> type, Filter filter) {
		return toJavaObject(json, type.getType(), filter);
	}

	public static <T> T toJavaObject(String json, TypeRef<T> type, Class<?>... autoTypes) {
		return toJavaObject(json, type.getType(), JSONReader.autoTypeFilter(true, autoTypes));
	}

	public static <T> T toJavaObject(String json, TypeRef<T> type, JSONReader.Feature... features) {
		return toJavaObject(json, type.getType(), features);
	}

	public static <T> T toJavaObject(String json, TypeRef<T> type, Filter filter, JSONReader.Feature... features) {
		return toJavaObject(json, type.getType(), filter, features);
	}

	public static <T> T toJavaObject(String text, Type type) {
		if (type instanceof JavaType) {
			return toJavaObject(text, ((JavaType<?>) type).getRawType());
		}
		return JSON.parseObject(text, type, readerFeatures);
	}

	public static <T> T toJavaObject(String text, Type type, Filter filter) {
		if (type instanceof JavaType) {
			return toJavaObject(text, ((JavaType<?>) type).getRawType(), filter);
		}
		return JSON.parseObject(text, type, filter, readerFeatures);
	}

	public static <T> T toJavaObject(String text, Type type, Class<?>... autoTypes) {
		if (type instanceof JavaType) {
			return toJavaObject(text, ((JavaType<?>) type).getRawType(), autoTypes);
		}
		return JSON.parseObject(text, type, JSONReader.autoTypeFilter(true, autoTypes), readerFeatures);
	}

	public static <T> T toJavaObject(String text, Type type, JSONReader.Feature... features) {
		if (type instanceof JavaType) {
			return toJavaObject(text, ((JavaType<?>) type).getRawType(), features);
		}
		return JSON.parseObject(text, type, merge(readerFeatures, features));
	}

	public static <T> T toJavaObject(String text, Type type, Filter filter, JSONReader.Feature... features) {
		if (type instanceof JavaType) {
			return toJavaObject(text, ((JavaType<?>) type).getRawType(), filter, features);
		}
		return JSON.parseObject(text, type, filter, merge(readerFeatures, features));
	}

	public static <T> T toJavaObject(String text, Class<T> clazz) {
		return JSON.parseObject(text, clazz, readerFeatures);
	}

	public static <T> T toJavaObject(String text, Class<T> clazz, Filter filter) {
		return JSON.parseObject(text, clazz, filter, readerFeatures);
	}

	public static <T> T toJavaObject(String text, Class<T> clazz, Class<?>... autoTypes) {
		return JSON.parseObject(text, clazz, JSONReader.autoTypeFilter(true, autoTypes), readerFeatures);
	}

	public static <T> T toJavaObject(String text, Class<T> clazz, Filter filter, JSONReader.Feature... features) {
		return JSON.parseObject(text, clazz, filter, merge(readerFeatures, features));
	}

	public static <T> T toJavaObject(String text, Class<T> clazz, JSONReader.Feature... features) {
		return JSON.parseObject(text, clazz, merge(readerFeatures, features));
	}

	public static <T> T toJavaObject(String text, TypeReference<T> clazz) {
		return JSON.parseObject(text, clazz, readerFeatures);
	}

	public static <T> T toJavaObject(String text, TypeReference<T> clazz, Filter filter) {
		return JSON.parseObject(text, clazz, filter, readerFeatures);
	}

	public static <T> T toJavaObject(String text, TypeReference<T> clazz, Class<?>... autoTypes) {
		return JSON.parseObject(text, clazz, JSONReader.autoTypeFilter(true, autoTypes), readerFeatures);
	}

	public static <T> T toJavaObject(String text, TypeReference<T> clazz, Filter filter, JSONReader.Feature... features) {
		return JSON.parseObject(text, clazz, filter, merge(readerFeatures, features));
	}

	public static <T> T toJavaObject(String text, TypeReference<T> clazz, JSONReader.Feature... features) {
		return JSON.parseObject(text, clazz, merge(readerFeatures, features));
	}

	public static <T> List<T> toJavaList(String text, Type type) {
		if (type instanceof JavaType) {
			return toJavaList(text, ((JavaType<?>) type).getRawType());
		}
		return JSON.parseArray(text, type, readerFeatures);
	}

	public static <T> List<T> toJavaList(String text, Type type, JSONReader.Feature... features) {
		if (type instanceof JavaType) {
			return toJavaList(text, ((JavaType<?>) type).getRawType(), features);
		}
		return JSON.parseArray(text, type, merge(readerFeatures, features));
	}

	public static <T> List<T> toJavaList(String text, Class<T> clazz) {
		return JSON.parseArray(text, clazz, readerFeatures);
	}

	public static <T> List<T> toJavaList(String text, Class<T> clazz, JSONReader.Feature... features) {
		return JSON.parseArray(text, clazz, merge(readerFeatures, features));
	}

	public static <T> Type toType(TypeReference<T> type) {
		return type.getType();
	}
}
