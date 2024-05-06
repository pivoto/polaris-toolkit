package io.polaris.core.asm.reflect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import io.polaris.core.asm.AsmUtils;
import io.polaris.core.lang.Types;
import io.polaris.core.lang.copier.CopyOptions;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.map.Maps;
import io.polaris.core.reflect.SerializableBiFunction;
import io.polaris.core.reflect.SerializableConsumer;
import io.polaris.core.reflect.SerializableConsumerWithArgs5;
import io.polaris.core.reflect.SerializableFunction;
import io.polaris.core.reflect.SerializableConsumerWithArgs4;
import io.polaris.core.reflect.SerializableTriFunction;
import io.polaris.core.string.StringCases;
import io.polaris.core.tuple.Tuple2;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Qt
 * @since 1.8,  Apr 14, 2024
 */
public abstract class BeanOptionsCopier<S, T> {
	private static ILogger log = ILoggers.of(BeanOptionsCopier.class);
	private static final String CLASS_NAME_PREFIX = "Options$";
	public static final String FIELD_PREFIX_TYPE = BeanCopier.FIELD_PREFIX_TYPE;
	public static final String FIELD_PREFIX_CLASS = BeanCopier.FIELD_PREFIX_CLASS;
	protected Class<T> targetType;
	protected BeanCopier<S> beanCopier;
	protected Map<String, BeanPropertyInfo> properties;


	protected final void copy(S source, T target, CopyOptions options) {
		Set<String> targetKeys = new LinkedHashSet<>(properties.keySet());
		if (!targetKeys.isEmpty()) {
			copySameKeys(source, target, options, targetKeys);
		}
		if (options.ignoreCapitalize() && !targetKeys.isEmpty()) {
			copyCapitalizeKeys(source, target, options, targetKeys);
		}
		if (options.enableUnderlineToCamelCase() && !targetKeys.isEmpty()) {
			copyUnderlineToCamelKeys(source, target, options, targetKeys);
		}
		if (options.enableCamelToUnderlineCase() && !targetKeys.isEmpty()) {
			copyCamelToUnderlineKeys(source, target, options, targetKeys);
		}
		if (options.ignoreCase() && !targetKeys.isEmpty()) {
			copySameKeysIgnoreCase(source, target, options, targetKeys);
			if (options.enableUnderlineToCamelCase() && !targetKeys.isEmpty()) {
				copyUnderlineToCamelKeysIgnoreCase(source, target, options, targetKeys);
			}
			if (options.enableCamelToUnderlineCase() && !targetKeys.isEmpty()) {
				copyCamelToUnderlineKeysIgnoreCase(source, target, options, targetKeys);
			}
		}
	}


	// region to overwrite

	protected abstract void initTypeFields();


	protected abstract void copySameKeys(S source, T target, CopyOptions options, Set<String> targetKeys);

	protected abstract void copyCapitalizeKeys(S source, T target, CopyOptions options, Set<String> targetKeys);

	protected abstract void copyUnderlineToCamelKeys(S source, T target, CopyOptions options, Set<String> targetKeys);

	protected abstract void copyCamelToUnderlineKeys(S source, T target, CopyOptions options, Set<String> targetKeys);

	protected abstract void copySameKeysIgnoreCase(S source, T target, CopyOptions options, Set<String> targetKeys);

	protected abstract void copyUnderlineToCamelKeysIgnoreCase(S source, T target, CopyOptions options, Set<String> targetKeys);

	protected abstract void copyCamelToUnderlineKeysIgnoreCase(S source, T target, CopyOptions options, Set<String> targetKeys);

	// endregion


	// region inner tool

	protected final void resolveCopyError(String propertyName, Throwable e, CopyOptions options) {
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

	protected final java.lang.reflect.Type getPropertyGenericType(String propertyName) {
		return properties.get(propertyName).getPropertyGenericType();
	}

	protected final Class<?> getPropertyType(String propertyName) {
		return properties.get(propertyName).getPropertyType();
	}

	// endregion

	 static List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> buildCustomKeyMapping(Map<String, BeanPropertyInfo> sourceProperties, Map<String, BeanPropertyInfo> targetProperties, Function<String, String> mapper,boolean ignoreCase) {
		// 自定义映射字段，从来源keys映射到目标keys
		List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> list = new ArrayList<>(sourceProperties.size());
		for (Map.Entry<String, BeanPropertyInfo> entry : sourceProperties.entrySet()) {
			String key = entry.getKey();
			String mapKey = mapper.apply(key);
			if (mapKey != null) {
				BeanPropertyInfo targetInfo = targetProperties.get(mapKey);
				BeanPropertyInfo sourceInfo = entry.getValue();
				if (targetInfo != null && sourceInfo.hasGetter() && targetInfo.hasSetter()) {
					if (ignoreCase){
						if (key.equals(sourceInfo.getPropertyName())){
							continue;
						}
					}
					list.add(Tuple2.of(sourceInfo, targetInfo));
				}
			}
		}
		return list;
	}
	 static List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> buildSameKeyMapping(Map<String, BeanPropertyInfo> sourceProperties, Map<String, BeanPropertyInfo> targetProperties, boolean ignoreCase) {
		List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> list = new ArrayList<>(targetProperties.size());
		for (Map.Entry<String, BeanPropertyInfo> entry : targetProperties.entrySet()) {
			String key = entry.getKey();
			BeanPropertyInfo sourceInfo = sourceProperties.get(key);
			BeanPropertyInfo targetInfo = entry.getValue();
			if (sourceInfo != null && sourceInfo.hasGetter() && targetInfo.hasSetter()) {
				if (ignoreCase){
					if (key.equals(sourceInfo.getPropertyName())){
						continue;
					}
				}
				list.add(Tuple2.of(sourceInfo, targetInfo));
			}
		}
		return list;
	}

	 static List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> buildCapitalizeKeyMapping(Map<String, BeanPropertyInfo> sourceProperties, Map<String, BeanPropertyInfo> targetProperties) {
		List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> list = new ArrayList<>(targetProperties.size());
		for (Map.Entry<String, BeanPropertyInfo> entry : targetProperties.entrySet()) {
			String key = entry.getKey();
			if (key.length() > 1) {
				// 以目标key视角，反向处理
				String mapKey;
				if (Character.isUpperCase(key.charAt(0))) {
					mapKey = Character.toLowerCase(key.charAt(0)) + key.substring(1);
				} else {
					mapKey = Character.toUpperCase(key.charAt(0)) + key.substring(1);
				}
				BeanPropertyInfo sourceInfo = sourceProperties.get(mapKey);
				BeanPropertyInfo targetInfo = entry.getValue();
				if (sourceInfo != null && sourceInfo.hasGetter() && targetInfo.hasSetter()) {
					list.add(Tuple2.of(sourceInfo, targetInfo));
				}
			}
		}
		return list;
	}

	 static List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> buildUnderlineToCamelKeyMapping(Map<String, BeanPropertyInfo> sourceProperties, Map<String, BeanPropertyInfo> targetProperties, boolean ignoreCase) {
		List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> list = new ArrayList<>(targetProperties.size());
		for (Map.Entry<String, BeanPropertyInfo> entry : targetProperties.entrySet()) {
			String key = entry.getKey();
			// 以目标key视角，反向处理
			String mapKey = StringCases.camelToUnderlineCase(key);
			if (!key.equals(mapKey)) {
				BeanPropertyInfo sourceInfo = sourceProperties.get(mapKey);
				BeanPropertyInfo targetInfo = entry.getValue();
				if (sourceInfo != null && sourceInfo.hasGetter() && targetInfo.hasSetter()) {
					if (ignoreCase){
						// 已存在完全匹配则忽略
						if (targetProperties.containsKey(mapKey)){
							continue;
						}
					}
					list.add(Tuple2.of(sourceInfo, targetInfo));
				}
			}
		}
		return list;
	}

	 static List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> buildCamelToUnderlineKeyMapping(Map<String, BeanPropertyInfo> sourceProperties, Map<String, BeanPropertyInfo> targetProperties, boolean ignoreCase) {
		List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> list = new ArrayList<>(targetProperties.size());
		for (Map.Entry<String, BeanPropertyInfo> entry : targetProperties.entrySet()) {
			String key = entry.getKey();
			// 以目标key视角，反向处理
			if (key.indexOf('_') >= 0) {
				String mapKey = StringCases.underlineToCamelCase(key);
				BeanPropertyInfo sourceInfo = sourceProperties.get(mapKey);
				BeanPropertyInfo targetInfo = entry.getValue();
				if (sourceInfo != null && sourceInfo.hasGetter() && targetInfo.hasSetter()) {
					if (ignoreCase){
						// 已存在完全匹配则忽略
						if (targetProperties.containsKey(mapKey)){
							continue;
						}
					}
					list.add(Tuple2.of(sourceInfo, targetInfo));
				}
			}
		}
		return list;
	}


	static <S, T> BeanOptionsCopier<S, T> create(BeanCopier<S> beanCopier, Class<T> targetType) {
		BeanPropertyInfo.Classification classification = BeanPropertyInfo.classify(targetType);
		String accessClassName = beanCopier.getClass().getName()
			+ CLASS_NAME_PREFIX
			+ (targetType.getName().replace("_", "__").replace('.', '_'));

		AccessClassLoader loader = AccessClassLoader.get(targetType);
		Class accessClass = loader.loadOrDefineClass(accessClassName, () -> buildOptionsCopierClass(loader, beanCopier, accessClassName, targetType, classification));
		BeanOptionsCopier<S, T> access;
		try {
			access = (BeanOptionsCopier<S, T>) accessClass.newInstance();
			// 全部设为只读属性
			access.targetType = targetType;
			access.beanCopier = beanCopier;
			access.properties = Collections.unmodifiableMap(classification.properties);
			access.initTypeFields();
			return access;
		} catch (Throwable t) {
			throw new IllegalStateException("创建访问类失败: " + accessClassName, t);
		}
	}

	private static <S, T> byte[] buildOptionsCopierClass(AccessClassLoader loader
		, BeanCopier<S> beanCopier, String accessClassName, Class<T> type
		, BeanPropertyInfo.Classification classification) {
		Map<String, BeanPropertyInfo> targetProperties = classification.properties;
		Map<String, BeanPropertyInfo> sourceProperties = beanCopier.properties;
		Map<String, BeanPropertyInfo> sourceUpperProperties = Maps.newUpperCaseLinkedHashMap();
		sourceUpperProperties.putAll(sourceProperties);

		List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> sameKeyMapping = buildSameKeyMapping(sourceProperties, targetProperties, false);
		List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> capitalizeKeyMapping = buildCapitalizeKeyMapping(sourceProperties, targetProperties);
		List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> underlineToCamelKeyMapping = buildUnderlineToCamelKeyMapping(sourceProperties, targetProperties, false);
		List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> camelToUnderlineKeyMapping = buildCamelToUnderlineKeyMapping(sourceProperties, targetProperties, false);

		List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> sameKeyMappingIgnoreCase = buildSameKeyMapping(sourceUpperProperties, targetProperties, true);
		List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> underlineToCamelKeyMappingIgnoreCase = buildUnderlineToCamelKeyMapping(sourceUpperProperties, targetProperties, true);
		List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> camelToUnderlineKeyMappingIgnoreCase = buildCamelToUnderlineKeyMapping(sourceUpperProperties, targetProperties, true);


		String accessClassNameInternal = accessClassName.replace('.', '/');

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		String superClassNameInternal = Type.getInternalName(BeanOptionsCopier.class);
		cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal,
			"L" + superClassNameInternal + "<L" + Type.getInternalName(beanCopier.sourceType) + ";L" + Type.getInternalName(type) + ";>;",
			superClassNameInternal, null);
		cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

		AsmUtils.insertDefaultConstructor(cw, superClassNameInternal);
		insertInitTypeFields(cw, superClassNameInternal, accessClassNameInternal, type, classification);

		insertCopyKeysMethod(cw, beanCopier, superClassNameInternal, accessClassNameInternal,
			type, sameKeyMapping, BeanOptionsCopier::copySameKeys);
		insertCopyKeysMethod(cw, beanCopier, superClassNameInternal, accessClassNameInternal,
			type, capitalizeKeyMapping, BeanOptionsCopier::copyCapitalizeKeys);
		insertCopyKeysMethod(cw, beanCopier, superClassNameInternal, accessClassNameInternal,
			type, underlineToCamelKeyMapping, BeanOptionsCopier::copyUnderlineToCamelKeys);
		insertCopyKeysMethod(cw, beanCopier, superClassNameInternal, accessClassNameInternal,
			type, camelToUnderlineKeyMapping, BeanOptionsCopier::copyCamelToUnderlineKeys);
		insertCopyKeysMethod(cw, beanCopier, superClassNameInternal, accessClassNameInternal,
			type, sameKeyMappingIgnoreCase, BeanOptionsCopier::copySameKeysIgnoreCase);
		insertCopyKeysMethod(cw, beanCopier, superClassNameInternal, accessClassNameInternal,
			type, underlineToCamelKeyMappingIgnoreCase, BeanOptionsCopier::copyUnderlineToCamelKeysIgnoreCase);
		insertCopyKeysMethod(cw, beanCopier, superClassNameInternal, accessClassNameInternal,
			type, camelToUnderlineKeyMappingIgnoreCase, BeanOptionsCopier::copyCamelToUnderlineKeysIgnoreCase);

		cw.visitEnd();
		byte[] byteArray = cw.toByteArray();
		return byteArray;
	}

	@SuppressWarnings("rawtypes")
	private static <T> void insertInitTypeFields(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertyInfo.Classification classification) {
		SerializableConsumer<BeanOptionsCopier> init = BeanOptionsCopier::initTypeFields;
		SerializableBiFunction<BeanOptionsCopier, String, java.lang.reflect.Type> getPropertyGenericType = BeanOptionsCopier::getPropertyGenericType;
		SerializableBiFunction<BeanOptionsCopier, String, Class> getPropertyType = BeanOptionsCopier::getPropertyType;

		BeanCopier.insertInitTypes(cw, superClassNameInternal, accessClassNameInternal, classification,
			init.serialized().getImplMethodName(),
			getPropertyGenericType.serialized().getImplMethodName(),
			getPropertyType.serialized().getImplMethodName());
	}

	private static <S, T> void insertCopyKeysMethod(ClassWriter cw, BeanCopier<S> beanCopier,
		String superClassNameInternal, String accessClassNameInternal, Class<T> type,
		List<Tuple2<BeanPropertyInfo, BeanPropertyInfo>> keyMapping,
		SerializableConsumerWithArgs5<BeanOptionsCopier, Object, Object, CopyOptions, Set<String>> copyKeysMethod) {

		String copyKeysMethodName = copyKeysMethod.serialized().getImplMethodName();

		// sub method
		keyMapping.forEach(tuple -> {
			insertCopyKeysSubMethod(cw, beanCopier, superClassNameInternal, accessClassNameInternal, type,
				tuple.getFirst(), tuple.getSecond(), copyKeysMethod);
		});

		// 实现方法
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, copyKeysMethodName, "(" +
				Type.getDescriptor(beanCopier.sourceType) +
				Type.getDescriptor(type) +
				Type.getDescriptor(CopyOptions.class) +
				Type.getDescriptor(Set.class) +
				")V", "(" +
				Type.getDescriptor(beanCopier.sourceType) +
				Type.getDescriptor(type) +
				Type.getDescriptor(CopyOptions.class) +
				"Ljava/util/Set<Ljava/lang/String;>;)V", null);
			methodVisitor.visitCode();

			Label label0 = new Label();
			keyMapping.forEach(tuple -> {
				methodVisitor.visitVarInsn(ALOAD, 4);
				methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "isEmpty", "()Z", true);
				methodVisitor.visitJumpInsn(IFNE, label0);

				String subMethodName = copyKeysMethodName + "$" + tuple.getFirst().getPropertyName() + "$" + tuple.getSecond().getPropertyName();
				methodVisitor.visitVarInsn(ALOAD, 0);
				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitVarInsn(ALOAD, 2);
				methodVisitor.visitVarInsn(ALOAD, 3);
				methodVisitor.visitVarInsn(ALOAD, 4);
				methodVisitor.visitMethodInsn(INVOKEVIRTUAL, accessClassNameInternal, subMethodName,
					"(" +
						Type.getDescriptor(beanCopier.sourceType) +
						Type.getDescriptor(type) +
						Type.getDescriptor(CopyOptions.class) +
						Type.getDescriptor(Set.class) +
						")V", false);
			});
			methodVisitor.visitLabel(label0);

			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(4, 8);
			methodVisitor.visitEnd();
		}
		// 泛型合成
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED | ACC_BRIDGE | ACC_SYNTHETIC, copyKeysMethodName, "(Ljava/lang/Object;Ljava/lang/Object;" +
				Type.getDescriptor(CopyOptions.class) +
				"Ljava/util/Set;)V", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(beanCopier.sourceType));
			methodVisitor.visitVarInsn(ALOAD, 2);
			methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
			methodVisitor.visitVarInsn(ALOAD, 3);
			methodVisitor.visitVarInsn(ALOAD, 4);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, accessClassNameInternal,
				copyKeysMethodName, "(" +
					Type.getDescriptor(beanCopier.sourceType) +
					Type.getDescriptor(type) +
					Type.getDescriptor(CopyOptions.class) +
					Type.getDescriptor(Set.class) +
					")V", false);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(5, 5);
			methodVisitor.visitEnd();
		}
	}

	private static <S, T> void insertCopyKeysSubMethod(ClassWriter cw, BeanCopier<S> beanCopier,
		String superClassNameInternal, String accessClassNameInternal, Class<T> type,
		BeanPropertyInfo sourceInfo, BeanPropertyInfo targetInfo,
		SerializableConsumerWithArgs5<BeanOptionsCopier, Object, Object, CopyOptions, Set<String>> copyKeysMethod) {
		String methodName = copyKeysMethod.serialized().getImplMethodName() + "$" + sourceInfo.getPropertyName() + "$" + targetInfo.getPropertyName();

		MethodVisitor methodVisitor = cw.visitMethod(ACC_PRIVATE, methodName, "(" +
			Type.getDescriptor(beanCopier.sourceType) +
			Type.getDescriptor(type) +
			Type.getDescriptor(CopyOptions.class) +
			Type.getDescriptor(Set.class) +
			")V", "(" +
			Type.getDescriptor(beanCopier.sourceType) +
			Type.getDescriptor(type) +
			Type.getDescriptor(CopyOptions.class) +
			"Ljava/util/Set<Ljava/lang/String;>;)V", null);
		methodVisitor.visitCode();

		insertCopyKeysSubMethodCode(methodVisitor, beanCopier, superClassNameInternal, accessClassNameInternal, type, sourceInfo, targetInfo);

		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(4, 8);
		methodVisitor.visitEnd();
	}

	@SuppressWarnings("all")
	private static <S, T> void insertCopyKeysSubMethodCode(MethodVisitor methodVisitor, BeanCopier<S> beanCopier, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertyInfo sourceInfo, BeanPropertyInfo targetInfo) {
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
		methodVisitor.visitLabel(label0);
		methodVisitor.visitLdcInsn(sourceInfo.getPropertyName());
		methodVisitor.visitVarInsn(ASTORE, 5);
		methodVisitor.visitLdcInsn(targetInfo.getPropertyName());
		methodVisitor.visitVarInsn(ASTORE, 6);

		// 	if (options.isIgnoredKey(sourceKey) || !targetKeys.contains(targetKey)) return
		methodVisitor.visitVarInsn(ALOAD, 3);
		methodVisitor.visitVarInsn(ALOAD, 5);
		SerializableBiFunction<CopyOptions, String, Boolean> isIgnoredKey = CopyOptions::isIgnoredKey;
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(CopyOptions.class), isIgnoredKey.serialized().getImplMethodName(), "(Ljava/lang/String;)Z", false);
		methodVisitor.visitJumpInsn(IFNE, label1);
		methodVisitor.visitVarInsn(ALOAD, 4);
		methodVisitor.visitVarInsn(ALOAD, 6);
		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "contains", "(Ljava/lang/Object;)Z", true);
		methodVisitor.visitJumpInsn(IFNE, label3);
		methodVisitor.visitLabel(label1);
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitLabel(label3);

		// target value
		if (targetInfo.hasGetter()) {
			methodVisitor.visitVarInsn(ALOAD, 3);
			SerializableFunction<CopyOptions, Boolean> override = CopyOptions::override;
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(CopyOptions.class), override.serialized().getImplMethodName(), "()Z", false);
			methodVisitor.visitJumpInsn(IFNE, label5);
			methodVisitor.visitVarInsn(ALOAD, 2);
			if (targetInfo.getField() != null) {
				Class var7Class = targetInfo.getField().getType();
				methodVisitor.visitFieldInsn(GETFIELD,
					Type.getInternalName(targetInfo.getField().getDeclaringClass()),
					targetInfo.getField().getName(), Type.getDescriptor(var7Class));
				// autoBoxing
				AsmUtils.autoBoxing(methodVisitor, var7Class);
			} else {
				Class var7Class = targetInfo.getReadMethod().getReturnType();
				boolean isInterface = targetInfo.getReadMethod().getDeclaringClass().isInterface();
				methodVisitor.visitMethodInsn(isInterface ? INVOKEINTERFACE : INVOKEVIRTUAL,
					Type.getInternalName(targetInfo.getReadMethod().getDeclaringClass()),
					targetInfo.getReadMethod().getName(), Type.getMethodDescriptor(targetInfo.getReadMethod()), isInterface);
				// autoBoxing
				AsmUtils.autoBoxing(methodVisitor, var7Class);
			}
			methodVisitor.visitVarInsn(ASTORE, 7);
			methodVisitor.visitVarInsn(ALOAD, 7);
			methodVisitor.visitJumpInsn(IFNULL, label5);
		} else {
			methodVisitor.visitJumpInsn(GOTO, label5);
		}
		methodVisitor.visitLabel(label4);
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitLabel(label5);
		methodVisitor.visitVarInsn(ALOAD, 1);
		if (sourceInfo.getField() != null) {
			Class var7Class = sourceInfo.getField().getType();
			methodVisitor.visitFieldInsn(GETFIELD, Type.getInternalName(sourceInfo.getField().getDeclaringClass()), sourceInfo.getField().getName(), Type.getDescriptor(var7Class));
			AsmUtils.autoBoxing(methodVisitor, var7Class);
		} else {
			Class var7Class = sourceInfo.getReadMethod().getReturnType();
			boolean isInterface = sourceInfo.getReadMethod().getDeclaringClass().isInterface();
			methodVisitor.visitMethodInsn(isInterface ? INVOKEINTERFACE : INVOKEVIRTUAL,
				Type.getInternalName(sourceInfo.getReadMethod().getDeclaringClass()),
				sourceInfo.getReadMethod().getName(), Type.getMethodDescriptor(sourceInfo.getReadMethod()), isInterface);
			AsmUtils.autoBoxing(methodVisitor, var7Class);
		}
		methodVisitor.visitVarInsn(ASTORE, 7);
		methodVisitor.visitVarInsn(ALOAD, 7);
		methodVisitor.visitJumpInsn(IFNONNULL, label7);
		methodVisitor.visitVarInsn(ALOAD, 3);
		SerializableFunction<CopyOptions, Boolean> ignoreNull = CopyOptions::ignoreNull;
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(CopyOptions.class), ignoreNull.serialized().getImplMethodName(), "()Z", false);
		methodVisitor.visitJumpInsn(IFEQ, label7);
		methodVisitor.visitLabel(label6);
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitLabel(label7);
		/* value = options.editValue(sourceKey, value);
			value = options.convert(type$intVal0, value);
			if (value == null && (options.ignoreNull() || class$strVal0.isPrimitive())) {
				return;
			} */
		methodVisitor.visitVarInsn(ALOAD, 3);
		methodVisitor.visitVarInsn(ALOAD, 5);
		methodVisitor.visitVarInsn(ALOAD, 7);
		SerializableTriFunction<CopyOptions, String, Object, Object> editValue = CopyOptions::editValue;
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(CopyOptions.class), editValue.serialized().getImplMethodName(), "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", false);
		methodVisitor.visitVarInsn(ASTORE, 7);
		methodVisitor.visitVarInsn(ALOAD, 3);
		methodVisitor.visitVarInsn(ALOAD, 0);
		methodVisitor.visitFieldInsn(GETFIELD, accessClassNameInternal, FIELD_PREFIX_TYPE + targetInfo.getPropertyName(), "Ljava/lang/reflect/Type;");
		methodVisitor.visitVarInsn(ALOAD, 7);
		SerializableTriFunction<CopyOptions, java.lang.reflect.Type, Object, Object> convert = CopyOptions::convert;
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(CopyOptions.class), convert.serialized().getImplMethodName(), "(Ljava/lang/reflect/Type;Ljava/lang/Object;)Ljava/lang/Object;", false);
		methodVisitor.visitVarInsn(ASTORE, 7);
		methodVisitor.visitVarInsn(ALOAD, 7);
		methodVisitor.visitJumpInsn(IFNONNULL, label9);
		methodVisitor.visitVarInsn(ALOAD, 3);
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(CopyOptions.class), ignoreNull.serialized().getImplMethodName(), "()Z", false);
		methodVisitor.visitJumpInsn(IFNE, label8);
		methodVisitor.visitVarInsn(ALOAD, 0);
		methodVisitor.visitFieldInsn(GETFIELD, accessClassNameInternal, FIELD_PREFIX_CLASS + targetInfo.getPropertyName(), "Ljava/lang/Class;");
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "isPrimitive", "()Z", false);
		methodVisitor.visitJumpInsn(IFEQ, label9);
		methodVisitor.visitLabel(label8);
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitLabel(label9);
		/* if (Types.getWrapperClass(class$strVal0).isInstance(value)){
				target.setIntVal0((Integer) value);
				targetKeys.remove(targetKey);
			} */
		methodVisitor.visitVarInsn(ALOAD, 0);
		methodVisitor.visitFieldInsn(GETFIELD, accessClassNameInternal, FIELD_PREFIX_CLASS + targetInfo.getPropertyName(), "Ljava/lang/Class;");
		SerializableFunction<Class, Class> getWrapperClass = Types::getWrapperClass;
		methodVisitor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Types.class), getWrapperClass.serialized().getImplMethodName(), "(Ljava/lang/Class;)Ljava/lang/Class;", false);
		methodVisitor.visitVarInsn(ALOAD, 7);
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "isInstance", "(Ljava/lang/Object;)Z", false);
		methodVisitor.visitJumpInsn(IFEQ, label10);
		methodVisitor.visitVarInsn(ALOAD, 2);
		methodVisitor.visitVarInsn(ALOAD, 7);
		AsmUtils.autoUnBoxing(methodVisitor, targetInfo.getPropertyType());
		if (targetInfo.getField() != null) {
			methodVisitor.visitFieldInsn(PUTFIELD, Type.getInternalName(targetInfo.getField().getDeclaringClass()), targetInfo.getField().getName(), Type.getDescriptor(targetInfo.getField().getType()));
		} else {
			boolean isInterface = targetInfo.getWriteMethod().getDeclaringClass().isInterface();
			methodVisitor.visitMethodInsn(isInterface ? INVOKEINTERFACE : INVOKEVIRTUAL,
				Type.getInternalName(targetInfo.getWriteMethod().getDeclaringClass()),
				targetInfo.getWriteMethod().getName(), Type.getMethodDescriptor(targetInfo.getWriteMethod()), isInterface);
		}
		methodVisitor.visitVarInsn(ALOAD, 4);
		methodVisitor.visitVarInsn(ALOAD, 6);
		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "remove", "(Ljava/lang/Object;)Z", true);
		methodVisitor.visitInsn(POP);
		methodVisitor.visitLabel(label10);

		Label label11 = new Label();
		methodVisitor.visitJumpInsn(GOTO, label11);
		methodVisitor.visitLabel(label2);
		methodVisitor.visitVarInsn(ASTORE, 5);
		methodVisitor.visitVarInsn(ALOAD, 0);
		methodVisitor.visitLdcInsn(sourceInfo.getPropertyName() + "->" + targetInfo.getPropertyName());
		methodVisitor.visitVarInsn(ALOAD, 5);
		methodVisitor.visitVarInsn(ALOAD, 3);
		SerializableConsumerWithArgs4<BeanOptionsCopier, String, Throwable,CopyOptions> resolveCopyError = BeanOptionsCopier::resolveCopyError;
		methodVisitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(BeanOptionsCopier.class), resolveCopyError.serialized().getImplMethodName(), "(Ljava/lang/String;Ljava/lang/Throwable;" +
			Type.getDescriptor(CopyOptions.class) + ")V", false);
		methodVisitor.visitLabel(label11);
	}


}
