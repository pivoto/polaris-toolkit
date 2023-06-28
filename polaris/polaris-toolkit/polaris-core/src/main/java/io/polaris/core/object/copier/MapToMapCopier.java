package io.polaris.core.object.copier;

import io.polaris.core.lang.Types;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
public class MapToMapCopier extends BaseCopier<Map, Map> {
	private final Type targetType;

	/**
	 * @param source      来源Map
	 * @param target      目标Bean对象
	 * @param targetType  目标泛型类型
	 * @param copyOptions 拷贝选项
	 */
	public MapToMapCopier(Map source, Map target, Type targetType, CopyOptions copyOptions) {
		super(source, target, copyOptions);
		this.targetType = targetType;
	}

	@Override
	public Map copy() {
		this.source.forEach((key, value) -> {
			if (null == key) {
				return;
			}
			if (options.ignoreNull && value == null) {
				return;
			}

			final String keyStr = options.editPropertyName(key.toString());
			if (keyStr == null) {
				return;
			}
			if (!options.isIncludePropertyName(keyStr)) {
				return;
			}

			if (!options.override && null != target.get(keyStr)) {
				return;
			}

			final Type[] typeArguments = Types.getTypeArguments(this.targetType);
			if (null != typeArguments) {
				value = options.convert(typeArguments[1], value);
				value = options.editPropertyValue(keyStr, value);
			}
			if (value == null && options.ignoreNull) {
				return;
			}
			// 目标赋值
			target.put(keyStr, value);
		});
		return this.target;
	}
}
