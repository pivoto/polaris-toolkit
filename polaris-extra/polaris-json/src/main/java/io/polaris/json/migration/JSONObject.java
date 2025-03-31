package io.polaris.json.migration;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.polaris.core.converter.Converters;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.lang.Types;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.lang.copier.Copiers;
import io.polaris.core.time.Dates;
import io.polaris.json.Jacksons;

import com.jayway.jsonpath.JsonPath;

/**
 * 用于迁移和替代 com.alibaba.fastjson2.JSONObject 类
 *
 * @author Qt
 * @since Mar 30, 2025
 */
@SuppressWarnings({"DuplicatedCode", "unchecked", "unused", "NullableProblems"})
public class JSONObject extends LinkedHashMap<String, Object> {

	private class Raw extends LinkedHashMap<String, Object> {
		private static final long serialVersionUID = 1L;

		public Raw() {
		}

		public Raw(Map<? extends String, ?> m) {
			super(m);
		}

		public Raw(int initialCapacity, float loadFactor, boolean accessOrder) {
			super(initialCapacity, loadFactor, accessOrder);
		}

		public Raw(int initialCapacity, float loadFactor) {
			super(initialCapacity, loadFactor);
		}

		public Raw(int initialCapacity) {
			super(initialCapacity);
		}

		@Override
		protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
			return JSONObject.this.removeEldestEntry(eldest);
		}
	}

	private static final long serialVersionUID = 1L;
	private final Map<String, Object> raw;

	public JSONObject(LinkedHashMap<String, Object> raw, boolean delegate) {
		if (delegate) {
			this.raw = raw;
		} else {
			this.raw = new Raw(raw);
		}
	}

	public JSONObject(Map<? extends String, ?> raw, boolean delegate) {
		if (delegate) {
			this.raw = (Map<String, Object>) raw;
		} else {
			this.raw = new Raw(raw);
		}
	}

	public JSONObject(Map<? extends String, ?> m) {
		this(m, false);
	}

	public JSONObject() {
		this.raw = new Raw();
	}

	public JSONObject(int initialCapacity) {
		this.raw = new Raw(initialCapacity);
	}

	public JSONObject(int initialCapacity, float loadFactor) {
		this.raw = new Raw(initialCapacity, loadFactor);
	}

	public JSONObject(int initialCapacity, float loadFactor, boolean accessOrder) {
		this.raw = new Raw(initialCapacity, loadFactor, accessOrder);
	}

	public static JSONObject of() {
		return new JSONObject();
	}

	public static JSONObject of(Map<? extends String, ?> m) {
		return new JSONObject(m, true);
	}

	public static JSONObject of(String key, Object value) {
		JSONObject object = new JSONObject(1, 1F);
		object.put(key, value);
		return object;
	}

	public static JSONObject of(String k1, Object v1, String k2, Object v2) {
		JSONObject object = new JSONObject(2, 1F);
		object.put(k1, v1);
		object.put(k2, v2);
		return object;
	}

	public static JSONObject of(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
		JSONObject object = new JSONObject(3);
		object.put(k1, v1);
		object.put(k2, v2);
		object.put(k3, v3);
		return object;
	}

	public static JSONObject of(
		String k1,
		Object v1,
		String k2,
		Object v2,
		String k3,
		Object v3,
		String k4,
		Object v4) {
		JSONObject object = new JSONObject(4, 1F);
		object.put(k1, v1);
		object.put(k2, v2);
		object.put(k3, v3);
		object.put(k4, v4);
		return object;
	}

	public static JSONObject of(
		String k1,
		Object v1,
		String k2,
		Object v2,
		String k3,
		Object v3,
		String k4,
		Object v4,
		String k5,
		Object v5
	) {
		JSONObject object = new JSONObject(5);
		object.put(k1, v1);
		object.put(k2, v2);
		object.put(k3, v3);
		object.put(k4, v4);
		object.put(k5, v5);
		return object;
	}

	public static JSONObject of(
		String k1,
		Object v1,
		String k2,
		Object v2,
		String k3,
		Object v3,
		String k4,
		Object v4,
		String k5,
		Object v5,
		Object... kvArray
	) {
		JSONObject object = of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
		if (kvArray != null && kvArray.length > 0) {
			of(object, kvArray);
		}
		return object;
	}

	private static JSONObject of(JSONObject object, Object... kvArray) {
		if (kvArray == null || kvArray.length <= 0) {
			throw new RuntimeException("The kvArray cannot be empty");
		}
		int kvArrayLength = kvArray.length;
		if ((kvArrayLength & 1) == 1) {
			throw new RuntimeException("The length of kvArray cannot be odd");
		}
		List<Object> keyList = IntStream.range(0, kvArrayLength).filter(i -> i % 2 == 0).mapToObj(i -> kvArray[i]).collect(Collectors.toList());
		keyList.forEach(key -> {
			if (key == null || !(key instanceof String)) {
				throw new RuntimeException("The value corresponding to the even bit index of kvArray is key, which cannot be null and must be of type string");
			}
		});
		List<Object> distinctKeyList = keyList.stream().distinct().collect(Collectors.toList());
		if (keyList.size() != distinctKeyList.size()) {
			throw new RuntimeException("The value corresponding to the even bit index of kvArray is key and cannot be duplicated");
		}
		List<Object> valueList = IntStream.range(0, kvArrayLength).filter(i -> i % 2 != 0).mapToObj(i -> kvArray[i]).collect(Collectors.toList());
		for (int i = 0; i < keyList.size(); i++) {
			object.put(keyList.get(i).toString(), valueList.get(i));
		}
		return object;
	}

	public static <T> T parseObject(String text, Class<T> objectClass) {
		return Jacksons.toJavaObject(text, objectClass);
	}

	public static <T> T parseObject(String text, Type objectType) {
		return Jacksons.toJavaObject(text, objectType);
	}

	public static <T> T parseObject(String text, TypeRef<T> typeReference) {
		return Jacksons.toJavaObject(text, typeReference);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static JSONObject parseObject(String text) {
		LinkedHashMap map = Jacksons.toJavaObject(text, LinkedHashMap.class);
		return new JSONObject(map, true);
	}

	public static JSONObject parse(String text) {
		return parseObject(text);
	}

	public static JSONObject from(Object object) {
		if (object == null) {
			return null;
		}
		if (object instanceof JSONObject) {
			return (JSONObject) object;
		}
		return parseObject(Jacksons.toJsonString(object));
	}


	// region 重写

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof JSONObject)) {
			return false;
		}
		JSONObject that = (JSONObject) o;
		return raw.equals(that.raw);
	}

	@Override
	public int hashCode() {
		return raw.hashCode();
	}


	public Object get(String key) {
		return raw.get(key);
	}

	@Override
	public Object get(Object key) {
		if (key instanceof Number
			|| key instanceof Character
			|| key instanceof Boolean
			|| key instanceof UUID
		) {
			Object value = raw.get(key.toString());
			if (value != null) {
				return value;
			}
		}
		return raw.get(key);
	}

	public Object getByPath(String jsonPath) {
		return JsonPath.read(raw, jsonPath);
	}

	public boolean containsKey(String key) {
		return raw.containsKey(key);
	}

	@Override
	public boolean containsKey(Object key) {
		if (key instanceof Number
			|| key instanceof Character
			|| key instanceof Boolean
			|| key instanceof UUID
		) {
			return raw.containsKey(key) || raw.containsKey(key.toString());
		}
		return raw.containsKey(key);
	}

	public Object getOrDefault(String key, Object defaultValue) {
		return raw.getOrDefault(key, defaultValue);
	}

	@Override
	public Object getOrDefault(Object key, Object defaultValue) {
		if (key instanceof Number
			|| key instanceof Character
			|| key instanceof Boolean
			|| key instanceof UUID
		) {
			return raw.getOrDefault(
				key.toString(), defaultValue
			);
		}
		return raw.getOrDefault(key, defaultValue);
	}

	public void forEchArrayObject(String key, Consumer<JSONObject> action) {
		JSONArray array = getJSONArray(key);
		if (array == null) {
			return;
		}

		for (int i = 0; i < array.size(); i++) {
			action.accept(array.getJSONObject(i));
		}
	}

	public JSONArray getJSONArray(String key) {
		Object value = raw.get(key);
		if (value == null) {
			return null;
		}

		if (value instanceof JSONArray) {
			return (JSONArray) value;
		}

		if (value instanceof JSONObject) {
			return JSONArray.of(value);
		}

		if (value instanceof String) {
			String str = (String) value;

			if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
				return null;
			}

			if (str.charAt(0) != '[') {
				return JSONArray.of(str);
			}

			ArrayList<Object> arrayList = Jacksons.toJavaObject(str, ArrayList.class);
			return new JSONArray(arrayList, true);
		}
		if (value instanceof Collection) {
			JSONArray array = new JSONArray((Collection<?>) value, true);
			put(key, array);
			return array;
		}

		if (value instanceof Object[]) {
			JSONArray array = JSONArray.of((Object[]) value);
			// todo put??
			put(key, array);
			return array;
		}

		Class<?> valueClass = value.getClass();
		if (valueClass.isArray()) {
			int length = Array.getLength(value);
			JSONArray jsonArray = new JSONArray(length);
			for (int i = 0; i < length; i++) {
				Object item = Array.get(value, i);
				jsonArray.add(item);
			}
			// todo put??
			put(key, jsonArray);
			return jsonArray;
		}
		return null;
	}

	public <T> List<T> getList(String key, Class<T> itemClass) {
		JSONArray jsonArray = getJSONArray(key);
		if (jsonArray == null) {
			return null;
		}
		return jsonArray.toList(itemClass);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public JSONObject getJSONObject(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return null;
		}

		if (value instanceof JSONObject) {
			return (JSONObject) value;
		}

		if (value instanceof String) {
			String str = (String) value;

			if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
				return null;
			}

			Map<String, Object> map = Jacksons.toJavaObject(str, Map.class);
			JSONObject object = new JSONObject(map, true);
			// todo put??
			put(key, object);
			return object;
		}

		if (value instanceof Map) {
			JSONObject object = new JSONObject((Map) value, true);
			put(key, object);
			return object;
		}

		Class valueClass = value.getClass();
		if (Beans.isBeanClass(valueClass)) {
			Map<String, Object> map = Jacksons.toJavaObject(Jacksons.toJsonString(value), Map.class);
			JSONObject object = new JSONObject(map, true);
			// todo put??
			put(key, object);
			return object;
		}

		return null;
	}

	public String getString(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return null;
		}

		if (value instanceof String) {
			return (String) value;
		}

		if (value instanceof Date) {
			return Dates.formatDefault((Date) value);
		}

		if (value instanceof Boolean
			|| value instanceof Character
			|| value instanceof Number
			|| value instanceof UUID
			|| value instanceof Enum
			|| value instanceof TemporalAccessor) {
			return value.toString();
		}

		return Jacksons.toJsonString(value);
	}

	public Double getDouble(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return null;
		}

		if (value instanceof Double) {
			return (Double) value;
		}

		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}

		if (value instanceof String) {
			String str = (String) value;

			if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
				return null;
			}

			return Double.parseDouble(str);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to Double");
	}

	public double getDoubleValue(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return 0D;
		}

		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}

		if (value instanceof String) {
			String str = (String) value;

			if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
				return 0D;
			}

			return Double.parseDouble(str);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to double value");
	}

	public Float getFloat(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return null;
		}

		if (value instanceof Float) {
			return (Float) value;
		}

		if (value instanceof Number) {
			return ((Number) value).floatValue();
		}

		if (value instanceof String) {
			String str = (String) value;

			if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
				return null;
			}

			return Float.parseFloat(str);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to Float");
	}

	public float getFloatValue(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return 0F;
		}

		if (value instanceof Number) {
			return ((Number) value).floatValue();
		}

		if (value instanceof String) {
			String str = (String) value;

			if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
				return 0F;
			}

			return Float.parseFloat(str);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to float value");
	}

	public Long getLong(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return null;
		}

		if (value instanceof Long) {
			return ((Long) value);
		}

		if (value instanceof Number) {
			return ((Number) value).longValue();
		}

		if (value instanceof String) {
			String str = (String) value;

			if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
				return null;
			}

			if (str.indexOf('.') != -1) {
				return (long) Double.parseDouble(str);
			}

			return Long.parseLong(str);
		}

		if (value instanceof Boolean) {
			return (boolean) value ? Long.valueOf(1) : Long.valueOf(0);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to Long");
	}

	public long getLongValue(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return 0;
		}

		if (value instanceof Number) {
			return ((Number) value).longValue();
		}

		if (value instanceof String) {
			String str = (String) value;

			if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
				return 0;
			}

			if (str.indexOf('.') != -1) {
				return (long) Double.parseDouble(str);
			}

			return Long.parseLong(str);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to long value");
	}

	public long getLongValue(String key, long defaultValue) {
		Object value = raw.get(key);

		if (value == null) {
			return defaultValue;
		}

		if (value instanceof Number) {
			return ((Number) value).longValue();
		}

		if (value instanceof String) {
			String str = (String) value;

			if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
				return defaultValue;
			}

			if (str.indexOf('.') != -1) {
				return (long) Double.parseDouble(str);
			}

			return Long.parseLong(str);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to long value");
	}

	public Integer getInteger(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return null;
		}

		if (value instanceof Integer) {
			return ((Integer) value);
		}

		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		if (value instanceof String) {
			String str = (String) value;

			if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
				return null;
			}

			if (str.indexOf('.') != -1) {
				return (int) Double.parseDouble(str);
			}

			return Integer.parseInt(str);
		}

		if (value instanceof Boolean) {
			return (boolean) value ? Integer.valueOf(1) : Integer.valueOf(0);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to Integer");
	}

	public int getIntValue(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return 0;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		if (value instanceof String) {
			String str = (String) value;

			if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
				return 0;
			}

			if (str.indexOf('.') != -1) {
				return (int) Double.parseDouble(str);
			}

			return Integer.parseInt(str);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to int value");
	}

	public int getIntValue(String key, int defaultValue) {
		Object value = raw.get(key);

		if (value == null) {
			return defaultValue;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		if (value instanceof String) {
			String str = (String) value;

			if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
				return defaultValue;
			}

			if (str.indexOf('.') != -1) {
				return (int) Double.parseDouble(str);
			}

			return Integer.parseInt(str);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to int value");
	}

	public Short getShort(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return null;
		}

		if (value instanceof Short) {
			return (Short) value;
		}

		if (value instanceof Number) {
			return ((Number) value).shortValue();
		}

		if (value instanceof String) {
			String str = (String) value;

			if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
				return null;
			}

			return Short.parseShort(str);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to Short");
	}

	public short getShortValue(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return 0;
		}

		if (value instanceof Number) {
			return ((Number) value).shortValue();
		}

		if (value instanceof String) {
			String str = (String) value;

			if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
				return 0;
			}

			return Short.parseShort(str);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to short value");
	}

	public Byte getByte(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return null;
		}

		if (value instanceof Number) {
			return ((Number) value).byteValue();
		}

		if (value instanceof String) {
			String str = (String) value;

			if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
				return null;
			}

			return Byte.parseByte(str);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to Byte");
	}

	public byte getByteValue(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return 0;
		}

		if (value instanceof Number) {
			return ((Number) value).byteValue();
		}

		if (value instanceof String) {
			String str = (String) value;

			if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
				return 0;
			}

			return Byte.parseByte(str);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to byte value");
	}

	public byte[] getBytes(String key) {
		Object value = get(key);

		if (value == null) {
			return null;
		}

		if (value instanceof byte[]) {
			return (byte[]) value;
		}
		if (value instanceof String) {
			return Base64.getDecoder().decode((String) value);
		}
		throw new JSONException("can not cast to byte[], value : " + value);
	}

	public Boolean getBoolean(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return null;
		}

		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue() == 1;
		}

		if (value instanceof String) {
			String str = (String) value;

			if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
				return null;
			}

			return "true".equalsIgnoreCase(str) || "1".equals(str);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to Boolean");
	}

	public boolean getBooleanValue(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return false;
		}

		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue() == 1;
		}

		if (value instanceof String) {
			String str = (String) value;
			return "true".equalsIgnoreCase(str) || "1".equals(str);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to boolean value");
	}

	public boolean getBooleanValue(String key, boolean defaultValue) {
		Object value = raw.get(key);

		if (value == null) {
			return defaultValue;
		}

		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue() == 1;
		}

		if (value instanceof String) {
			String str = (String) value;
			return "true".equalsIgnoreCase(str) || "1".equals(str);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to boolean value");
	}

	public BigInteger getBigInteger(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return null;
		}

		if (value instanceof BigInteger) {
			return (BigInteger) value;
		}

		if (value instanceof Number) {
			if (value instanceof BigDecimal) {
				return ((BigDecimal) value).toBigInteger();
			}

			long longValue = ((Number) value).longValue();
			return BigInteger.valueOf(longValue);
		}

		if (value instanceof String) {
			String str = (String) value;

			if (str.isEmpty() || "null".equalsIgnoreCase(str)) {
				return null;
			}

			return new BigInteger(str);
		}

		if (value instanceof Boolean) {
			return (boolean) value ? BigInteger.ONE : BigInteger.ZERO;
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to BigInteger");
	}

	public BigDecimal getBigDecimal(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return null;
		}

		if (value instanceof Number) {
			if (value instanceof BigDecimal) {
				return (BigDecimal) value;
			}

			if (value instanceof BigInteger) {
				return new BigDecimal((BigInteger) value);
			}

			if (value instanceof Float) {
				float floatValue = (Float) value;
				return new BigDecimal(floatValue);
			}

			if (value instanceof Double) {
				double doubleValue = (Double) value;
				return new BigDecimal(doubleValue);
			}

			long longValue = ((Number) value).longValue();
			return BigDecimal.valueOf(longValue);
		}

		if (value instanceof String) {
			String str = (String) value;
			return new BigDecimal(str);
		}

		if (value instanceof Boolean) {
			return (boolean) value ? BigDecimal.ONE : BigDecimal.ZERO;
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to BigDecimal");
	}

	public Date getDate(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return null;
		}

		if (value instanceof Date) {
			return (Date) value;
		}

		if (value instanceof String) {
			return Dates.parseDate((String) value);
		}

		if (value instanceof Number) {
			long millis = ((Number) value).longValue();
			return new Date(millis);
		}
		return Converters.convertQuietly(Date.class, value);
	}

	public Date getDate(String key, Date defaultValue) {
		Date date = getDate(key);
		if (date == null) {
			date = defaultValue;
		}
		return date;
	}

	public Instant getInstant(String key) {
		Object value = raw.get(key);

		if (value == null) {
			return null;
		}

		if (value instanceof Instant) {
			return (Instant) value;
		}

		if (value instanceof Number) {
			long millis = ((Number) value).longValue();
			if (millis == 0) {
				return null;
			}
			return Instant.ofEpochMilli(millis);
		}
		return Converters.convertQuietly(Instant.class, value);
	}

	@Override
	public String toString() {
		return Jacksons.toJsonString(raw);
	}

	public String toJSONString() {
		return Jacksons.toJsonString(raw);
	}

	public static String toJSONString(Object object) {
		return Jacksons.toJsonString(object);
	}

	public byte[] toJSONBBytes() {
		return Jacksons.toJsonBytes(raw);
	}

	public <T> T to(Function<JSONObject, T> function) {
		return function.apply(this);
	}

	public <T> T to(Type type) {
		if (type == String.class) {
			return (T) toString();
		}
		if (type == JSON.class || type == JSONObject.class) {
			return (T) this;
		}
		return Jacksons.toJavaObject(Jacksons.toJsonString(raw), type);
	}


	public <T> T to(TypeRef<T> typeReference) {
		return to(typeReference.getType());
	}

	public <T> T to(Class<T> clazz) {
		if (clazz == String.class) {
			return (T) toString();
		}
		if (clazz == JSON.class || clazz == JSONObject.class) {
			return (T) this;
		}
		return Jacksons.toJavaObject(Jacksons.toJsonString(raw), clazz);
	}

	public void copyTo(Object object) {
		Copiers.copy(this.raw, object);
	}

	public <T> T toJavaObject(Class<T> clazz) {
		return to(clazz);
	}

	public <T> T toJavaObject(Type type) {
		return to(type);
	}

	public <T> T toJavaObject(TypeRef<T> typeReference) {
		return to(typeReference);
	}

	@SuppressWarnings({"unchecked"})
	public <T> T getObject(String key, Class<T> type) {
		Object value = raw.get(key);
		if (value == null) {
			return null;
		}
		if (type == Object.class) {
			return (T) value;
		}
		if (type.isInstance(value)) {
			return (T) value;
		}
		return Converters.convertQuietly(type, value);
	}

	@SuppressWarnings({"unchecked"})
	public <T> T getObject(String key, Type type) {
		Object value = raw.get(key);
		if (value == null) {
			return null;
		}
		if (type == Object.class) {
			return (T) value;
		}
		if (Types.getClass(type).isInstance(value)) {
			return (T) value;
		}
		return Converters.convertQuietly(type, value);
	}

	public <T> T getObject(String key, TypeRef<T> typeReference) {
		return getObject(key, typeReference.getType());
	}

	public <T> T getObject(String key, Function<JSONObject, T> creator) {
		JSONObject object = getJSONObject(key);
		if (object == null) {
			return null;
		}
		return creator.apply(object);
	}

	public JSONArray putArray(String name) {
		JSONArray array = new JSONArray();
		put(name, array);
		return array;
	}

	public JSONObject putObject(String name) {
		JSONObject object = new JSONObject();
		put(name, object);
		return object;
	}

	public JSONObject fluentPut(String key, Object value) {
		put(key, value);
		return this;
	}

	@SuppressWarnings("MethodDoesntCallSuperMethod")
	@Override
	public Object clone() {
		return new JSONObject(raw, false);
	}

	public Object eval(JsonPath path) {
		return path.read(raw);
	}

	public int getSize(String key) {
		Object value = get(key);
		if (value instanceof Map) {
			return ((Map<?, ?>) value).size();
		}
		if (value instanceof Collection) {
			return ((Collection<?>) value).size();
		}
		return 0;
	}

	public boolean isArray(Object key) {
		Object object = raw.get(key);
		return object instanceof JSONArray || object != null && object.getClass().isArray();
	}

	// endregion 重写

	// region delegate

	@Override
	public int size() {
		return raw.size();
	}

	@Override
	public boolean isEmpty() {
		return raw.isEmpty();
	}


	@Override
	public Object put(String key, Object value) {
		return raw.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ?> m) {
		raw.putAll(m);
	}

	@Override
	public Object remove(Object key) {
		return raw.remove(key);
	}

	@Override
	public Object putIfAbsent(String key, Object value) {
		return raw.putIfAbsent(key, value);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return raw.remove(key, value);
	}

	@Override
	public boolean replace(String key, Object oldValue, Object newValue) {
		return raw.replace(key, oldValue, newValue);
	}

	@Override
	public Object replace(String key, Object value) {
		return raw.replace(key, value);
	}

	@Override
	public Object computeIfAbsent(String key, Function<? super String, ?> mappingFunction) {
		return raw.computeIfAbsent(key, mappingFunction);
	}

	@Override
	public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
		return raw.computeIfPresent(key, remappingFunction);
	}

	@Override
	public Object compute(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
		return raw.compute(key, remappingFunction);
	}

	@Override
	public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
		return raw.merge(key, value, remappingFunction);
	}


	@Override
	public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
		raw.replaceAll(function);
	}

	@Override
	public void forEach(BiConsumer<? super String, ? super Object> action) {
		raw.forEach(action);
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		return raw.entrySet();
	}

	@Override
	public Collection<Object> values() {
		return raw.values();
	}

	@Override
	public Set<String> keySet() {
		return raw.keySet();
	}

	@Override
	public void clear() {
		raw.clear();
	}


	@Override
	public boolean containsValue(Object value) {
		return raw.containsValue(value);
	}

	// endregion delegate

}
