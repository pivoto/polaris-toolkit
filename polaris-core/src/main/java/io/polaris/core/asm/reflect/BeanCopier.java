package io.polaris.core.asm.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import io.polaris.core.asm.internal.Asms;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.lang.Types;
import io.polaris.core.lang.bean.property.BeanProperties;
import io.polaris.core.lang.bean.property.BeanPropertiesClassification;
import io.polaris.core.lang.bean.property.BeanPropertyInfo;
import io.polaris.core.lang.copier.CopyOptions;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.map.CaseInsensitiveMap;
import io.polaris.core.map.Maps;
import io.polaris.core.reflect.*;
import io.polaris.core.string.StringCases;
import io.polaris.core.tuple.Tuple2;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

/**
 * 提供Bean属性复制方法，包含基本方法与可定制方法。
 * <ul>
 *   <li>基本方法：需要属性名完全匹配且字段类型兼容，复制过程忽略空值，非空值则直接覆盖目标属性值（无视目标属性原值），如提供类型转换器参数，则尽可能转换属性类型以适配目标</li>
 *   <li>定制方法：可通过{@linkplain CopyOptions}参数定制规则，{@linkplain CopyOptions}参数默认值与基本复制方法规则保持一致，可定制如支持下划线与驼峰格式转换、不覆盖目标值、不忽略空值、属性key自定义转换、自定义类型转换器等规则</li>
 * </ul>
 * 定制方法与基本方法相关性能会有所影响。
 *
 * @author Qt
 * @since Apr 14, 2024
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class BeanCopier<S> {
	/**
	 * 日志记录器
	 */
	private static final Logger log = Loggers.of(BeanCopier.class);
	/**
	 * 访问类缓存池
	 */
	@SuppressWarnings({"rawtypes"})
	private static final AccessClassPool<Class<?>, BeanCopier> pool = new AccessClassPool<>();
	/**
	 * 类型字段前缀标识
	 */
	public static final String FIELD_PREFIX_TYPE = "type_";
	/**
	 * Class 字段前缀标识
	 */
	public static final String FIELD_PREFIX_CLASS = "class_";
	/**
	 * 快速复制器缓存（针对相同类型的 Bean）
	 */
	private final Map<Class<?>, BeanDirectCopier> fastCopiers = new ConcurrentHashMap<>();
	/**
	 * 选项复制器缓存（针对不同配置项的 Bean 复制）
	 */
	private final Map<Class<?>, BeanOptionsCopier> optionsCopiers = new ConcurrentHashMap<>();

	/**
	 * 源 Bean 类型
	 */
	protected Class<S> sourceType;
	/**
	 * 属性信息映射表
	 */
	protected Map<String, BeanPropertyInfo> properties;

	/**
	 * 默认构造函数
	 */
	protected BeanCopier() {
	}

	/**
	 * 初始化类型字段（子类实现）
	 * <p>用于存储泛型类型信息，供后续类型转换使用</p>
	 */
	protected abstract void initTypeFields();

	/**
	 * 复制 Bean 到 Map（基础方法）
	 * <p>将源 Bean 的所有可读属性复制到目标 Map 中</p>
	 *
	 * @param source 源 Bean 对象
	 * @param target 目标 Map 对象
	 */
	public abstract void copyBeanToMap(@Nonnull S source, @Nonnull Map<String, Object> target);

	/**
	 * 复制 Bean 到 Map（带类型转换器）
	 * <p>将源 Bean 的属性复制到目标 Map，并使用提供的转换器进行类型转换</p>
	 *
	 * @param source 源 Bean 对象
	 * @param targetType 目标 Map 的泛型类型
	 * @param target 目标 Map 对象
	 * @param converter 类型转换器，用于将属性值转换为目标类型
	 * @param <K> Map 键类型
	 * @param <V> Map 值类型
	 */
	public final <K, V> void copyBeanToMap(@Nonnull S source, @Nonnull java.lang.reflect.Type targetType, @Nonnull Map<K, V> target, BiFunction<java.lang.reflect.Type, Object, Object> converter) {
		JavaType javaType = JavaType.of(targetType);
		JavaType keyType = JavaType.of(javaType.getActualType(Map.class, 0));
		JavaType valueType = JavaType.of(javaType.getActualType(Map.class, 1));
		copyBeanToMapByConverter(source, target, converter, keyType, valueType);
	}

	/**
	 * 通过转换器复制 Bean 到 Map（子类实现）
	 *
	 * @param source 源 Bean 对象
	 * @param target 目标 Map 对象
	 * @param converter 类型转换器
	 * @param keyType Map 键类型
	 * @param valueType Map 值类型
	 */
	protected abstract void copyBeanToMapByConverter(@Nonnull Object source, @Nonnull Map target, BiFunction<java.lang.reflect.Type, Object, Object> converter, @Nonnull JavaType keyType, @Nonnull JavaType valueType);

	/**
	 * 复制 Map 到 Bean（基础方法）
	 * <p>将源 Map 中的属性值复制到目标 Bean 中</p>
	 *
	 * @param source 源 Map 对象
	 * @param target 目标 Bean 对象
	 */
	public abstract void copyMapToBean(@Nonnull Map<String, Object> source, @Nonnull S target);

	/**
	 * 复制 Map 到 Bean（带类型转换器）
	 * <p>将源 Map 中的属性值复制到目标 Bean，并使用提供的转换器进行类型转换</p>
	 *
	 * @param source 源 Map 对象
	 * @param target 目标 Bean 对象
	 * @param converter 类型转换器
	 */
	public abstract void copyMapToBean(@Nonnull Map<String, Object> source, @Nonnull S target, BiFunction<java.lang.reflect.Type, Object, Object> converter);

	/**
	 * 复制 Bean 到 Bean（自动判断目标类型）
	 * <p>将源 Bean 的属性复制到目标 Bean，自动获取目标对象的类型</p>
	 *
	 * @param source 源 Bean 对象
	 * @param target 目标 Bean 对象
	 * @param <T> 目标 Bean 类型
	 */
	public final <T> void copyBeanToBean(@Nonnull S source, @Nonnull T target) {
		copyBeanToBean(source, (Class<T>) target.getClass(), target);
	}

	/**
	 * 复制 Bean 到 Bean（指定目标类型）
	 * <p>将源 Bean 的属性复制到目标 Bean，支持相同类型和不同类型的复制</p>
	 * <ul>
	 *   <li>相同类型：直接使用优化的同类型复制器</li>
	 *   <li>不同类型：创建或获取对应的直接复制器进行处理</li>
	 * </ul>
	 *
	 * @param source 源 Bean 对象
	 * @param type 目标 Bean 的类型（可为 null，为 null 时使用 target 的实际类型）
	 * @param target 目标 Bean 对象
	 * @param <T> 目标 Bean 类型
	 */
	public final <T> void copyBeanToBean(@Nonnull S source, Class<T> type, @Nonnull T target) {
		Class<T> targetType = type == null ? (Class<T>) target.getClass() : type;
		// 完全相同时，直接拷贝同属性，否则即使有继承关系也可能存在属性、字段覆盖等问题，需要按不同类型处理
		if (sourceType.equals(targetType)) {
			copyBeanToBeanBySameType(source, (S) target);
		} else {
			BeanDirectCopier copier = fastCopiers.computeIfAbsent(targetType,
				(k) -> BeanDirectCopier.create(this, targetType));
			copier.copy(source, target);
		}
	}

	/**
	 * 通过相同类型复制 Bean（子类实现）
	 * <p>针对源和目标类型完全相同的情况进行优化的复制方法</p>
	 *
	 * @param source 源 Bean 对象
	 * @param target 目标 Bean 对象
	 */
	protected abstract void copyBeanToBeanBySameType(@Nonnull S source, @Nonnull S target);

	/**
	 * 复制 Bean 到 Map（带选项配置）
	 * <p>使用指定的复制选项将 Bean 属性复制到 Map</p>
	 *
	 * @param source 源 Bean 对象
	 * @param target 目标 Map 对象
	 * @param options 复制选项配置
	 */
	public final void copyBeanToMap(@Nonnull S source, @Nonnull Map<String, Object> target, @Nonnull CopyOptions options) {
		copyBeanToMap(source, new TypeRef<Map<String, Object>>() {}.getType(), target, options);
	}

	/**
	 * 复制 Bean 到 Map（带选项配置和类型参数）
	 * <p>使用指定的复制选项和泛型类型信息将 Bean 属性复制到 Map</p>
	 *
	 * @param source 源 Bean 对象
	 * @param targetType 目标 Map 的泛型类型
	 * @param target 目标 Map 对象
	 * @param options 复制选项配置
	 * @param <K> Map 键类型
	 * @param <V> Map 值类型
	 */
	public final <K, V> void copyBeanToMap(@Nonnull S source, @Nonnull java.lang.reflect.Type targetType, @Nonnull Map<K, V> target, @Nonnull CopyOptions options) {
		JavaType javaType = JavaType.of(targetType);
		JavaType keyType = JavaType.of(javaType.getActualType(Map.class, 0));
		JavaType valueType = JavaType.of(javaType.getActualType(Map.class, 1));
		copyBeanToMapByOptions(source, target, options, keyType, valueType);
	}

	/**
	 * 通过选项配置复制 Bean 到 Map（子类实现）
	 *
	 * @param source 源 Bean 对象
	 * @param target 目标 Map 对象
	 * @param options 复制选项配置
	 * @param keyType Map 键类型
	 * @param valueType Map 值类型
	 * @param <K> Map 键类型
	 * @param <V> Map 值类型
	 */
	protected abstract <K, V> void copyBeanToMapByOptions(@Nonnull S source, @Nonnull Map<K, V> target, @Nonnull CopyOptions options, @Nonnull JavaType keyType, @Nonnull JavaType valueType);


	/**
	 * 使用选项配置复制单个属性到 Map
	 * <p>处理单个属性的复制逻辑，包括空值检查、键转换、值转换等</p>
	 *
	 * @param target 目标 Map
	 * @param options 复制选项配置
	 * @param keyType Map 键类型
	 * @param valueType Map 值类型
	 * @param sourceKey 源属性名
	 * @param sourceValue 源属性值
	 */
	protected final void copyBeanToMapWithKeyByOptions(@Nonnull Map target, @Nonnull CopyOptions options, @Nonnull JavaType keyType, @Nonnull JavaType valueType, String sourceKey, Object sourceValue) {
		if (sourceValue == null && options.ignoreNull()) {
			return;
		}
		// dynamic key
		Object k = options.editKey(sourceKey);
		if (k == null) {
			return;
		}
		k = options.convert(keyType.getRawType(), k);
		if (k == null) {
			return;
		}
		sourceValue = options.editValue(sourceKey, sourceValue);
		if (sourceValue == null && options.ignoreNull()) {
			return;
		}
		if (!options.override()) {
			Object orig = target.get(k);
			if (orig != null) {
				return;
			}
		}
		sourceValue = options.convert(valueType.getRawType(), sourceValue);
		if (sourceValue == null && options.ignoreNull()) {
			return;
		}
		target.put(k, sourceValue);
	}


	public final void copyMapToBean(@Nonnull Map<String, Object> source, @Nonnull S target, @Nonnull CopyOptions options) {
		Set<String> targetKeys = new LinkedHashSet<>(this.properties.size());
		this.properties.forEach((k, v) -> {
			if (v.hasSetter()) {
				targetKeys.add(v.getPropertyName());
			}
		});
		if (!options.hasKeyMapping()) {
			// 不需要动态处理key，使用生成的静态代码
			copyMapToBeanWithSameKeys(source, target, options, targetKeys);
		} else {
			// 需要动态处理key
			Map<String, String> keyMapping = new HashMap<>();
			for (String key : source.keySet()) {
				String mapKey = options.editKey(key);
				if (targetKeys.contains(mapKey)) {
					keyMapping.putIfAbsent(mapKey, key);
				}
			}
			copyMapToBeanWithCustomKeys(source, target, options, targetKeys, keyMapping);
		}
		// ignoreCapitalize
		if (options.ignoreCapitalize() && !targetKeys.isEmpty()) {
			copyMapToBeanWithCapitalizeKeys(source, target, options, targetKeys);
		}
		// underlineToCamelCase
		if (options.enableUnderlineToCamelCase() && !targetKeys.isEmpty()) {
			copyMapToBeanWithUnderlineToCamelKeys(source, target, options, targetKeys);
		}
		// camelToUnderlineCase
		if (options.enableCamelToUnderlineCase() && !targetKeys.isEmpty()) {
			copyMapToBeanWithCamelToUnderlineKeys(source, target, options, targetKeys);
		}
		// ignoreCase
		if (options.ignoreCase() && !targetKeys.isEmpty()) {
			Map<String, Object> upperCaseSource = null;
			if (!options.hasKeyMapping()) {
				if (upperCaseSource == null) {
					upperCaseSource = new CaseInsensitiveMap(HashMap::new, source);
				}
				copyMapToBeanWithSameKeys(upperCaseSource, target, options, targetKeys);
			} else {
				// 需要动态处理key
				Map<String, String> upperCaseTarget = new CaseInsensitiveMap(HashMap::new);
				for (String key : targetKeys) {
					upperCaseTarget.put(key, key);
				}
				Map<String, String> keyMapping = new HashMap<>();
				for (String key : source.keySet()) {
					String mapKey = options.editKey(key);
					String targetKey = upperCaseTarget.get(mapKey);
					if (targetKey != null) {
						keyMapping.putIfAbsent(targetKey, key);
					}
				}
				copyMapToBeanWithCustomKeys(source, target, options, targetKeys, keyMapping);
			}
			if (options.enableUnderlineToCamelCase() && !targetKeys.isEmpty()) {
				if (upperCaseSource == null) {
					upperCaseSource = new CaseInsensitiveMap(HashMap::new, source);
				}
				copyMapToBeanWithUnderlineToCamelKeys(upperCaseSource, target, options, targetKeys);
			}
			if (options.enableCamelToUnderlineCase() && !targetKeys.isEmpty()) {
				if (upperCaseSource == null) {
					upperCaseSource = new CaseInsensitiveMap(HashMap::new, source);
				}
				copyMapToBeanWithCamelToUnderlineKeys(upperCaseSource, target, options, targetKeys);
			}
		}
	}

	protected abstract void copyMapToBeanWithCustomKeys(@Nonnull Map<String, Object> source, @Nonnull Object target, @Nonnull CopyOptions options, @Nonnull Set<String> targetKeys, @Nonnull Map<String, String> keyMapping);

	protected abstract void copyMapToBeanWithSameKeys(@Nonnull Map<String, Object> source, @Nonnull Object target, @Nonnull CopyOptions options, @Nonnull Set<String> targetKeys);

	protected abstract void copyMapToBeanWithCapitalizeKeys(@Nonnull Map<String, Object> source, @Nonnull Object target, @Nonnull CopyOptions options, @Nonnull Set<String> targetKeys);

	protected abstract void copyMapToBeanWithUnderlineToCamelKeys(@Nonnull Map<String, Object> source, @Nonnull Object target, @Nonnull CopyOptions options, @Nonnull Set<String> targetKeys);

	protected abstract void copyMapToBeanWithCamelToUnderlineKeys(@Nonnull Map<String, Object> source, @Nonnull Object target, @Nonnull CopyOptions options, @Nonnull Set<String> targetKeys);

	@SuppressWarnings("unchecked")
	public final <T> void copyBeanToBean(@Nonnull S source, @Nonnull T target, @Nonnull CopyOptions options) {
		copyBeanToBean(source, (Class<T>) target.getClass(), target, options);
	}

	@SuppressWarnings("unchecked")
	public final <T> void copyBeanToBean(@Nonnull S source, @Nonnull Class<T> targetType, @Nonnull T target, @Nonnull CopyOptions options) {
		if (!options.hasKeyMapping()) {
			// 不需要动态处理key，生成静态代码
			BeanOptionsCopier<S, T> copier = optionsCopiers.computeIfAbsent(targetType,
				(k) -> BeanOptionsCopier.create(this, targetType));
			copier.copy(source, target, options);
		} else {
			// 需要动态处理key
			BeanAccess<S> sourceBeanAccess = BeanAccess.get(this.sourceType);
			BeanAccess<T> targetBeanAccess = BeanAccess.get(targetType);

			Map<String, BeanPropertyInfo> sourceProperties = sourceBeanAccess.properties();
			Map<String, BeanPropertyInfo> targetProperties = targetBeanAccess.properties();

			Set<String> targetKeys = new LinkedHashSet<>(targetBeanAccess.allPropertyNames());
			// 完全匹配或自定义匹配
			List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> sameKeyMapping = BeanOptionsCopier.buildCustomKeyMapping(sourceProperties, targetProperties, options.keyMapping(), false);
			copyBeanToBeanWithDynamicKeyMappings(sourceBeanAccess, source, targetBeanAccess, target, options, targetKeys, sameKeyMapping);
			// ignoreCapitalize
			if (options.ignoreCapitalize() && !targetKeys.isEmpty()) {
				List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> capitalizeKeyMapping = BeanOptionsCopier.buildCapitalizeKeyMapping(sourceProperties, targetProperties);
				copyBeanToBeanWithDynamicKeyMappings(sourceBeanAccess, source, targetBeanAccess, target, options, targetKeys, capitalizeKeyMapping);
			}
			// underlineToCamelCase
			if (options.enableUnderlineToCamelCase() && !targetKeys.isEmpty()) {
				List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> underlineToCamelKeyMapping = BeanOptionsCopier.buildUnderlineToCamelKeyMapping(sourceProperties, targetProperties, false);
				copyBeanToBeanWithDynamicKeyMappings(sourceBeanAccess, source, targetBeanAccess, target, options, targetKeys, underlineToCamelKeyMapping);
			}
			// camelToUnderlineCase
			if (options.enableCamelToUnderlineCase() && !targetKeys.isEmpty()) {
				List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> camelToUnderlineKeyMapping = BeanOptionsCopier.buildCamelToUnderlineKeyMapping(sourceProperties, targetProperties, false);
				copyBeanToBeanWithDynamicKeyMappings(sourceBeanAccess, source, targetBeanAccess, target, options, targetKeys, camelToUnderlineKeyMapping);
			}
			// ignoreCase
			if (options.ignoreCase() && !targetKeys.isEmpty()) {
				Map<String, BeanPropertyInfo> sourceUpperProperties = Maps.newUpperCaseHashMap();
				sourceUpperProperties.putAll(sourceProperties);
				List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> sameKeyMappingIgnoreCase = BeanOptionsCopier.buildSameKeyMapping(sourceUpperProperties, targetProperties, true);
				copyBeanToBeanWithDynamicKeyMappings(sourceBeanAccess, source, targetBeanAccess, target, options, targetKeys, sameKeyMappingIgnoreCase);
				if (options.enableUnderlineToCamelCase()) {
					List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> underlineToCamelKeyMappingIgnoreCase = BeanOptionsCopier.buildUnderlineToCamelKeyMapping(sourceUpperProperties, targetProperties, true);
					copyBeanToBeanWithDynamicKeyMappings(sourceBeanAccess, source, targetBeanAccess, target, options, targetKeys, underlineToCamelKeyMappingIgnoreCase);
				}
				if (options.enableCamelToUnderlineCase()) {
					List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> camelToUnderlineKeyMappingIgnoreCase = BeanOptionsCopier.buildCamelToUnderlineKeyMapping(sourceUpperProperties, targetProperties, true);
					copyBeanToBeanWithDynamicKeyMappings(sourceBeanAccess, source, targetBeanAccess, target, options, targetKeys, camelToUnderlineKeyMappingIgnoreCase);
				}
			}
		}
	}

	protected final <T> void copyBeanToBeanWithDynamicKeyMappings(@Nonnull BeanAccess<S> sourceBeanAccess, @Nonnull S source, @Nonnull BeanAccess<T> targetBeanAccess, @Nonnull T target, @Nonnull CopyOptions options, @Nonnull Set<String> targetKeys, @Nonnull List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> mapping) {
		for (Tuple2<BeanPropertyInfo, BeanPropertyInfo> tuple : mapping) {
			String sourceKey = tuple.getFirst().getPropertyName();
			String targetKey = tuple.getSecond().getPropertyName();
			try {
				copyBeanToBeanWithDynamicKeyMapping(targetBeanAccess, target, sourceKey, targetKey,
					() -> sourceBeanAccess.getPropertyOrField(source, sourceKey),
					options, targetKeys);
			} catch (Throwable e) {
				this.resolveCopyError(sourceKey + "->" + targetKey, e, options);
			}
		}
	}

	protected final <T> void copyBeanToBeanWithDynamicKeyMapping(@Nonnull BeanAccess<T> targetBeanAccess, @Nonnull T target, @Nonnull String sourceKey, @Nonnull String targetKey, @Nonnull Supplier<Object> valueSupplier, @Nonnull CopyOptions options, @Nonnull Set<String> targetKeys) {
		if (options.isIgnoredKey(sourceKey) || !targetKeys.contains(targetKey)) {
			return;
		}
		Map<String, BeanPropertyInfo> map = targetBeanAccess.properties();
		BeanPropertyInfo info = map.get(targetKey);
		if (info == null) {
			return;
		}
		if (info.getField() != null) {
			if (!options.override()) {
				Object orig = targetBeanAccess.getField(target, targetKey);
				if (orig != null) {
					return;
				}
			}
			Object value = valueSupplier.get();
			if (value == null && options.ignoreNull()) {
				return;
			}
			java.lang.reflect.Type type = info.getPropertyGenericType();
			value = options.editValue(sourceKey, value);
			value = options.convert(type, value);
			if (value == null) {
				if (options.ignoreNull()) {
					return;
				}
				if (info.getPropertyType().isPrimitive()) {
					return;
				}
			}
			targetBeanAccess.setField(target, targetKey, value);
			targetKeys.remove(targetKey);
		} else {
			if (info.getWriteMethod() == null) {
				return;
			}
			if (!options.override() && info.getReadMethod() != null) {
				Object orig = targetBeanAccess.getProperty(target, targetKey);
				if (orig != null) {
					return;
				}
			}

			Object value = valueSupplier.get();
			if (value == null && options.ignoreNull()) {
				return;
			}
			java.lang.reflect.Type type = info.getPropertyGenericType();
			value = options.editValue(sourceKey, value);
			value = options.convert(type, value);
			if (value == null) {
				if (options.ignoreNull()) {
					return;
				}
				if (info.getPropertyType().isPrimitive()) {
					return;
				}
			}
			targetBeanAccess.setProperty(target, targetKey, value);
			targetKeys.remove(targetKey);
		}
	}


	protected final void resolveCopyError(String propertyName, Throwable e) {
		if (log.isDebugEnabled()) {
			log.debug("复制属性[" + propertyName + "]失败", e);
		}
	}

	protected final void resolveCopyError(@Nonnull String propertyName, @Nonnull Throwable e, @Nonnull CopyOptions options) {
		String msg = "复制属性[" + propertyName + "]失败";
		if (options.ignoreError()) {
			log.warn(msg + ": " + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug(msg, e);
			}
		} else {
			throw new IllegalArgumentException(msg, e);
		}
	}

	protected final java.lang.reflect.Type getPropertyGenericType(@Nonnull String propertyName) {
		return properties.get(propertyName).getPropertyGenericType();
	}

	protected final Class<?> getPropertyType(@Nonnull String propertyName) {
		return properties.get(propertyName).getPropertyType();
	}

	protected final Object convert(@Nonnull java.lang.reflect.Type type, Object value, BiFunction<java.lang.reflect.Type, Object, Object> converter) {
		if (value == null) {
			return null;
		}
		if (JavaType.of(type).isInstance(value)) {
			return value;
		}
		if (converter == null) {
			return null;
		}
		return converter.apply(type, value);
	}

	/**
	 * 获取或创建 Bean 复制器实例
	 * <p>从缓存池中获取，如果不存在则创建</p>
	 *
	 * @param type Bean 类型
	 * @param <T> Bean 类型
	 * @return Bean 复制器实例
	 */
	public static <T> BeanCopier<T> get(@Nonnull Class<T> type) {
		return pool.computeIfAbsent(type, BeanCopier::create);
	}

	/**
	 * 创建 Bean 复制器实例
	 * <p>通过 ASM 动态生成字节码类并初始化</p>
	 *
	 * @param type Bean 类型
	 * @param <T> Bean 类型
	 * @return Bean 复制器实例
	 */
	public static <T> BeanCopier<T> create(@Nonnull Class<T> type) {
		BeanPropertiesClassification classification = BeanProperties.classify(type);
		String accessClassName = AccessClassLoader.buildAccessClassName(type, BeanCopier.class);

		AccessClassLoader loader = AccessClassLoader.get(type);
		Class accessClass = loader.loadOrDefineClass(accessClassName, () -> buildAccessClass(loader, accessClassName, type, classification));
		BeanCopier<T> access;
		try {
			access = (BeanCopier<T>) accessClass.newInstance();
			// 全部为只读属性
			access.sourceType = type;
			access.properties = Collections.unmodifiableMap(classification.properties);
			access.initTypeFields();
			return access;
		} catch (Throwable t) {
			throw new IllegalStateException("创建访问类失败: " + accessClassName, t);
		}
	}

	/**
	 * 构建访问器类的字节码
	 * <p>
	 * 该方法使用ASM框架动态生成一个继承自BeanCopier的类，该类针对特定类型T优化了所有属性拷贝操作。
	 * 生成的类包含以下核心功能：
	 * 1. 初始化类型字段缓存（避免反射开销）
	 * 2. Bean到Map的转换（支持直接访问字段和getter方法）
	 * 3. Map到Bean的转换（支持多种键名映射策略）
	 * 4. Bean到Bean的同类型拷贝
	 * 5. 支持类型转换器的高级拷贝功能
	 * </p>
	 *
	 * @param loader 访问类加载器，用于加载生成的字节码类
	 * @param accessClassName 生成的访问器类的完整类名（包含包路径）
	 * @param type 目标Bean的类型，所有生成的方法都将针对此类型进行优化
	 * @param classification Bean属性分类信息，包含所有需要处理的属性元数据
	 * @param <T> Bean 类型
	 * @return 生成的类的字节码数组，可直接通过ClassLoader定义类
	 */
	private static <T> byte[] buildAccessClass(AccessClassLoader loader
		, String accessClassName, Class<T> type
		, BeanPropertiesClassification classification) {
		// 将类名转换为JVM内部格式（点号替换为斜杠）
		String accessClassNameInternal = accessClassName.replace('.', '/');

		// 创建ClassWriter，启用自动计算栈帧和局部变量表
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		String superClassNameInternal = BeanCopier.class.getName().replace('.', '/');
		// 定义生成的类：public class AccessClass extends BeanCopier<Type>
		// 签名指定泛型类型为实际的目标类型
		cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal,
			"L" + superClassNameInternal + "<L" + Type.getInternalName(type) + ";>;",
			superClassNameInternal, null);
		// 声明内部类引用，用于后续MethodHandle相关操作： import java.lang.invoke.MethodHandles.Lookup;
		cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

		// 插入默认构造函数，调用父类构造方法
		Asms.insertDefaultConstructor(cw, superClassNameInternal);
		// 插入初始化类型字段的方法，为每个属性创建Type和Class缓存字段
		insertInitTypeFields(cw, superClassNameInternal, accessClassNameInternal, type, classification);
		// 插入Bean转Map的核心方法实现
		insertBeanToMap(cw, superClassNameInternal, accessClassNameInternal, type, classification);
		// 插入带类型转换器的Bean转Map方法实现
		insertBeanToMapByConverter(cw, superClassNameInternal, accessClassNameInternal, type, classification);
		// 插入Map转Bean的核心方法实现
		insertMapToBean(cw, superClassNameInternal, accessClassNameInternal, type, classification);
		// 插入带类型转换器的Map转Bean方法实现
		insertMapToBeanByConverter(cw, superClassNameInternal, accessClassNameInternal, type, classification);
		// 插入同类型Bean之间的直接拷贝方法实现
		insertBeanToBeanBySameType(cw, superClassNameInternal, accessClassNameInternal, type, classification);
		// 插入带选项配置的Bean转Map方法实现
		insertBeanToMapByOptions(cw, superClassNameInternal, accessClassNameInternal, type, classification);

		// 插入使用所有源键的Map转Bean方法（遍历Map所有键尝试匹配）
		insertMapToBeanWithSourceKeyAll(cw, superClassNameInternal, accessClassNameInternal, type, classification);
		// 插入自定义键映射的Map转Bean方法 copyMapToBeanWithCustomKeys
		insertMapToBeanWithCustomKeys(cw, accessClassNameInternal, type, classification);
		// 插入相同键名的Map转Bean方法（Map键与Bean属性名完全一致） copyMapToBeanWithSameKeys
		insertMapToBeanWithSourceKeys(cw, accessClassNameInternal, type, classification, BeanCopier::copyMapToBeanWithSameKeys, key -> key);
		// 插入首字母大小写转换的Map转Bean方法 copyMapToBeanWithCapitalizeKeys
		insertMapToBeanWithSourceKeys(cw, accessClassNameInternal, type, classification, BeanCopier::copyMapToBeanWithCapitalizeKeys, key -> {
			if (key.length() > 1) {
				String mapKey;
				if (Character.isUpperCase(key.charAt(0))) {
					mapKey = Character.toLowerCase(key.charAt(0)) + key.substring(1);
				} else {
					mapKey = Character.toUpperCase(key.charAt(0)) + key.substring(1);
				}
				return mapKey;
			}
			return null;
		});
		// 插入下划线转驼峰的Map转Bean方法（适用于数据库字段到Java属性的映射） copyMapToBeanWithUnderlineToCamelKeys
		insertMapToBeanWithSourceKeys(cw, accessClassNameInternal, type, classification, BeanCopier::copyMapToBeanWithUnderlineToCamelKeys, key -> {
			String mapKey = StringCases.camelToUnderlineCase(key);
			if (!key.equals(mapKey)) {
				return mapKey;
			}
			return null;
		});
		// 插入驼峰转下划线的Map转Bean方法（反向映射场景） copyMapToBeanWithCamelToUnderlineKeys
		insertMapToBeanWithSourceKeys(cw, accessClassNameInternal, type, classification, BeanCopier::copyMapToBeanWithCamelToUnderlineKeys, key -> {
			if (key.indexOf('_') >= 0) {
				String mapKey = StringCases.underlineToCamelCase(key);
				return mapKey;
			}
			return null;
		});

		// 完成类的构建
		cw.visitEnd();
		// 获取生成的字节码数组
		byte[] byteArray = cw.toByteArray();
		return byteArray;
	}

	/**
	 * 插入类型字段初始化方法的字节码
	 * <p>
	 * 该方法为每个Bean属性生成两个缓存字段：
	 * 1. type_{propertyName}: 存储属性的泛型类型信息（java.lang.reflect.Type）
	 * 2. class_{propertyName}: 存储属性的原始类型信息（java.lang.Class）
	 * </p>
	 * <p>
	 * 同时生成initTypeFields()方法的字节码实现，在运行时通过反射获取并缓存这些类型信息，
	 * 避免后续类型转换时重复反射调用，提升性能。
	 * </p>
	 *
	 * @param cw ASM类写入器
	 * @param superClassNameInternal 父类的JVM内部名称
	 * @param accessClassNameInternal 生成的访问器类的JVM内部名称
	 * @param type Bean的类型
	 * @param classification Bean属性分类信息
	 * @param <T> Bean类型参数
	 */
	@SuppressWarnings("all")
	private static <T> void insertInitTypeFields(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertiesClassification classification) {
		SerializableConsumer<BeanCopier> initTypeFields = BeanCopier::initTypeFields;
		SerializableBiFunction<BeanCopier, String, java.lang.reflect.Type> getPropertyGenericType = BeanCopier::getPropertyGenericType;
		SerializableBiFunction<BeanCopier, String, Class> getPropertyType = BeanCopier::getPropertyType;

		insertInitTypeFields(cw, superClassNameInternal, accessClassNameInternal, classification,
			initTypeFields.serialized().getImplMethodName(),
			getPropertyGenericType.serialized().getImplMethodName(),
			getPropertyType.serialized().getImplMethodName());
	}

	/**
	 * 生成类型字段初始化的字节码实现
	 * <p>
	 * 该方法执行以下操作：
	 * 1. 为每个属性创建两个protected字段：type_{name}和class_{name}
	 * 2. 生成initTypes()方法，遍历所有属性并调用父类的getPropertyGenericType()和getPropertyType()
	 * 3. 将返回的类型信息存入对应的字段中
	 * </p>
	 *
	 * @param cw ASM类写入器
	 * @param superClassNameInternal 父类的JVM内部名称
	 * @param accessClassNameInternal 访问器类的JVM内部名称
	 * @param classification Bean属性分类信息
	 * @param initTypeFieldsMethodName 初始化方法的名称
	 * @param getPropertyGenericTypeMethodName 获取泛型类型的方法名
	 * @param getPropertyTypeMethodName 获取原始类型的方法名
	 */
	static void insertInitTypeFields(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, BeanPropertiesClassification classification, String initTypeFieldsMethodName, String getPropertyGenericTypeMethodName, String getPropertyTypeMethodName) {
		// 为每个属性创建Type和Class缓存字段
		classification.properties.forEach((name, info) -> {
			// protected Type type_fieldName;
			FieldVisitor fieldVisitor = cw.visitField(ACC_PROTECTED, FIELD_PREFIX_TYPE + name, "Ljava/lang/reflect/Type;", null, null);
			fieldVisitor.visitEnd();
			// protected Class class_fieldName;
			fieldVisitor = cw.visitField(ACC_PROTECTED, FIELD_PREFIX_CLASS + name, "Ljava/lang/Class;", null, null);
			fieldVisitor.visitEnd();
		});

		// 生成i initTypeFields()方法的字节码： protected void initTypeFields()
		MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, initTypeFieldsMethodName, "()V", null, null);
		methodVisitor.visitCode();
		// 遍历所有属性，为每个属性生成类型信息的获取和赋值指令
		classification.properties.forEach((name, info) -> {
			// 获取泛型类型：super.getPropertyGenericType(fieldName)
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitLdcInsn(name);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassNameInternal, getPropertyGenericTypeMethodName, "(Ljava/lang/String;)Ljava/lang/reflect/Type;", false);
			// 将泛型类型存入type_fieldName字段: this.type_fieldName = super.getPropertyGenericType(fieldName)
			methodVisitor.visitFieldInsn(PUTFIELD, accessClassNameInternal, FIELD_PREFIX_TYPE + name, "Ljava/lang/reflect/Type;");

			// 获取原始类型：super.getPropertyType(fieldName)
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitLdcInsn(name);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassNameInternal, getPropertyTypeMethodName, "(Ljava/lang/String;)Ljava/lang/Class;", false);
			// 将原始类型存入class_fieldName字段：this.class_fieldName = super.getPropertyType(fieldName)
			methodVisitor.visitFieldInsn(PUTFIELD, accessClassNameInternal, FIELD_PREFIX_CLASS + name, "Ljava/lang/Class;");
		});
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(0, 0);
		methodVisitor.visitEnd();
	}


	/**
	 * 插入Bean转Map方法的字节码实现
	 * <p>
	 * 该方法生成copyBeanToMap()的字节码，实现将Bean的所有可读属性复制到Map中。
	 * 对于每个属性：
	 * - 如果有公共字段：直接通过GETFIELD读取字段值
	 * - 如果有getter方法：通过方法调用读取属性值，并添加异常处理
	 * - 自动装箱基本类型为包装类型后放入Map
	 * </p>
	 * <p>
	 * 同时生成桥接方法以支持泛型擦除后的调用。
	 * </p>
	 *
	 * @param cw ASM类写入器
	 * @param superClassNameInternal 父类的JVM内部名称
	 * @param accessClassNameInternal 访问器类的JVM内部名称
	 * @param type Bean的类型
	 * @param classification Bean属性分类信息
	 * @param <T> Bean类型参数
	 */
	@SuppressWarnings("all")
	private static <T> void insertBeanToMap(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertiesClassification classification) {
		SerializableTriConsumer<BeanCopier, String, Throwable> resolveCopyError = BeanCopier::resolveCopyError;
		SerializableTriConsumer<BeanCopier, Object, Map<String, Object>> copyBeanToMap = BeanCopier::copyBeanToMap;
		String copyBeanToMapName = copyBeanToMap.serialized().getImplMethodName();

		// 生成具体的 copyBeanToMap(Type source, Map target)方法实现
		{
			// public copyBeanToMap(Type source, Map target)
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC, copyBeanToMapName, "(L" + Type.getInternalName(type) + ";Ljava/util/Map;)V", "(L" + Type.getInternalName(type) + ";Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;)V", null);
			methodVisitor.visitCode();

			// 遍历所有属性，为每个属性生成读取并放入Map的字节码
			classification.properties.forEach((name, info) -> {
				if (info.getField() != null) {
					// 有公共字段：直接通过GETFIELD读取
					methodVisitor.visitVarInsn(ALOAD, 2); // targetMap
					methodVisitor.visitLdcInsn(name); // fieldName
					methodVisitor.visitVarInsn(ALOAD, 1); // sourceObj
					methodVisitor.visitFieldInsn(GETFIELD, Type.getInternalName(info.getField().getDeclaringClass()), name, Type.getDescriptor(info.getField().getType()));
					// 基本类型自动装箱
					Asms.autoBoxing(methodVisitor, info.getField().getType());
					// targetMap.put(fieldName, sourceObj.fieldName)
					methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
					methodVisitor.visitInsn(POP);
				} else if (info.getReadMethod() != null) {
					// 有getter方法：通过方法调用读取，需要异常处理
					boolean hasThrows = info.getReadMethod().getExceptionTypes().length > 0;
					Label labelStart = new Label();
					Label labelEnd = new Label();
					Label labelCatch = new Label();
					if (hasThrows) {
						methodVisitor.visitTryCatchBlock(labelStart, labelEnd, labelCatch, "java/lang/Throwable");
						methodVisitor.visitLabel(labelStart);
					}
					// try块：调用getter方法获取属性值
					Method readMethod = info.getReadMethod();
					Class<?> declaringClass = readMethod.getDeclaringClass();
					boolean isInterface = declaringClass.isInterface();
					int invokeOpcode;
					if (isInterface) {
						invokeOpcode = INVOKEINTERFACE;
					} else if (Modifier.isStatic(readMethod.getModifiers())) {
						invokeOpcode = INVOKESTATIC;
					} else {
						invokeOpcode = INVOKEVIRTUAL;
					}
					// 准备方法调用参数：target, key, source
					methodVisitor.visitVarInsn(ALOAD, 2); // targetMap
					methodVisitor.visitLdcInsn(name); // fieldName
					methodVisitor.visitVarInsn(ALOAD, 1); // sourceObj
					methodVisitor.visitMethodInsn(invokeOpcode, Type.getInternalName(declaringClass), readMethod.getName(), Type.getMethodDescriptor(readMethod), isInterface);
					Class<?> returnType = readMethod.getReturnType();
					// 返回值自动装箱
					Asms.autoBoxing(methodVisitor, returnType);
					// targetMap.put(fieldName, sourceObj.getFieldName())
					methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
					methodVisitor.visitInsn(POP);
					// try块结束标记
					if (hasThrows) {
						methodVisitor.visitLabel(labelEnd);
						Label labelFinal = new Label();
						methodVisitor.visitJumpInsn(GOTO, labelFinal);
						// catch
						methodVisitor.visitLabel(labelCatch);
						methodVisitor.visitVarInsn(ASTORE, 3); // throwable
						methodVisitor.visitVarInsn(ALOAD, 0); // this
						methodVisitor.visitLdcInsn(name); // fieldName
						methodVisitor.visitVarInsn(ALOAD, 3); // throwable
						// 调用resolveCopyError: super.resolveCopyError(fieldName, throwable)
						methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassNameInternal, resolveCopyError.serialized().getImplMethodName(), "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
						// finally块标记
						methodVisitor.visitLabel(labelFinal);
					}
				}
			});
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}
		// 泛型合成方法, 生成桥接方法以支持泛型擦除：copyBeanToMap(Object, Map)
		{
			/*
			public copyBeanToMap(Object source, Map target){
				return copyBeanToMap((S) source, target);
			}
			 */
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, copyBeanToMapName, "(Ljava/lang/Object;Ljava/util/Map;)V", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ALOAD, 0); // this
			methodVisitor.visitVarInsn(ALOAD, 1); // source
			methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
			methodVisitor.visitVarInsn(ALOAD, 2); // target
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, accessClassNameInternal, copyBeanToMapName, "(L" + Type.getInternalName(type) + ";Ljava/util/Map;)V", false);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(3, 3);
			methodVisitor.visitEnd();
		}
	}

	/**
	 * 插入带类型转换器的Bean转Map方法的字节码实现
	 * <p>
	 * 该方法生成copyBeanToMapByConverter()的字节码，支持在复制过程中使用自定义的类型转换器。
	 * 对于每个属性：
	 * 1. 读取属性值（通过字段或getter）
	 * 2. 调用convert()方法进行类型转换
	 * 3. 如果转换后的键和值都不为null，则放入Map
	 * 4. 包含完整的异常处理机制
	 * </p>
	 *
	 * @param cw ASM类写入器
	 * @param superClassNameInternal 父类的JVM内部名称
	 * @param accessClassNameInternal 访问器类的JVM内部名称
	 * @param type Bean的类型
	 * @param classification Bean属性分类信息
	 * @param <T> Bean类型参数
	 */
	@SuppressWarnings("all")
	private static <T> void insertBeanToMapByConverter(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertiesClassification classification) {
		SerializableTriConsumer<BeanCopier, String, Throwable> resolveCopyError = BeanCopier::resolveCopyError;
		SerializableConsumerWithArgs6<BeanCopier, Object, Map, BiFunction<java.lang.reflect.Type, Object, Object>, JavaType, JavaType> copyBeanToMap = BeanCopier::copyBeanToMapByConverter;
		SerializableFunctionWithArgs4<BeanCopier, java.lang.reflect.Type, Object, BiFunction<java.lang.reflect.Type, Object, Object>, Object> convert = BeanCopier::convert;
		// 生成带转换器的copyBeanToMapByConverter方法实现
		{
			// protected void copyBeanToMapByConverter(Object source, Map target, BiFunction<Type, Object, Object> converter, JavaType keyType, JavaType valueType)
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, copyBeanToMap.serialized().getImplMethodName(),
				"(Ljava/lang/Object;Ljava/util/Map;Ljava/util/function/BiFunction;" +
					Type.getDescriptor(JavaType.class) +
					Type.getDescriptor(JavaType.class) +
					")V", "(Ljava/lang/Object;Ljava/util/Map;Ljava/util/function/BiFunction<Ljava/lang/reflect/Type;Ljava/lang/Object;Ljava/lang/Object;>;" +
					Type.getDescriptor(JavaType.class) +
					Type.getDescriptor(JavaType.class) +
					")V", null);
			methodVisitor.visitCode();
			classification.properties.forEach((name, info) -> {
				if (!info.hasGetter()) {
					return;
				}
				Label labelTryStart = new Label();
				Label labelTryEnd = new Label();
				Label labelCatch = new Label();
				methodVisitor.visitTryCatchBlock(labelTryStart, labelTryEnd, labelCatch, "java/lang/Throwable");
				methodVisitor.visitLabel(labelTryStart);
				methodVisitor.visitVarInsn(ALOAD, 1); // sourceObj
				methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
				if (info.getField() != null) {
					// Object var = ((S)sourceObj).fieldName;
					methodVisitor.visitFieldInsn(GETFIELD, Type.getInternalName(info.getField().getDeclaringClass()), name, Type.getDescriptor(info.getField().getType()));
					Asms.autoBoxing(methodVisitor, info.getField().getType());
				} else {
					// Object var = ((S)sourceObj).getFieldName();
					Method readMethod = info.getReadMethod();
					Class<?> declaringClass = readMethod.getDeclaringClass();
					boolean isInterface = declaringClass.isInterface();
					int invokeOpcode;
					if (isInterface) {
						invokeOpcode = INVOKEINTERFACE;
					} else if (Modifier.isStatic(readMethod.getModifiers())) {
						invokeOpcode = INVOKESTATIC;
					} else {
						invokeOpcode = INVOKEVIRTUAL;
					}
					methodVisitor.visitMethodInsn(invokeOpcode, Type.getInternalName(declaringClass), readMethod.getName(), Type.getMethodDescriptor(readMethod), isInterface);
					Class<?> returnType = readMethod.getReturnType();
					Asms.autoBoxing(methodVisitor, returnType);
				}
				methodVisitor.visitVarInsn(ASTORE, 6); // var

				methodVisitor.visitVarInsn(ALOAD, 0); // this
				methodVisitor.visitVarInsn(ALOAD, 5); // valueType
				methodVisitor.visitVarInsn(ALOAD, 6); // var
				methodVisitor.visitVarInsn(ALOAD, 3); // converter
				// var = super.convert(valueType, var, converter);
				methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassNameInternal,
					convert.serialized().getImplMethodName(),
					"(Ljava/lang/reflect/Type;Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;", false);
				methodVisitor.visitVarInsn(ASTORE, 6);

				methodVisitor.visitVarInsn(ALOAD, 0); // this
				methodVisitor.visitVarInsn(ALOAD, 4); // keyType
				methodVisitor.visitLdcInsn(info.getPropertyName()); // fieldName
				methodVisitor.visitVarInsn(ALOAD, 3); // converter
				// varKey = super.convert(valueType, fieldName, converter);
				methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassNameInternal,
					convert.serialized().getImplMethodName(),
					"(Ljava/lang/reflect/Type;Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;", false);
				methodVisitor.visitVarInsn(ASTORE, 7);

				/*
				if(var != null && varKey != null) {
					targetMap.put(varKey, var);
				}
				 */
				methodVisitor.visitVarInsn(ALOAD, 7); // varKey
				methodVisitor.visitJumpInsn(IFNULL, labelTryEnd);
				methodVisitor.visitVarInsn(ALOAD, 6);  // var
				methodVisitor.visitJumpInsn(IFNULL, labelTryEnd);
				methodVisitor.visitVarInsn(ALOAD, 2); // targetMap
				methodVisitor.visitVarInsn(ALOAD, 7); // varKey
				methodVisitor.visitVarInsn(ALOAD, 6); // var
				methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
				methodVisitor.visitInsn(POP);

				methodVisitor.visitLabel(labelTryEnd);
				Label labelFinally = new Label();
				methodVisitor.visitJumpInsn(GOTO, labelFinally);
				/*
				catch(Throwable throwable) {
					super.resolveCopyError(fieldName, throwable);
				}
				 */
				methodVisitor.visitLabel(labelCatch);
				methodVisitor.visitVarInsn(ASTORE, 6); // throwable
				methodVisitor.visitVarInsn(ALOAD, 0); // this
				methodVisitor.visitLdcInsn(info.getPropertyName()); // fieldName
				methodVisitor.visitVarInsn(ALOAD, 6); // throwable
				methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassNameInternal, resolveCopyError.serialized().getImplMethodName(), "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);

				methodVisitor.visitLabel(labelFinally);
			});
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}
	}


	@SuppressWarnings("all")
	private static <T> void insertMapToBean(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertiesClassification classification) {
		SerializableTriConsumer<BeanCopier, String, Throwable> resolveCopyError = BeanCopier::resolveCopyError;
		SerializableTriConsumer<BeanCopier, Map<String, Object>, Object> copyMapToBean = BeanCopier::copyMapToBean;
		String copyMapToBeanName = copyMapToBean.serialized().getImplMethodName();

		// 生成具体的copyMapToBean(Map source, Type target)方法实现
		{
			// public void copyMapToBean(Map<String, Object> sourceMap, S targetObj)
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC, copyMapToBeanName, "(Ljava/util/Map;L" + Type.getInternalName(type) + ";)V", "(LLjava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;" + Type.getInternalName(type) + ";)V", null);
			methodVisitor.visitCode();

			// 遍历所有属性，为每个属性生成从Map读取并设置到Bean的字节码
			classification.properties.forEach((name, info) -> {
				if (info.getField() != null) {
					// 有公共字段：直接从Map获取并设置字段
					// Object var = sourceMap.get(fieldName);
					methodVisitor.visitVarInsn(ALOAD, 1); // sourceMap
					methodVisitor.visitLdcInsn(name);
					methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
					methodVisitor.visitVarInsn(ASTORE, 3);
					methodVisitor.visitVarInsn(ALOAD, 3);
					/*
					if(var != null && FieldType.class.isInstance(var)) {
						targetObj.setFieldName(var);
					}
					 */
					Label labelIfEnd = new Label();
					// 检查值是否为null
					methodVisitor.visitJumpInsn(IFNULL, labelIfEnd);
					// 检查类型是否匹配
					methodVisitor.visitLdcInsn(Type.getType(Types.getWrapperClass(info.getField().getType())));
					methodVisitor.visitVarInsn(ALOAD, 3); // var
					methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "isInstance", "(Ljava/lang/Object;)Z", false);
					methodVisitor.visitJumpInsn(IFEQ, labelIfEnd);
					// 类型匹配，设置字段值（自动拆箱）
					methodVisitor.visitVarInsn(ALOAD, 2); // targetObj
					methodVisitor.visitVarInsn(ALOAD, 3); // var
					Asms.autoUnBoxing(methodVisitor, info.getField().getType());
					methodVisitor.visitFieldInsn(PUTFIELD, Type.getInternalName(info.getField().getDeclaringClass()), name, Type.getDescriptor(info.getField().getType()));
					methodVisitor.visitLabel(labelIfEnd);
				} else if (info.getWriteMethod() != null) {
					boolean hasThrows = info.getWriteMethod().getExceptionTypes().length > 0;
					Label labelStart = new Label();
					Label labelEnd = new Label();
					Label labelCatch = new Label();
					if (hasThrows) {
						methodVisitor.visitTryCatchBlock(labelStart, labelEnd, labelCatch, "java/lang/Throwable");
						methodVisitor.visitLabel(labelStart);
					}
					// try
					Method method = info.getWriteMethod();
					Class<?> declaringClass = method.getDeclaringClass();
					boolean isInterface = declaringClass.isInterface();
					int invokeOpcode;
					if (isInterface) {
						invokeOpcode = INVOKEINTERFACE;
					} else if (Modifier.isStatic(method.getModifiers())) {
						invokeOpcode = INVOKESTATIC;
					} else {
						invokeOpcode = INVOKEVIRTUAL;
					}
					methodVisitor.visitVarInsn(ALOAD, 1);
					methodVisitor.visitLdcInsn(name);
					methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);

					/*
					if(var != null && FieldType.class.isInstance(var)) {
						targetObj.setFieldName(var);
					}
					 */
					methodVisitor.visitVarInsn(ASTORE, 3);
					methodVisitor.visitVarInsn(ALOAD, 3);
					Label labelIf = new Label();
					methodVisitor.visitJumpInsn(IFNULL, labelIf);
					methodVisitor.visitLdcInsn(Type.getType(Types.getWrapperClass(method.getParameterTypes()[0])));
					methodVisitor.visitVarInsn(ALOAD, 3);
					methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "isInstance", "(Ljava/lang/Object;)Z", false);
					methodVisitor.visitJumpInsn(IFEQ, labelIf);

					methodVisitor.visitVarInsn(ALOAD, 2);
					methodVisitor.visitVarInsn(ALOAD, 3);

					Type paramType = Type.getType(method.getParameterTypes()[0]);
					Asms.autoUnBoxing(methodVisitor, paramType);
					methodVisitor.visitMethodInsn(invokeOpcode, Type.getInternalName(declaringClass), method.getName(), Type.getMethodDescriptor(method), isInterface);
					methodVisitor.visitLabel(labelIf);
					// end
					if (hasThrows) {
						methodVisitor.visitLabel(labelEnd);
						Label labelFinal = new Label();
						methodVisitor.visitJumpInsn(GOTO, labelFinal);
						// catch块：捕获异常并记录错误
						methodVisitor.visitLabel(labelCatch);
						methodVisitor.visitVarInsn(ASTORE, 3);
						methodVisitor.visitVarInsn(ALOAD, 0);
						methodVisitor.visitLdcInsn(name);
						methodVisitor.visitVarInsn(ALOAD, 3);
						methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassNameInternal, resolveCopyError.serialized().getImplMethodName(), "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
						// finally块标记
						methodVisitor.visitLabel(labelFinal);
					}
				}
			});
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}

		// 泛型合成方法
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, copyMapToBeanName, "(Ljava/util/Map;Ljava/lang/Object;)V", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitVarInsn(ALOAD, 2);
			methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, accessClassNameInternal, copyMapToBeanName, "(Ljava/util/Map;L" + Type.getInternalName(type) + ";)V", false);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(3, 3);
			methodVisitor.visitEnd();
		}
	}


	@SuppressWarnings("all")
	private static <T> void insertMapToBeanByConverter(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertiesClassification classification) {
		SerializableConsumerWithArgs4<BeanCopier, Map<String, Object>, Object, BiFunction<java.lang.reflect.Type, Object, Object>> copyMapToBean = BeanCopier::copyMapToBean;
		SerializableFunctionWithArgs4<BeanCopier, java.lang.reflect.Type, Object, BiFunction<java.lang.reflect.Type, Object, Object>, Object> convert = BeanCopier::convert;
		SerializableTriConsumer<BeanCopier, String, Throwable> resolveCopyError = BeanCopier::resolveCopyError;

		String copyMapToBeanName = copyMapToBean.serialized().getImplMethodName();

		// 重写方法
		{
			// protected void copyMapToBean(Map<String, Object> source, S target, BiFunction<Type, Object, Object> converter)
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, copyMapToBeanName, "(Ljava/util/Map;L" + Type.getInternalName(type) + ";Ljava/util/function/BiFunction;)V", "(Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;L" + Type.getInternalName(type) + ";Ljava/util/function/BiFunction<Ljava/lang/reflect/Type;Ljava/lang/Object;Ljava/lang/Object;>;)V", null);
			methodVisitor.visitCode();

			classification.properties.forEach((name, info) -> {

				if (info.getField() != null) {
					Label labelStart = new Label();
					Label labelEnd = new Label();
					Label labelCatch = new Label();
					methodVisitor.visitTryCatchBlock(labelStart, labelEnd, labelCatch, "java/lang/Throwable");
					methodVisitor.visitLabel(labelStart);

					// try
					/*
					try {
						Object var = source.get(filedName);
						if( var != null ){
							var = super.convert(this.type_filedName, var, converter);
							if (var != null) {
								target.fieldName = (FieldType)var;
							}
						}
					}
					 */
					methodVisitor.visitVarInsn(ALOAD, 1);
					methodVisitor.visitLdcInsn(name);
					methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
					methodVisitor.visitVarInsn(ASTORE, 4);

					// if
					methodVisitor.visitVarInsn(ALOAD, 4);
					Label labelIf = new Label();
					methodVisitor.visitJumpInsn(IFNULL, labelIf);

					methodVisitor.visitVarInsn(ALOAD, 0);
					methodVisitor.visitVarInsn(ALOAD, 0);
					methodVisitor.visitFieldInsn(GETFIELD, accessClassNameInternal, FIELD_PREFIX_TYPE + name, "Ljava/lang/reflect/Type;");
					methodVisitor.visitVarInsn(ALOAD, 4);
					methodVisitor.visitVarInsn(ALOAD, 3);
					methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassNameInternal, convert.serialized().getImplMethodName(), "(Ljava/lang/reflect/Type;Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;", false);
					methodVisitor.visitVarInsn(ASTORE, 4);
					methodVisitor.visitVarInsn(ALOAD, 4);
					methodVisitor.visitJumpInsn(IFNULL, labelIf);
					methodVisitor.visitVarInsn(ALOAD, 2);
					methodVisitor.visitVarInsn(ALOAD, 4);
					Asms.autoUnBoxing(methodVisitor, Type.getType(info.getField().getType()));
					methodVisitor.visitFieldInsn(PUTFIELD, Type.getInternalName(info.getField().getDeclaringClass()), name, Type.getDescriptor(info.getField().getType()));

					methodVisitor.visitLabel(labelIf);

					// end
					methodVisitor.visitLabel(labelEnd);
					Label labelFinal = new Label();
					methodVisitor.visitJumpInsn(GOTO, labelFinal);

					// catch
					/*
					catch (Throwable t) {
						super.resolveCopyError(fieldName, t);
					}
					 */
					methodVisitor.visitLabel(labelCatch);
					methodVisitor.visitVarInsn(ASTORE, 4);
					methodVisitor.visitVarInsn(ALOAD, 0);
					methodVisitor.visitLdcInsn(name);
					methodVisitor.visitVarInsn(ALOAD, 4);
					methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassNameInternal, resolveCopyError.serialized().getImplMethodName(), "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
					//finally
					methodVisitor.visitLabel(labelFinal);
				} else if (info.getWriteMethod() != null) {
					Label labelStart = new Label();
					Label labelEnd = new Label();
					Label labelCatch = new Label();
					methodVisitor.visitTryCatchBlock(labelStart, labelEnd, labelCatch, "java/lang/Throwable");
					methodVisitor.visitLabel(labelStart);

					Method method = info.getWriteMethod();
					// try
					/*
					try {
						Object var = source.get(filedName);
						if( var != null ){
							var = super.convert(this.type_filedName, var, converter);
							if (var != null) {
								target.setFieldName((FieldType)var);
							}
						}
					}
					 */
					methodVisitor.visitVarInsn(ALOAD, 1);
					methodVisitor.visitLdcInsn(name);
					methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
					methodVisitor.visitVarInsn(ASTORE, 4);
					// if
					methodVisitor.visitVarInsn(ALOAD, 4);
					Label labelIf = new Label();
					methodVisitor.visitJumpInsn(IFNULL, labelIf);

					methodVisitor.visitVarInsn(ALOAD, 0);
					methodVisitor.visitVarInsn(ALOAD, 0);
					methodVisitor.visitFieldInsn(GETFIELD, accessClassNameInternal, FIELD_PREFIX_TYPE + name, "Ljava/lang/reflect/Type;");
					methodVisitor.visitVarInsn(ALOAD, 4);
					methodVisitor.visitVarInsn(ALOAD, 3);
					methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassNameInternal, convert.serialized().getImplMethodName(), "(Ljava/lang/reflect/Type;Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;", false);
					methodVisitor.visitVarInsn(ASTORE, 4);
					methodVisitor.visitVarInsn(ALOAD, 4);
					methodVisitor.visitJumpInsn(IFNULL, labelIf);
					methodVisitor.visitVarInsn(ALOAD, 2);
					methodVisitor.visitVarInsn(ALOAD, 4);
					Asms.autoUnBoxing(methodVisitor, Type.getType(method.getParameterTypes()[0]));

					Class<?> declaringClass = method.getDeclaringClass();
					boolean isInterface = declaringClass.isInterface();
					int invokeOpcode;
					if (isInterface) {
						invokeOpcode = INVOKEINTERFACE;
					} else if (Modifier.isStatic(method.getModifiers())) {
						invokeOpcode = INVOKESTATIC;
					} else {
						invokeOpcode = INVOKEVIRTUAL;
					}

					methodVisitor.visitMethodInsn(invokeOpcode, Type.getInternalName(declaringClass), method.getName(), Type.getMethodDescriptor(method), isInterface);

					methodVisitor.visitLabel(labelIf);

					// end
					methodVisitor.visitLabel(labelEnd);
					Label labelFinal = new Label();
					methodVisitor.visitJumpInsn(GOTO, labelFinal);

					// catch
					/*
					catch (Throwable t) {
						super.resolveCopyError(fieldName, t);
					}
					 */
					methodVisitor.visitLabel(labelCatch);
					methodVisitor.visitVarInsn(ASTORE, 4);
					methodVisitor.visitVarInsn(ALOAD, 0);
					methodVisitor.visitLdcInsn(name);
					methodVisitor.visitVarInsn(ALOAD, 4);
					methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassNameInternal, resolveCopyError.serialized().getImplMethodName(), "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);

					//finally
					methodVisitor.visitLabel(labelFinal);
				}
			});
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}

		// 泛型合成方法
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED | ACC_BRIDGE | ACC_SYNTHETIC, copyMapToBeanName, "(Ljava/util/Map;Ljava/lang/Object;Ljava/util/function/BiFunction;)V", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitVarInsn(ALOAD, 2);
			methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
			methodVisitor.visitVarInsn(ALOAD, 3);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, accessClassNameInternal, copyMapToBeanName, "(Ljava/util/Map;L" + Type.getInternalName(type) + ";Ljava/util/function/BiFunction;)V", false);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(4, 4);
			methodVisitor.visitEnd();
		}
	}

	/**
	 * 插入同类型Bean拷贝方法的字节码实现
	 * <p>
	 * 该方法生成copyBeanToBeanBySameType()的字节码，针对源和目标类型完全相同的场景进行优化。
	 * 对于每个属性：
	 * - 直接从源对象读取属性值（通过字段或getter）
	 * - 如果非基本类型且值为null则跳过
	 * - 直接设置到目标对象（通过字段或setter）
	 * - 包含可选的异常处理机制
	 * </p>
	 * <p>
	 * 这是性能最高的拷贝方式，因为不需要类型检查和转换。
	 * </p>
	 *
	 * @param cw ASM类写入器
	 * @param superClassNameInternal 父类的JVM内部名称
	 * @param accessClassNameInternal 访问器类的JVM内部名称
	 * @param type Bean的类型
	 * @param classification Bean属性分类信息
	 * @param <T> Bean类型参数
	 */
	@SuppressWarnings("all")
	private static <T> void insertBeanToBeanBySameType(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertiesClassification classification) {
		SerializableTriConsumer<BeanCopier, Object, Object> copyBeanToBeanBySameType = BeanCopier::copyBeanToBeanBySameType;
		SerializableTriConsumer<BeanCopier, String, Throwable> resolveCopyError = BeanCopier::resolveCopyError;

		String copyBeanToSameName = copyBeanToBeanBySameType.serialized().getImplMethodName();
		// 生成具体的copyBeanToBeanBySameType(Type source, Type target)方法实现
		{
			// protected void copyBeanToBeanBySameType(S source, S target)
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, copyBeanToSameName,
				"(L" + Type.getInternalName(type) + ";L" + Type.getInternalName(type) + ";)V", null, null);
			methodVisitor.visitCode();

			// 遍历所有属性，为每个属性生成直接拷贝的字节码
			classification.properties.forEach((name, info) -> {
				// 基本类型不可能为空，直接赋值
				if (info.getField() != null) {
					// 有公共字段：直接GETFIELD -> PUTFIELD
					/*
					target.fieldName = source.fieldName;
					 */
					methodVisitor.visitVarInsn(ALOAD, 1); // source
					methodVisitor.visitFieldInsn(GETFIELD, Type.getInternalName(info.getField().getDeclaringClass()), name, Type.getDescriptor(info.getField().getType()));
					// 存储到局部变量表
					Asms.storeVar(methodVisitor, info.getPropertyType(), 3);

					Label labelIf = null;
					// 非基本类型需要检查null
					if (!Types.isPrimitive(info.getPropertyType())) {
						labelIf = new Label();
						Asms.loadVar(methodVisitor, info.getPropertyType(), 3);
						methodVisitor.visitJumpInsn(IFNULL, labelIf);
					}

					methodVisitor.visitVarInsn(ALOAD, 2);
					Asms.loadVar(methodVisitor, info.getPropertyType(), 3);
					methodVisitor.visitFieldInsn(PUTFIELD, Type.getInternalName(info.getField().getDeclaringClass()), name, Type.getDescriptor(info.getField().getType()));
					if (labelIf != null) {
						methodVisitor.visitLabel(labelIf);
					}
				} else if (info.getReadMethod() != null && info.getWriteMethod() != null) {
					boolean hasThrows = info.getReadMethod().getExceptionTypes().length > 0
						|| info.getWriteMethod().getExceptionTypes().length > 0;
					Label labelStart = new Label();
					Label labelEnd = new Label();
					Label labelCatch = new Label();
					if (hasThrows) {
						methodVisitor.visitTryCatchBlock(labelStart, labelEnd, labelCatch, "java/lang/Throwable");
						methodVisitor.visitLabel(labelStart);
					}
					// try
					/*
					try{
						Object var = source.getFieldName();
						target.setFieldName(var);
					}
					 */
					methodVisitor.visitVarInsn(ALOAD, 1);
					{
						Class<?> declaringClass = info.getReadMethod().getDeclaringClass();
						boolean isInterface = declaringClass.isInterface();
						int invokeOpcode;
						if (isInterface) {
							invokeOpcode = INVOKEINTERFACE;
						} else if (Modifier.isStatic(info.getReadMethod().getModifiers())) {
							invokeOpcode = INVOKESTATIC;
						} else {
							invokeOpcode = INVOKEVIRTUAL;
						}
						methodVisitor.visitMethodInsn(invokeOpcode, Type.getInternalName(declaringClass), info.getReadMethod().getName(), Type.getMethodDescriptor(info.getReadMethod()), isInterface);
					}
					Asms.storeVar(methodVisitor, info.getPropertyType(), 3);
					Label labelIf = null;
					if (!Types.isPrimitive(info.getPropertyType())) {
						labelIf = new Label();
						Asms.loadVar(methodVisitor, info.getPropertyType(), 3);
						methodVisitor.visitJumpInsn(IFNULL, labelIf);
					}

					methodVisitor.visitVarInsn(ALOAD, 2);
					Asms.loadVar(methodVisitor, info.getPropertyType(), 3);
					{
						Class<?> declaringClass = info.getWriteMethod().getDeclaringClass();
						boolean isInterface = declaringClass.isInterface();
						int invokeOpcode;
						if (isInterface) {
							invokeOpcode = INVOKEINTERFACE;
						} else if (Modifier.isStatic(info.getWriteMethod().getModifiers())) {
							invokeOpcode = INVOKESTATIC;
						} else {
							invokeOpcode = INVOKEVIRTUAL;
						}
						methodVisitor.visitMethodInsn(invokeOpcode, Type.getInternalName(declaringClass), info.getWriteMethod().getName(), Type.getMethodDescriptor(info.getWriteMethod()), isInterface);
						if (labelIf != null) {
							methodVisitor.visitLabel(labelIf);
						}
					}
					// end
					if (hasThrows) {
						methodVisitor.visitLabel(labelEnd);
						Label labelFinal = new Label();
						methodVisitor.visitJumpInsn(GOTO, labelFinal);
						// catch
						/*
						catch (Throwable t) {
							super.resolveCopyError(fieldName, t);
						}
						 */
						methodVisitor.visitLabel(labelCatch);
						methodVisitor.visitVarInsn(ASTORE, 3);
						methodVisitor.visitVarInsn(ALOAD, 0);
						methodVisitor.visitLdcInsn(name);
						methodVisitor.visitVarInsn(ALOAD, 3);
						methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassNameInternal, resolveCopyError.serialized().getImplMethodName(), "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
						//finally
						methodVisitor.visitLabel(labelFinal);
					}
				}

			});

			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}
		// 泛型合成方法
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED | ACC_BRIDGE | ACC_SYNTHETIC, copyBeanToSameName, "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
			methodVisitor.visitVarInsn(ALOAD, 2);
			methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, accessClassNameInternal, copyBeanToSameName, "(L" + Type.getInternalName(type) + ";L" + Type.getInternalName(type) + ";)V", false);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(3, 3);
			methodVisitor.visitEnd();
		}
	}

	/**
	 * 插入带选项配置的Bean转Map方法的字节码实现
	 * <p>
	 * 该方法生成copyBeanToMapByOptions()的字节码，支持通过CopyOptions配置各种复制规则：
	 * - 忽略null值
	 * - 键名转换（驼峰、下划线等）
	 * - 值编辑和类型转换
	 * - 是否覆盖目标值
	 * </p>
	 * <p>
	 * 为每个属性生成独立的子方法copyBeanToMapWithKeyByOptions${propertyName}，
	 * 主方法依次调用所有子方法完成全部属性的复制。
	 * </p>
	 *
	 * @param cw ASM类写入器
	 * @param superClassNameInternal 父类的JVM内部名称
	 * @param accessClassNameInternal 访问器类的JVM内部名称
	 * @param type Bean的类型
	 * @param classification Bean属性分类信息
	 * @param <T> Bean类型参数
	 */
	@SuppressWarnings("all")
	private static <T> void insertBeanToMapByOptions(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertiesClassification classification) {
		String subMethodPrefix = "copyBeanToMapWithKeyByOptions$";
		List<String> subMethods = new ArrayList<>();
		// 为每个有getter的属性生成独立的子方法
		for (Map.Entry<String, BeanPropertyInfo> entry : classification.properties.entrySet()) {
			BeanPropertyInfo info = entry.getValue();
			if (!info.hasGetter()) {
				continue;
			}
			String subMethod = subMethodPrefix + info.getPropertyName();
			insertBeanToMapWithKeyByOptions(cw, superClassNameInternal, accessClassNameInternal, type, info, subMethod);
			subMethods.add(subMethod);
		}
		SerializableConsumerWithArgs6<BeanCopier, Object, Map, CopyOptions, JavaType, JavaType> copyBeanToMapByOptions = BeanCopier::copyBeanToMapByOptions;
		// 生成主方法：依次调用所有子方法
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, copyBeanToMapByOptions.serialized().getImplMethodName(),
				"(" +
					Type.getDescriptor(type) +
					"Ljava/util/Map;" +
					Type.getDescriptor(CopyOptions.class) +
					Type.getDescriptor(JavaType.class) +
					Type.getDescriptor(JavaType.class) + ")V",
				"<K:Ljava/lang/Object;V:Ljava/lang/Object;>(" +
					Type.getDescriptor(type) +
					"Ljava/util/Map<TK;TV;>;" +
					Type.getDescriptor(CopyOptions.class) +
					Type.getDescriptor(JavaType.class) +
					Type.getDescriptor(JavaType.class) +
					")V", null);
			methodVisitor.visitCode();
			// 依次调用每个属性的子方法
			for (String subMethod : subMethods) {
				methodVisitor.visitVarInsn(ALOAD, 0);
				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitVarInsn(ALOAD, 2);
				methodVisitor.visitVarInsn(ALOAD, 3);
				methodVisitor.visitVarInsn(ALOAD, 4);
				methodVisitor.visitVarInsn(ALOAD, 5);
				methodVisitor.visitMethodInsn(INVOKEVIRTUAL, accessClassNameInternal,
					subMethod, "(" +
						Type.getDescriptor(type) +
						"Ljava/util/Map;" +
						Type.getDescriptor(CopyOptions.class) +
						Type.getDescriptor(JavaType.class) +
						Type.getDescriptor(JavaType.class) + ")V", false);
			}
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(6, 6);
			methodVisitor.visitEnd();
		}
		// 泛型合成方法
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED | ACC_BRIDGE | ACC_SYNTHETIC,
				copyBeanToMapByOptions.serialized().getImplMethodName(),
				"(Ljava/lang/Object;Ljava/util/Map;" +
					Type.getDescriptor(CopyOptions.class) +
					Type.getDescriptor(JavaType.class) +
					Type.getDescriptor(JavaType.class) +
					")V", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
			methodVisitor.visitVarInsn(ALOAD, 2);
			methodVisitor.visitVarInsn(ALOAD, 3);
			methodVisitor.visitVarInsn(ALOAD, 4);
			methodVisitor.visitVarInsn(ALOAD, 5);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, accessClassNameInternal,
				copyBeanToMapByOptions.serialized().getImplMethodName(),
				"(" +
					Type.getDescriptor(type) +
					"Ljava/util/Map;" +
					Type.getDescriptor(CopyOptions.class) +
					Type.getDescriptor(JavaType.class) +
					Type.getDescriptor(JavaType.class) + ")V", false);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(6, 6);
			methodVisitor.visitEnd();
		}
	}

	private static <T> void insertBeanToMapWithKeyByOptions(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertyInfo info, String subMethod) {
		MethodVisitor methodVisitor = cw.visitMethod(ACC_PRIVATE, subMethod,
			"(" +
				Type.getDescriptor(type) +
				"Ljava/util/Map;" +
				Type.getDescriptor(CopyOptions.class) +
				Type.getDescriptor(JavaType.class) +
				Type.getDescriptor(JavaType.class) + ")V", null, null);
		methodVisitor.visitCode();
		Label label0 = new Label();
		Label label1 = new Label();
		Label label2 = new Label();
		methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Throwable");
		Label label3 = new Label();
		Label label4 = new Label();
		methodVisitor.visitTryCatchBlock(label3, label4, label2, "java/lang/Throwable");
		methodVisitor.visitLabel(label0);
		methodVisitor.visitLdcInsn(info.getPropertyName());
		methodVisitor.visitVarInsn(ASTORE, 6);
		methodVisitor.visitVarInsn(ALOAD, 3);
		methodVisitor.visitVarInsn(ALOAD, 6);
		SerializableBiFunction<CopyOptions, String, Boolean> isIgnoredKey = CopyOptions::isIgnoredKey;
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(CopyOptions.class), isIgnoredKey.serialized().getImplMethodName(), "(Ljava/lang/String;)Z", false);
		methodVisitor.visitJumpInsn(IFEQ, label3);
		methodVisitor.visitLabel(label1);
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitLabel(label3);
		methodVisitor.visitVarInsn(ALOAD, 1);
		if (info.getField() != null) {
			Class var7Class = info.getField().getType();
			methodVisitor.visitFieldInsn(GETFIELD,
				Type.getInternalName(info.getField().getDeclaringClass()),
				info.getField().getName(), Type.getDescriptor(var7Class));
			// autoBoxing
			Asms.autoBoxing(methodVisitor, var7Class);
		} else {
			Class<?> var7Class = info.getReadMethod().getReturnType();
			boolean isInterface = info.getReadMethod().getDeclaringClass().isInterface();
			methodVisitor.visitMethodInsn(isInterface ? INVOKEINTERFACE : INVOKEVIRTUAL,
				Type.getInternalName(info.getReadMethod().getDeclaringClass()),
				info.getReadMethod().getName(), Type.getMethodDescriptor(info.getReadMethod()), isInterface);
			// autoBoxing
			Asms.autoBoxing(methodVisitor, var7Class);
		}
		methodVisitor.visitVarInsn(ASTORE, 7);
		methodVisitor.visitVarInsn(ALOAD, 0);
		methodVisitor.visitVarInsn(ALOAD, 2);
		methodVisitor.visitVarInsn(ALOAD, 3);
		methodVisitor.visitVarInsn(ALOAD, 4);
		methodVisitor.visitVarInsn(ALOAD, 5);
		methodVisitor.visitVarInsn(ALOAD, 6);
		methodVisitor.visitVarInsn(ALOAD, 7);
		SerializableConsumerWithArgs7<BeanCopier, Map, CopyOptions, JavaType, JavaType, String, Object> copyBeanToMapWithKey = BeanCopier::copyBeanToMapWithKeyByOptions;
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, accessClassNameInternal,
			copyBeanToMapWithKey.serialized().getImplMethodName(),
			"(Ljava/util/Map;" +
				Type.getDescriptor(CopyOptions.class) +
				Type.getDescriptor(JavaType.class) +
				Type.getDescriptor(JavaType.class) +
				"Ljava/lang/String;Ljava/lang/Object;)V", false);
		methodVisitor.visitLabel(label4);
		Label label5 = new Label();
		methodVisitor.visitJumpInsn(GOTO, label5);
		methodVisitor.visitLabel(label2);
		methodVisitor.visitVarInsn(ASTORE, 6);
		methodVisitor.visitVarInsn(ALOAD, 0);
		methodVisitor.visitLdcInsn(info.getPropertyName());
		methodVisitor.visitVarInsn(ALOAD, 6);
		methodVisitor.visitVarInsn(ALOAD, 3);
		SerializableTriConsumer<BeanCopier, String, Throwable> resolveCopyError = BeanCopier::resolveCopyError;
		methodVisitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(BeanCopier.class), resolveCopyError.serialized().getImplMethodName(),
			"(Ljava/lang/String;Ljava/lang/Throwable;" +
				Type.getDescriptor(CopyOptions.class) + ")V", false);
		methodVisitor.visitLabel(label5);
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(7, 8);
		methodVisitor.visitEnd();
	}

	/**
	 * 插入自定义键映射的Map转Bean方法的字节码实现
	 * <p>
	 * 该方法生成copyMapToBeanWithCustomKeys()的字节码，处理需要动态键名映射的场景。
	 * 通过keyMapping参数指定源Map键到目标Bean属性的映射关系，
	 * 然后调用对应的copyMapToBeanWithSourceKeys${propertyName}子方法完成属性设置。
	 * </p>
	 *
	 * @param cw ASM类写入器
	 * @param accessClassNameInternal 访问器类的JVM内部名称
	 * @param type Bean的类型
	 * @param classification Bean属性分类信息
	 * @param <T> Bean类型参数
	 */
	@SuppressWarnings("all")
	private static <T> void insertMapToBeanWithCustomKeys(ClassWriter cw, String accessClassNameInternal,
		Class<T> type, BeanPropertiesClassification classification) {
		SerializableConsumerWithArgs6<BeanCopier, Map<String, Object>, Object, CopyOptions, Set<String>, Map<String, String>> copyMapToBeanWithCustomKeys = BeanCopier::copyMapToBeanWithCustomKeys;
		MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED,
			copyMapToBeanWithCustomKeys.serialized().getImplMethodName(),
			"(Ljava/util/Map;Ljava/lang/Object;" +
				Type.getDescriptor(CopyOptions.class) +
				"Ljava/util/Set;Ljava/util/Map;)V",
			"(Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;Ljava/lang/Object;" +
				Type.getDescriptor(CopyOptions.class) +
				"Ljava/util/Set<Ljava/lang/String;>;Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;)V", null);
		methodVisitor.visitCode();
		classification.properties.forEach((key, info) -> {
			if (info.hasSetter()) {
				methodVisitor.visitVarInsn(ALOAD, 4);
				methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "isEmpty", "()Z", true);
				Label label0 = new Label();
				methodVisitor.visitJumpInsn(IFNE, label0);
				Label label1 = new Label();
				methodVisitor.visitJumpInsn(GOTO, label1);
				methodVisitor.visitLabel(label0);
				methodVisitor.visitInsn(RETURN);
				methodVisitor.visitLabel(label1);
				methodVisitor.visitVarInsn(ALOAD, 0);
				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitVarInsn(ALOAD, 2);
				methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
				methodVisitor.visitVarInsn(ALOAD, 3);
				methodVisitor.visitVarInsn(ALOAD, 4);
				methodVisitor.visitVarInsn(ALOAD, 5);
				methodVisitor.visitLdcInsn(info.getPropertyName());
				methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
				methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/String");
				String methodName = "copyMapToBeanWithSourceKeys$" + info.getPropertyName();
				methodVisitor.visitMethodInsn(INVOKESPECIAL, accessClassNameInternal, methodName, "(Ljava/util/Map;" +
					Type.getDescriptor(type) +
					Type.getDescriptor(CopyOptions.class) +
					"Ljava/util/Set;Ljava/lang/String;)V", false);
			}
		});
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(7, 7);
		methodVisitor.visitEnd();
	}

	/**
	 * 插入带源键映射的Map转Bean方法的字节码实现
	 * <p>
	 * 该方法生成各种键名映射策略的Map转Bean方法，包括：
	 * - copyMapToBeanWithSameKeys: 相同键名
	 * - copyMapToBeanWithCapitalizeKeys: 首字母大小写转换
	 * - copyMapToBeanWithUnderlineToCamelKeys: 下划线转驼峰
	 * - copyMapToBeanWithCamelToUnderlineKeys: 驼峰转下划线
	 * </p>
	 * <p>
	 * 通过keyMapper函数将目标属性名转换为源Map的键名，
	 * 如果转换后的键名不为null，则调用对应的子方法进行属性设置。
	 * </p>
	 *
	 * @param cw ASM类写入器
	 * @param accessClassNameInternal 访问器类的JVM内部名称
	 * @param type Bean的类型
	 * @param classification Bean属性分类信息
	 * @param copyMethod 要生成的方法引用
	 * @param keyMapper 键名转换函数，返回null表示跳过该属性
	 * @param <T> Bean类型参数
	 */
	@SuppressWarnings("all")
	private static <T> void insertMapToBeanWithSourceKeys(ClassWriter cw, String accessClassNameInternal,
		Class<T> type, BeanPropertiesClassification classification,
		SerializableConsumerWithArgs5<BeanCopier, Map<String, Object>, Object, CopyOptions, Set<String>> copyMethod,
		Function<String, String> keyMapper) {
		MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC, copyMethod.serialized().getImplMethodName(),
			"(Ljava/util/Map;Ljava/lang/Object;" +
				Type.getDescriptor(CopyOptions.class) +
				"Ljava/util/Set;)V",
			"(Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;Ljava/lang/Object;" +
				Type.getDescriptor(CopyOptions.class) + "Ljava/util/Set<Ljava/lang/String;>;)V", null);
		methodVisitor.visitCode();
		// 遍历所有有setter的属性，根据keyMapper生成对应的源键名
		classification.properties.forEach((key, info) -> {
			if (info.hasSetter()) {
				String sourceKey = keyMapper.apply(info.getPropertyName());
				// 如果keyMapper返回null，表示该属性不需要在此方法中处理
				if (sourceKey == null) {
					return;
				}
				Label label0 = new Label();
				methodVisitor.visitVarInsn(ALOAD, 4);
				methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "isEmpty", "()Z", true);
				Label label1 = new Label();
				methodVisitor.visitJumpInsn(IFEQ, label1);
				methodVisitor.visitLabel(label0);
				methodVisitor.visitInsn(RETURN);
				methodVisitor.visitLabel(label1);
				methodVisitor.visitVarInsn(ALOAD, 0);
				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitVarInsn(ALOAD, 2);
				methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
				methodVisitor.visitVarInsn(ALOAD, 3);
				methodVisitor.visitVarInsn(ALOAD, 4);
				methodVisitor.visitLdcInsn(sourceKey);
				String methodName = "copyMapToBeanWithSourceKeys$" + info.getPropertyName();
				methodVisitor.visitMethodInsn(INVOKESPECIAL, accessClassNameInternal, methodName, "(Ljava/util/Map;" +
					Type.getDescriptor(type) +
					Type.getDescriptor(CopyOptions.class) +
					"Ljava/util/Set;Ljava/lang/String;)V", false);
			}
		});

		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(3, 6);
		methodVisitor.visitEnd();
	}

	/**
	 * 插入使用所有源键的Map转Bean方法的字节码实现
	 * <p>
	 * 该方法为每个有setter的属性生成一个独立的子方法copyMapToBeanWithSourceKeys${propertyName}，
	 * 这些子方法被insertMapToBeanWithCustomKeys和insertMapToBeanWithSourceKeys调用，
	 * 实现具体的属性设置逻辑。
	 * </p>
	 *
	 * @param cw ASM类写入器
	 * @param superClassNameInternal 父类的JVM内部名称
	 * @param accessClassNameInternal 访问器类的JVM内部名称
	 * @param type Bean的类型
	 * @param classification Bean属性分类信息
	 * @param <T> Bean类型参数
	 */
	@SuppressWarnings("all")
	private static <T> void insertMapToBeanWithSourceKeyAll(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertiesClassification classification) {
		classification.properties.forEach((key, info) -> {
			if (info.hasSetter()) {
				insertMapToBeanWithSourceKey(cw, superClassNameInternal, accessClassNameInternal, type, info);
			}
		});
	}

	/**
	 * 插入单个属性的Map转Bean子方法的字节码实现
	 * <p>
	 * 该方法为每个属性生成一个私有的copyMapToBeanWithSourceKeys${propertyName}方法，
	 * 实现从Map中读取指定键的值并设置到Bean属性的完整逻辑。
	 * </p>
	 * <p>
	 * 生成的字节码包含以下检查和处理：
	 * 1. 检查源键是否为null或被忽略
	 * 2. 检查目标键是否在待处理集合中
	 * 3. 如果不覆盖且目标已有值则跳过
	 * 4. 从Map获取值并检查null
	 * 5. 调用options.editValue()编辑值
	 * 6. 调用options.convert()进行类型转换
	 * 7. 再次检查转换后的值和基本类型
	 * 8. 类型匹配后设置字段或调用setter
	 * 9. 从待处理集合中移除已处理的键
	 * 10. 完整的异常处理和错误报告
	 * </p>
	 *
	 * @param cw ASM类写入器
	 * @param superClassNameInternal 父类的JVM内部名称
	 * @param accessClassNameInternal 访问器类的JVM内部名称
	 * @param type Bean的类型
	 * @param info 当前属性的信息
	 * @param <T> Bean类型参数
	 */
	@SuppressWarnings("all")
	private static <T> void insertMapToBeanWithSourceKey(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertyInfo info) {
		String methodName = "copyMapToBeanWithSourceKeys$" + info.getPropertyName();
		String targetKey = info.getPropertyName();
		{
			// 生成私有方法：copyMapToBeanWithSourceKeys$propertyName(Map, Bean, CopyOptions, Set<String>, String)
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PRIVATE, methodName, "(Ljava/util/Map;" +
				Type.getDescriptor(type) +
				Type.getDescriptor(CopyOptions.class) +
				"Ljava/util/Set;Ljava/lang/String;)V", "(Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;" +
				Type.getDescriptor(type) +
				Type.getDescriptor(CopyOptions.class) +
				"Ljava/util/Set<Ljava/lang/String;>;Ljava/lang/String;)V", null);
			methodVisitor.visitCode();
			// 定义多个异常处理块，覆盖不同的代码段
			Label label0 = new Label();
			Label label1 = new Label();
			Label label2 = new Label();
			methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Throwable");
			Label label3 = new Label();
			Label label4 = new Label();
			methodVisitor.visitTryCatchBlock(label3, label4, label2, "java/lang/Throwable");
			Label label5 = new Label();
			Label label6 = new Label();
			methodVisitor.visitTryCatchBlock(label5, label6, label2, "java/lang/Throwable");
			Label label7 = new Label();
			Label label8 = new Label();
			methodVisitor.visitTryCatchBlock(label7, label8, label2, "java/lang/Throwable");
			Label label9 = new Label();
			Label label10 = new Label();
			methodVisitor.visitTryCatchBlock(label9, label10, label2, "java/lang/Throwable");
			Label label11 = new Label();
			Label label12 = new Label();
			methodVisitor.visitTryCatchBlock(label11, label12, label2, "java/lang/Throwable");
			methodVisitor.visitLdcInsn(targetKey);
			methodVisitor.visitVarInsn(ASTORE, 6);
			methodVisitor.visitLabel(label0);
			/* 步骤1: 检查源键是否有效 */
			/* if (sourceKey == null || options.isIgnoredKey(sourceKey)) {
				return;
			} */
			methodVisitor.visitVarInsn(ALOAD, 5); // sourceKey
			methodVisitor.visitJumpInsn(IFNULL, label1);
			methodVisitor.visitVarInsn(ALOAD, 3);
			methodVisitor.visitVarInsn(ALOAD, 5);
			SerializableBiFunction<CopyOptions, String, Boolean> isIgnoredKey = CopyOptions::isIgnoredKey;
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(CopyOptions.class), isIgnoredKey.serialized().getImplMethodName(), "(Ljava/lang/String;)Z", false);
			methodVisitor.visitJumpInsn(IFEQ, label3);
			methodVisitor.visitLabel(label1);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitLabel(label3);
			/* 步骤2: 检查目标键是否在待处理集合中 */
			/* if (!targetKeys.contains(targetKey)) {
				return;
			} */
			methodVisitor.visitVarInsn(ALOAD, 4); // targetKeys
			methodVisitor.visitVarInsn(ALOAD, 6);
			methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "contains", "(Ljava/lang/Object;)Z", true);
			methodVisitor.visitJumpInsn(IFNE, label5);
			methodVisitor.visitLabel(label4);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitLabel(label5);
			/* 步骤3: 检查是否需要覆盖目标值 */
			/* if (!options.override()) {
				Object orig = var2.get();
				if (orig != null) {
					return;
				}
			} */
			methodVisitor.visitVarInsn(ALOAD, 3); // options
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(CopyOptions.class), "override", "()Z", false);
			methodVisitor.visitJumpInsn(IFNE, label7);
			if (info.getField() != null) {
				methodVisitor.visitVarInsn(ALOAD, 2);
				methodVisitor.visitFieldInsn(GETFIELD, Type.getDescriptor(info.getField().getDeclaringClass()), info.getField().getName(), Type.getDescriptor(info.getField().getType()));
				Asms.autoBoxing(methodVisitor, info.getField().getType());
				methodVisitor.visitVarInsn(ASTORE, 7);
				methodVisitor.visitVarInsn(ALOAD, 7);
				methodVisitor.visitJumpInsn(IFNULL, label7);
			} else if (info.getReadMethod() != null) {
				methodVisitor.visitVarInsn(ALOAD, 2);
				boolean isInterface = info.getReadMethod().getDeclaringClass().isInterface();
				methodVisitor.visitMethodInsn(isInterface ? INVOKEINTERFACE : INVOKEVIRTUAL,
					Type.getInternalName(info.getReadMethod().getDeclaringClass()),
					info.getReadMethod().getName(), Type.getMethodDescriptor(info.getReadMethod()), isInterface);
				// autoBoxing
				Asms.autoBoxing(methodVisitor, info.getReadMethod().getReturnType());
				methodVisitor.visitVarInsn(ASTORE, 7);
				methodVisitor.visitVarInsn(ALOAD, 7);
				methodVisitor.visitJumpInsn(IFNULL, label7);
			} else {
				methodVisitor.visitJumpInsn(GOTO, label7);
			}
			methodVisitor.visitLabel(label6);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitLabel(label7);
			/* 步骤4: 从Map获取值并检查null */
			/* Object value = var1.get(sourceKey);
			if (value == null && options.ignoreNull()) {
				return;
			} */
			methodVisitor.visitVarInsn(ALOAD, 1); // source Map
			methodVisitor.visitVarInsn(ALOAD, 5);
			methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
			methodVisitor.visitVarInsn(ASTORE, 7);
			methodVisitor.visitVarInsn(ALOAD, 7);
			methodVisitor.visitJumpInsn(IFNONNULL, label9);
			methodVisitor.visitVarInsn(ALOAD, 3);
			SerializableFunction<CopyOptions, Boolean> ignoreNull = CopyOptions::ignoreNull;
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(CopyOptions.class), ignoreNull.serialized().getImplMethodName(), "()Z", false);
			methodVisitor.visitJumpInsn(IFEQ, label9);
			methodVisitor.visitLabel(label8);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitLabel(label9);
			/* 步骤5: 编辑值 */
			/* value = options.editValue(sourceKey, value); */
			methodVisitor.visitVarInsn(ALOAD, 3); // options
			methodVisitor.visitVarInsn(ALOAD, 5);
			methodVisitor.visitVarInsn(ALOAD, 7);
			SerializableTriFunction<CopyOptions, String, Object, Object> editValue = CopyOptions::editValue;
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(CopyOptions.class), editValue.serialized().getImplMethodName(), "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", false);
			/* 步骤6: 类型转换 */
			/* value = options.convert(this.type$field, value); */
			methodVisitor.visitVarInsn(ASTORE, 7);
			methodVisitor.visitVarInsn(ALOAD, 3);
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitFieldInsn(GETFIELD, accessClassNameInternal, FIELD_PREFIX_TYPE + info.getPropertyName(), "Ljava/lang/reflect/Type;");
			methodVisitor.visitVarInsn(ALOAD, 7);
			SerializableTriFunction<CopyOptions, java.lang.reflect.Type, Object, Object> convert = CopyOptions::convert;
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(CopyOptions.class), convert.serialized().getImplMethodName(), "(Ljava/lang/reflect/Type;Ljava/lang/Object;)Ljava/lang/Object;", false);
			methodVisitor.visitVarInsn(ASTORE, 7);
			/* 步骤7: 再次检查转换后的值和基本类型 */
			/* if (value == null && (options.ignoreNull() || class$booleanVal0.isPrimitive())) {
				return;
			} */
			methodVisitor.visitVarInsn(ALOAD, 7); // value
			methodVisitor.visitJumpInsn(IFNONNULL, label11);
			methodVisitor.visitVarInsn(ALOAD, 3);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(CopyOptions.class), ignoreNull.serialized().getImplMethodName(), "()Z", false);
			methodVisitor.visitJumpInsn(IFNE, label10);
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitFieldInsn(GETFIELD, accessClassNameInternal, FIELD_PREFIX_CLASS + info.getPropertyName(), "Ljava/lang/Class;");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "isPrimitive", "()Z", false);
			methodVisitor.visitJumpInsn(IFEQ, label11);
			methodVisitor.visitLabel(label10);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitLabel(label11);
			/* 步骤8: 类型匹配后设置字段或调用setter */
			/* if (Types.getWrapperClass(class$field).isInstance(value)) {
				var2.set( (Boolean) value);
				targetKeys.remove(targetKey);
			} */
			methodVisitor.visitVarInsn(ALOAD, 0); // this
			methodVisitor.visitFieldInsn(GETFIELD, accessClassNameInternal, FIELD_PREFIX_CLASS + info.getPropertyName(), "Ljava/lang/Class;");
			SerializableFunction<Class, Class> getWrapperClass = Types::getWrapperClass;
			methodVisitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Types.class), getWrapperClass.serialized().getImplMethodName(), "(Ljava/lang/Class;)Ljava/lang/Class;", false);
			methodVisitor.visitVarInsn(ALOAD, 7);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "isInstance", "(Ljava/lang/Object;)Z", false);
			methodVisitor.visitJumpInsn(IFEQ, label12);
			// 类型匹配，设置属性值
			methodVisitor.visitVarInsn(ALOAD, 2); // target Bean
			methodVisitor.visitVarInsn(ALOAD, 7);
			Asms.autoUnBoxing(methodVisitor, info.getPropertyType());
			if (info.getField() != null) {
				methodVisitor.visitFieldInsn(PUTFIELD, Type.getInternalName(info.getField().getDeclaringClass()), info.getField().getName(), Type.getDescriptor(info.getField().getType()));
			} else {
				boolean isInterface = info.getWriteMethod().getDeclaringClass().isInterface();
				methodVisitor.visitMethodInsn(isInterface ? INVOKEINTERFACE : INVOKEVIRTUAL,
					Type.getInternalName(info.getWriteMethod().getDeclaringClass()),
					info.getWriteMethod().getName(), Type.getMethodDescriptor(info.getWriteMethod()), isInterface);
			}
			// 步骤9: 从待处理集合中移除已处理的键
			methodVisitor.visitVarInsn(ALOAD, 4); // targetKeys
			methodVisitor.visitVarInsn(ALOAD, 6);
			methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "remove", "(Ljava/lang/Object;)Z", true);
			methodVisitor.visitInsn(POP);
			methodVisitor.visitLabel(label12);
			Label label13 = new Label();
			methodVisitor.visitJumpInsn(GOTO, label13);
			// catch块：异常处理
			methodVisitor.visitLabel(label2);
			methodVisitor.visitVarInsn(ASTORE, 7);
			methodVisitor.visitVarInsn(ALOAD, 0);

			// 构建错误消息：sourceKey + "->" + targetKey
			methodVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
			methodVisitor.visitVarInsn(ALOAD, 5);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
			methodVisitor.visitLdcInsn("->" + targetKey);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
			methodVisitor.visitVarInsn(ALOAD, 7);
			methodVisitor.visitVarInsn(ALOAD, 3);
			SerializableConsumerWithArgs4<BeanCopier, String, Throwable, CopyOptions> resolveCopyError = BeanCopier::resolveCopyError;
			methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassNameInternal, resolveCopyError.serialized().getImplMethodName(), "(Ljava/lang/String;Ljava/lang/Throwable;" +
				Type.getDescriptor(CopyOptions.class) +
				")V", false);
			methodVisitor.visitLabel(label13);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(3, 8);
			methodVisitor.visitEnd();
		}
	}

}
