package io.polaris.core.asm.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import io.polaris.core.asm.AsmUtils;
import io.polaris.core.err.InvocationException;
import io.polaris.core.lang.Types;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.reflect.SerializableFiveElementFunction;
import io.polaris.core.reflect.SerializableFunction;
import io.polaris.core.reflect.SerializableQuaternionConsumer;
import io.polaris.core.reflect.SerializableTernaryFunction;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_VARARGS;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.BIPUSH;
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
 * @since 1.8,  Aug 05, 2023
 */
@SuppressWarnings("all")
public abstract class ClassAccess<T> {
	private static final AccessPool<Class, ClassAccess> pool = new AccessPool<>();
	private static ILogger log = ILoggers.of(ClassAccess.class);
	private final int defaultConstructorIndex;
	private final Class[][] constructorParamTypes;

	private final String[] methodNames;
	private final Class[][][] methodParamTypes;
	private final String[] fieldNames;
	private final Class[] fieldTypes;
	private final Map<String, Integer> methodIndices;
	private final Map<String, Integer> fieldIndices;

	protected ClassAccess() {
		this.constructorParamTypes = this.buildConstructorParamTypes();
		int defaultConstructorIndex = -1;
		for (int i = 0; i < constructorParamTypes.length; i++) {
			if (constructorParamTypes[i].length == 0) {
				defaultConstructorIndex = i;
				break;
			}
		}
		this.defaultConstructorIndex = defaultConstructorIndex;

		this.methodNames = this.buildMethodNames();
		this.methodParamTypes = this.buildMethodParamTypes();
		this.fieldNames = this.buildFieldNames();
		this.fieldTypes = this.buildFieldTypes();
		this.methodIndices = new HashMap<>(2 * this.methodNames.length);
		this.fieldIndices = new HashMap<>(2 * this.fieldNames.length);
		for (int i = 0; i < this.methodNames.length; i++) {
			this.methodIndices.put(this.methodNames[i], i);
		}
		for (int i = 0; i < this.fieldNames.length; i++) {
			this.fieldIndices.put(this.fieldNames[i], i);
		}
	}


	// region expect to overwrite

	protected Class[][] buildConstructorParamTypes() {
		return new Class[0][0];
	}

	protected String[] buildMethodNames() {
		return new String[0];
	}

	protected Class[][][] buildMethodParamTypes() {
		return new Class[0][][];
	}

	protected Class[] buildMethodReturnTypes() {
		return new Class[0];
	}

	protected String[] buildFieldNames() {
		return new String[0];
	}

	protected Class[] buildFieldTypes() {
		return new Class[0];
	}

	protected Object newIndexInstance(int index, Object... args) {
		throw new IllegalArgumentException("Constructor not found");
	}

	protected Object invokeIndexMethod(Object instance, int index, int overloadIndex, Object... args) {
		throw new IllegalArgumentException("Method not found");
	}

	protected Object getIndexField(Object instance, int index) {
		throw new IllegalArgumentException("Field not found");
	}

	protected void setIndexField(Object instance, int index, Object value) {
		throw new IllegalArgumentException("Field not found");
	}

	// endregion


	// region constructor access

	public boolean containsDefaultConstructor() {
		return defaultConstructorIndex >= 0;
	}

	public boolean containsConstructor(Class... types) {
		return getConstructorIndex(types) >= 0;
	}

	public T newInstance() {
		if (defaultConstructorIndex >= 0) {
			try {
				return (T) newIndexInstance(defaultConstructorIndex, new Object[0]);
			} catch (Throwable e) {
				throw InvocationException.of(e);
			}
		}
		throw new IllegalArgumentException("Constructor not found");
	}

	public T newInstance(Class<?>[] types, Object... args) {
		int index = getConstructorIndex(types);
		if (index < 0) {
			throw new IllegalArgumentException("Constructor not found");
		}
		try {
			return (T) newIndexInstance(index, args);
		} catch (Throwable e) {
			throw InvocationException.of(e);
		}
	}

	public T newInstance(Object... args) {
		int index = getConstructorIndex(args);
		if (index < 0) {
			throw new IllegalArgumentException("Constructor not found");
		}
		try {
			return (T) newIndexInstance(index, args);
		} catch (Throwable e) {
			throw InvocationException.of(e);
		}
	}

	public T newInstanceOrNoop() {
		if (defaultConstructorIndex >= 0) {
			try {
				return (T) newIndexInstance(defaultConstructorIndex, new Object[0]);
			} catch (Throwable e) {
				throw InvocationException.of(e);
			}
		}
		return null;
	}

	public T newInstanceOrNoop(Class<?>[] types, Object... args) {
		int index = getConstructorIndex(types);
		if (index < 0) {
			return null;
		}
		try {
			return (T) newIndexInstance(index, args);
		} catch (Throwable e) {
			throw InvocationException.of(e);
		}
	}

	public T newInstanceOrNoop(Object... args) {
		int index = getConstructorIndex(args);
		if (index < 0) {
			return null;
		}
		try {
			return (T) newIndexInstance(index, args);
		} catch (Throwable e) {
			throw InvocationException.of(e);
		}
	}

	private int getConstructorIndex(Class<?>... types) {
		if (types == null || types.length == 0) {
			return defaultConstructorIndex;
		}
		for (int i = 0; i < constructorParamTypes.length; i++) {
			Class[] constructorParamType = constructorParamTypes[i];
			// 完全匹配
			if (Types.isEquals(constructorParamType, types)) {
				return i;
			}
		}
		for (int i = 0; i < constructorParamTypes.length; i++) {
			Class[] constructorParamType = constructorParamTypes[i];
			// 匹配子类型
			if (Types.isAssignable(constructorParamType, types)) {
				return i;
			}
		}
		return -1;
	}

	private int getConstructorIndex(Object... args) {
		if (args.length == 0) {
			return defaultConstructorIndex;
		}
		for (int i = 0; i < constructorParamTypes.length; i++) {
			Class[] constructorParamType = constructorParamTypes[i];
			if (args.length == constructorParamType.length) {
				boolean matched = true;
				for (int j = 0; j < args.length; j++) {
					if (args[j] == null) {
						continue;
					}
					if (constructorParamType[j].isPrimitive()) {
						if (!constructorParamType[j].isInstance(args[j]) && !Types.getWrapperClass(constructorParamType[j]).isInstance(args[j])) {
							matched = false;
							break;
						}
					} else {
						if (!constructorParamType[j].isInstance(args[j])) {
							matched = false;
							break;
						}
					}
				}
				if (matched) {
					return i;
				}
			}
		}
		throw new IllegalArgumentException("Constructor not found");
	}


	// endregion

	// region method access

	public Set<String> getMethodNames() {
		return new LinkedHashSet<>(methodIndices.keySet());
	}

	public boolean containsMethod(String methodName) {
		return getMethodIndex(methodName) >= 0;
	}

	public boolean containsMethod(String methodName, Class... paramTypes) {
		int methodIndex = getMethodIndex(methodName);
		int methodOverloadIndex = getMethodOverloadIndex(methodIndex, paramTypes);
		if (methodIndex < 0 || methodOverloadIndex < 0) {
			return false;
		}
		return true;
	}

	private int getMethodIndex(String methodName, Class... paramTypes) {
		Integer idx = this.methodIndices.get(methodName);
		if (idx != null) {
			return idx.intValue();
		}
//		int idx = Arrays.binarySearch(methodNames, methodName, Comparator.naturalOrder());
//		if (idx >= 0) {
//			return idx;
//		}
		return -1;
	}

	private int getMethodOverloadIndex(int methodIdx, Class... paramTypes) {
		if (methodIdx >= 0) {
			Class[][] definedTypesArray = this.methodParamTypes[methodIdx];
			for (int i = 0; i < definedTypesArray.length; i++) {
				Class[] definedTypes = definedTypesArray[i];
				if (Types.isEquals(definedTypes, paramTypes)) {
					return i;
				}
			}
			for (int i = 0; i < definedTypesArray.length; i++) {
				Class[] definedTypes = definedTypesArray[i];
				if (Types.isAssignable(definedTypes, paramTypes)) {
					return i;
				}
			}
		}
		return -1;
	}

	private int getMethodOverloadIndex(int methodIdx, Object... args) {
		if (methodIdx >= 0) {
			Class[][] definedTypesArray = this.methodParamTypes[methodIdx];
			for (int i = 0; i < definedTypesArray.length; i++) {
				Class[] definedTypes = definedTypesArray[i];
				if (args.length != definedTypes.length) {
					continue;
				}
				boolean matched = true;
				for (int j = 0; j < args.length; j++) {
					if (args[j] == null) {
						continue;
					}
					if (definedTypes[j].isPrimitive()) {
						if (!definedTypes[j].isInstance(args[j]) && !Types.getWrapperClass(definedTypes[j]).isInstance(args[j])) {
							matched = false;
							break;
						}
					} else {
						if (!definedTypes[j].isInstance(args[j])) {
							matched = false;
							break;
						}
					}
				}
				if (matched) {
					return i;
				}
			}
		}
		return -1;
	}

	public Object invokeMethod(Object object, String methodName, Class[] paramTypes, Object... args) {
		int methodIndex = getMethodIndex(methodName);
		int methodOverloadIndex = getMethodOverloadIndex(methodIndex, paramTypes);
		if (methodIndex < 0 || methodOverloadIndex < 0) {
			throw new IllegalArgumentException("Method not found：" + methodName + " " + Arrays.toString(paramTypes));
		}
		try {
			return this.invokeIndexMethod(object, methodIndex, methodOverloadIndex, args);
		} catch (Throwable e) {
			throw InvocationException.of(e);
		}
	}

	public Object invokeMethod(Object object, String methodName, Object... args) {
		int methodIndex = getMethodIndex(methodName);
		int methodOverloadIndex = getMethodOverloadIndex(methodIndex, args);
		if (methodIndex < 0 || methodOverloadIndex < 0) {
			throw new IllegalArgumentException("Method not found：" + methodName);
		}
		try {
			return this.invokeIndexMethod(object, methodIndex, methodOverloadIndex, args);
		} catch (Throwable e) {
			throw InvocationException.of(e);
		}
	}

	public Object invokeMethodOrNoop(Object object, String methodName, Class[] paramTypes, Object... args) {
		int methodIndex = getMethodIndex(methodName);
		int methodOverloadIndex = getMethodOverloadIndex(methodIndex, paramTypes);
		if (methodIndex < 0 || methodOverloadIndex < 0) {
			return null;
		}
		try {
			return this.invokeIndexMethod(object, methodIndex, methodOverloadIndex, args);
		} catch (Throwable e) {
			throw InvocationException.of(e);
		}
	}

	public Object invokeMethodOrNoop(Object object, String methodName, Object... args) {
		int methodIndex = getMethodIndex(methodName);
		int methodOverloadIndex = getMethodOverloadIndex(methodIndex, args);
		if (methodIndex < 0 || methodOverloadIndex < 0) {
			return null;
		}
		try {
			return this.invokeIndexMethod(object, methodIndex, methodOverloadIndex, args);
		} catch (Throwable e) {
			throw InvocationException.of(e);
		}
	}

	// endregion

	// region field access

	public Set<String> getFieldNames() {
		return new LinkedHashSet<>(fieldIndices.keySet());
	}

	public boolean containsField(String fieldName) {
		return getFieldIndex(fieldName) >= 0;
	}

	private int getFieldIndex(String fieldName) {
		Integer idx = this.fieldIndices.get(fieldName);
		if (idx != null) {
			return idx.intValue();
		}
//		int idx = Arrays.binarySearch(this.fieldNames, fieldName, Comparator.naturalOrder());
//		if (idx >= 0) {
//			return idx;
//		}
		return -1;
	}

	public Class getFieldType(String fieldName) {
		int i = getFieldIndex(fieldName);
		if (i < 0) {
			throw new IllegalArgumentException("Field not found：" + fieldName);
		}
		return this.fieldTypes[i];
	}

	public void setField(Object object, String fieldName, Object value) {
		int i = getFieldIndex(fieldName);
		if (i < 0) {
			throw new IllegalArgumentException("Field not found：" + fieldName);
		}
		this.setIndexField(object, i, value);
	}

	public Object getField(Object object, String fieldName) {
		int i = getFieldIndex(fieldName);
		if (i < 0) {
			throw new IllegalArgumentException("Field not found：" + fieldName);
		}
		return this.getIndexField(object, i);
	}

	public void setFieldOrNoop(Object object, String fieldName, Object value) {
		int i = getFieldIndex(fieldName);
		if (i < 0) {
			return;
		}
		this.setIndexField(object, i, value);
	}

	public Object getFieldOrNoop(Object object, String fieldName) {
		int i = getFieldIndex(fieldName);
		if (i < 0) {
			return null;
		}
		return this.getIndexField(object, i);
	}

	// endregion

	public static <T> ClassAccess<T> get(Class<T> type) {
		return pool.computeIfAbsent(type, ClassAccess::create);
	}

	public static <T> ClassAccess<T> create(Class<T> type) {
		String accessClassName = AccessClassLoader.buildAccessClassName(type, ClassAccess.class);
		Class accessClass;
		AccessClassLoader loader = AccessClassLoader.get(type);
		synchronized (loader) {
			accessClass = loader.loadAccessClass(accessClassName);
			if (accessClass == null) {
				accessClass = buildAccessClass(loader, accessClassName, type);
			}
		}
		ClassAccess<T> access;
		try {
			access = (ClassAccess<T>) accessClass.newInstance();
		} catch (Throwable t) {
			throw new IllegalStateException("实例化构造器访问类失败: " + accessClassName, t);
		}
		return access;
	}

	private static <T> Class buildAccessClass(AccessClassLoader loader
		, String accessClassName, Class<T> type) {
		String accessClassNameInternal = accessClassName.replace('.', '/');
		String classNameInternal = type.getName().replace('.', '/');

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		String superclassNameInternal = ClassAccess.class.getName().replace('.', '/');
		cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal,
			"L" + superclassNameInternal + "<L" + accessClassNameInternal + ";>;",
			superclassNameInternal, null);
		cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

		insertSelfConstructor(cw, superclassNameInternal);
		insertConstructorInvokers(cw, accessClassNameInternal, type);
		insertMethodInvokers(cw, accessClassNameInternal, type);
		insertFieldInvokers(cw, accessClassNameInternal, type);

		cw.visitEnd();
		byte[] byteArray = cw.toByteArray();

		return loader.defineAccessClass(accessClassName, byteArray);
	}


	private static void insertSelfConstructor(ClassWriter cw, String superclassNameInternal) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, superclassNameInternal, "<init>", "()V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	private static <T> void insertConstructorInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type) {
		List<Constructor<T>> constructorList = new ArrayList<>();
		for (Constructor<?> declaredConstructor : type.getDeclaredConstructors()) {
			if (Modifier.isPrivate(declaredConstructor.getModifiers())) {
				// 忽略私有
				continue;
			}
			constructorList.add((Constructor<T>) declaredConstructor);
		}
		int size = constructorList.size();
		SerializableFunction<ClassAccess, Class[][]> buildConstructorParamTypes = ClassAccess::buildConstructorParamTypes;
		SerializableTernaryFunction<ClassAccess, Integer, Object[], Object> newIndexInstance = ClassAccess::newIndexInstance;

		// 生成各构造器的参数类型列表
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, buildConstructorParamTypes.serialized().getImplMethodName(), "()[[Ljava/lang/Class;", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitLdcInsn(Integer.valueOf(size));
			methodVisitor.visitTypeInsn(ANEWARRAY, "[Ljava/lang/Class;");
			methodVisitor.visitVarInsn(ASTORE, 1);

			for (int idx = 0; idx < size; idx++) {
				Constructor<T> constructor = constructorList.get(idx);
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				int len = parameterTypes.length;
				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitIntInsn(BIPUSH, idx);
				methodVisitor.visitIntInsn(BIPUSH, len);
				methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Class");
				for (int idxParam = 0; idxParam < len; idxParam++) {
					Class<?> parameterType = parameterTypes[idxParam];
					methodVisitor.visitInsn(DUP);
					methodVisitor.visitIntInsn(BIPUSH, idxParam);
					if (parameterType.isPrimitive()) {
						methodVisitor.visitFieldInsn(GETSTATIC, Type.getInternalName(Types.getWrapperClass(parameterType)), "TYPE", "Ljava/lang/Class;");
					} else {
						methodVisitor.visitLdcInsn(Type.getType(parameterType));
					}
					methodVisitor.visitInsn(AASTORE);
				}
				methodVisitor.visitInsn(AASTORE);
			}

			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitInsn(ARETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}

		// 生成各构造器的索引式调用方法
		if (size > 0) {
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED | ACC_VARARGS, newIndexInstance.serialized().getImplMethodName(), "(I[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ILOAD, 1);

			Label[] labels = new Label[size];
			for (int idx = 0; idx < size; idx++) {
				labels[idx] = new Label();
			}
			Label labelDefault = new Label();
			methodVisitor.visitTableSwitchInsn(0, size - 1, labelDefault, labels);

			for (int idx = 0; idx < size; idx++) {
				Constructor<T> constructor = constructorList.get(idx);
				boolean hasThrows = constructor.getExceptionTypes().length > 0;
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				int len = parameterTypes.length;

				methodVisitor.visitLabel(labels[idx]);
				methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

				MethodVisitor mv = methodVisitor;
				{
					Label labelStart = new Label();
					Label labelEnd = new Label();
					Label labelCatch = new Label();
					if (hasThrows) {
						mv.visitTryCatchBlock(labelStart, labelEnd, labelCatch, "java/lang/Throwable");
						mv.visitLabel(labelStart);
					}

					mv.visitTypeInsn(NEW, Type.getInternalName(type));
					mv.visitInsn(DUP);
					for (int iParam = 0; iParam < len; iParam++) {
						Class<?> parameterType = parameterTypes[iParam];
						mv.visitVarInsn(ALOAD, 2);
						mv.visitIntInsn(BIPUSH, iParam);
						mv.visitInsn(AALOAD);
						Type paramType = Type.getType(parameterType);
						AsmUtils.autoUnBoxing(mv, paramType);
					}
					String constructorDescriptor = Type.getConstructorDescriptor(constructor);
					mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(type), "<init>",
						constructorDescriptor, false);
					if (hasThrows) {
						mv.visitLabel(labelEnd);
					}
					methodVisitor.visitInsn(ARETURN);
					if (hasThrows) {
						mv.visitLabel(labelCatch);
						mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
						mv.visitVarInsn(ASTORE, 2);
						mv.visitTypeInsn(NEW, Type.getInternalName(InvocationException.class));
						mv.visitInsn(DUP);
						mv.visitVarInsn(ALOAD, 2);
						mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(InvocationException.class), "<init>", "(Ljava/lang/Throwable;)V", false);
						mv.visitInsn(ATHROW);
					}
				}

			}
			methodVisitor.visitLabel(labelDefault);
			methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitLdcInsn("Constructor not found");
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
			methodVisitor.visitInsn(ATHROW);

			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}

	}


	private static <T> void insertMethodInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type) {
		SerializableFiveElementFunction<ClassAccess, Object, Integer, Integer, Object[], Object> invokeIndexMethod = ClassAccess::invokeIndexMethod;
		SerializableFunction<ClassAccess, String[]> buildMethodNames = ClassAccess::buildMethodNames;
		SerializableFunction<ClassAccess, Class[][][]> buildMethodParamTypes = ClassAccess::buildMethodParamTypes;

		Map<String, List<Method>> declaredMethods = getDeclaredMethods(type);
		Map.Entry<String, List<Method>>[] methodEntryArray = declaredMethods.entrySet().toArray(new Map.Entry[0]);

		// buildMethodNames
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, buildMethodNames.serialized().getImplMethodName(), "()[Ljava/lang/String;", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitLdcInsn(Integer.valueOf(methodEntryArray.length));
			methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/String");
			methodVisitor.visitVarInsn(ASTORE, 1);

			for (int iName = 0; iName < methodEntryArray.length; iName++) {
				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitLdcInsn(Integer.valueOf(iName));
				methodVisitor.visitLdcInsn(methodEntryArray[iName].getKey());
				methodVisitor.visitInsn(AASTORE);
			}

			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitInsn(ARETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}
		// buildMethodParamTypes
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, buildMethodParamTypes.serialized().getImplMethodName(), "()[[[Ljava/lang/Class;", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitLdcInsn(Integer.valueOf(methodEntryArray.length));
			methodVisitor.visitTypeInsn(ANEWARRAY, "[[Ljava/lang/Class;");
			methodVisitor.visitVarInsn(ASTORE, 1);

			for (int iName = 0; iName < methodEntryArray.length; iName++) {
				Map.Entry<String, List<Method>> entry = methodEntryArray[iName];
				List<Method> list = entry.getValue();
				int count = list.size();
				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitLdcInsn(Integer.valueOf(iName));
				methodVisitor.visitLdcInsn(Integer.valueOf(count));
				methodVisitor.visitTypeInsn(ANEWARRAY, "[Ljava/lang/Class;");
				methodVisitor.visitInsn(AASTORE);
				for (int iOverload = 0; iOverload < count; iOverload++) {
					Method method = list.get(iOverload);
					Class<?>[] parameterTypes = method.getParameterTypes();
					methodVisitor.visitVarInsn(ALOAD, 1);
					methodVisitor.visitLdcInsn(Integer.valueOf(iName));
					methodVisitor.visitInsn(AALOAD);
					methodVisitor.visitLdcInsn(Integer.valueOf(iOverload));
					methodVisitor.visitLdcInsn(Integer.valueOf(parameterTypes.length));
					methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Class");
					methodVisitor.visitInsn(AASTORE);
					for (int idxParams = 0; idxParams < parameterTypes.length; idxParams++) {
						Class<?> parameterType = parameterTypes[idxParams];
						methodVisitor.visitVarInsn(ALOAD, 1);
						methodVisitor.visitLdcInsn(Integer.valueOf(iName));
						methodVisitor.visitInsn(AALOAD);
						methodVisitor.visitLdcInsn(Integer.valueOf(iOverload));
						methodVisitor.visitInsn(AALOAD);
						methodVisitor.visitLdcInsn(Integer.valueOf(idxParams));
						if (parameterType.isPrimitive()) {
							methodVisitor.visitFieldInsn(GETSTATIC, Type.getInternalName(Types.getWrapperClass(parameterType)), "TYPE", "Ljava/lang/Class;");
						} else {
							methodVisitor.visitLdcInsn(Type.getType(parameterType));
						}
						methodVisitor.visitInsn(AASTORE);
					}
				}
			}

			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitInsn(ARETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}

		// 生成各方法的索引式调用方法
		if (methodEntryArray.length > 0) {
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED | ACC_VARARGS, invokeIndexMethod.serialized().getImplMethodName(), "(Ljava/lang/Object;II[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
			methodVisitor.visitCode();
			// switch 方法名分支
			{
				methodVisitor.visitVarInsn(ILOAD, 2);

				Label[] labels = AsmUtils.newLabels(methodEntryArray.length);
				Label labelDefault = new Label();
				methodVisitor.visitTableSwitchInsn(0, methodEntryArray.length - 1, labelDefault, labels);
				// case
				for (int idxName = 0; idxName < methodEntryArray.length; idxName++) {
					methodVisitor.visitLabel(labels[idxName]);
					methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

					Map.Entry<String, List<Method>> entry = methodEntryArray[idxName];
					String methodName = entry.getKey();
					List<Method> list = entry.getValue();
					int count = list.size();
					if (count == 0) {
						methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
						methodVisitor.visitInsn(DUP);
						methodVisitor.visitLdcInsn("Method not found");
						methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
						methodVisitor.visitInsn(ATHROW);
					} else {
						// switch 重载参数分支
						{
							Label[] labelsInner = AsmUtils.newLabels(count);
							Label labelInnerDefault = new Label();
							if (count > 1) {
								methodVisitor.visitVarInsn(ILOAD, 3);
								methodVisitor.visitTableSwitchInsn(0, count - 1, labelInnerDefault, labelsInner);
							}
							for (int idxOverload = 0; idxOverload < count; idxOverload++) {
								if (count > 1) {
									methodVisitor.visitLabel(labelsInner[idxOverload]);
									methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
								}
								Method method = list.get(idxOverload);
								boolean hasThrows = method.getExceptionTypes().length > 0;
								Class<?>[] parameterTypes = method.getParameterTypes();
								int paramCount = parameterTypes.length;

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
									// param convert
									for (int idxParams = 0; idxParams < paramCount; idxParams++) {
										Class<?> parameterType = parameterTypes[idxParams];
										methodVisitor.visitVarInsn(ALOAD, 4);
										methodVisitor.visitIntInsn(BIPUSH, idxParams);
										methodVisitor.visitInsn(AALOAD);
										Type paramType = Type.getType(parameterType);
										AsmUtils.autoUnBoxing(methodVisitor, paramType);
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
										methodVisitor.visitVarInsn(ASTORE, 5);
										methodVisitor.visitTypeInsn(NEW, Type.getInternalName(InvocationException.class));
										methodVisitor.visitInsn(DUP);
										methodVisitor.visitVarInsn(ALOAD, 5);
										methodVisitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(InvocationException.class), "<init>", "(Ljava/lang/Throwable;)V", false);
										methodVisitor.visitInsn(ATHROW);
									}
								}
							}
							if (count > 1) {
								methodVisitor.visitLabel(labelInnerDefault);
								methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
								methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
								methodVisitor.visitInsn(DUP);
								methodVisitor.visitLdcInsn("Method not found");
								methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
								methodVisitor.visitInsn(ATHROW);
							}
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

	private static Map<String, List<Method>> getDeclaredMethods(Class type) {
		Map<String, Method> allMethods = new HashMap<>();
		boolean isInterface = type.isInterface();
		if (!isInterface) {
			Class nextClass = type;
			while (nextClass != null) { // 也包含Object类的方法
				recursiveAddMethodsToMap(nextClass, allMethods);
				nextClass = nextClass.getSuperclass();
			}
		} else {
			recursiveAddMethodsToMap(type, allMethods);
		}
		Map<String, List<Method>> overloadMethods = new TreeMap<>(Comparator.naturalOrder());
		allMethods.forEach((k, v) -> {
			List<Method> list = overloadMethods.computeIfAbsent(v.getName(), name -> new ArrayList<>());
			list.add(v);
		});
		return overloadMethods;
	}

	private static void recursiveAddMethodsToMap(Class interfaceType, Map<String, Method> methods) {
		Method[] declaredMethods = interfaceType.getDeclaredMethods();
		for (int i = 0, n = declaredMethods.length; i < n; i++) {
			Method method = declaredMethods[i];
			int modifiers = method.getModifiers();
			if (Modifier.isPrivate(modifiers)) {
				continue;
			}
			// 忽略几下Object方法
			if (Reflects.isFinalizeMethod(method) ||
				Reflects.isNotifyMethod(method) || Reflects.isNotifyAllMethod(method) ||
				Reflects.isWaitMethod(method)) {
				continue;
			}
			// 忽略非覆写的clone方法
			if (Reflects.isCloneMethod(method) && method.getDeclaringClass() == Object.class) {
				continue;
			}
			methods.putIfAbsent(method.getName() + Type.getMethodDescriptor(method), method);
		}
		for (Class nextInterface : interfaceType.getInterfaces()) {
			recursiveAddMethodsToMap(nextInterface, methods);
		}
	}


	private static <T> void insertFieldInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type) {
		Map<String, Field> fields = new TreeMap<>(Comparator.naturalOrder());
		Class nextClass = type;
		while (nextClass != null && nextClass != Object.class) {
			Field[] declaredFields = nextClass.getDeclaredFields();
			for (int i = 0, n = declaredFields.length; i < n; i++) {
				Field field = declaredFields[i];
				int modifiers = field.getModifiers();
				if (Modifier.isPrivate(modifiers)) {
					continue;
				}
				fields.putIfAbsent(field.getName(), field);
			}
			nextClass = nextClass.getSuperclass();
		}

		SerializableFunction<ClassAccess, String[]> buildFieldNames = ClassAccess::buildFieldNames;
		SerializableFunction<ClassAccess, Class[]> buildFieldTypes = ClassAccess::buildFieldTypes;
		SerializableTernaryFunction<ClassAccess, Object, Integer, Object> getIndexField = ClassAccess::getIndexField;
		SerializableQuaternionConsumer<ClassAccess, Object, Integer, Object> setIndexField = ClassAccess::setIndexField;
		Map.Entry<String, Field>[] fieldEntryArray = fields.entrySet().toArray(new Map.Entry[0]);

		// buildFieldNames
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, buildFieldNames.serialized().getImplMethodName(), "()[Ljava/lang/String;", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitLdcInsn(Integer.valueOf(fieldEntryArray.length));
			methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/String");
			methodVisitor.visitVarInsn(ASTORE, 1);
			for (int idxField = 0; idxField < fieldEntryArray.length; idxField++) {
				Map.Entry<String, Field> entry = fieldEntryArray[idxField];
				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitLdcInsn(Integer.valueOf(idxField));
				methodVisitor.visitLdcInsn(entry.getKey());
				methodVisitor.visitInsn(AASTORE);
			}
			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitInsn(ARETURN);
			methodVisitor.visitMaxs(3, 2);
			methodVisitor.visitEnd();
		}
		// buildFieldTypes
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, buildFieldTypes.serialized().getImplMethodName(), "()[Ljava/lang/Class;", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitLdcInsn(Integer.valueOf(fieldEntryArray.length));
			methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Class");
			methodVisitor.visitVarInsn(ASTORE, 1);
			for (int idxField = 0; idxField < fieldEntryArray.length; idxField++) {
				Map.Entry<String, Field> entry = fieldEntryArray[idxField];
				Class<?> fieldType = entry.getValue().getType();
				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitLdcInsn(Integer.valueOf(idxField));
				if (fieldType.isPrimitive()) {
					methodVisitor.visitFieldInsn(GETSTATIC, Type.getInternalName(Types.getWrapperClass(fieldType)), "TYPE", "Ljava/lang/Class;");
				} else {
					methodVisitor.visitLdcInsn(Type.getType(fieldType));
				}
				methodVisitor.visitInsn(AASTORE);
			}
			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitInsn(ARETURN);
			methodVisitor.visitMaxs(3, 2);
			methodVisitor.visitEnd();
		}

		// getIndexField
		if (fieldEntryArray.length > 0) {
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, getIndexField.serialized().getImplMethodName(), "(Ljava/lang/Object;I)Ljava/lang/Object;", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ILOAD, 2);

			Label[] labels = new Label[fieldEntryArray.length];
			for (int i = 0; i < fieldEntryArray.length; i++) {
				labels[i] = new Label();
			}
			Label labelDefault = new Label();
			methodVisitor.visitTableSwitchInsn(0, fieldEntryArray.length - 1, labelDefault, labels);
			for (int idxField = 0; idxField < fieldEntryArray.length; idxField++) {
				methodVisitor.visitLabel(labels[idxField]);
				methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

				Map.Entry<String, Field> entry = fieldEntryArray[idxField];
				String fieldName = entry.getKey();
				Field field = entry.getValue();
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
		// setIndexField
		if (fieldEntryArray.length > 0) {
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, setIndexField.serialized().getImplMethodName(), "(Ljava/lang/Object;ILjava/lang/Object;)V", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ILOAD, 2);

			Label[] labels = new Label[fieldEntryArray.length];
			for (int i = 0; i < fieldEntryArray.length; i++) {
				labels[i] = new Label();
			}
			Label labelDefault = new Label();
			methodVisitor.visitTableSwitchInsn(0, fieldEntryArray.length - 1, labelDefault, labels);
			for (int idxField = 0; idxField < fieldEntryArray.length; idxField++) {
				methodVisitor.visitLabel(labels[idxField]);
				methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

				Map.Entry<String, Field> entry = fieldEntryArray[idxField];
				String fieldName = entry.getKey();
				Field field = entry.getValue();
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
