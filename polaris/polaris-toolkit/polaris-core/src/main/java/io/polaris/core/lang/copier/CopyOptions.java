package io.polaris.core.lang.copier;

import io.polaris.core.collection.Iterables;
import io.polaris.core.converter.ConverterRegistry;
import io.polaris.core.function.TernaryFunction;
import lombok.Getter;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8
 */
@Getter
public class CopyOptions {
	/**
	 * 限制的类或接口，必须为目标对象的实现接口或父类，用于限制拷贝的属性，例如一个类我只想复制其父类的一些属性，就可以将editable设置为父类<br>
	 * 如果目标对象是Map，源对象是Bean，则作用于源对象上
	 */
	private Class<?> editable;
	/** 是否忽略空值，当源对象的值为null时，true: 忽略而不注入此值，false: 注入null */
	private boolean ignoreNull = true;
	/** 是否忽略属性注入错误 */
	private boolean ignoreError = true;
	/** 是否忽略属性大小写 */
	private boolean ignoreCase = false;
	/** 是否覆盖目标值，如果不覆盖，会先读取目标对象的值，非null则写，否则忽略。如果覆盖，则不判断直接写 */
	private boolean override = true;
	/** 属性属性编辑器，用于自定义属性转换规则，例如驼峰转下划线等 */
	private Function<String, String> keyMapping;
	/** 属性值编辑器，用于自定义属性值转换规则，例如null转""等 */
	private BiFunction<String, Object, Object> valueEditor;
	/** 需要忽略的属性名 */
	private Set<String> ignoreKeys;
	/** 属性过滤器，断言通过的属性才会被复制。<br> 断言参数中Field为源对象的属性对象,如果不存在则使用目标对象，Object为源对象的对应值 */
	private TernaryFunction<String, Type, Object, Boolean> filter;
	/** 自定义类型转换器 */
	private BiFunction<Type, Object, Object> converter = (type, value) -> {
		if (value == null) {
			return null;
		}
		Object rs = ConverterRegistry.INSTANCE.convertQuietly(type, value);
		if (rs == null) {
			if (type instanceof Class) {
				PropertyEditor editor = PropertyEditorManager.findEditor((Class<?>) type);
				if (editor != null) {
					editor.setValue(value);
				}
			}
		}
		return rs;
	};


	public static CopyOptions create() {
		return new CopyOptions();
	}

	/**
	 * 限制的类或接口，必须为目标对象的实现接口或父类，用于限制拷贝的属性，例如一个类我只想复制其父类的一些属性，就可以将editable设置为父类<br>
	 * 如果目标对象是Map，源对象是Bean，则作用于源对象上
	 */
	public CopyOptions editable(Class<?> editable) {
		this.editable = editable;
		return this;
	}

	/** 是否忽略空值，当源对象的值为null时，true: 忽略而不注入此值，false: 注入null */
	public CopyOptions ignoreNull(boolean ignoreNullValue) {
		this.ignoreNull = ignoreNullValue;
		return this;
	}

	/** 是否忽略空值，当源对象的值为null时，true: 忽略而不注入此值，false: 注入null */
	public CopyOptions ignoreNull() {
		return ignoreNull(true);
	}


	/**
	 * 属性过滤器，断言通过的属性才会被复制<br>
	 * 断言参数中Field为源对象的属性对象,如果不存在则使用目标对象，Object为源对象的对应值
	 */
	public CopyOptions filter(TernaryFunction<String, Type, Object, Boolean> filter) {
		this.filter = filter;
		return this;
	}

	/** 源对象和目标对象都是Map 时, 需要忽略的源对象Map key */
	public CopyOptions ignoreKeys(String... keys) {
		this.ignoreKeys = Iterables.asCollection(HashSet::new, keys);
		return this;
	}

	/** 是否忽略属性注入错误 */
	public CopyOptions ignoreError(boolean ignoreError) {
		this.ignoreError = ignoreError;
		return this;
	}

	/** 忽略属性注入错误 */
	public CopyOptions ignoreError() {
		return ignoreError(true);
	}

	/** 是否忽略属性大小写 */
	public CopyOptions ignoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
		return this;
	}

	/** 忽略属性大小写 */
	public CopyOptions ignoreCase() {
		return ignoreCase(true);
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
	public CopyOptions valueEditor(BiFunction<String, Object, Object> valueEditor) {
		this.valueEditor = valueEditor;
		return this;
	}

	/** 是否覆盖目标值，如果不覆盖，会先读取目标对象的值，非null则写，否则忽略。如果覆盖，则不判断直接写 */
	public CopyOptions override(boolean override) {
		this.override = override;
		return this;
	}

	/** 自定义类型转换器 */
	public CopyOptions converter(BiFunction<Type, Object, Object> converter) {
		this.converter = converter;
		return this;
	}


}
