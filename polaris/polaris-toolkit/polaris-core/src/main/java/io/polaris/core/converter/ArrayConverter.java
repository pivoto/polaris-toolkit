package io.polaris.core.converter;

import io.polaris.core.collection.Iterables;
import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.io.Serializations;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
public class ArrayConverter extends AbstractConverter<Object> {
	private final Class<?> targetType;
	private final Class<?> targetComponentType;

	public ArrayConverter(Class<?> targetType) {
		if (null == targetType) {
			// 默认Object数组
			targetType = Object[].class;
		}

		if (targetType.isArray()) {
			this.targetType = targetType;
			this.targetComponentType = targetType.getComponentType();
		} else {
			//用户传入类为非数组时，按照数组元素类型对待
			this.targetComponentType = targetType;
			this.targetType = Array.newInstance(targetType, 0).getClass();
		}
	}

	@Override
	public Class getTargetType() {
		return this.targetType;
	}

	@Override
	protected Object convertInternal(Object value, Class<? extends Object> targetType) {
		return value.getClass().isArray() ? convertArrayToArray(value) : convertObjectToArray(value);
	}

	private Object convertArrayToArray(Object array) {
		final Class<?> valueComponentType = array.getClass().getComponentType();

		if (valueComponentType == targetComponentType) {
			return array;
		}

		final int len = Array.getLength(array);
		final Object result = Array.newInstance(targetComponentType, len);

		for (int i = 0; i < len; i++) {
			Array.set(result, i, convertComponentType(Array.get(array, i)));
		}
		return result;
	}

	private Object convertObjectToArray(Object value) {
		if (value instanceof CharSequence) {
			if (targetComponentType == char.class || targetComponentType == Character.class) {
				return convertArrayToArray(value.toString().toCharArray());
			}
			String[] arr = value.toString().split(SymbolConsts.COMMA);
			return convertArrayToArray(arr);
		}

		Object result;
		if (value instanceof List) {
			// List转数组
			List<?> list = (List<?>) value;
			result = Array.newInstance(targetComponentType, list.size());
			for (int i = 0; i < list.size(); i++) {
				Array.set(result, i, convertComponentType(list.get(i)));
			}
		} else if (value instanceof Collection) {
			// 集合转数组
			Collection<?> collection = (Collection<?>) value;
			result = Array.newInstance(targetComponentType, collection.size());
			int i = 0;
			for (Object element : collection) {
				Array.set(result, i, convertComponentType(element));
				i++;
			}
		} else if (value instanceof Iterable) {
			List<?> list = Iterables.asList((Iterable<?>) value);
			result = Array.newInstance(targetComponentType, list.size());
			for (int i = 0; i < list.size(); i++) {
				Array.set(result, i, convertComponentType(list.get(i)));
			}
		} else if (value instanceof Iterator) {
			List<?> list = Iterables.asList((Iterator<?>) value);
			result = Array.newInstance(targetComponentType, list.size());
			for (int i = 0; i < list.size(); i++) {
				Array.set(result, i, convertComponentType(list.get(i)));
			}
		} else if (value instanceof Serializable && byte.class == targetComponentType) {
			// 用户可能想序列化指定对象
			result = Serializations.serialize(value);
		} else {
			result = convertToSingleElementArray(value);
		}
		return result;
	}

	private Object convertToSingleElementArray(Object value) {
		final Object result = Array.newInstance(targetComponentType, 1);
		Array.set(result, 0, convertComponentType(value));
		return result;
	}

	private Object convertComponentType(Object value) {
		return ConverterRegistry.INSTANCE.convertQuietly(this.targetComponentType, value, null);
	}
}
