package io.polaris.core.lang.copier;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.polaris.core.converter.Converters;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;

/**
 * @author Qt
 * @since 1.8
 */
public class CopyOptions {

	public static final BiFunction<Type, Object, Object> DEFAULT_CONVERTER = Converters::convertQuietly;
	public static final BiFunction<Type, Object, Object> PROPERTY_EDITOR_CONVERTER = (type, value) -> {
		if (value == null) {
			return null;
		}
		Class<?> rawClass = Types.getWrapperClass(Types.getClass(type));
		Class<?> valueClass = Types.getWrapperClass(value.getClass());
		if (rawClass.isAssignableFrom(valueClass)) {
			return value;
		}
		PropertyEditor editor = PropertyEditorManager.findEditor((Class<?>) type);
		if (editor != null) {
			if (value instanceof String) {
				editor.setAsText((String) value);
			} else {
				editor.setAsText(value.toString());
			}
			return editor.getValue();
		}
		return value;
	};
	/** 是否忽略属性注入错误 */
	private boolean ignoreError = true;
	/** 需要忽略的源属性名 */
	private Set<String> ignoreKeys;
	/** 是否忽略属性大小写 */
	private boolean ignoreCase = false;
	/** 是否忽略JavaBean属性的首字母大小写处理模式，可应对lombok对双大写字母前缀字段的错误处理 */
	private boolean ignoreCapitalize = false;
	/** 是否支持属性下划线转驼峰 */
	private boolean enableUnderlineToCamelCase = false;
	/** 是否支持属性驼峰转下划线 */
	private boolean enableCamelToUnderlineCase = false;
	/** 是否忽略空值，当源对象的值为null时，true: 忽略而不注入此值，false: 注入null */
	private boolean ignoreNull = true;
	/** 是否覆盖目标值，如果不覆盖，会先读取目标对象的值，非null则写，否则忽略。如果覆盖，则不判断直接写 */
	private boolean override = true;
	/** 属性名转换编辑器，用于自定义属性转换规则，例如驼峰转下划线等 */
	private Function<String, String> keyMapping;
	/** 属性值转换编辑器，用于自定义属性值转换规则，例如null转""等 */
	private BiFunction<String, Object, Object> valueMapping;
	/** 是否启用类型转换器 */
	private boolean enableConverter = true;
	/** 自定义类型转换器 */
	private BiFunction<Type, Object, Object> converter = DEFAULT_CONVERTER;


	public static CopyOptions create() {
		return new CopyOptions();
	}

	public boolean hasKeyMapping() {
		return keyMapping != null;
	}

	public boolean isIgnoredKey(String key) {
		return ignoreKeys != null && ignoreKeys.contains(key);
	}

	public Object convert(Type type, Object value) {
		if (value == null) {
			return null;
		}
		if (JavaType.of(type).isInstance(value)) {
			return value;
		}
		if (!enableConverter) {
			return null;
		}
		if (converter == null) {
			return null;
		}
		return converter.apply(type, value);
	}

	public String editKey(String key) {
		if (keyMapping != null) {
			return keyMapping.apply(key);
		}
		return key;
	}

	public Object editValue(String key, Object value) {
		if (valueMapping != null) {
			return valueMapping.apply(key, value);
		}
		return value;
	}


	// region setters

	/** 是否忽略空值，当源对象的值为null时，true: 忽略而不注入此值，false: 注入null */
	public CopyOptions ignoreNull(boolean ignoreNullValue) {
		this.ignoreNull = ignoreNullValue;
		return this;
	}


	/** 需要忽略的源属性名 */
	public CopyOptions ignoreKeys(Set<String> keys) {
		this.ignoreKeys = keys;
		return this;
	}

	/** 是否忽略属性注入错误 */
	public CopyOptions ignoreError(boolean ignoreError) {
		this.ignoreError = ignoreError;
		return this;
	}

	/** 是否忽略属性大小写 */
	public CopyOptions ignoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
		return this;
	}

	/** 是否忽略JavaBean属性的首字母大小写处理模式，可应对lombok对双大写字母前缀字段的错误处理 */
	public CopyOptions ignoreCapitalize(boolean ignoreCapitalize) {
		this.ignoreCapitalize = ignoreCapitalize;
		return this;
	}

	/** 是否支持属性下划线转驼峰 */
	public CopyOptions enableUnderlineToCamelCase(boolean underlineToCamelCase) {
		this.enableUnderlineToCamelCase = underlineToCamelCase;
		return this;
	}

	/** 是否支持属性驼峰转下划线 */
	public CopyOptions enableCamelToUnderlineCase(boolean camelToUnderlineCase) {
		this.enableCamelToUnderlineCase = camelToUnderlineCase;
		return this;
	}

	/** 属性名映射 */
	public CopyOptions keyMapping(Map<String, String> keyMapping) {
		return keyMapping((key -> keyMapping.getOrDefault(key, key)));
	}

	/** 属性名映射 */
	public CopyOptions keyMapping(Function<String, String> keyMapping) {
		this.keyMapping = keyMapping;
		return this;
	}

	/** 属性值编辑器 */
	public CopyOptions valueMapping(BiFunction<String, Object, Object> valueMapping) {
		this.valueMapping = valueMapping;
		return this;
	}

	/** 是否覆盖目标值，如果不覆盖，会先读取目标对象的值，非null则写，否则忽略。如果覆盖，则不判断直接写 */
	public CopyOptions override(boolean override) {
		this.override = override;
		return this;
	}


	/** 是否启用类型转换器 */
	public CopyOptions enableConverter(boolean enableConverter) {
		this.enableConverter = enableConverter;
		return this;
	}

	/** 自定义类型转换器 */
	public CopyOptions converter(BiFunction<Type, Object, Object> converter) {
		this.converter = converter;
		return this;
	}

	public CopyOptions useDefaultConverter() {
		this.converter = DEFAULT_CONVERTER;
		return this;
	}

	public CopyOptions usePropertyEditorConverter() {
		this.converter = PROPERTY_EDITOR_CONVERTER;
		return this;
	}

	// endregion setters

	// region getters

	public boolean ignoreNull() {
		return ignoreNull;
	}

	public boolean ignoreError() {
		return ignoreError;
	}

	public boolean ignoreCase() {
		return ignoreCase;
	}

	public boolean ignoreCapitalize() {
		return ignoreCapitalize;
	}

	public boolean enableUnderlineToCamelCase() {
		return enableUnderlineToCamelCase;
	}

	public boolean enableCamelToUnderlineCase() {
		return enableCamelToUnderlineCase;
	}

	public boolean override() {
		return override;
	}

	public Function<String, String> keyMapping() {
		return keyMapping;
	}

	public BiFunction<String, Object, Object> valueMapping() {
		return valueMapping;
	}

	public Set<String> ignoreKeys() {
		return ignoreKeys;
	}

	public boolean enableConverter() {
		return enableConverter;
	}

	public BiFunction<Type, Object, Object> converter() {
		return converter;
	}

	// endregion getters

}
