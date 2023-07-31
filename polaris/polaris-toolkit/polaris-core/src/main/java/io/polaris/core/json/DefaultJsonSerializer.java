package io.polaris.core.json;

import io.polaris.core.converter.ConverterRegistry;
import io.polaris.core.date.Dates;
import io.polaris.core.object.BeanMap;
import io.polaris.core.object.Beans;
import io.polaris.core.script.Evaluator;
import io.polaris.core.script.ScriptEvaluators;
import io.polaris.core.service.ServiceDefault;
import jdk.nashorn.api.scripting.JSObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Qt
 * @since 1.8
 */
@ServiceDefault
public class DefaultJsonSerializer implements IJsonSerializer {
	@Override
	public String serialize(Object value) {
		try {
			if (value == null) {
				return "null";
			}
			if (value instanceof TemporalAccessor) {
				return Objects.toString(Dates.toDate((TemporalAccessor) value).getTime());
			}
			if (value instanceof Calendar) {
				return Objects.toString(((Calendar) value).getTime().getTime());
			}
			if (value instanceof Date) {
				return Objects.toString(((Date) value).getTime());
			}
			if (value instanceof BigDecimal) {
				return "\"" + ((BigDecimal) value).toPlainString() + "\"";
			}
			if (value instanceof Long
				|| value instanceof BigInteger
				|| value instanceof AtomicLong) {
				return "\"" + value + "\"";
			}
			if (value instanceof Number
				|| value instanceof Boolean || value instanceof AtomicBoolean
				|| value.getClass().isPrimitive()) {
				return Objects.toString(value);
			}
			if (value instanceof Character || value instanceof CharSequence) {
				return wrapperString(value.toString());
			}
			if (value instanceof Enum) {
				return "\"" + ((Enum<?>) value).name() + "\"";
			}


			StringBuilder sb = new StringBuilder();
			if (value.getClass().isArray()) {
				sb.append("[");
				int len = Array.getLength(value);
				if (len > 0) {
					sb.append(serialize(Array.get(value, 0)));
					for (int i = 1; i < len; i++) {
						sb.append(",").append(serialize(Array.get(value, i)));
					}
				}
				sb.append("]");
				return sb.toString();
			}
			if (value instanceof Map) {
				sb.append("{");
				Set<? extends Map.Entry<?, ?>> entries = ((Map<?, ?>) value).entrySet();
				Iterator<? extends Map.Entry<?, ?>> iter = entries.iterator();
				if (iter.hasNext()) {
					Map.Entry<?, ?> next = iter.next();
					sb.append(wrapperString(next.getKey().toString())).append(":")
						.append(serialize(next.getValue()));
					while (iter.hasNext()) {
						next = iter.next();
						sb.append(",").append(wrapperString(next.getKey().toString())).append(":")
							.append(serialize(next.getValue()));
					}
				}
				sb.append("}");
				return sb.toString();
			}
			if (value instanceof Collection) {
				sb.append("[");
				Iterator<?> iter = ((Collection<?>) value).iterator();
				if (iter.hasNext()) {
					Object next = iter.next();
					sb.append(serialize(next));
					while (iter.hasNext()) {
						next = iter.next();
						sb.append(",").append(serialize(next));
					}
				}
				sb.append("]");
				return sb.toString();
			}
			if (Beans.isBeanClass(value.getClass())) {
				BeanMap beanMap = Beans.asBeanMap(value);
				sb.append("{");
				Set<String> keys = beanMap.keySet();
				Iterator<String> iter = keys.iterator();
				if (iter.hasNext()) {
					String next = iter.next();
					sb.append(next).append(":")
						.append(serialize(beanMap.get(next)));
					while (iter.hasNext()) {
						next = iter.next();
						sb.append(",").append(next).append(":")
							.append(serialize(beanMap.get(next)));
					}
				}
				sb.append("}");
				return sb.toString();
			}
			return wrapperString(value.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String wrapperString(String str) {
		return "\"" + str.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
	}

	@Override
	public <T> T deserialize(String json, Type type) {
		try {
			Evaluator engine = ScriptEvaluators.getEvaluator("javascript");
			String content = "JSON.parse(input)";
			Object rs = engine.eval(content, json);
			Object javaObject = toJavaObject(rs);
			return ConverterRegistry.INSTANCE.convert(type, javaObject);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Object toJavaObject(Object rs) {
		if (rs == null) {
			return null;
		}
		boolean isJsObject = rs instanceof JSObject;
		if (isJsObject) {
			if (((JSObject) rs).isArray()) {
				Collection<Object> values = ((JSObject) rs).values();
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
			Set<String> keys = ((JSObject) rs).keySet();
			Map<Object, Object> map = new HashMap<>(keys.size());
			for (String key : keys) {
				map.put(key, toJavaObject(((JSObject) rs).getMember(key)));
			}
			return map;
		}
		return rs;
	}
}
