package io.polaris.core.converter;

import io.polaris.core.collection.Iterables;
import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.io.Serializations;
import io.polaris.core.json.JsonSerializer;
import io.polaris.core.lang.JavaType;
import io.polaris.core.log.ILogger;
import io.polaris.core.service.StatefulServiceLoader;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @author Qt
 * @since 1.8
 */
public class ArrayConverter<T> extends AbstractConverter<T[]> {
	private static final ILogger log = ILogger.of(ArrayConverter.class);
	private final JavaType<T[]> targetType;
	private final JavaType<T> targetComponentType;

	public ArrayConverter(Class<T[]> targetType) {
		this(targetType == null ? null : JavaType.of(targetType));
	}

	public ArrayConverter(JavaType<T[]> targetType) {
		if (targetType == null) {
			// 默认Object数组
			this.targetType = JavaType.of((Type) Object[].class);
			this.targetComponentType = JavaType.of((Type) Object.class);
		} else {
			if (targetType.getRawType() instanceof GenericArrayType) {
				this.targetType = targetType;
				this.targetComponentType = JavaType.of(((GenericArrayType) targetType.getRawType()).getGenericComponentType());
			} else if (targetType.getRawClass().isArray()) {
				this.targetType = targetType;
				this.targetComponentType = JavaType.of((Type) targetType.getRawClass().getComponentType());
			} else {
				//用户传入类为非数组时，按照数组元素类型对待
				this.targetComponentType = JavaType.of(targetType.getRawType());
				this.targetType = JavaType.of(new GenericArrayType() {
					@Override
					public Type getGenericComponentType() {
						return targetComponentType.getRawType();
					}

					@Override
					public String toString() {
						return targetComponentType.getRawType().getTypeName() + "[]";
					}
				});
			}
		}
	}

	@Override
	public JavaType<T[]> getTargetType() {
		return this.targetType;
	}

	@Override
	protected <S> T[] doConvert(S value, JavaType<T[]> targetType, JavaType<S> sourceType) {
		if (sourceType.getRawType() instanceof Class) {
			if (((Class<?>) sourceType.getRawType()).isArray() && targetComponentType.getRawClass().isAssignableFrom(((Class<?>) sourceType.getRawType()).getComponentType())) {
				return (T[]) value;
			}
		} else if (sourceType.getRawType() instanceof GenericArrayType) {
			Type genericComponentType = ((GenericArrayType) sourceType.getRawType()).getGenericComponentType();
			if (this.targetComponentType == genericComponentType) {
				return (T[]) value;
			}
		}
		return (T[]) (value.getClass().isArray() ? convertArrayToArray(value) : convertObjectToArray(value));
	}

	private Object convertArrayToArray(Object array) {
		final int len = Array.getLength(array);
		final Object result = Array.newInstance(targetComponentType.getRawClass(), len);
		for (int i = 0; i < len; i++) {
			Array.set(result, i, convertComponentType(Array.get(array, i)));
		}
		return result;
	}

	private String[] splitCharSequence(CharSequence value) {
		int begin = 0;
		int end = value.length();
		for (int i = 0; i < value.length(); i++) {
			char ch = value.charAt(i);
			if (Character.isWhitespace(ch)) {
				begin = i;
			} else {
				if (ch == '[') {
					begin = i + 1;
				}
				break;
			}
		}
		for (int i = value.length() - 1; i >= 0; i--) {
			char ch = value.charAt(i);
			if (Character.isWhitespace(ch)) {
				end = i;
			} else {
				if (ch == ']') {
					end = i;
				}
				break;
			}
		}
		if (end > begin) {
			return value.subSequence(begin, end).toString().trim().split(SymbolConsts.COMMA);
		} else {
			return new String[]{""};
		}
	}

	private Object convertObjectToArray(Object value) {
		if (value instanceof CharSequence) {
			if (targetComponentType.getRawClass() == char.class || targetComponentType.getRawClass() == Character.class) {
				return convertArrayToArray(value.toString().toCharArray());
			}
			try {
				// 扩展json实现，
				Optional<JsonSerializer> optional = StatefulServiceLoader.load(JsonSerializer.class).optionalService();
				if (optional.isPresent()) {
					String json = value.toString();
					return optional.get().deserialize(json, targetType.getRawType());
				}
			} catch (Exception e) {
				log.warn("解析JSON失败：{}", e.getMessage());
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage(), e);
				}
			}
			return convertArrayToArray(splitCharSequence((CharSequence) value));
		}

		if (value instanceof List) {
			// List转数组
			List<?> list = (List<?>) value;
			Object result = Array.newInstance(targetComponentType.getRawClass(), list.size());
			for (int i = 0; i < list.size(); i++) {
				Array.set(result, i, convertComponentType(list.get(i)));
			}
			return result;
		}
		if (value instanceof Collection) {
			// 集合转数组
			Collection<?> collection = (Collection<?>) value;
			Object result = Array.newInstance(targetComponentType.getRawClass(), collection.size());
			int i = 0;
			for (Object element : collection) {
				Array.set(result, i, convertComponentType(element));
				i++;
			}
			return result;
		}
		if (value instanceof Iterable) {
			List<?> list = Iterables.asList((Iterable<?>) value);
			Object result = Array.newInstance(targetComponentType.getRawClass(), list.size());
			for (int i = 0; i < list.size(); i++) {
				Array.set(result, i, convertComponentType(list.get(i)));
			}
			return result;
		}
		if (value instanceof Iterator) {
			List<?> list = Iterables.asList((Iterator<?>) value);
			Object result = Array.newInstance(targetComponentType.getRawClass(), list.size());
			for (int i = 0; i < list.size(); i++) {
				Array.set(result, i, convertComponentType(list.get(i)));
			}
			return result;
		}
		if (value instanceof Serializable && byte.class == targetComponentType.getRawClass()) {
			// 用户可能想序列化指定对象
			return Serializations.serialize(value);
		}
		return convertToSingleElementArray(value);
	}

	private Object convertToSingleElementArray(Object value) {
		final Object result = Array.newInstance(targetComponentType.getRawClass(), 1);
		Array.set(result, 0, convertComponentType(value));
		return result;
	}

	private Object convertComponentType(Object value) {
		return ConverterRegistry.INSTANCE.convert(this.targetComponentType, value, null);
	}
}
