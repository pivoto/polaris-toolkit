package io.polaris.core.json;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import io.polaris.core.asm.reflect.BeanAccess;
import io.polaris.core.converter.Converters;
import io.polaris.core.script.Evaluator;
import io.polaris.core.script.ScriptEvaluators;
import io.polaris.core.service.ServiceDefault;
import io.polaris.core.time.Dates;

/**
 * @author Qt
 * @since 1.8
 */
@ServiceDefault
public class DefaultJsonSerializer implements JsonSerializer {
	@Override
	public String serialize(Object value) {
		return _serialize(value).toString();
	}


	private CharSequence _serialize(Object value) {
		try {
			if (value == null) {
				return "null";
			}
			if (value instanceof TemporalAccessor) {
				return new StringBuilder(22).append(Dates.toDate((TemporalAccessor) value).getTime());
			}
			if (value instanceof Calendar) {
				return new StringBuilder(22).append(((Calendar) value).getTime().getTime());
			}
			if (value instanceof Date) {
				return new StringBuilder(22).append(((Date) value).getTime());
			}
			if (value instanceof BigDecimal) {
				return new StringBuilder(32)
					.append("\"")
					.append(((BigDecimal) value).toPlainString())
					.append("\"");
			}
			if (value instanceof Long
				|| value instanceof BigInteger
				|| value instanceof AtomicLong) {
				return new StringBuilder(32)
					.append("\"")
					.append(value)
					.append("\"");
			}
			if (value instanceof Number
				|| value instanceof Boolean || value instanceof AtomicBoolean
				|| value.getClass().isPrimitive()) {
				return Objects.toString(value);
			}
			if (value instanceof Character) {
				return new StringBuilder(3)
					.append("\"")
					.append(((Character) value).charValue())
					.append("\"");
			}
			if (value instanceof CharSequence) {
				String str = value.toString();
				return new StringBuilder(str.length() + 8)
					.append("\"")
					.append(str.replace("\\", "\\\\").replace("\"", "\\\""))
					.append("\"");
			}
			if (value instanceof Enum) {
				String name = ((Enum<?>) value).name();
				return new StringBuilder(name.length() + 2)
					.append("\"")
					.append(name)
					.append("\"");
			}


			StringBuilder sb = new StringBuilder();
			if (value instanceof Map) {
				sb.append("{");
				Set<? extends Map.Entry<?, ?>> entries = ((Map<?, ?>) value).entrySet();
				Iterator<? extends Map.Entry<?, ?>> iter = entries.iterator();
				if (iter.hasNext()) {
					Map.Entry<?, ?> next = iter.next();
					String str1 = next.getKey().toString();
					sb.append("\"" + str1.replace("\\", "\\\\").replace("\"", "\\\"") + "\"").append(":")
						.append(_serialize(next.getValue()));
					while (iter.hasNext()) {
						next = iter.next();
						String str = next.getKey().toString();
						sb.append(",").append("\"" + str.replace("\\", "\\\\").replace("\"", "\\\"") + "\"").append(":")
							.append(_serialize(next.getValue()));
					}
				}
				sb.append("}");
				return sb;
			}
			if (value instanceof Collection) {
				sb.append("[");
				if (value instanceof List && value instanceof RandomAccess) {
					int size = ((List<?>) value).size();
					if (size > 0) {
						sb.append(_serialize(((List<?>) value).get(0)));
					}
					for (int i = 1; i < size; i++) {
						Object next = ((List<?>) value).get(i);
						sb.append(",").append(_serialize(next));
					}
				} else {
					Iterator<?> iter = ((Collection<?>) value).iterator();
					if (iter.hasNext()) {
						Object next = iter.next();
						sb.append(_serialize(next));
						while (iter.hasNext()) {
							next = iter.next();
							sb.append(",").append(_serialize(next));
						}
					}
				}
				sb.append("]");
				return sb;
			}
			if (value.getClass().isArray()) {
				sb.append("[");
				int len = Array.getLength(value);
				if (len > 0) {
					sb.append(_serialize(Array.get(value, 0)));
					for (int i = 1; i < len; i++) {
						sb.append(",").append(_serialize(Array.get(value, i)));
					}
				}
				sb.append("]");
				return sb;
			}

			// bean
			{
				BeanAccess<?> access = BeanAccess.get(value.getClass());
				sb.append("{");
				boolean first = true;
				{
					Map<String, Integer> getterIndices = access.getterIndices();
					for (Map.Entry<String, Integer> entry : getterIndices.entrySet()) {
						String key = entry.getKey();
						Integer idx = entry.getValue();
						Object val = access.getIndexProperty(value, idx);
						if (first) {
							first = false;
						} else {
							sb.append(",");
						}
						sb.append("\"" + key.replace("\\", "\\\\").replace("\"", "\\\"") + "\"").append(":")
							.append(_serialize(val));
					}
				}
				{
					Map<String, Integer> fieldIndices = access.fieldIndices();
					for (Map.Entry<String, Integer> entry : fieldIndices.entrySet()) {
						String key = entry.getKey();
						Integer idx = entry.getValue();
						Object val = access.getIndexField(value, idx);
						if (first) {
							first = false;
						} else {
							sb.append(",");
						}
						sb.append("\"" + key.replace("\\", "\\\\").replace("\"", "\\\"") + "\"").append(":")
							.append(_serialize(val));
					}
				}
				sb.append("}");
				return sb;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> T deserialize(String json, Type type) {
		try {
			Evaluator engine = ScriptEvaluators.getEvaluator("javascript");
			String content = "JSON.parse(input)";
			Object rs = engine.eval(content, json);
			Object javaObject = toJavaObject(rs);
			return Converters.convert(type, javaObject);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private Object toJavaObject(Object rs) throws ReflectiveOperationException{
		if (rs == null) {
			return null;
		}
		Class<?> jsObjectClass = Class.forName("jdk.nashorn.api.scripting.JSObject");
		boolean isJsObject = jsObjectClass.isInstance(rs);
		if (isJsObject) {
			Boolean isArray = (Boolean) jsObjectClass.getMethod("isArray").invoke(rs);
			if(isArray){
				Collection<Object> values = (Collection<Object>) jsObjectClass.getMethod("values").invoke(rs);
				List<Object> list = new ArrayList<>(values.size());
				for (Object value : values) {
					list.add(toJavaObject(value));
				}
				return list;
			}
		}
		if (rs instanceof Map) {
			Map<Object, Object> map = new HashMap<>(((Map<?, ?>) rs).size());
			for (Map.Entry<?, ?> entry : ((Map<?, ?>) rs).entrySet()) {
				Object k = entry.getKey();
				Object v = entry.getValue();
				map.put(toJavaObject(k), toJavaObject(v));
			}
			return map;
		}
		if (isJsObject) {
			Set<String> keys = (Set<String>) jsObjectClass.getMethod("keySet").invoke(rs);;
			Map<Object, Object> map = new HashMap<>(keys.size());
			for (String key : keys) {
				Object val = jsObjectClass.getMethod("getMember", String.class).invoke(rs);
				map.put(key, toJavaObject(val));
			}
			return map;
		}
		return rs;
	}
}
