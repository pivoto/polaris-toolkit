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

import io.polaris.core.asm.AsmUtils;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.lang.Types;
import io.polaris.core.lang.copier.CopyOptions;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
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
 * @since 1.8,  Apr 14, 2024
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class BeanCopier<S> {
	private static ILogger log = ILoggers.of(BeanCopier.class);
	@SuppressWarnings({"rawtypes"})
	private static final AccessPool<Class<?>, BeanCopier> pool = new AccessPool<>();
	public static final String FIELD_PREFIX_TYPE = "type_";
	public static final String FIELD_PREFIX_CLASS = "class_";
	private final Map<Class<?>, BeanDirectCopier> fastCopiers = new ConcurrentHashMap<>();
	private final Map<Class<?>, BeanOptionsCopier> optionsCopiers = new ConcurrentHashMap<>();

	protected Class<S> sourceType;
	protected Map<String, BeanPropertyInfo> properties;

	protected BeanCopier() {
	}

	protected abstract void initTypeFields();

	public abstract void copyBeanToMap(@Nonnull S source, @Nonnull Map<String, Object> target);

	@SuppressWarnings({"rawtypes", "unchecked"})
	public final <K, V> void copyBeanToMap(@Nonnull S source, @Nonnull java.lang.reflect.Type targetType, @Nonnull Map<K, V> target, BiFunction<java.lang.reflect.Type, Object, Object> converter) {
		JavaType javaType = JavaType.of(targetType);
		JavaType keyType = JavaType.of(javaType.getActualType(Map.class, 0));
		JavaType valueType = JavaType.of(javaType.getActualType(Map.class, 1));
		copyBeanToMapByConverter(source, target, converter, keyType, valueType);
	}

	protected abstract void copyBeanToMapByConverter(@Nonnull Object source, @Nonnull Map target, BiFunction<java.lang.reflect.Type, Object, Object> converter, @Nonnull JavaType keyType, @Nonnull JavaType valueType);

	public abstract void copyMapToBean(@Nonnull Map<String, Object> source, @Nonnull S target);

	public abstract void copyMapToBean(@Nonnull Map<String, Object> source, @Nonnull S target, BiFunction<java.lang.reflect.Type, Object, Object> converter);

	@SuppressWarnings({"unchecked"})
	public final <T> void copyBeanToBean(@Nonnull S source, @Nonnull T target) {
		copyBeanToBean(source, (Class<T>) target.getClass(), target);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
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

	protected abstract void copyBeanToBeanBySameType(@Nonnull S source, @Nonnull S target);

	public final void copyBeanToMap(@Nonnull S source, @Nonnull Map<String, Object> target, @Nonnull CopyOptions options) {
		copyBeanToMap(source, new TypeRef<Map<String, Object>>() {}.getType(), target, options);
	}

	public final <K, V> void copyBeanToMap(@Nonnull S source, @Nonnull java.lang.reflect.Type targetType, @Nonnull Map<K, V> target, @Nonnull CopyOptions options) {
		JavaType javaType = JavaType.of(targetType);
		JavaType keyType = JavaType.of(javaType.getActualType(Map.class, 0));
		JavaType valueType = JavaType.of(javaType.getActualType(Map.class, 1));
		copyBeanToMapByOptions(source, target, options, keyType, valueType);
	}

	protected abstract <K, V> void copyBeanToMapByOptions(@Nonnull S source, @Nonnull Map<K, V> target, @Nonnull CopyOptions options, @Nonnull JavaType keyType, @Nonnull JavaType valueType);


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

	protected final <T> void copyBeanToBeanWithDynamicKeyMappings(@Nonnull BeanAccess<S> sourceBeanAccess, @Nonnull S source,@Nonnull  BeanAccess<T> targetBeanAccess, @Nonnull T target, @Nonnull CopyOptions options, @Nonnull Set<String> targetKeys, @Nonnull List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> mapping) {
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

	protected final <T> void copyBeanToBeanWithDynamicKeyMapping(@Nonnull BeanAccess<T> targetBeanAccess,@Nonnull  T target, @Nonnull String sourceKey,@Nonnull  String targetKey, @Nonnull Supplier<Object> valueSupplier, @Nonnull CopyOptions options,@Nonnull  Set<String> targetKeys) {
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

	@SuppressWarnings("unchecked")
	public static <T> BeanCopier<T> get(@Nonnull Class<T> type) {
		return pool.computeIfAbsent(type, BeanCopier::create);
	}

	@SuppressWarnings({"unchecked"})
	public static <T> BeanCopier<T> create(@Nonnull Class<T> type) {
		BeanPropertyInfo.Classification classification = BeanPropertyInfo.classify(type);
		String accessClassName = AccessClassLoader.buildAccessClassName(type, BeanCopier.class);

		AccessClassLoader loader = AccessClassLoader.get(type);
		Class accessClass = loader.loadOrDefineClass(accessClassName, () -> buildAccessClass(loader, accessClassName, type, classification));
		BeanCopier<T> access;
		try {
			access = (BeanCopier<T>) accessClass.newInstance();
			// 全部设为只读属性
			access.sourceType = type;
			access.properties = Collections.unmodifiableMap(classification.properties);
			access.initTypeFields();
			return access;
		} catch (Throwable t) {
			throw new IllegalStateException("创建访问类失败: " + accessClassName, t);
		}
	}

	private static <T> byte[] buildAccessClass(AccessClassLoader loader
		, String accessClassName, Class<T> type
		, BeanPropertyInfo.Classification classification) {
		String accessClassNameInternal = accessClassName.replace('.', '/');

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		String superClassNameInternal = BeanCopier.class.getName().replace('.', '/');
		cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal,
			"L" + superClassNameInternal + "<L" + Type.getInternalName(type) + ";>;",
			superClassNameInternal, null);
		cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

		AsmUtils.insertDefaultConstructor(cw, superClassNameInternal);
		insertInitTypeFields(cw, superClassNameInternal, accessClassNameInternal, type, classification);
		insertBeanToMap(cw, superClassNameInternal, accessClassNameInternal, type, classification);
		insertBeanToMapByConverter(cw, superClassNameInternal, accessClassNameInternal, type, classification);
		insertMapToBean(cw, superClassNameInternal, accessClassNameInternal, type, classification);
		insertMapToBeanByConverter(cw, superClassNameInternal, accessClassNameInternal, type, classification);
		insertBeanToBeanBySameType(cw, superClassNameInternal, accessClassNameInternal, type, classification);
		insertBeanToMapByOptions(cw, superClassNameInternal, accessClassNameInternal, type, classification);

		insertMapToBeanWithSourceKeyAll(cw, superClassNameInternal, accessClassNameInternal, type, classification);
		// copyMapToBeanWithCustomKeys
		insertMapToBeanWithCustomKeys(cw, accessClassNameInternal, type, classification);
		// copyMapToBeanWithSameKeys
		insertMapToBeanWithSourceKeys(cw, accessClassNameInternal, type, classification, BeanCopier::copyMapToBeanWithSameKeys, key -> key);
		// copyMapToBeanWithCapitalizeKeys
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
		// copyMapToBeanWithUnderlineToCamelKeys
		insertMapToBeanWithSourceKeys(cw, accessClassNameInternal, type, classification, BeanCopier::copyMapToBeanWithUnderlineToCamelKeys, key -> {
			String mapKey = StringCases.camelToUnderlineCase(key);
			if (!key.equals(mapKey)) {
				return mapKey;
			}
			return null;
		});
		// copyMapToBeanWithCamelToUnderlineKeys
		insertMapToBeanWithSourceKeys(cw, accessClassNameInternal, type, classification, BeanCopier::copyMapToBeanWithCamelToUnderlineKeys, key -> {
			if (key.indexOf('_') >= 0) {
				String mapKey = StringCases.underlineToCamelCase(key);
				return mapKey;
			}
			return null;
		});

		cw.visitEnd();
		byte[] byteArray = cw.toByteArray();
		return byteArray;
	}

	@SuppressWarnings("all")
	private static <T> void insertInitTypeFields(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertyInfo.Classification classification) {
		SerializableConsumer<BeanCopier> init = BeanCopier::initTypeFields;
		SerializableBiFunction<BeanCopier, String, java.lang.reflect.Type> getPropertyGenericType = BeanCopier::getPropertyGenericType;
		SerializableBiFunction<BeanCopier, String, Class> getPropertyType = BeanCopier::getPropertyType;

		insertInitTypes(cw, superClassNameInternal, accessClassNameInternal, classification,
			init.serialized().getImplMethodName(),
			getPropertyGenericType.serialized().getImplMethodName(),
			getPropertyType.serialized().getImplMethodName());
	}

	static void insertInitTypes(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, BeanPropertyInfo.Classification classification, String initTypesMethodName, String getPropertyGenericTypeMethodName, String getPropertyTypeMethodName) {
		// field
		classification.properties.forEach((name, info) -> {
			FieldVisitor fieldVisitor = cw.visitField(ACC_PROTECTED, FIELD_PREFIX_TYPE + name, "Ljava/lang/reflect/Type;", null, null);
			fieldVisitor.visitEnd();
			fieldVisitor = cw.visitField(ACC_PROTECTED, FIELD_PREFIX_CLASS + name, "Ljava/lang/Class;", null, null);
			fieldVisitor.visitEnd();
		});

		MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, initTypesMethodName, "()V", null, null);
		methodVisitor.visitCode();
		classification.properties.forEach((name, info) -> {
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitLdcInsn(name);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassNameInternal, getPropertyGenericTypeMethodName, "(Ljava/lang/String;)Ljava/lang/reflect/Type;", false);
			methodVisitor.visitFieldInsn(PUTFIELD, accessClassNameInternal, FIELD_PREFIX_TYPE + name, "Ljava/lang/reflect/Type;");

			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitLdcInsn(name);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassNameInternal, getPropertyTypeMethodName, "(Ljava/lang/String;)Ljava/lang/Class;", false);
			methodVisitor.visitFieldInsn(PUTFIELD, accessClassNameInternal, FIELD_PREFIX_CLASS + name, "Ljava/lang/Class;");
		});
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(0, 0);
		methodVisitor.visitEnd();
	}


	@SuppressWarnings("all")
	private static <T> void insertBeanToMap(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertyInfo.Classification classification) {
		SerializableTriConsumer<BeanCopier, String, Throwable> resolveCopyError = BeanCopier::resolveCopyError;
		SerializableTriConsumer<BeanCopier, Object, Map<String, Object>> copyBeanToMap = BeanCopier::copyBeanToMap;
		String copyBeanToMapName = copyBeanToMap.serialized().getImplMethodName();

		// 重写方法
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC, copyBeanToMapName, "(L" + Type.getInternalName(type) + ";Ljava/util/Map;)V", "(L" + Type.getInternalName(type) + ";Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;)V", null);
			methodVisitor.visitCode();

			classification.properties.forEach((name, info) -> {
				if (info.getField() != null) {
					methodVisitor.visitVarInsn(ALOAD, 2);
					methodVisitor.visitLdcInsn(name);
					methodVisitor.visitVarInsn(ALOAD, 1);
					methodVisitor.visitFieldInsn(GETFIELD, Type.getInternalName(info.getField().getDeclaringClass()), name, Type.getDescriptor(info.getField().getType()));
					AsmUtils.autoBoxing(methodVisitor, info.getField().getType());
					methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
					methodVisitor.visitInsn(POP);
				} else if (info.getReadMethod() != null) {
					boolean hasThrows = info.getReadMethod().getExceptionTypes().length > 0;
					Label labelStart = new Label();
					Label labelEnd = new Label();
					Label labelCatch = new Label();
					if (hasThrows) {
						methodVisitor.visitTryCatchBlock(labelStart, labelEnd, labelCatch, "java/lang/Throwable");
						methodVisitor.visitLabel(labelStart);
					}
					// try
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
					methodVisitor.visitVarInsn(ALOAD, 2);
					methodVisitor.visitLdcInsn(name);
					methodVisitor.visitVarInsn(ALOAD, 1);
					methodVisitor.visitMethodInsn(invokeOpcode, Type.getInternalName(declaringClass), readMethod.getName(), Type.getMethodDescriptor(readMethod), isInterface);
					Class<?> returnType = readMethod.getReturnType();
					AsmUtils.autoBoxing(methodVisitor, returnType);
					methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
					methodVisitor.visitInsn(POP);
					// end
					methodVisitor.visitLabel(labelEnd);
					Label labelFinal = new Label();
					methodVisitor.visitJumpInsn(GOTO, labelFinal);
					// catch
					methodVisitor.visitLabel(labelCatch);
					methodVisitor.visitVarInsn(ASTORE, 3);
					methodVisitor.visitVarInsn(ALOAD, 0);
					methodVisitor.visitLdcInsn(name);
					methodVisitor.visitVarInsn(ALOAD, 3);
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
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, copyBeanToMapName, "(Ljava/lang/Object;Ljava/util/Map;)V", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
			methodVisitor.visitVarInsn(ALOAD, 2);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, accessClassNameInternal, copyBeanToMapName, "(L" + Type.getInternalName(type) + ";Ljava/util/Map;)V", false);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(3, 3);
			methodVisitor.visitEnd();
		}
	}

	@SuppressWarnings("all")
	private static <T> void insertBeanToMapByConverter(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertyInfo.Classification classification) {
		SerializableTriConsumer<BeanCopier, String, Throwable> resolveCopyError = BeanCopier::resolveCopyError;
		SerializableConsumerWithArgs6<BeanCopier, Object, Map, BiFunction<java.lang.reflect.Type, Object, Object>, JavaType, JavaType> copyBeanToMap = BeanCopier::copyBeanToMapByConverter;
		SerializableFunctionWithArgs4<BeanCopier, java.lang.reflect.Type, Object, BiFunction<java.lang.reflect.Type, Object, Object>, Object> convert = BeanCopier::convert;
		// 重写方法
		{
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
				Label label0 = new Label();
				Label label1 = new Label();
				Label label2 = new Label();
				methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Throwable");
				methodVisitor.visitLabel(label0);
				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
				if (info.getField() != null) {
					methodVisitor.visitFieldInsn(GETFIELD, Type.getInternalName(info.getField().getDeclaringClass()), name, Type.getDescriptor(info.getField().getType()));
					AsmUtils.autoBoxing(methodVisitor, info.getField().getType());
				} else {
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
					AsmUtils.autoBoxing(methodVisitor, returnType);
				}
				methodVisitor.visitVarInsn(ASTORE, 6);
				methodVisitor.visitVarInsn(ALOAD, 0);
				methodVisitor.visitVarInsn(ALOAD, 5);
				methodVisitor.visitVarInsn(ALOAD, 6);
				methodVisitor.visitVarInsn(ALOAD, 3);
				methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassNameInternal,
					convert.serialized().getImplMethodName(),
					"(Ljava/lang/reflect/Type;Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;", false);
				methodVisitor.visitVarInsn(ASTORE, 6);
				methodVisitor.visitVarInsn(ALOAD, 0);
				methodVisitor.visitVarInsn(ALOAD, 4);
				methodVisitor.visitLdcInsn(info.getPropertyName());
				methodVisitor.visitVarInsn(ALOAD, 3);
				methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassNameInternal,
					convert.serialized().getImplMethodName(),
					"(Ljava/lang/reflect/Type;Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;", false);
				methodVisitor.visitVarInsn(ASTORE, 7);
				methodVisitor.visitVarInsn(ALOAD, 7);
				methodVisitor.visitJumpInsn(IFNULL, label1);
				methodVisitor.visitVarInsn(ALOAD, 6);
				methodVisitor.visitJumpInsn(IFNULL, label1);
				methodVisitor.visitVarInsn(ALOAD, 2);
				methodVisitor.visitVarInsn(ALOAD, 7);
				methodVisitor.visitVarInsn(ALOAD, 6);
				methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
				methodVisitor.visitInsn(POP);
				methodVisitor.visitLabel(label1);
				Label label3 = new Label();
				methodVisitor.visitJumpInsn(GOTO, label3);
				methodVisitor.visitLabel(label2);
				methodVisitor.visitVarInsn(ASTORE, 6);
				methodVisitor.visitVarInsn(ALOAD, 0);
				methodVisitor.visitLdcInsn(info.getPropertyName());
				methodVisitor.visitVarInsn(ALOAD, 6);
				methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassNameInternal, resolveCopyError.serialized().getImplMethodName(), "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
				methodVisitor.visitLabel(label3);
			});
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}
	}


	@SuppressWarnings("all")
	private static <T> void insertMapToBean(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertyInfo.Classification classification) {
		SerializableTriConsumer<BeanCopier, String, Throwable> resolveCopyError = BeanCopier::resolveCopyError;
		SerializableTriConsumer<BeanCopier, Map<String, Object>, Object> copyMapToBean = BeanCopier::copyMapToBean;
		String copyMapToBeanName = copyMapToBean.serialized().getImplMethodName();

		// 重写方法
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC, copyMapToBeanName, "(Ljava/util/Map;L" + Type.getInternalName(type) + ";)V", "(LLjava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;" + Type.getInternalName(type) + ";)V", null);
			methodVisitor.visitCode();

			classification.properties.forEach((name, info) -> {
				if (info.getField() != null) {
					methodVisitor.visitVarInsn(ALOAD, 1);
					methodVisitor.visitLdcInsn(name);
					methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
					methodVisitor.visitVarInsn(ASTORE, 3);
					methodVisitor.visitVarInsn(ALOAD, 3);
					Label labelIf = new Label();
					methodVisitor.visitJumpInsn(IFNULL, labelIf);
					methodVisitor.visitLdcInsn(Type.getType(Types.getWrapperClass(info.getField().getType())));
					methodVisitor.visitVarInsn(ALOAD, 3);
					methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "isInstance", "(Ljava/lang/Object;)Z", false);
					methodVisitor.visitJumpInsn(IFEQ, labelIf);
					methodVisitor.visitVarInsn(ALOAD, 2);
					methodVisitor.visitVarInsn(ALOAD, 3);
					AsmUtils.autoUnBoxing(methodVisitor, info.getField().getType());
					methodVisitor.visitFieldInsn(PUTFIELD, Type.getInternalName(info.getField().getDeclaringClass()), name, Type.getDescriptor(info.getField().getType()));
					methodVisitor.visitLabel(labelIf);
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
					AsmUtils.autoUnBoxing(methodVisitor, paramType);
					methodVisitor.visitMethodInsn(invokeOpcode, Type.getInternalName(declaringClass), method.getName(), Type.getMethodDescriptor(method), isInterface);
					methodVisitor.visitLabel(labelIf);
					// end
					methodVisitor.visitLabel(labelEnd);
					Label labelFinal = new Label();
					methodVisitor.visitJumpInsn(GOTO, labelFinal);
					// catch
					methodVisitor.visitLabel(labelCatch);
					methodVisitor.visitVarInsn(ASTORE, 3);
					methodVisitor.visitVarInsn(ALOAD, 0);
					methodVisitor.visitLdcInsn(name);
					methodVisitor.visitVarInsn(ALOAD, 3);
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
	private static <T> void insertMapToBeanByConverter(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertyInfo.Classification classification) {
		SerializableConsumerWithArgs4<BeanCopier, Map<String, Object>, Object, BiFunction<java.lang.reflect.Type, Object, Object>> copyMapToBean = BeanCopier::copyMapToBean;
		SerializableFunctionWithArgs4<BeanCopier, java.lang.reflect.Type, Object, BiFunction<java.lang.reflect.Type, Object, Object>, Object> convert = BeanCopier::convert;
		SerializableTriConsumer<BeanCopier, String, Throwable> resolveCopyError = BeanCopier::resolveCopyError;

		String copyMapToBeanName = copyMapToBean.serialized().getImplMethodName();

		// 重写方法
		{
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
					AsmUtils.autoUnBoxing(methodVisitor, Type.getType(info.getField().getType()));
					methodVisitor.visitFieldInsn(PUTFIELD, Type.getInternalName(info.getField().getDeclaringClass()), name, Type.getDescriptor(info.getField().getType()));

					methodVisitor.visitLabel(labelIf);

					// end
					methodVisitor.visitLabel(labelEnd);
					Label labelFinal = new Label();
					methodVisitor.visitJumpInsn(GOTO, labelFinal);

					// catch
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
					AsmUtils.autoUnBoxing(methodVisitor, Type.getType(method.getParameterTypes()[0]));

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

	@SuppressWarnings("all")
	private static <T> void insertBeanToBeanBySameType(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertyInfo.Classification classification) {
		SerializableTriConsumer<BeanCopier, Object, Object> copyBeanToBeanBySameType = BeanCopier::copyBeanToBeanBySameType;
		SerializableTriConsumer<BeanCopier, String, Throwable> resolveCopyError = BeanCopier::resolveCopyError;

		String copyBeanToSameName = copyBeanToBeanBySameType.serialized().getImplMethodName();
		// 重写方法
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, copyBeanToSameName,
				"(L" + Type.getInternalName(type) + ";L" + Type.getInternalName(type) + ";)V", null, null);
			methodVisitor.visitCode();

			classification.properties.forEach((name, info) -> {
				// 基本类不可能为空，直接赋值
				if (info.getField() != null) {
					methodVisitor.visitVarInsn(ALOAD, 1);
					methodVisitor.visitFieldInsn(GETFIELD, Type.getInternalName(info.getField().getDeclaringClass()), name, Type.getDescriptor(info.getField().getType()));
					AsmUtils.storeVar(methodVisitor, info.getPropertyType(), 3);

					Label labelIf = null;
					if (!Types.isPrimitive(info.getPropertyType())) {
						labelIf = new Label();
						AsmUtils.loadVar(methodVisitor, info.getPropertyType(), 3);
						methodVisitor.visitJumpInsn(IFNULL, labelIf);
					}

					methodVisitor.visitVarInsn(ALOAD, 2);
					AsmUtils.loadVar(methodVisitor, info.getPropertyType(), 3);
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
					AsmUtils.storeVar(methodVisitor, info.getPropertyType(), 3);
					Label labelIf = null;
					if (!Types.isPrimitive(info.getPropertyType())) {
						labelIf = new Label();
						AsmUtils.loadVar(methodVisitor, info.getPropertyType(), 3);
						methodVisitor.visitJumpInsn(IFNULL, labelIf);
					}

					methodVisitor.visitVarInsn(ALOAD, 2);
					AsmUtils.loadVar(methodVisitor, info.getPropertyType(), 3);
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

	@SuppressWarnings("all")
	private static <T> void insertBeanToMapByOptions(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertyInfo.Classification classification) {
		String subMethodPrefix = "copyBeanToMapWithKeyByOptions$";
		List<String> subMethods = new ArrayList<>();
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
		// 重写方法
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
			AsmUtils.autoBoxing(methodVisitor, var7Class);
		} else {
			Class<?> var7Class = info.getReadMethod().getReturnType();
			boolean isInterface = info.getReadMethod().getDeclaringClass().isInterface();
			methodVisitor.visitMethodInsn(isInterface ? INVOKEINTERFACE : INVOKEVIRTUAL,
				Type.getInternalName(info.getReadMethod().getDeclaringClass()),
				info.getReadMethod().getName(), Type.getMethodDescriptor(info.getReadMethod()), isInterface);
			// autoBoxing
			AsmUtils.autoBoxing(methodVisitor, var7Class);
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

	@SuppressWarnings("all")
	private static <T> void insertMapToBeanWithCustomKeys(ClassWriter cw, String accessClassNameInternal,
		Class<T> type, BeanPropertyInfo.Classification classification) {
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

	@SuppressWarnings("all")
	private static <T> void insertMapToBeanWithSourceKeys(ClassWriter cw, String accessClassNameInternal,
		Class<T> type, BeanPropertyInfo.Classification classification,
		SerializableConsumerWithArgs5<BeanCopier, Map<String, Object>, Object, CopyOptions, Set<String>> copyMethod,
		Function<String, String> keyMapper) {
		MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC, copyMethod.serialized().getImplMethodName(),
			"(Ljava/util/Map;Ljava/lang/Object;" +
				Type.getDescriptor(CopyOptions.class) +
				"Ljava/util/Set;)V",
			"(Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;Ljava/lang/Object;" +
				Type.getDescriptor(CopyOptions.class) + "Ljava/util/Set<Ljava/lang/String;>;)V", null);
		methodVisitor.visitCode();
		classification.properties.forEach((key, info) -> {
			if (info.hasSetter()) {
				String sourceKey = keyMapper.apply(info.getPropertyName());
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

	@SuppressWarnings("all")
	private static <T> void insertMapToBeanWithSourceKeyAll(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertyInfo.Classification classification) {
		classification.properties.forEach((key, info) -> {
			if (info.hasSetter()) {
				insertMapToBeanWithSourceKey(cw, superClassNameInternal, accessClassNameInternal, type, info);
			}
		});
	}

	@SuppressWarnings("all")
	private static <T> void insertMapToBeanWithSourceKey(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertyInfo info) {
		String methodName = "copyMapToBeanWithSourceKeys$" + info.getPropertyName();
		String targetKey = info.getPropertyName();
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PRIVATE, methodName, "(Ljava/util/Map;" +
				Type.getDescriptor(type) +
				Type.getDescriptor(CopyOptions.class) +
				"Ljava/util/Set;Ljava/lang/String;)V", "(Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;" +
				Type.getDescriptor(type) +
				Type.getDescriptor(CopyOptions.class) +
				"Ljava/util/Set<Ljava/lang/String;>;Ljava/lang/String;)V", null);
			methodVisitor.visitCode();
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
			/* if (sourceKey == null || options.isIgnoredKey(sourceKey)) {
				return;
			} */
			methodVisitor.visitVarInsn(ALOAD, 5);
			methodVisitor.visitJumpInsn(IFNULL, label1);
			methodVisitor.visitVarInsn(ALOAD, 3);
			methodVisitor.visitVarInsn(ALOAD, 5);
			SerializableBiFunction<CopyOptions, String, Boolean> isIgnoredKey = CopyOptions::isIgnoredKey;
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(CopyOptions.class), isIgnoredKey.serialized().getImplMethodName(), "(Ljava/lang/String;)Z", false);
			methodVisitor.visitJumpInsn(IFEQ, label3);
			methodVisitor.visitLabel(label1);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitLabel(label3);
			/* if (!targetKeys.contains(targetKey)) {
				return;
			} */
			methodVisitor.visitVarInsn(ALOAD, 4);
			methodVisitor.visitVarInsn(ALOAD, 6);
			methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "contains", "(Ljava/lang/Object;)Z", true);
			methodVisitor.visitJumpInsn(IFNE, label5);
			methodVisitor.visitLabel(label4);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitLabel(label5);
			/* if (!options.override()) {
				Object orig = var2.get();
				if (orig != null) {
					return;
				}
			} */
			methodVisitor.visitVarInsn(ALOAD, 3);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(CopyOptions.class), "override", "()Z", false);
			methodVisitor.visitJumpInsn(IFNE, label7);
			if (info.getField() != null) {
				methodVisitor.visitVarInsn(ALOAD, 2);
				methodVisitor.visitFieldInsn(GETFIELD, Type.getDescriptor(info.getField().getDeclaringClass()), info.getField().getName(), Type.getDescriptor(info.getField().getType()));
				AsmUtils.autoBoxing(methodVisitor, info.getField().getType());
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
				AsmUtils.autoBoxing(methodVisitor, info.getReadMethod().getReturnType());
				methodVisitor.visitVarInsn(ASTORE, 7);
				methodVisitor.visitVarInsn(ALOAD, 7);
				methodVisitor.visitJumpInsn(IFNULL, label7);
			} else {
				methodVisitor.visitJumpInsn(GOTO, label7);
			}
			methodVisitor.visitLabel(label6);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitLabel(label7);
			/* Object value = var1.get(sourceKey);
			if (value == null && options.ignoreNull()) {
				return;
			} */
			methodVisitor.visitVarInsn(ALOAD, 1);
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
			/* value = options.editValue(sourceKey, value); */
			methodVisitor.visitVarInsn(ALOAD, 3);
			methodVisitor.visitVarInsn(ALOAD, 5);
			methodVisitor.visitVarInsn(ALOAD, 7);
			SerializableTriFunction<CopyOptions, String, Object, Object> editValue = CopyOptions::editValue;
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(CopyOptions.class), editValue.serialized().getImplMethodName(), "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", false);
			/* value = options.convert(this.type$field, value); */
			methodVisitor.visitVarInsn(ASTORE, 7);
			methodVisitor.visitVarInsn(ALOAD, 3);
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitFieldInsn(GETFIELD, accessClassNameInternal, FIELD_PREFIX_TYPE + info.getPropertyName(), "Ljava/lang/reflect/Type;");
			methodVisitor.visitVarInsn(ALOAD, 7);
			SerializableTriFunction<CopyOptions, java.lang.reflect.Type, Object, Object> convert = CopyOptions::convert;
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(CopyOptions.class), convert.serialized().getImplMethodName(), "(Ljava/lang/reflect/Type;Ljava/lang/Object;)Ljava/lang/Object;", false);
			methodVisitor.visitVarInsn(ASTORE, 7);
			/* if (value == null && (options.ignoreNull() || class$booleanVal0.isPrimitive())) {
				return;
			} */
			methodVisitor.visitVarInsn(ALOAD, 7);
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
			/* if (Types.getWrapperClass(class$field).isInstance(value)) {
				var2.set( (Boolean) value);
				targetKeys.remove(targetKey);
			} */
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitFieldInsn(GETFIELD, accessClassNameInternal, FIELD_PREFIX_CLASS + info.getPropertyName(), "Ljava/lang/Class;");
			SerializableFunction<Class, Class> getWrapperClass = Types::getWrapperClass;
			methodVisitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Types.class), getWrapperClass.serialized().getImplMethodName(), "(Ljava/lang/Class;)Ljava/lang/Class;", false);
			methodVisitor.visitVarInsn(ALOAD, 7);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "isInstance", "(Ljava/lang/Object;)Z", false);
			methodVisitor.visitJumpInsn(IFEQ, label12);
			methodVisitor.visitVarInsn(ALOAD, 2);
			methodVisitor.visitVarInsn(ALOAD, 7);
			AsmUtils.autoUnBoxing(methodVisitor, info.getPropertyType());
			if (info.getField() != null) {
				methodVisitor.visitFieldInsn(PUTFIELD, Type.getInternalName(info.getField().getDeclaringClass()), info.getField().getName(), Type.getDescriptor(info.getField().getType()));
			} else {
				boolean isInterface = info.getWriteMethod().getDeclaringClass().isInterface();
				methodVisitor.visitMethodInsn(isInterface ? INVOKEINTERFACE : INVOKEVIRTUAL,
					Type.getInternalName(info.getWriteMethod().getDeclaringClass()),
					info.getWriteMethod().getName(), Type.getMethodDescriptor(info.getWriteMethod()), isInterface);
			}
			methodVisitor.visitVarInsn(ALOAD, 4);
			methodVisitor.visitVarInsn(ALOAD, 6);
			methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "remove", "(Ljava/lang/Object;)Z", true);
			methodVisitor.visitInsn(POP);
			methodVisitor.visitLabel(label12);
			Label label13 = new Label();
			methodVisitor.visitJumpInsn(GOTO, label13);
			methodVisitor.visitLabel(label2);
			methodVisitor.visitVarInsn(ASTORE, 7);
			methodVisitor.visitVarInsn(ALOAD, 0);

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
