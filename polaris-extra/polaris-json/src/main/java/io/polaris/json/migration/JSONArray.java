package io.polaris.json.migration;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import io.polaris.core.collection.Lists;
import io.polaris.core.converter.Converters;
import io.polaris.core.lang.TypeRefs;
import io.polaris.core.lang.Types;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.time.Dates;
import io.polaris.json.Jacksons;


/**
 * 用于迁移和替代 com.alibaba.fastjson2.JSONArray 类
 *
 * @author Qt
 * @since Mar 30, 2025
 */
@SuppressWarnings({"DuplicatedCode", "unchecked", "unused", "NullableProblems"})
public class JSONArray extends ArrayList<Object> {

	private static class Raw extends ArrayList<Object> {
		private static final long serialVersionUID = 1L;

		public Raw(int initialCapacity) {
			super(initialCapacity);
		}

		public Raw() {
		}

		public Raw(Collection<?> c) {
			super(c);
		}
	}

	private static final long serialVersionUID = 1L;
	private final List<Object> raw;

	public JSONArray(ArrayList<Object> raw, boolean delegate) {
		if (delegate) {
			this.raw = raw;
		} else {
			this.raw = new Raw(raw);
		}
	}

	public JSONArray(Collection<?> c, boolean delegate) {
		if (delegate && c instanceof List) {
			this.raw = (List<Object>) c;
		} else {
			this.raw = new Raw(c);
		}
	}

	public JSONArray(Collection<?> c) {
		this(c, false);
	}

	public JSONArray(int initialCapacity) {
		this.raw = new Raw(initialCapacity);
	}

	public JSONArray() {
		this.raw = new Raw();
	}

	public static JSONArray of(Object... items) {
		JSONArray array = new JSONArray();
		Collections.addAll(array, items);
		return array;
	}

	public static JSONArray of(Object item) {
		JSONArray array = new JSONArray(1);
		array.add(item);
		return array;
	}

	public static JSONArray of(List<Object> collection) {
		return new JSONArray(collection, true);
	}

	@SuppressWarnings("rawtypes")
	public static JSONArray of(Collection collection) {
		return new JSONArray(collection, true);
	}

	@SuppressWarnings("rawtypes")
	public static JSONArray copyOf(Collection collection) {
		return new JSONArray(collection, false);
	}

	@SuppressWarnings({"unchecked"})
	public static JSONArray parseArray(String text) {
		ArrayList<Object> arrayList = Jacksons.toJavaObject(text, ArrayList.class);
		return new JSONArray(arrayList, true);
	}

	public static <T> List<T> parseArray(String text, Class<T> type) {
		return Jacksons.toJavaObject(text, TypeRefs.getType(List.class, type));
	}

	public static JSONArray parse(String text) {
		return parseArray(text);
	}

	public static JSONArray from(Object object) {
		if (object == null) {
			return null;
		}
		if (object instanceof JSONArray) {
			return (JSONArray) object;
		}
		return parseArray(Jacksons.toJsonString(object));
	}


	// region 重写


	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof JSONArray)) {
			return false;
		}
		JSONArray that = (JSONArray) o;
		return raw.equals(that.raw);
	}

	@Override
	public int hashCode() {
		return raw.hashCode();
	}


	@Override
	public Object set(int index, Object element) {
		int size = raw.size();
		if (index < 0) {
			index += size;
			if (index < 0) {
				// left join elem
				raw.add(0, element);
				return null;
			}
			return raw.set(
				index, element
			);
		}

		if (index < size) {
			return raw.set(
				index, element
			);
		}

		// max expansion (size + 4096)
		if (index < size + 4096) {
			while (index-- != size) {
				raw.add(null);
			}
			raw.add(element);
		}
		return null;
	}

	public JSONArray getJSONArray(int index) {
		Object value = get(index);

		if (value == null) {
			return null;
		}

		if (value instanceof JSONArray) {
			return (JSONArray) value;
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
			set(index, array);
			return array;
		}

		if (value instanceof Object[]) {
			return JSONArray.of((Object[]) value);
		}

		Class<?> valueClass = value.getClass();
		if (valueClass.isArray()) {
			int length = Array.getLength(value);
			JSONArray jsonArray = new JSONArray(length);
			for (int i = 0; i < length; i++) {
				Object item = Array.get(value, i);
				jsonArray.add(item);
			}
			return jsonArray;
		}

		return null;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public JSONObject getJSONObject(int index) {
		Object value = get(index);

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
			return new JSONObject(map, true);
		}

		if (value instanceof Map) {
			JSONObject object = new JSONObject((Map) value, true);
			set(index, object);
			return object;
		}

		Class valueClass = value.getClass();
		if (Beans.isBeanClass(valueClass)) {
			Map<String, Object> map = Jacksons.toJavaObject(Jacksons.toJsonString(value), Map.class);
			JSONObject object = new JSONObject(map, true);
			set(index, object);
			return object;
		}
		return null;
	}

	public String getString(int index) {
		Object value = get(index);

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

	public Double getDouble(int index) {
		Object value = get(index);

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

	public double getDoubleValue(int index) {
		Object value = get(index);

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

	public Float getFloat(int index) {
		Object value = get(index);

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

	public float getFloatValue(int index) {
		Object value = get(index);

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

	public Long getLong(int index) {
		Object value = get(index);
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

			return Long.parseLong(str);
		}

		if (value instanceof Boolean) {
			return (boolean) value ? Long.valueOf(1) : Long.valueOf(0);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to Long");
	}

	public long getLongValue(int index) {
		Object value = get(index);

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

			return Long.parseLong(str);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to long value");
	}

	public Integer getInteger(int index) {
		Object value = get(index);
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

			return Integer.parseInt(str);
		}

		if (value instanceof Boolean) {
			return (boolean) value ? Integer.valueOf(1) : Integer.valueOf(0);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to Integer");
	}

	public int getIntValue(int index) {
		Object value = get(index);

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

			return Integer.parseInt(str);
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to int value");
	}

	public Short getShort(int index) {
		Object value = get(index);

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

	public short getShortValue(int index) {
		Object value = get(index);

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

	public Byte getByte(int index) {
		Object value = get(index);

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

	public byte getByteValue(int index) {
		Object value = get(index);

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

	public Boolean getBoolean(int index) {
		Object value = get(index);

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

	public boolean getBooleanValue(int index) {
		Object value = get(index);

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

	public BigInteger getBigInteger(int index) {
		Object value = get(index);

		if (value == null) {
			return null;
		}

		if (value instanceof Number) {
			if (value instanceof BigInteger) {
				return (BigInteger) value;
			}

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

	public BigDecimal getBigDecimal(int index) {
		Object value = get(index);

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
			return new BigDecimal((String) value);
		}

		if (value instanceof Boolean) {
			return (boolean) value ? BigDecimal.ONE : BigDecimal.ZERO;
		}

		throw new JSONException("Can not cast '" + value.getClass() + "' to BigDecimal");
	}

	public Date getDate(int index) {
		Object value = get(index);

		if (value == null) {
			return null;
		}

		if (value instanceof Date) {
			return (Date) value;
		}

		if (value instanceof Number) {
			long millis = ((Number) value).longValue();
			if (millis == 0) {
				return null;
			}
			return new Date(millis);
		}
		return Converters.convertQuietly(Date.class, value);
	}

	public Date getDate(int index, Date defaultValue) {
		Date date = getDate(index);
		if (date == null) {
			date = defaultValue;
		}
		return date;
	}

	public Instant getInstant(int index) {
		Object value = get(index);

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

	public byte[] toJSONBBytes() {
		return Jacksons.toJsonBytes(raw);
	}

	public <T> T to(Type type) {
		if (type == String.class) {
			return (T) toString();
		}
		if (type == JSON.class || type == JSONArray.class) {
			return (T) this;
		}
		return Jacksons.toJavaObject(Jacksons.toJsonString(raw), type);
	}

	public <T> T to(Class<T> type) {
		if (type == String.class) {
			return (T) toString();
		}

		if (type == JSON.class || type == JSONArray.class) {
			return (T) this;
		}
		return Jacksons.toJavaObject(Jacksons.toJsonString(raw), type);
	}

	/**
	 * @deprecated please use {@link #to(Type)}
	 */
	@Deprecated
	public <T> T toJavaObject(Type type) {
		return to(type);
	}

	@SuppressWarnings({"unchecked"})
	public <T> List<T> toList(Class<T> itemClass) {
		List<T> list = new ArrayList<>(size());
		for (int i = 0; i < this.size(); i++) {
			Object item = this.get(i);

			T classItem;
			if (item instanceof JSONObject) {
				classItem = ((JSONObject) item).to(itemClass);
			} else if (item instanceof Map) {
				classItem = Jacksons.toJavaObject(Jacksons.toJsonString(item), itemClass);
			} else if (item == null || itemClass.isInstance(item)) {
				classItem = (T) item;
			} else if (Beans.isBeanClass(itemClass)) {
				classItem = Jacksons.toJavaObject(Jacksons.toJsonString(item), itemClass);
			} else {
				classItem = Converters.convert(itemClass, item);
			}
			list.add(classItem);
		}
		return list;
	}

	@SuppressWarnings({"unchecked"})
	public <T> T[] toArray(Class<T> itemClass) {
		T[] list = (T[]) Array.newInstance(itemClass, size());
		for (int i = 0; i < this.size(); i++) {
			Object item = this.get(i);
			T classItem;
			if (item instanceof JSONObject) {
				classItem = ((JSONObject) item).to(itemClass);
			} else if (item instanceof Map) {
				classItem = Jacksons.toJavaObject(Jacksons.toJsonString(item), itemClass);
			} else if (item == null || itemClass.isInstance(item)) {
				classItem = (T) item;
			} else if (Beans.isBeanClass(itemClass)) {
				classItem = Jacksons.toJavaObject(Jacksons.toJsonString(item), itemClass);
			} else {
				classItem = Converters.convert(itemClass, item);
			}
			list[i] = classItem;
		}

		return list;
	}

	public <T> List<T> toJavaList(Class<T> clazz) {
		return toList(clazz);
	}

	@SuppressWarnings({"unchecked"})
	public <T> T getObject(int index, Type type) {
		Object value = get(index);

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

	@SuppressWarnings({"unchecked"})
	public <T> T getObject(int index, Class<T> type) {
		Object value = get(index);

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

	public <T> T getObject(int index, Function<JSONObject, T> creator) {
		JSONObject object = getJSONObject(index);

		if (object == null) {
			return null;
		}

		return creator.apply(object);
	}

	public JSONObject addObject() {
		JSONObject object = new JSONObject();
		add(object);
		return object;
	}

	public JSONArray addArray() {
		JSONArray array = new JSONArray();
		add(array);
		return array;
	}

	public JSONArray fluentAdd(Object element) {
		add(element);
		return this;
	}

	public JSONArray fluentClear() {
		clear();
		return this;
	}

	public JSONArray fluentRemove(int index) {
		remove(index);
		return this;
	}

	public JSONArray fluentSet(int index, Object element) {
		set(index, element);
		return this;
	}

	public JSONArray fluentRemove(Object o) {
		remove(o);
		return this;
	}

	public JSONArray fluentRemoveAll(Collection<?> c) {
		removeAll(c);
		return this;
	}

	public JSONArray fluentAddAll(Collection<?> c) {
		addAll(c);
		return this;
	}

	@SuppressWarnings({"MethodDoesntCallSuperMethod"})
	@Override
	public Object clone() {
		return new JSONArray(raw, false);
	}


	// endregion 重写

	// region delegate

	@Override
	public void trimToSize() {
		if (raw instanceof ArrayList) {
			((ArrayList<Object>) raw).trimToSize();
		}
	}

	@Override
	public void ensureCapacity(int minCapacity) {
		if (raw instanceof ArrayList) {
			((ArrayList<Object>) raw).ensureCapacity(minCapacity);
		}
	}

	@Override
	public int size() {
		return raw.size();
	}

	@Override
	public boolean isEmpty() {
		return raw.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return raw.contains(o);
	}

	@Override
	public int indexOf(Object o) {
		return raw.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return raw.lastIndexOf(o);
	}

	@Override
	public Object[] toArray() {
		return raw.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return raw.toArray(a);
	}

	@Override
	public Object get(int index) {
		return raw.get(index);
	}


	@Override
	public boolean add(Object o) {
		return raw.add(o);
	}

	@Override
	public void add(int index, Object element) {
		raw.add(index, element);
	}

	@Override
	public Object remove(int index) {
		return raw.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		return raw.remove(o);
	}

	@Override
	public void clear() {
		raw.clear();
	}

	@Override
	public boolean addAll(Collection<?> c) {
		return raw.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<?> c) {
		return raw.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return raw.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return raw.retainAll(c);
	}

	@Override
	public ListIterator<Object> listIterator(int index) {
		return raw.listIterator(index);
	}

	@Override
	public ListIterator<Object> listIterator() {
		return raw.listIterator();
	}

	@Override
	public Iterator<Object> iterator() {
		return raw.iterator();
	}

	@Override
	public List<Object> subList(int fromIndex, int toIndex) {
		return raw.subList(fromIndex, toIndex);
	}

	@Override
	public void forEach(Consumer<? super Object> action) {
		raw.forEach(action);
	}

	@Override
	public Spliterator<Object> spliterator() {
		return raw.spliterator();
	}

	@Override
	public boolean removeIf(Predicate<? super Object> filter) {
		return raw.removeIf(filter);
	}

	@Override
	public void replaceAll(UnaryOperator<Object> operator) {
		raw.replaceAll(operator);
	}

	@Override
	public void sort(Comparator<? super Object> c) {
		raw.sort(c);
	}


	@Override
	public boolean containsAll(Collection<?> c) {
		return raw.containsAll(c);
	}


	@Override
	public Stream<Object> stream() {
		return raw.stream();
	}

	@Override
	public Stream<Object> parallelStream() {
		return raw.parallelStream();
	}


	// endregion delegate


}
