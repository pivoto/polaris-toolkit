package io.polaris.core.converter;

import io.polaris.core.json.IJsonSerializer;
import io.polaris.core.lang.Types;
import io.polaris.core.object.Beans;
import io.polaris.core.object.Copiers;
import io.polaris.core.object.copier.CopyOptions;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.service.StatefulServiceLoader;

import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Qt
 * @since 1.8
 */
public class MapConverter extends AbstractConverter<Map<?, ?>> {
	/**
	 * Map类型
	 */
	private final Type mapType;
	/**
	 * 键类型
	 */
	private final Type keyType;
	/**
	 * 值类型
	 */
	private final Type valueType;

	public MapConverter(Type mapType) {
		this(mapType, Types.getTypeArgument(mapType, 0), Types.getTypeArgument(mapType, 1));
	}

	public MapConverter(Type mapType, Type keyType, Type valueType) {
		this.mapType = mapType;
		this.keyType = keyType;
		this.valueType = valueType;
	}

	public Class<Map<?, ?>> getTargetType() {
		return (Class<Map<?, ?>>) Types.getClass(this.mapType);
	}

	@Override
	protected Map<?, ?> convertInternal(Object value, Class<? extends Map<?, ?>> targetType) {
		if (value instanceof Map) {
			final Class<?> valueClass = value.getClass();
			if (valueClass.equals(this.mapType)) {
				final Type[] typeArguments = Types.getTypeArguments(valueClass);
				if (null != typeArguments //
					&& 2 == typeArguments.length//
					&& Objects.equals(this.keyType, typeArguments[0]) //
					&& Objects.equals(this.valueType, typeArguments[1])) {
					//对于键值对类型一致的Map对象，不再做转换，直接返回原对象
					return (Map) value;
				}
			}
			Map map;
			try {
				map = (Map) Reflects.newInstanceIfPossible(Types.getClass(mapType));
			} catch (Exception e) {
				map = new HashMap();
			}
			if (map != null) {
				map = new HashMap();
			}
			convertMapToMap((Map) value, map);
			return map;
		} else if (Beans.isBeanClass(value.getClass())) {
			LinkedHashMap<Object, Object> tmp = Copiers.copy(value, new LinkedHashMap<>(), CopyOptions.create().ignoreNull(false));
			// 二次转换，转换键值类型
			return convertInternal(tmp, targetType);
		} else if (value instanceof CharSequence) {
			// 扩展json实现，
			Optional<IJsonSerializer> optional = StatefulServiceLoader.load(IJsonSerializer.class).optionalService();
			if (optional.isPresent()) {
				String json = value.toString();
				return optional.get().deserialize(json, targetType);
			}
		}
		throw new UnsupportedOperationException();
	}

	private void convertMapToMap(Map<?, ?> srcMap, Map<Object, Object> targetMap) {
		srcMap.forEach((key, value) -> {
			key = Types.isUnknown(this.keyType) ? key : ConverterRegistry.INSTANCE.convert(this.keyType, key);
			value = Types.isUnknown(this.valueType) ? value : ConverterRegistry.INSTANCE.convert(this.valueType, value);
			targetMap.put(key, value);
		});
	}
}
