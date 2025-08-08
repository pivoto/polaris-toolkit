package io.polaris.core.asm.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import io.polaris.core.asm.internal.AsmUtils;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.reflect.SerializableConsumerWithArgs4;
import io.polaris.core.reflect.SerializableTriFunction;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Qt
 * @since  Apr 11, 2024
 */
public abstract class BeanAccess<T> {
	private static Logger log = Loggers.of(BeanAccess.class);
	@SuppressWarnings({"rawtypes"})
	private static final AccessClassPool<Class<?>, BeanAccess> pool = new AccessClassPool<>();

	private Map<String, BeanPropertyInfo> properties;
	private Map<String, Integer> setterIndices;
	private Map<String, Integer> getterIndices;
	private Map<String, Integer> fieldIndices;

	protected BeanAccess() {
	}

	// region to be inherited

	public Object getIndexProperty(Object o, int methodIndex) {
		throw new IllegalArgumentException("Method not found");
	}

	public void setIndexProperty(Object o, int methodIndex, Object val) {
		throw new IllegalArgumentException("Method not found");
	}

	public Object getIndexField(Object o, int fieldIndex) {
		throw new IllegalArgumentException("Field not found");
	}

	public void setIndexField(Object o, int methodIndex, Object val) {
		throw new IllegalArgumentException("Field not found");
	}

	// endregion

	// region info


	public Map<String, BeanPropertyInfo> properties() {
		return properties;
	}

	public boolean containsAny(String property) {
		return properties.containsKey(property);
	}

	public Set<String> allPropertyNames() {
		return properties.keySet();
	}

	public java.lang.reflect.Type propertyGenericType(String property) {
		BeanPropertyInfo info = this.properties.get(property);
		return info == null ? null : info.getPropertyGenericType();
	}

	public java.lang.reflect.Type propertyType(String property) {
		BeanPropertyInfo info = this.properties.get(property);
		return info == null ? null : info.getPropertyType();
	}

	// endregion

	// region property or field

	public boolean containsSetterOrField(String key) {
		BeanPropertyInfo info = properties.get(key);
		if (info != null) {
			return info.getField() != null || info.getWriteMethod() != null;
		}
		return false;
	}

	public boolean containsGetterOrField(String key) {
		BeanPropertyInfo info = properties.get(key);
		if (info != null) {
			return info.getField() != null || info.getReadMethod() != null;
		}
		return false;
	}

	public Object getPropertyOrField(Object o, String key) {
		int i = getGetterIndex(key);
		if (i >= 0) {
			return getIndexProperty(o, i);
		}
		i = getFieldIndex(key);
		if (i >= 0) {
			return getIndexField(o, i);
		}
		return null;
	}

	public boolean setPropertyOrField(Object o, String key, Object val) {
		int i = getSetterIndex(key);
		if (i >= 0) {
			setIndexProperty(o, i, val);
			return true;
		}
		i = getFieldIndex(key);
		if (i >= 0) {
			setIndexField(o, i, val);
			return true;
		}
		return false;
	}

	public boolean setPropertyOrField(Object o, String key, Object val, BiFunction<java.lang.reflect.Type, Object, Object> converter) {
		BeanPropertyInfo info = this.properties.get(key);
		if (info == null) {
			return false;
		}
		java.lang.reflect.Type type = info.getPropertyGenericType();
		if (info.getField() != null) {
			setField(o, key, converter.apply(type, val));
			return true;
		}
		if (info.getWriteMethod() != null) {
			setProperty(o, key, converter.apply(type, val));
			return true;
		}
		return false;
	}

	// endregion

	// region setter

	public boolean containsSetter(String property) {
		return setterIndices.containsKey(property);
	}

	public Map<String, Integer> setterIndices() {
		return setterIndices;
	}

	public Set<String> setterPropertyNames() {
		return setterIndices.keySet();
	}

	public int getSetterIndex(String property) {
		Integer index = setterIndices.get(property);
		return index == null ? -1 : index;
	}

	public void setProperty(Object o, String property, Object val) {
		int index = getSetterIndex(property);
		if (index == -1) {
			throw new IllegalArgumentException("Method not found");
		}
		setIndexProperty(o, index, val);
	}

	public void setPropertyOrNoop(Object o, String property, Object val) {
		int index = getSetterIndex(property);
		if (index == -1) {
			return;
		}
		setIndexProperty(o, index, val);
	}

	// endregion

	// region getter

	public boolean containsGetter(String property) {
		return getterIndices.containsKey(property);
	}

	public Map<String, Integer> getterIndices() {
		return getterIndices;
	}

	public Set<String> getterPropertyNames() {
		return getterIndices.keySet();
	}

	public int getGetterIndex(String property) {
		Integer index = getterIndices.get(property);
		return index == null ? -1 : index;
	}

	public Object getProperty(Object o, String property) {
		int index = getGetterIndex(property);
		if (index == -1) {
			throw new IllegalArgumentException("Method not found");
		}
		return getIndexProperty(o, index);
	}

	public Object getPropertyOrNoop(Object o, String property) {
		int index = getSetterIndex(property);
		if (index == -1) {
			return null;
		}
		return getIndexProperty(o, index);
	}

	// endregion

	// region field

	public boolean containsField(String property) {
		return fieldIndices.containsKey(property);
	}

	public Map<String, Integer> fieldIndices() {
		return fieldIndices;
	}

	public Set<String> fieldNames() {
		return fieldIndices.keySet();
	}

	public int getFieldIndex(String property) {
		Integer index = fieldIndices.get(property);
		return index == null ? -1 : index;
	}

	public void setField(Object o, String property, Object val) {
		int index = getFieldIndex(property);
		if (index == -1) {
			throw new IllegalArgumentException("Field not found");
		}
		setIndexField(o, index, val);
	}

	public Object getField(Object o, String property) {
		int index = getFieldIndex(property);
		if (index == -1) {
			throw new IllegalArgumentException("Field not found");
		}
		return getIndexField(o, index);
	}

	public void setFieldOrNoop(Object o, String property, Object val) {
		int index = getFieldIndex(property);
		if (index == -1) {
			return;
		}
		setIndexField(o, index, val);
	}

	public Object getFieldOrNoop(Object o, String property) {
		int index = getFieldIndex(property);
		if (index == -1) {
			return null;
		}
		return getIndexField(o, index);
	}

	// endregion

	@SuppressWarnings("unchecked")
	public static <T> BeanAccess<T> get(Class<T> type) {
		return pool.computeIfAbsent(type, BeanAccess::create);
	}

	@SuppressWarnings({"unchecked"})
	public static <T> BeanAccess<T> create(Class<T> type) {
		BeanPropertyInfo.Classification classification = BeanPropertyInfo.classify(type);
		String accessClassName = AccessClassLoader.buildAccessClassName(type, BeanAccess.class);
		Class accessClass;
		AccessClassLoader loader = AccessClassLoader.get(type);
		synchronized (loader) {
			accessClass = loader.loadAccessClass(accessClassName);
			if (accessClass == null) {
				accessClass = buildAccessClass(loader, accessClassName, type, classification);
			}
		}
		BeanAccess<T> access;
		try {
			access = (BeanAccess<T>) accessClass.newInstance();
			// 全部设为只读属性
			access.properties = Collections.unmodifiableMap(classification.properties);
			{
				Map<String, Integer> setterIndices = new HashMap<>();
				for (int i = 0; i < classification.setters.size(); i++) {
					setterIndices.put(classification.setters.get(i).getPropertyName(), i);
				}
				access.setterIndices = Collections.unmodifiableMap(setterIndices);
			}
			{
				Map<String, Integer> getterIndices = new HashMap<>();
				for (int i = 0; i < classification.getters.size(); i++) {
					getterIndices.put(classification.getters.get(i).getPropertyName(), i);
				}
				access.getterIndices = Collections.unmodifiableMap(getterIndices);
			}
			{
				Map<String, Integer> fieldIndices = new HashMap<>();
				for (int i = 0; i < classification.fields.size(); i++) {
					fieldIndices.put(classification.fields.get(i).getPropertyName(), i);
				}
				access.fieldIndices = Collections.unmodifiableMap(fieldIndices);
			}
		} catch (Throwable t) {
			throw new IllegalStateException("创建访问类失败: " + accessClassName, t);
		}
		return access;
	}

	private static <T> Class buildAccessClass(AccessClassLoader loader
		, String accessClassName, Class<T> type
		, BeanPropertyInfo.Classification classification) {
		String accessClassNameInternal = accessClassName.replace('.', '/');
		String classNameInternal = type.getName().replace('.', '/');

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		String superClassNameInternal = BeanAccess.class.getName().replace('.', '/');
		cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal,
			"L" + superClassNameInternal + "<L" + Type.getInternalName(type) + ";>;",
			superClassNameInternal, null);
		cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

		AsmUtils.insertDefaultConstructor(cw, superClassNameInternal);
		insertSetterInvokers(cw, accessClassNameInternal, type, classification.setters);
		insertGetterInvokers(cw, accessClassNameInternal, type, classification.getters);
		insertFieldInvokers(cw, accessClassNameInternal, type, classification.fields);

		cw.visitEnd();
		byte[] byteArray = cw.toByteArray();

		return loader.defineAccessClass(accessClassName, byteArray);
	}

	private static <T> void insertSetterInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type, List<BeanPropertyInfo> setters) {
		// ignore
		if (setters.isEmpty()) {
			return;
		}

		SerializableConsumerWithArgs4<BeanAccess<?>, Object, Integer, Object> setIndexProperty = BeanAccess::setIndexProperty;
		// 生成各方法的索引式调用方法
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED | ACC_VARARGS, setIndexProperty.serialized().getImplMethodName(), "(Ljava/lang/Object;ILjava/lang/Object;)V", null, null);
			methodVisitor.visitCode();
			// switch 方法名分支
			{
				methodVisitor.visitVarInsn(ILOAD, 2);

				Label[] labels = AsmUtils.newLabels(setters.size());
				Label labelDefault = new Label();
				Label labelBreak = new Label();
				methodVisitor.visitTableSwitchInsn(0, setters.size() - 1, labelDefault, labels);
				// case
				for (int idxName = 0; idxName < setters.size(); idxName++) {
					methodVisitor.visitLabel(labels[idxName]);
					BeanPropertyInfo info = setters.get(idxName);
					Method method = info.getWriteMethod();
					String methodName = method.getName();
					boolean hasThrows = method.getExceptionTypes().length > 0;
					Class<?>[] parameterTypes = method.getParameterTypes();

					// 方法调用
					{
						Label labelStart = new Label();
						Label labelEnd = new Label();
						Label labelCatch = new Label();
						if (hasThrows) {
							methodVisitor.visitTryCatchBlock(labelStart, labelEnd, labelCatch, "java/lang/Throwable");
							methodVisitor.visitLabel(labelStart);
						}
						// try
						methodVisitor.visitVarInsn(ALOAD, 1);
						methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
						if (Modifier.isStatic(method.getModifiers())) {
							methodVisitor.visitInsn(POP);
						}

						methodVisitor.visitVarInsn(ALOAD, 3);
						Class<?> parameterType = parameterTypes[0];
						Type paramType = Type.getType(parameterType);
						AsmUtils.autoUnBoxing(methodVisitor, paramType);

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
						methodVisitor.visitMethodInsn(invokeOpcode, Type.getInternalName(declaringClass), methodName, Type.getMethodDescriptor(method), isInterface);
						Class<?> returnType = method.getReturnType();
						AsmUtils.autoBoxing(methodVisitor, returnType);
						methodVisitor.visitJumpInsn(Opcodes.GOTO, labelBreak);

						if (hasThrows) {
							methodVisitor.visitLabel(labelEnd);
						}

						// catch
						if (hasThrows) {
							methodVisitor.visitLabel(labelCatch);
							methodVisitor.visitVarInsn(ASTORE, 4);
							methodVisitor.visitTypeInsn(NEW, Type.getInternalName(IllegalArgumentException.class));
							methodVisitor.visitInsn(DUP);
							methodVisitor.visitVarInsn(ALOAD, 4);
							methodVisitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(IllegalArgumentException.class), "<init>", "(Ljava/lang/Throwable;)V", false);
							methodVisitor.visitInsn(ATHROW);
						}

					}
				}
				// default
				methodVisitor.visitLabel(labelDefault);
				methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
				methodVisitor.visitInsn(DUP);
				methodVisitor.visitLdcInsn("Method not found");
				methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
				methodVisitor.visitInsn(ATHROW);

				// break
				methodVisitor.visitLabel(labelBreak);
			}

			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}
	}

	private static <T> void insertGetterInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type, List<BeanPropertyInfo> getters) {
		// ignore
		if (getters.isEmpty()) {
			return;
		}
		SerializableTriFunction<BeanAccess<?>, Object, Integer, Object> getIndexProperty = BeanAccess::getIndexProperty;

		// 生成各方法的索引式调用方法
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED | ACC_VARARGS, getIndexProperty.serialized().getImplMethodName(), "(Ljava/lang/Object;I)Ljava/lang/Object;", null, null);
			methodVisitor.visitCode();
			// switch 方法名分支
			{
				methodVisitor.visitVarInsn(ILOAD, 2);
				Label[] labels = AsmUtils.newLabels(getters.size());
				Label labelDefault = new Label();
				Label labelBreak = new Label();
				methodVisitor.visitTableSwitchInsn(0, getters.size() - 1, labelDefault, labels);

				// case
				for (int idxName = 0; idxName < getters.size(); idxName++) {
					methodVisitor.visitLabel(labels[idxName]);
					BeanPropertyInfo info = getters.get(idxName);
					Method method = info.getReadMethod();
					String methodName = method.getName();
					boolean hasThrows = method.getExceptionTypes().length > 0;

					// 方法调用
					{
						Label labelStart = new Label();
						Label labelEnd = new Label();
						Label labelCatch = new Label();
						if (hasThrows) {
							methodVisitor.visitTryCatchBlock(labelStart, labelEnd, labelCatch, "java/lang/Throwable");
							methodVisitor.visitLabel(labelStart);
						}
						// try
						methodVisitor.visitVarInsn(ALOAD, 1);
						methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
						if (Modifier.isStatic(method.getModifiers())) {
							methodVisitor.visitInsn(POP);
						}
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
						methodVisitor.visitMethodInsn(invokeOpcode, Type.getInternalName(declaringClass), methodName, Type.getMethodDescriptor(method), isInterface);
						Class<?> returnType = method.getReturnType();
						if (returnType.equals(Void.TYPE)) {
							methodVisitor.visitInsn(ACONST_NULL);
						} else {
							AsmUtils.autoBoxing(methodVisitor, returnType);
						}
						if (hasThrows) {
							methodVisitor.visitLabel(labelEnd);
						}
						methodVisitor.visitInsn(ARETURN);
						// catch
						if (hasThrows) {
							methodVisitor.visitLabel(labelCatch);
							methodVisitor.visitVarInsn(ASTORE, 3);
							methodVisitor.visitTypeInsn(NEW, Type.getInternalName(IllegalArgumentException.class));
							methodVisitor.visitInsn(DUP);
							methodVisitor.visitVarInsn(ALOAD, 3);
							methodVisitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(IllegalArgumentException.class), "<init>", "(Ljava/lang/Throwable;)V", false);
							methodVisitor.visitInsn(ATHROW);
						}
					}
				}
				// default
				methodVisitor.visitLabel(labelDefault);
				methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
				methodVisitor.visitInsn(DUP);
				methodVisitor.visitLdcInsn("Method not found");
				methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
				methodVisitor.visitInsn(ATHROW);
			}
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}
	}

	private static <T> void insertFieldInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type, List<BeanPropertyInfo> fields) {
		// ignore
		if (fields.isEmpty()) {
			return;
		}
		SerializableTriFunction<BeanAccess<?>, Object, Integer, Object> getIndexField = BeanAccess::getIndexField;
		SerializableConsumerWithArgs4<BeanAccess<?>, Object, Integer, Object> setIndexField = BeanAccess::setIndexField;
		insertStdFieldInvoker(cw, type, fields, getIndexField, setIndexField);
	}

	private static <T> void insertStdFieldInvoker(ClassWriter cw, Class<T> type, List<BeanPropertyInfo> fields, SerializableTriFunction<BeanAccess<?>, Object, Integer, Object> getter, SerializableConsumerWithArgs4<BeanAccess<?>, Object, Integer, Object> setter) {
		// getter
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, getter.serialized().getImplMethodName(), "(Ljava/lang/Object;I)Ljava/lang/Object;", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ILOAD, 2);

			Label[] labels = AsmUtils.newLabels(fields.size());
			Label labelDefault = new Label();
			methodVisitor.visitTableSwitchInsn(0, fields.size() - 1, labelDefault, labels);

			for (int idxField = 0; idxField < fields.size(); idxField++) {
				methodVisitor.visitLabel(labels[idxField]);

				BeanPropertyInfo info = fields.get(idxField);
				Field field = info.getField();
				String fieldName = field.getName();
				Type fieldType = Type.getType(field.getType());

				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
				if (Modifier.isStatic(field.getModifiers())) {
					methodVisitor.visitInsn(POP);
					methodVisitor.visitFieldInsn(GETSTATIC, Type.getInternalName(field.getDeclaringClass()), fieldName, Type.getDescriptor(field.getType()));
				} else {
					methodVisitor.visitFieldInsn(GETFIELD, Type.getInternalName(type), fieldName, Type.getDescriptor(field.getType()));
				}
				AsmUtils.autoBoxing(methodVisitor, fieldType);
				methodVisitor.visitInsn(ARETURN);
			}

			// default
			methodVisitor.visitLabel(labelDefault);
			methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitLdcInsn("Field not found");
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
			methodVisitor.visitInsn(ATHROW);

			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();

		}
		// setter
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, setter.serialized().getImplMethodName(), "(Ljava/lang/Object;ILjava/lang/Object;)V", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ILOAD, 2);

			Label[] labels = AsmUtils.newLabels(fields.size());

			Label labelDefault = new Label();
			methodVisitor.visitTableSwitchInsn(0, fields.size() - 1, labelDefault, labels);
			for (int idxField = 0; idxField < fields.size(); idxField++) {
				methodVisitor.visitLabel(labels[idxField]);

				BeanPropertyInfo info = fields.get(idxField);
				Field field = info.getField();
				String fieldName = field.getName();
				Type fieldType = Type.getType(field.getType());

				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
				if (Modifier.isStatic(field.getModifiers())) {
					methodVisitor.visitInsn(POP);
				}
				methodVisitor.visitVarInsn(ALOAD, 3);
				AsmUtils.autoUnBoxing(methodVisitor, fieldType);
				if (Modifier.isStatic(field.getModifiers())) {
					methodVisitor.visitFieldInsn(PUTSTATIC, Type.getInternalName(field.getDeclaringClass()), fieldName, fieldType.getDescriptor());
				} else {
					methodVisitor.visitFieldInsn(PUTFIELD, Type.getInternalName(field.getDeclaringClass()), fieldName, fieldType.getDescriptor());
				}
				methodVisitor.visitInsn(RETURN);
			}
			// default
			methodVisitor.visitLabel(labelDefault);
			methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitLdcInsn("Field not found");
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
			methodVisitor.visitInsn(ATHROW);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}
	}


}
