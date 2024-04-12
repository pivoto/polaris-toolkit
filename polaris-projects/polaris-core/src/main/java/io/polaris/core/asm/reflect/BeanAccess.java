package io.polaris.core.asm.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.polaris.core.asm.AsmUtils;
import io.polaris.core.err.InvocationException;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.reflect.SerializableQuaternionConsumer;
import io.polaris.core.reflect.SerializableTernaryFunction;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_VARARGS;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;

/**
 * @author Qt
 * @since 1.8,  Apr 11, 2024
 */
public abstract class BeanAccess<T> {
	private static final AccessPool<Class, BeanAccess> pool = new AccessPool<>();
	private static ILogger log = ILoggers.of(BeanAccess.class);

	private Map<String, BeanPropertyInfo> properties;
	private Map<String, Integer> setterIndices;
	private Map<String, Integer> getterIndices;
	private Map<String, Integer> fieldIndices;
	private Map<String, Integer> staticFieldIndices;

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

	public Object getIndexStaticField(Object o, int fieldIndex) {
		throw new IllegalArgumentException("Field not found");
	}

	public void setIndexStaticField(Object o, int methodIndex, Object val) {
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
		return Collections.unmodifiableSet(properties.keySet());
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

	// region setter

	public boolean containsSetter(String property) {
		return setterIndices.containsKey(property);
	}

	public Map<String, Integer> setterIndices() {
		return Collections.unmodifiableMap(setterIndices);
	}

	public Set<String> setterPropertyNames() {
		return Collections.unmodifiableSet(setterIndices.keySet());
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
		return Collections.unmodifiableMap(getterIndices);
	}

	public Set<String> getterPropertyNames() {
		return Collections.unmodifiableSet(getterIndices.keySet());
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

	// region normal field

	public boolean containsField(String property) {
		return fieldIndices.containsKey(property);
	}

	public Map<String, Integer> fieldIndices() {
		return Collections.unmodifiableMap(fieldIndices);
	}

	public Set<String> fieldNames() {
		return Collections.unmodifiableSet(fieldIndices.keySet());
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

	// region static field

	public boolean containsStaticField(String property) {
		return staticFieldIndices.containsKey(property);
	}

	public Map<String, Integer> staticFieldIndices() {
		return Collections.unmodifiableMap(setterIndices);
	}

	public Set<String> staticFieldNames() {
		return Collections.unmodifiableSet(staticFieldIndices.keySet());
	}

	public int getStaticFieldIndex(String property) {
		Integer index = staticFieldIndices.get(property);
		return index == null ? -1 : index;
	}

	public void setStaticField(Object o, String property, Object val) {
		int index = getStaticFieldIndex(property);
		if (index == -1) {
			throw new IllegalArgumentException("Field not found");
		}
		setIndexStaticField(o, index, val);
	}

	public Object getStaticField(Object o, String property) {
		int index = getStaticFieldIndex(property);
		if (index == -1) {
			throw new IllegalArgumentException("Field not found");
		}
		return getIndexStaticField(o, index);
	}

	public void setStaticFieldOrNoop(Object o, String property, Object val) {
		int index = getStaticFieldIndex(property);
		if (index == -1) {
			return;
		}
		setIndexStaticField(o, index, val);
	}

	public Object getStaticFieldOrNoop(Object o, String property) {
		int index = getStaticFieldIndex(property);
		if (index == -1) {
			return null;
		}
		return getIndexStaticField(o, index);
	}

	// endregion


	public static <T> BeanAccess<T> get(Class<T> type) {
		return pool.computeIfAbsent(type, BeanAccess::create);
	}

	public static <T> BeanAccess<T> create(Class<T> type) {
		Map<String, BeanPropertyInfo> propertyInfoMap = BeanPropertyInfo.mapOf(type);
		List<BeanPropertyInfo> setters = new ArrayList<>();
		List<BeanPropertyInfo> getters = new ArrayList<>();
		List<BeanPropertyInfo> fields = new ArrayList<>();
		List<BeanPropertyInfo> staticFields = new ArrayList<>();

		for (Map.Entry<String, BeanPropertyInfo> entry : propertyInfoMap.entrySet()) {
			BeanPropertyInfo beanPropertyInfo = entry.getValue();
			if (beanPropertyInfo.getWriteMethod() != null) {
				setters.add(beanPropertyInfo);
			}
			if (beanPropertyInfo.getReadMethod() != null) {
				getters.add(beanPropertyInfo);
			}
			if (beanPropertyInfo.getField() != null) {
				if (Modifier.isStatic(beanPropertyInfo.getField().getModifiers())) {
					staticFields.add(beanPropertyInfo);
				} else {
					fields.add(beanPropertyInfo);
				}
			}
		}

		String accessClassName = AccessClassLoader.buildAccessClassName(type, BeanAccess.class);
		Class accessClass;
		AccessClassLoader loader = AccessClassLoader.get(type);
		synchronized (loader) {
			accessClass = loader.loadAccessClass(accessClassName);
			if (accessClass == null) {
				accessClass = buildAccessClass(loader, accessClassName, type, setters, getters, fields, staticFields);
			}
		}
		BeanAccess<T> access;
		try {
			access = (BeanAccess<T>) accessClass.newInstance();
			access.properties = propertyInfoMap;
			{
				Map<String, Integer> setterIndices = new HashMap<>();
				for (int i = 0; i < setters.size(); i++) {
					setterIndices.put(setters.get(i).getPropertyName(), i);
				}
				access.setterIndices = setterIndices;
			}
			{
				Map<String, Integer> getterIndices = new HashMap<>();
				for (int i = 0; i < getters.size(); i++) {
					getterIndices.put(getters.get(i).getPropertyName(), i);
				}
				access.getterIndices = getterIndices;
			}
			{
				Map<String, Integer> fieldIndices = new HashMap<>();
				for (int i = 0; i < fields.size(); i++) {
					fieldIndices.put(fields.get(i).getPropertyName(), i);
				}
				access.fieldIndices = fieldIndices;
			}
			{
				Map<String, Integer> staticFieldIndices = new HashMap<>();
				for (int i = 0; i < staticFields.size(); i++) {
					staticFieldIndices.put(staticFields.get(i).getPropertyName(), i);
				}
				access.staticFieldIndices = staticFieldIndices;
			}

		} catch (Throwable t) {
			throw new IllegalStateException("实例化构造器访问类失败: " + accessClassName, t);
		}
		return access;
	}

	private static <T> Class buildAccessClass(AccessClassLoader loader
		, String accessClassName, Class<T> type
		, List<BeanPropertyInfo> setters, List<BeanPropertyInfo> getters
		, List<BeanPropertyInfo> fields, List<BeanPropertyInfo> staticFields) {
		String accessClassNameInternal = accessClassName.replace('.', '/');
		String classNameInternal = type.getName().replace('.', '/');

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		String superclassNameInternal = BeanAccess.class.getName().replace('.', '/');
		cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal,
			"L" + superclassNameInternal + "<L" + accessClassNameInternal + ";>;",
			superclassNameInternal, null);
		cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

		insertSelfConstructor(cw, superclassNameInternal);
		insertSetterInvokers(cw, accessClassNameInternal, type, setters);
		insertGetterInvokers(cw, accessClassNameInternal, type, getters);
		insertFieldInvokers(cw, accessClassNameInternal, type, fields);
		insertStaticFieldInvokers(cw, accessClassNameInternal, type, staticFields);

		cw.visitEnd();
		byte[] byteArray = cw.toByteArray();

		return loader.defineAccessClass(accessClassName, byteArray);
	}

	private static void insertSelfConstructor(ClassWriter cw, String superclassNameInternal) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, superclassNameInternal, "<init>", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	private static <T> void insertSetterInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type, List<BeanPropertyInfo> setters) {
		// ignore
		if (setters.isEmpty()) {
			return;
		}

		SerializableQuaternionConsumer<BeanAccess, Object, Integer, Object> setIndexProperty = BeanAccess::setIndexProperty;
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
					methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
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
							methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
							methodVisitor.visitVarInsn(ASTORE, 4);
							methodVisitor.visitTypeInsn(NEW, Type.getInternalName(InvocationException.class));
							methodVisitor.visitInsn(DUP);
							methodVisitor.visitVarInsn(ALOAD, 4);
							methodVisitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(InvocationException.class), "<init>", "(Ljava/lang/Throwable;)V", false);
							methodVisitor.visitInsn(ATHROW);
						}

					}
				}
				// default
				methodVisitor.visitLabel(labelDefault);
				methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
				methodVisitor.visitInsn(DUP);
				methodVisitor.visitLdcInsn("Method not found");
				methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
				methodVisitor.visitInsn(ATHROW);

				// break
				methodVisitor.visitLabel(labelBreak);
				methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
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
		SerializableTernaryFunction<BeanAccess, Object, Integer, Object> getIndexProperty = BeanAccess::getIndexProperty;

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
					methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
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
							methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
							methodVisitor.visitVarInsn(ASTORE, 3);
							methodVisitor.visitTypeInsn(NEW, Type.getInternalName(InvocationException.class));
							methodVisitor.visitInsn(DUP);
							methodVisitor.visitVarInsn(ALOAD, 3);
							methodVisitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(InvocationException.class), "<init>", "(Ljava/lang/Throwable;)V", false);
							methodVisitor.visitInsn(ATHROW);
						}
					}
				}
				// default
				methodVisitor.visitLabel(labelDefault);
				methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
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
		SerializableTernaryFunction<BeanAccess, Object, Integer, Object> getIndexField = BeanAccess::getIndexField;
		SerializableQuaternionConsumer<BeanAccess, Object, Integer, Object> setIndexField = BeanAccess::setIndexField;
		insertStdFieldInvoker(cw, type, fields, getIndexField, setIndexField);
	}

	private static <T> void insertStaticFieldInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type, List<BeanPropertyInfo> staticFields) {
		// ignore
		if (staticFields.isEmpty()) {
			return;
		}
		SerializableQuaternionConsumer<BeanAccess, Object, Integer, Object> setIndexStaticField = BeanAccess::setIndexStaticField;
		SerializableTernaryFunction<BeanAccess, Object, Integer, Object> getIndexStaticField = BeanAccess::getIndexStaticField;
		insertStdFieldInvoker(cw, type, staticFields, getIndexStaticField, setIndexStaticField);
	}

	private static <T> void insertStdFieldInvoker(ClassWriter cw, Class<T> type, List<BeanPropertyInfo> fields, SerializableTernaryFunction<BeanAccess, Object, Integer, Object> getter, SerializableQuaternionConsumer<BeanAccess, Object, Integer, Object> setter) {
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
				methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

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
			methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
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
				methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

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
			methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
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
