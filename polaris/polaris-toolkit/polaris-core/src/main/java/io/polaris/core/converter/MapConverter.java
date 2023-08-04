package io.polaris.core.converter;

import io.polaris.core.json.IJsonSerializer;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.lang.Types;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.lang.copier.Copiers;
import io.polaris.core.lang.copier.CopyOptions;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.service.StatefulServiceLoader;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Qt
 * @since 1.8
 */
public class MapConverter<K, V> extends AbstractConverter<Map<K, V>> {
	/** Map类型 */
	private final JavaType<Map<K, V>> mapType;
	/** 键类型 */
	private final JavaType<K> keyType;
	/** 值类型 */
	private final JavaType<V> valueType;

	public MapConverter(JavaType<Map<K, V>> mapType) {
		this(mapType, JavaType.of(mapType.getActualType(Map.class, 0)),
			JavaType.of(mapType.getActualType(Map.class, 1)));
	}

	public MapConverter(Type mapType) {
		this(JavaType.of(mapType));
	}

	public MapConverter(JavaType<Map<K, V>> mapType, JavaType<K> keyType, JavaType<V> valueType) {
		this.mapType = mapType;
		this.keyType = keyType == null ? (JavaType<K>) JavaType.of(Object.class) : keyType;
		this.valueType = valueType == null ? (JavaType<V>) JavaType.of(Object.class) : valueType;
	}


	@Override
	public JavaType<Map<K, V>> getTargetType() {
		return this.mapType;
	}

	@Override
	protected <S> Map<K, V> doConvert(S value, JavaType<Map<K, V>> targetType, JavaType<S> sourceType) {
		if (this.mapType.getRawClass().isAssignableFrom(sourceType.getRawClass())) {
			boolean matchKeyType = false, matchValueType = false;
			{
				Type sourceKeyType = sourceType.getActualType(Map.class, 0);
				if (sourceKeyType instanceof Class) {
					if (this.keyType.getRawClass().isAssignableFrom((Class<?>) sourceKeyType)) {
						matchKeyType = true;
					}
				} else if (this.keyType.getRawType() == sourceKeyType) {
					matchKeyType = true;
				}
			}
			{
				Type sourceValueType = sourceType.getActualType(Map.class, 1);
				if (sourceValueType instanceof Class) {
					if (this.valueType.getRawClass().isAssignableFrom((Class<?>) sourceValueType)) {
						matchValueType = true;
					}
				} else if (this.valueType.getRawType() == sourceValueType) {
					matchValueType = true;
				}
			}
			if (matchKeyType && matchValueType) {
				// 元素泛型匹配
				return (Map<K, V>) value;
			}
		}

		if (value instanceof Map) {
			Map<K, V> map;
			try {
				map = (Map) Reflects.newInstanceIfPossible(Types.getClass(mapType));
				if (map == null) {
					map = new HashMap<>();
				}
			} catch (Exception e) {
				map = new HashMap<>();
			}
			convertMapToMap((Map) value, map);
			return map;
		} else if (Beans.isBeanClass(value.getClass())) {
			LinkedHashMap<String, Object> tmp = Copiers.copy(value, new LinkedHashMap<>(), CopyOptions.create().ignoreNull(false));
			// 二次转换，转换键值类型
			return doConvert(tmp, targetType, JavaType.of(new TypeRef<LinkedHashMap<String, Object>>() {
			}));
		} else if (value instanceof CharSequence) {
			// 扩展json实现，
			Optional<IJsonSerializer> optional = StatefulServiceLoader.load(IJsonSerializer.class).optionalService();
			if (optional.isPresent()) {
				String json = value.toString();
				return optional.get().deserialize(json, targetType.getRawType());
			}
		}
		throw new UnsupportedOperationException();
	}

	private void convertMapToMap(Map<?, ?> srcMap, Map<K, V> targetMap) {
		srcMap.forEach((key, value) -> targetMap.put(ConverterRegistry.INSTANCE.convert(this.keyType, key),
			ConverterRegistry.INSTANCE.convert(this.valueType, value)));
	}
}
