package io.polaris.core.asm.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;

import io.polaris.core.asm.internal.Asms;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.core.lang.bean.property.BeanProperties;
import io.polaris.core.lang.bean.property.BeanPropertiesClassification;
import io.polaris.core.lang.bean.property.BeanPropertyInfo;
import io.polaris.core.lang.copier.CopyOptions;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.reflect.SerializableBiFunction;
import io.polaris.core.reflect.SerializableConsumer;
import io.polaris.core.reflect.SerializableTriConsumer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

/**
 * Bean 直接复制器
 * <p>默认规则下的 Bean 属性复制，不接受 CopyOptions 参数</p>
 * <p>通过 ASM 字节码生成技术，为相同类型的 Bean 提供高性能的直接属性复制能力</p>
 *
 * @param <S> 源 Bean 类型
 * @param <T> 目标 Bean 类型
 * @author Qt
 * @since Apr 14, 2024
 */
public abstract class BeanDirectCopier<S, T> {
	/**
	 * 日志记录器
	 */
	private static final Logger log = Loggers.of(BeanCopier.class);
	/**
	 * 直接复制器类名前缀
	 */
	private static final String CLASS_NAME_PREFIX = "Direct$";
	/**
	 * 类型字段前缀标识
	 */
	public static final String FIELD_PREFIX_TYPE = BeanCopier.FIELD_PREFIX_TYPE;
	/**
	 * Class 字段前缀标识
	 */
	public static final String FIELD_PREFIX_CLASS = BeanCopier.FIELD_PREFIX_CLASS;
	/**
	 * 目标 Bean 类型
	 */
	protected Class<T> targetType;
	/**
	 * Bean 复制器实例
	 */
	protected BeanCopier<S> beanCopier;
	/**
	 * 属性信息映射表
	 */
	protected Map<String, BeanPropertyInfo> properties;

	/**
	 * 复制属性到目标对象
	 * <p>将源对象的属性值复制到目标对象</p>
	 *
	 * @param source 源对象
	 * @param target 目标对象
	 */
	protected void copy(S source, T target) {
	}

	/**
	 * 初始化类型字段（子类实现）
	 * <p>用于存储泛型类型信息，供后续类型转换使用</p>
	 */
	protected abstract void initTypeFields();


	// region inner tool

	/**
	 * 处理复制错误（基础版本）
	 * <p>仅在 debug 模式下记录日志</p>
	 *
	 * @param propertyName 属性名
	 * @param e 异常对象
	 */
	protected final void resolveCopyError(String propertyName, Throwable e) {
		if (log.isDebugEnabled()) {
			log.debug("复制属性[" + propertyName + "]失败", e);
		}
	}

	/**
	 * 处理复制错误（带选项配置）
	 * <p>根据选项配置决定是抛出异常还是记录日志</p>
	 *
	 * @param propertyName 属性名
	 * @param e 异常对象
	 * @param options 复制选项配置
	 */
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

	/**
	 * 获取属性的泛型类型
	 *
	 * @param propertyName 属性名
	 * @return 属性的泛型类型
	 */
	protected final java.lang.reflect.Type getPropertyGenericType(String propertyName) {
		return properties.get(propertyName).getPropertyGenericType();
	}

	/**
	 * 获取属性的原始类型
	 *
	 * @param propertyName 属性名
	 * @return 属性的 Class 类型
	 */
	protected final Class<?> getPropertyType(String propertyName) {
		return properties.get(propertyName).getPropertyType();
	}

	/**
	 * 类型转换辅助方法
	 * <p>使用提供的转换器将值转换为目标类型</p>
	 *
	 * @param type 目标类型
	 * @param value 原始值
	 * @param converter 类型转换器
	 * @return 转换后的值
	 */
	protected final Object convert(java.lang.reflect.Type type, Object value, BiFunction<java.lang.reflect.Type, Object, Object> converter) {
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

	// endregion

	/**
	 * 创建直接复制器实例
	 * <p>通过 ASM 动态生成字节码类并初始化</p>
	 *
	 * @param beanCopier Bean 复制器
	 * @param targetType 目标类型
	 * @param <S> 源 Bean 类型
	 * @param <T> 目标 Bean 类型
	 * @return 直接复制器实例
	 */
	@SuppressWarnings({"unchecked"})
	static <S, T> BeanDirectCopier<S, T> create(BeanCopier<S> beanCopier, Class<T> targetType) {
		BeanPropertiesClassification classification = BeanProperties.classify(targetType);
		String accessClassName = beanCopier.getClass().getName()
			+ CLASS_NAME_PREFIX
			+ (targetType.getName().replace("_", "__").replace('.', '_'));

		AccessClassLoader loader = AccessClassLoader.get(targetType);
		Class accessClass = loader.loadOrDefineClass(accessClassName, () -> buildFastCopierClass(loader, beanCopier, accessClassName, targetType, classification));
		BeanDirectCopier<S, T> access;
		try {
			access = (BeanDirectCopier<S, T>) accessClass.newInstance();
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

	/**
	 * 构建快速复制器类字节码
	 * <p>使用 ASM 动态生成 Bean 直接复制器的子类字节码</p>
	 *
	 * @param loader 访问类加载器
	 * @param beanCopier Bean 复制器
	 * @param accessClassName 访问类名称
	 * @param type 目标类型
	 * @param classification 属性分类信息
	 * @param <S> 源 Bean 类型
	 * @param <T> 目标 Bean 类型
	 * @return 生成的类字节码数组
	 */
	private static <S, T> byte[] buildFastCopierClass(AccessClassLoader loader
		, BeanCopier<S> beanCopier, String accessClassName, Class<T> type
		, BeanPropertiesClassification classification) {
		String accessClassNameInternal = accessClassName.replace('.', '/');
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		String superClassNameInternal = org.objectweb.asm.Type.getInternalName(BeanDirectCopier.class);
		cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal,
			"L" + superClassNameInternal + "<L" + org.objectweb.asm.Type.getInternalName(beanCopier.sourceType) + ";L" + org.objectweb.asm.Type.getInternalName(type) + ";>;",
			superClassNameInternal, null);
		cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

		Asms.insertDefaultConstructor(cw, superClassNameInternal);
		insertInitTypeFields(cw, superClassNameInternal, accessClassNameInternal, type, classification);
		insertMethodCopy(cw, beanCopier, superClassNameInternal, accessClassNameInternal, type, classification);

		cw.visitEnd();
		byte[] byteArray = cw.toByteArray();
		return byteArray;
	}

	/**
	 * 插入初始化类型字段的字节码
	 *
	 * @param cw ClassWriter
	 * @param superClassNameInternal 父类内部名
	 * @param accessClassNameInternal 访问类内部名
	 * @param type 目标类型
	 * @param classification 属性分类信息
	 * @param <T> 目标类型
	 */
	@SuppressWarnings("rawtypes")
	private static <T> void insertInitTypeFields(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertiesClassification classification) {
		SerializableConsumer<BeanDirectCopier> init = BeanDirectCopier::initTypeFields;
		SerializableBiFunction<BeanDirectCopier, String, java.lang.reflect.Type> getPropertyGenericType = BeanDirectCopier::getPropertyGenericType;
		SerializableBiFunction<BeanDirectCopier, String, Class> getPropertyType = BeanDirectCopier::getPropertyType;

		BeanCopier.insertInitTypeFields(cw, superClassNameInternal, accessClassNameInternal, classification,
			init.serialized().getImplMethodName(),
			getPropertyGenericType.serialized().getImplMethodName(),
			getPropertyType.serialized().getImplMethodName());

	}

	/**
	 * 插入复制方法的字节码
	 * <p>生成所有属性复制的代码逻辑</p>
	 *
	 * @param cw ClassWriter
	 * @param beanCopier Bean 复制器
	 * @param superClassNameInternal 父类内部名
	 * @param accessClassNameInternal 访问类内部名
	 * @param type 目标类型
	 * @param classification 属性分类信息
	 * @param <S> 源 Bean 类型
	 * @param <T> 目标 Bean 类型
	 */
	@SuppressWarnings("all")
	private static <S, T> void insertMethodCopy(ClassWriter cw, BeanCopier<S> beanCopier, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertiesClassification classification) {

		SerializableTriConsumer<BeanDirectCopier, String, Throwable> logCopyError = BeanDirectCopier::resolveCopyError;
		SerializableTriConsumer<BeanDirectCopier, Object, Object> copy = BeanDirectCopier::copy;
		String copyName = copy.serialized().getImplMethodName();

		// 重写方法
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC, copyName, "(L" +
				Type.getInternalName(beanCopier.sourceType) + ";L" + Type.getInternalName(type) + ";)V", "(L" + Type.getInternalName(beanCopier.sourceType) + ";L" + Type.getInternalName(type) + ";)V", null);
			methodVisitor.visitCode();

			classification.properties.forEach((name, targetInfo) -> {
				BeanPropertyInfo sourceInfo = beanCopier.properties.get(name);
				if (sourceInfo == null) {
					return;
				}
				if (targetInfo.getPropertyType() != sourceInfo.getPropertyType()) {
					// 不匹配
					return;
				}
				if (sourceInfo.getField() != null && targetInfo.getField() != null) {
					insertCodeFieldToField(methodVisitor, sourceInfo, targetInfo);
				} else if (sourceInfo.getReadMethod() != null && targetInfo.getField() != null) {
					insertCodeMethodToField(methodVisitor, sourceInfo, targetInfo);
				} else if (sourceInfo.getField() != null && targetInfo.getWriteMethod() != null) {
					insertCodeFieldToMethod(methodVisitor, sourceInfo, targetInfo);
				} else if (sourceInfo.getReadMethod() != null && targetInfo.getWriteMethod() != null) {
					insertCodeMethodToMethod(methodVisitor, sourceInfo, targetInfo);
				}
			});

			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}
		// 泛型合成方法
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED | ACC_BRIDGE | ACC_SYNTHETIC, "copy", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(beanCopier.sourceType));
			methodVisitor.visitVarInsn(ALOAD, 2);
			methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, accessClassNameInternal, "copy", "(L" +
				Type.getInternalName(beanCopier.sourceType) + ";L" +
				Type.getInternalName(type) + ";)V", false);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(3, 3);
			methodVisitor.visitEnd();
		}

	}

	/**
	 * 插入字段到字段的复制代码
	 *
	 * @param methodVisitor MethodVisitor
	 * @param sourceInfo 源属性信息
	 * @param targetInfo 目标属性信息
	 */
	private static void insertCodeFieldToField(MethodVisitor methodVisitor, BeanPropertyInfo sourceInfo, BeanPropertyInfo targetInfo) {
		methodVisitor.visitVarInsn(ALOAD, 1);
		methodVisitor.visitFieldInsn(GETFIELD, Type.getInternalName(sourceInfo.getField().getDeclaringClass()), sourceInfo.getField().getName(), Type.getDescriptor(sourceInfo.getField().getType()));
		Asms.storeVar(methodVisitor, sourceInfo.getField().getType(), 3);
		Label labelIf = null;
		if (Types.isPrimitive(sourceInfo.getField().getType())) {
			methodVisitor.visitVarInsn(ALOAD, 2);
			Asms.loadVar(methodVisitor, sourceInfo.getField().getType(), 3);
		} else {
			Asms.loadVar(methodVisitor, sourceInfo.getField().getType(), 3);
			labelIf = new Label();
			methodVisitor.visitJumpInsn(IFNULL, labelIf);
			methodVisitor.visitVarInsn(ALOAD, 2);
			Asms.loadVar(methodVisitor, sourceInfo.getField().getType(), 3);
		}
		methodVisitor.visitFieldInsn(PUTFIELD, Type.getInternalName(targetInfo.getField().getDeclaringClass()), targetInfo.getField().getName(), Type.getDescriptor(targetInfo.getField().getType()));
		if (labelIf != null) {
			methodVisitor.visitLabel(labelIf);
		}
	}

	/**
	 * 插入方法到字段的复制代码
	 *
	 * @param methodVisitor MethodVisitor
	 * @param sourceInfo 源属性信息
	 * @param targetInfo 目标属性信息
	 */
	private static void insertCodeMethodToField(MethodVisitor methodVisitor, BeanPropertyInfo sourceInfo, BeanPropertyInfo targetInfo) {
		SerializableTriConsumer<BeanDirectCopier, String, Throwable> logCopyError = BeanDirectCopier::resolveCopyError;
		Method readMethod = sourceInfo.getReadMethod();
		boolean hasThrows = readMethod.getExceptionTypes().length > 0;
		Label labelStart = new Label();
		Label labelEnd = new Label();
		Label labelCatch = new Label();
		if (hasThrows) {
			methodVisitor.visitTryCatchBlock(labelStart, labelEnd, labelCatch, "java/lang/Throwable");
			methodVisitor.visitLabel(labelStart);
		}
		// try
		methodVisitor.visitVarInsn(ALOAD, 1);
		boolean isInterface = readMethod.getDeclaringClass().isInterface();
		int invokeOpcode;
		if (isInterface) {
			invokeOpcode = INVOKEINTERFACE;
		} else if (Modifier.isStatic(readMethod.getModifiers())) {
			invokeOpcode = INVOKESTATIC;
		} else {
			invokeOpcode = INVOKEVIRTUAL;
		}
		methodVisitor.visitMethodInsn(invokeOpcode, Type.getInternalName(readMethod.getDeclaringClass()), readMethod.getName(), Type.getMethodDescriptor(readMethod), isInterface);
		Asms.storeVar(methodVisitor, readMethod.getReturnType(), 3);

		Label labelIf = null;
		if (Types.isPrimitive(readMethod.getReturnType())) {
			methodVisitor.visitVarInsn(ALOAD, 2);
			Asms.loadVar(methodVisitor, readMethod.getReturnType(), 3);
		} else {
			Asms.loadVar(methodVisitor, readMethod.getReturnType(), 3);
			labelIf = new Label();
			methodVisitor.visitJumpInsn(IFNULL, labelIf);
			methodVisitor.visitVarInsn(ALOAD, 2);
			Asms.loadVar(methodVisitor, readMethod.getReturnType(), 3);
		}
		methodVisitor.visitFieldInsn(PUTFIELD, Type.getInternalName(targetInfo.getField().getDeclaringClass()), targetInfo.getField().getName(), Type.getDescriptor(targetInfo.getField().getType()));
		if (labelIf != null) {
			methodVisitor.visitLabel(labelIf);
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
			methodVisitor.visitLdcInsn(targetInfo.getPropertyName());
			methodVisitor.visitVarInsn(ALOAD, 3);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(BeanDirectCopier.class), logCopyError.serialized().getImplMethodName(), "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
			//finally
			methodVisitor.visitLabel(labelFinal);
		}
	}

	/**
	 * 插入方法到方法的复制代码
	 *
	 * @param methodVisitor MethodVisitor
	 * @param sourceInfo 源属性信息
	 * @param targetInfo 目标属性信息
	 */
	private static void insertCodeMethodToMethod(MethodVisitor methodVisitor, BeanPropertyInfo sourceInfo, BeanPropertyInfo targetInfo) {
		SerializableTriConsumer<BeanDirectCopier, String, Throwable> logCopyError = BeanDirectCopier::resolveCopyError;
		Method readMethod = sourceInfo.getReadMethod();
		Method writeMethod = targetInfo.getWriteMethod();
		boolean hasThrows = readMethod.getExceptionTypes().length > 0
			|| writeMethod.getExceptionTypes().length > 0;
		Label labelStart = new Label();
		Label labelEnd = new Label();
		Label labelCatch = new Label();
		if (hasThrows) {
			methodVisitor.visitTryCatchBlock(labelStart, labelEnd, labelCatch, "java/lang/Throwable");
			methodVisitor.visitLabel(labelStart);
		}
		// try
		methodVisitor.visitVarInsn(ALOAD, 1);
		boolean isInterface = readMethod.getDeclaringClass().isInterface();
		int invokeOpcode;
		if (isInterface) {
			invokeOpcode = INVOKEINTERFACE;
		} else if (Modifier.isStatic(readMethod.getModifiers())) {
			invokeOpcode = INVOKESTATIC;
		} else {
			invokeOpcode = INVOKEVIRTUAL;
		}
		methodVisitor.visitMethodInsn(invokeOpcode, Type.getInternalName(readMethod.getDeclaringClass()), readMethod.getName(), Type.getMethodDescriptor(readMethod), isInterface);
		Asms.storeVar(methodVisitor, readMethod.getReturnType(), 3);

		Label labelIf = null;
		if (Types.isPrimitive(readMethod.getReturnType())) {
			methodVisitor.visitVarInsn(ALOAD, 2);
			Asms.loadVar(methodVisitor, readMethod.getReturnType(), 3);
		} else {
			Asms.loadVar(methodVisitor, readMethod.getReturnType(), 3);
			labelIf = new Label();
			methodVisitor.visitJumpInsn(IFNULL, labelIf);
			methodVisitor.visitVarInsn(ALOAD, 2);
			Asms.loadVar(methodVisitor, readMethod.getReturnType(), 3);
		}

		isInterface = writeMethod.getDeclaringClass().isInterface();
		if (isInterface) {
			invokeOpcode = INVOKEINTERFACE;
		} else if (Modifier.isStatic(writeMethod.getModifiers())) {
			invokeOpcode = INVOKESTATIC;
		} else {
			invokeOpcode = INVOKEVIRTUAL;
		}

		methodVisitor.visitMethodInsn(invokeOpcode, Type.getInternalName(writeMethod.getDeclaringClass()), writeMethod.getName(), Type.getMethodDescriptor(writeMethod));
		if (labelIf != null) {
			methodVisitor.visitLabel(labelIf);
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
			methodVisitor.visitLdcInsn(targetInfo.getPropertyName());
			methodVisitor.visitVarInsn(ALOAD, 3);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(BeanDirectCopier.class), logCopyError.serialized().getImplMethodName(), "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
			//finally
			methodVisitor.visitLabel(labelFinal);
		}
	}

	/**
	 * 插入字段到方法的复制代码
	 *
	 * @param methodVisitor MethodVisitor
	 * @param sourceInfo 源属性信息
	 * @param targetInfo 目标属性信息
	 */
	private static void insertCodeFieldToMethod(MethodVisitor methodVisitor, BeanPropertyInfo sourceInfo, BeanPropertyInfo targetInfo) {
		SerializableTriConsumer<BeanDirectCopier, String, Throwable> logCopyError = BeanDirectCopier::resolveCopyError;
		Method writeMethod = targetInfo.getWriteMethod();
		boolean hasThrows = writeMethod.getExceptionTypes().length > 0;
		Label labelStart = new Label();
		Label labelEnd = new Label();
		Label labelCatch = new Label();
		if (hasThrows) {
			methodVisitor.visitTryCatchBlock(labelStart, labelEnd, labelCatch, "java/lang/Throwable");
			methodVisitor.visitLabel(labelStart);
		}
		// try
		methodVisitor.visitVarInsn(ALOAD, 1);

		methodVisitor.visitFieldInsn(GETFIELD, Type.getInternalName(sourceInfo.getField().getDeclaringClass()), sourceInfo.getField().getName(), Type.getDescriptor(sourceInfo.getField().getType()));
		Asms.storeVar(methodVisitor, sourceInfo.getField().getType(), 3);

		Label labelIf = null;
		if (Types.isPrimitive(sourceInfo.getField().getType())) {
			methodVisitor.visitVarInsn(ALOAD, 2);
			Asms.loadVar(methodVisitor, sourceInfo.getField().getType(), 3);
		} else {
			Asms.loadVar(methodVisitor, sourceInfo.getField().getType(), 3);
			labelIf = new Label();
			methodVisitor.visitJumpInsn(IFNULL, labelIf);
			methodVisitor.visitVarInsn(ALOAD, 2);
			Asms.loadVar(methodVisitor, sourceInfo.getField().getType(), 3);
		}
		boolean isInterface = writeMethod.getDeclaringClass().isInterface();
		int invokeOpcode;
		if (isInterface) {
			invokeOpcode = INVOKEINTERFACE;
		} else if (Modifier.isStatic(writeMethod.getModifiers())) {
			invokeOpcode = INVOKESTATIC;
		} else {
			invokeOpcode = INVOKEVIRTUAL;
		}

		methodVisitor.visitMethodInsn(invokeOpcode, Type.getInternalName(writeMethod.getDeclaringClass()), writeMethod.getName(), Type.getMethodDescriptor(writeMethod));
		if (labelIf != null) {
			methodVisitor.visitLabel(labelIf);
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
			methodVisitor.visitLdcInsn(targetInfo.getPropertyName());
			methodVisitor.visitVarInsn(ALOAD, 3);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(BeanDirectCopier.class), logCopyError.serialized().getImplMethodName(), "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
			//finally
			methodVisitor.visitLabel(labelFinal);
		}
	}

}
