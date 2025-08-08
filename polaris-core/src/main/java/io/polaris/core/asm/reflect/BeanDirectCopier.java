package io.polaris.core.asm.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;

import io.polaris.core.asm.internal.AsmUtils;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.core.lang.copier.CopyOptions;
import io.polaris.core.log.ILogger;
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
 * 默认规则下的Bean属性复制，不接受CopyOptions参数
 *
 * @author Qt
 * @since Apr 14, 2024
 */
public abstract class BeanDirectCopier<S, T> {
	private static ILogger log = Loggers.of(BeanCopier.class);
	private static final String CLASS_NAME_PREFIX = "Direct$";
	public static final String FIELD_PREFIX_TYPE = BeanCopier.FIELD_PREFIX_TYPE;
	public static final String FIELD_PREFIX_CLASS = BeanCopier.FIELD_PREFIX_CLASS;
	protected Class<T> targetType;
	protected BeanCopier<S> beanCopier;
	protected Map<String, BeanPropertyInfo> properties;

	protected void copy(S source, T target) {
	}

	protected abstract void initTypeFields();


	// region inner tool

	protected final void resolveCopyError(String propertyName, Throwable e) {
		if (log.isDebugEnabled()) {
			log.debug("复制属性[" + propertyName + "]失败", e);
		}
	}

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

	@SuppressWarnings({"unchecked"})
	static <S, T> BeanDirectCopier<S, T> create(BeanCopier<S> beanCopier, Class<T> targetType) {
		BeanPropertyInfo.Classification classification = BeanPropertyInfo.classify(targetType);
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

	private static <S, T> byte[] buildFastCopierClass(AccessClassLoader loader
		, BeanCopier<S> beanCopier, String accessClassName, Class<T> type
		, BeanPropertyInfo.Classification classification) {
		String accessClassNameInternal = accessClassName.replace('.', '/');
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		String superClassNameInternal = org.objectweb.asm.Type.getInternalName(BeanDirectCopier.class);
		cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal,
			"L" + superClassNameInternal + "<L" + org.objectweb.asm.Type.getInternalName(beanCopier.sourceType) + ";L" + org.objectweb.asm.Type.getInternalName(type) + ";>;",
			superClassNameInternal, null);
		cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

		AsmUtils.insertDefaultConstructor(cw, superClassNameInternal);
		insertInitTypeFields(cw, superClassNameInternal, accessClassNameInternal, type, classification);
		insertMethodCopy(cw, beanCopier, superClassNameInternal, accessClassNameInternal, type, classification);

		cw.visitEnd();
		byte[] byteArray = cw.toByteArray();
		return byteArray;
	}

	@SuppressWarnings("rawtypes")
	private static <T> void insertInitTypeFields(ClassWriter cw, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertyInfo.Classification classification) {
		SerializableConsumer<BeanDirectCopier> init = BeanDirectCopier::initTypeFields;
		SerializableBiFunction<BeanDirectCopier, String, java.lang.reflect.Type> getPropertyGenericType = BeanDirectCopier::getPropertyGenericType;
		SerializableBiFunction<BeanDirectCopier, String, Class> getPropertyType = BeanDirectCopier::getPropertyType;

		BeanCopier.insertInitTypes(cw, superClassNameInternal, accessClassNameInternal, classification,
			init.serialized().getImplMethodName(),
			getPropertyGenericType.serialized().getImplMethodName(),
			getPropertyType.serialized().getImplMethodName());

	}

	@SuppressWarnings("all")
	private static <S, T> void insertMethodCopy(ClassWriter cw, BeanCopier<S> beanCopier, String superClassNameInternal, String accessClassNameInternal, Class<T> type, BeanPropertyInfo.Classification classification) {

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

	private static void insertCodeFieldToField(MethodVisitor methodVisitor, BeanPropertyInfo sourceInfo, BeanPropertyInfo targetInfo) {
		methodVisitor.visitVarInsn(ALOAD, 1);
		methodVisitor.visitFieldInsn(GETFIELD, Type.getInternalName(sourceInfo.getField().getDeclaringClass()), sourceInfo.getField().getName(), Type.getDescriptor(sourceInfo.getField().getType()));
		AsmUtils.storeVar(methodVisitor, sourceInfo.getField().getType(), 3);
		Label labelIf = null;
		if (Types.isPrimitive(sourceInfo.getField().getType())) {
			methodVisitor.visitVarInsn(ALOAD, 2);
			AsmUtils.loadVar(methodVisitor, sourceInfo.getField().getType(), 3);
		} else {
			AsmUtils.loadVar(methodVisitor, sourceInfo.getField().getType(), 3);
			labelIf = new Label();
			methodVisitor.visitJumpInsn(IFNULL, labelIf);
			methodVisitor.visitVarInsn(ALOAD, 2);
			AsmUtils.loadVar(methodVisitor, sourceInfo.getField().getType(), 3);
		}
		methodVisitor.visitFieldInsn(PUTFIELD, Type.getInternalName(targetInfo.getField().getDeclaringClass()), targetInfo.getField().getName(), Type.getDescriptor(targetInfo.getField().getType()));
		if (labelIf != null) {
			methodVisitor.visitLabel(labelIf);
		}
	}

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
		AsmUtils.storeVar(methodVisitor, readMethod.getReturnType(), 3);

		Label labelIf = null;
		if (Types.isPrimitive(readMethod.getReturnType())) {
			methodVisitor.visitVarInsn(ALOAD, 2);
			AsmUtils.loadVar(methodVisitor, readMethod.getReturnType(), 3);
		} else {
			AsmUtils.loadVar(methodVisitor, readMethod.getReturnType(), 3);
			labelIf = new Label();
			methodVisitor.visitJumpInsn(IFNULL, labelIf);
			methodVisitor.visitVarInsn(ALOAD, 2);
			AsmUtils.loadVar(methodVisitor, readMethod.getReturnType(), 3);
		}
		methodVisitor.visitFieldInsn(PUTFIELD, Type.getInternalName(targetInfo.getField().getDeclaringClass()), targetInfo.getField().getName(), Type.getDescriptor(targetInfo.getField().getType()));
		if (labelIf != null) {
			methodVisitor.visitLabel(labelIf);
		}

		// end
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
		AsmUtils.storeVar(methodVisitor, readMethod.getReturnType(), 3);

		Label labelIf = null;
		if (Types.isPrimitive(readMethod.getReturnType())) {
			methodVisitor.visitVarInsn(ALOAD, 2);
			AsmUtils.loadVar(methodVisitor, readMethod.getReturnType(), 3);
		} else {
			AsmUtils.loadVar(methodVisitor, readMethod.getReturnType(), 3);
			labelIf = new Label();
			methodVisitor.visitJumpInsn(IFNULL, labelIf);
			methodVisitor.visitVarInsn(ALOAD, 2);
			AsmUtils.loadVar(methodVisitor, readMethod.getReturnType(), 3);
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
		AsmUtils.storeVar(methodVisitor, sourceInfo.getField().getType(), 3);

		Label labelIf = null;
		if (Types.isPrimitive(sourceInfo.getField().getType())) {
			methodVisitor.visitVarInsn(ALOAD, 2);
			AsmUtils.loadVar(methodVisitor, sourceInfo.getField().getType(), 3);
		} else {
			AsmUtils.loadVar(methodVisitor, sourceInfo.getField().getType(), 3);
			labelIf = new Label();
			methodVisitor.visitJumpInsn(IFNULL, labelIf);
			methodVisitor.visitVarInsn(ALOAD, 2);
			AsmUtils.loadVar(methodVisitor, sourceInfo.getField().getType(), 3);
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
