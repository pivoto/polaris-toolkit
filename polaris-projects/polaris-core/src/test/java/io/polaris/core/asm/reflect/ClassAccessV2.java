package io.polaris.core.asm.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.polaris.core.collection.Iterables;
import io.polaris.core.lang.Types;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.reflect.SerializableConsumerWithArgs4;
import io.polaris.core.reflect.SerializableFunction;
import io.polaris.core.reflect.SerializableFunctionWithArgs5;
import io.polaris.core.reflect.SerializableTriFunction;
import io.polaris.core.tuple.Tuple2;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Qt
 * @since 1.8,  Aug 05, 2023
 */
@SuppressWarnings("all")
public abstract class ClassAccessV2<T> {
	private static ILogger log = ILoggers.of(ClassAccessV2.class);
	private int defaultConstructorIndex = -1;
	private Class[][] constructorParamTypes;

	private String[] methodNames;
	private Class[][][] methodParamTypes;

	private String[] fieldNames;
	private Class[] fieldTypes;


	private Function<Object[], Object>[] constructors;
	private Map<String, Tuple2<Class[], BiFunction<Object, Object[], Object>>[]> methods;
	private Map<String, Tuple2<Function<Object, Object>, BiConsumer<Object, Object>>> fields;

	static {
		AccessClassLoader.registerBaseClass(ClassAccessV2.class);
	}

	protected ClassAccessV2() {
		this.constructorParamTypes = this.buildConstructorParamTypes();
		for (int i = 0; i < constructorParamTypes.length; i++) {
			if (constructorParamTypes[i].length == 0) {
				defaultConstructorIndex = i;
				break;
			}
		}

		this.methodNames = this.buildMethodNames();
		this.methodParamTypes = this.buildMethodParamTypes();
		this.fieldNames = this.buildFieldNames();
		this.fieldTypes = this.buildFieldTypes();

		this.constructors = this.buildConstructors();
		this.methods = this.buildMethods();
		this.fields = this.buildFields();
	}

	public Set<String> getMethodNames() {
		return Iterables.asSet(this.methodNames);
	}

	public Set<String> getFieldNames() {
		return Iterables.asSet(this.fieldNames);
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
		throw new IllegalArgumentException("找不到指定的非私有构造方法");
	}

	protected Object invokeIndexMethod(Object instance, int index, int overloadIndex, Object... args) {
		throw new IllegalArgumentException("找不到指定的非私有方法");
	}

	protected Object getIndexField(Object instance, int index) {
		throw new IllegalArgumentException("找不到指定的非私有成员");
	}

	protected void setIndexField(Object instance, int index, Object value) {
		throw new IllegalArgumentException("找不到指定的非私有成员");
	}

	protected Function<Object[], Object>[] buildConstructors() {
		return new Function[0];
	}

	protected Map<String, Tuple2<Function<Object, Object>, BiConsumer<Object, Object>>> buildFields() {
		return Collections.emptyMap();
	}

	protected Map<String, Tuple2<Class[], BiFunction<Object, Object[], Object>>[]> buildMethods() {
		return Collections.emptyMap();
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
				throw new IllegalArgumentException(e);
			}
		}
		throw new IllegalArgumentException("找不到指定的非私有构造方法");
	}

	public T newInstance(Class<?>[] types, Object... args) {
		int index = getConstructorIndex(types);
		if (index < 0) {
			throw new IllegalArgumentException("找不到指定的非私有构造方法");
		}
		try {
			return (T) newIndexInstance(index, args);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}

	public T newInstance(Object... args) {
		int index = getConstructorIndex(args);
		if (index < 0) {
			throw new IllegalArgumentException("找不到指定的非私有构造方法");
		}
		try {
			return (T) newIndexInstance(index, args);
			//return (T) constructors[index].apply(new Object[0]);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
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
		throw new IllegalArgumentException("找不到指定的非私有构造方法");
	}

	public Function<Object[], Object> getDefaultConstructor() {
		if (defaultConstructorIndex >= 0) {
			return constructors[defaultConstructorIndex];
		}
		return null;
	}

	public Function<Object[], Object> getConstructor(Class<?>... types) {
		int index = getConstructorIndex(types);
		if (index < 0) {
			return null;
		}
		return constructors[index];
	}

	public Function<Object[], Object> getConstructor(Object... args) {
		int index = getConstructorIndex(args);
		if (index < 0) {
			return null;
		}
		return constructors[index];
	}

	// endregion

	// region method access
	public boolean containsMethod(String methodName) {
		return methods.containsKey(methodName);
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
		int idx = Arrays.binarySearch(methodNames, methodName, Comparator.naturalOrder());
		if (idx >= 0) {
			return idx;
		}
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
			throw new IllegalArgumentException("找不到指定的非私有方法：" + methodName + " " + Arrays.toString(paramTypes));
		}
		try {
			return this.invokeIndexMethod(object, methodIndex, methodOverloadIndex, args);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}

	public Object invokeMethod(Object object, String methodName, Object... args) {
		int methodIndex = getMethodIndex(methodName);
		int methodOverloadIndex = getMethodOverloadIndex(methodIndex, args);
		if (methodIndex < 0 || methodOverloadIndex < 0) {
			throw new IllegalArgumentException("找不到指定的非私有方法：" + methodName);
		}
		try {
			return this.invokeIndexMethod(object, methodIndex, methodOverloadIndex, args);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}

	public BiFunction<Object, Object[], Object> getMethodInvoker(String methodName, Class... paramTypes) {
		Tuple2<Class[], BiFunction<Object, Object[], Object>>[] tuples = this.methods.get(methodName);
		if (tuples == null) {
			return null;
		}
		for (Tuple2<Class[], BiFunction<Object, Object[], Object>> tuple : tuples) {
			Class[] definedTypes = tuple.getFirst();
			if (Types.isEquals(definedTypes, paramTypes)) {
				return tuple.getSecond();
			}
		}
		for (Tuple2<Class[], BiFunction<Object, Object[], Object>> tuple : tuples) {
			Class[] definedTypes = tuple.getFirst();
			if (Types.isAssignable(definedTypes, paramTypes)) {
				return tuple.getSecond();
			}
		}
		return null;
	}

	public BiFunction<Object, Object[], Object> getMethodInvoker(String methodName, Object... args) {
		Tuple2<Class[], BiFunction<Object, Object[], Object>>[] tuples = this.methods.get(methodName);
		if (tuples == null) {
			return null;
		}
		for (Tuple2<Class[], BiFunction<Object, Object[], Object>> tuple : tuples) {
			Class[] definedTypes = tuple.getFirst();
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
				return tuple.getSecond();
			}
		}
		return null;
	}

	// endregion

	// region field access

	public boolean containsField(String fieldName) {
		return getFieldIndex(fieldName) >= 0;
	}

	private int getFieldIndex(String fieldName) {
		int idx = Arrays.binarySearch(this.fieldNames, fieldName, Comparator.naturalOrder());
		if (idx >= 0) {
			return idx;
		}
		return -1;
	}

	public Class getFieldType(String fieldName){
		int i = getFieldIndex(fieldName);
		if (i < 0) {
			throw new IllegalArgumentException("找不到指定的非私有字段：" + fieldName);
		}
		return this.fieldTypes[i];
	}

	public void setField(Object object, String fieldName, Object value) {
		int i = getFieldIndex(fieldName);
		if (i < 0) {
			throw new IllegalArgumentException("找不到指定的非私有字段：" + fieldName);
		}
		this.setIndexField(object, i, value);
	}

	public Object getField(Object object, String fieldName) {
		int i = getFieldIndex(fieldName);
		if (i < 0) {
			throw new IllegalArgumentException("找不到指定的非私有字段：" + fieldName);
		}
		return this.getIndexField(object, i);
	}

	public BiConsumer<Object, Object> getFieldSetter(String fieldName) {
		Tuple2<Function<Object, Object>, BiConsumer<Object, Object>> tuple = this.fields.get(fieldName);
		if (tuple == null) {
			throw new IllegalArgumentException("找不到指定的非私有字段：" + fieldName);
		}
		return tuple.getSecond();
	}

	public Function<Object, Object> getFieldGetter(String fieldName) {
		Tuple2<Function<Object, Object>, BiConsumer<Object, Object>> tuple = this.fields.get(fieldName);
		if (tuple == null) {
			throw new IllegalArgumentException("找不到指定的非私有字段：" + fieldName);
		}
		return tuple.getFirst();
	}

	// endregion

	public static <T> ClassAccessV2<T> get(Class<T> type) {
		String accessClassName = AccessClassLoader.buildAccessClassName(type, ClassAccessV2.class);
		Class accessClass;
		AccessClassLoader loader = AccessClassLoader.get(type);
		synchronized (loader) {
			accessClass = loader.loadAccessClass(accessClassName);
			if (accessClass == null) {
				accessClass = buildAccessClass(loader, accessClassName, type);
			}
		}
		ClassAccessV2<T> access;
		try {
			access = (ClassAccessV2<T>) accessClass.newInstance();
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
		String superclassNameInternal = ClassAccessV2.class.getName().replace('.', '/');
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
		SerializableFunction<ClassAccessV2, Class[][]> buildConstructorParamTypes = ClassAccessV2::buildConstructorParamTypes;
		SerializableTriFunction<ClassAccessV2, Integer, Object[], Object> newIndexInstance = ClassAccessV2::newIndexInstance;

		SerializableFunction<ClassAccessV2, Function<Object[], Object>[]> buildConstructors = ClassAccessV2::buildConstructors;
		final String lambdaPrefixOfConstructors = "lambda$" + buildConstructors.serialized().getImplMethodName() + "$";

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
		{
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
						switch (paramType.getSort()) {
							case Type.BOOLEAN:
								mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
								mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
								break;
							case Type.BYTE:
								mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
								mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
								break;
							case Type.CHAR:
								mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
								mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
								break;
							case Type.SHORT:
								mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
								mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
								break;
							case Type.INT:
								mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
								mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
								break;
							case Type.FLOAT:
								mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
								mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
								break;
							case Type.LONG:
								mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
								mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
								break;
							case Type.DOUBLE:
								mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
								mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
								break;
							case Type.ARRAY:
								mv.visitTypeInsn(CHECKCAST, paramType.getDescriptor());
								break;
							case Type.OBJECT:
								mv.visitTypeInsn(CHECKCAST, paramType.getInternalName());
								break;
						}
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
						mv.visitTypeInsn(NEW, Type.getInternalName(IllegalArgumentException.class));
						mv.visitInsn(DUP);
						mv.visitVarInsn(ALOAD, 2);
						mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(IllegalArgumentException.class), "<init>", "(Ljava/lang/Throwable;)V", false);
						mv.visitInsn(ATHROW);
					}
				}

			}
			methodVisitor.visitLabel(labelDefault);
			methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitLdcInsn("找不到指定的非私有构造方法");
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
			methodVisitor.visitInsn(ATHROW);

			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}

		// 生成各构造器的lambda方法的调用方法列表
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, buildConstructors.serialized().getImplMethodName(),
				"()[Ljava/util/function/Function;", "()[Ljava/util/function/Function<[Ljava/lang/Object;Ljava/lang/Object;>;", null);
			methodVisitor.visitCode();
			methodVisitor.visitLdcInsn(Integer.valueOf(size));
			methodVisitor.visitTypeInsn(ANEWARRAY, "java/util/function/Function");
			methodVisitor.visitVarInsn(ASTORE, 1);
			for (int idx = 0; idx < size; idx++) {
				Constructor<T> constructor = constructorList.get(idx);
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				int len = parameterTypes.length;

				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitIntInsn(BIPUSH, idx);
				methodVisitor.visitInvokeDynamicInsn("apply", "()Ljava/util/function/Function;"
					, new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory",
						"metafactory",
						"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false)
					, new Object[]{
						Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;")
						, new Handle(Opcodes.H_INVOKESTATIC, accessClassNameInternal, lambdaPrefixOfConstructors + idx, "([Ljava/lang/Object;)Ljava/lang/Object;", false)
						, Type.getType("([Ljava/lang/Object;)Ljava/lang/Object;")
					}
				);
				methodVisitor.visitInsn(AASTORE);
			}
			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitInsn(ARETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}

		// 生成各构造器的lambda方法
		{
			for (int idx = 0; idx < size; idx++) {
				Constructor<T> constructor = constructorList.get(idx);
				boolean hasThrows = constructor.getExceptionTypes().length > 0;

				MethodVisitor mv = cw.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC,
					lambdaPrefixOfConstructors + idx, "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
				mv.visitCode();
				Label label0 = new Label();
				Label label1 = new Label();
				Label label2 = new Label();
				if (hasThrows) {
					mv.visitTryCatchBlock(label0, label1, label2, "java/lang/Throwable");
					mv.visitLabel(label0);
				}
				mv.visitTypeInsn(NEW, Type.getInternalName(type));
				mv.visitInsn(DUP);

				Class<?>[] parameterTypes = constructor.getParameterTypes();
				int len = parameterTypes.length;
				for (int iParam = 0; iParam < len; iParam++) {
					Class<?> parameterType = parameterTypes[iParam];
					mv.visitVarInsn(ALOAD, 0);
					mv.visitIntInsn(BIPUSH, iParam);
					mv.visitInsn(AALOAD);
					Type paramType = Type.getType(parameterType);
					switch (paramType.getSort()) {
						case Type.BOOLEAN:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
							break;
						case Type.BYTE:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
							break;
						case Type.CHAR:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
							break;
						case Type.SHORT:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
							break;
						case Type.INT:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
							break;
						case Type.FLOAT:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
							break;
						case Type.LONG:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
							break;
						case Type.DOUBLE:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
							break;
						case Type.ARRAY:
							mv.visitTypeInsn(CHECKCAST, paramType.getDescriptor());
							break;
						case Type.OBJECT:
							mv.visitTypeInsn(CHECKCAST, paramType.getInternalName());
							break;
					}
				}
				String constructorDescriptor = Type.getConstructorDescriptor(constructor);
				mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(type), "<init>",
					constructorDescriptor, false);
				if (hasThrows) {
					mv.visitLabel(label1);
				}
				mv.visitInsn(ARETURN);
				if (hasThrows) {
					mv.visitLabel(label2);
					mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
					mv.visitVarInsn(ASTORE, 2);
					mv.visitTypeInsn(NEW, Type.getInternalName(IllegalArgumentException.class));
					mv.visitInsn(DUP);
					mv.visitVarInsn(ALOAD, 2);
					mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(IllegalArgumentException.class), "<init>", "(Ljava/lang/Throwable;)V", false);
					mv.visitInsn(ATHROW);
				}

				mv.visitMaxs(2, 1);
				mv.visitEnd();
			}
		}

	}


	private static <T> void insertMethodInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type) {
		SerializableFunctionWithArgs5<ClassAccessV2, Object, Integer, Integer, Object[], Object> invokeIndexMethod = ClassAccessV2::invokeIndexMethod;
		SerializableFunction<ClassAccessV2, String[]> buildMethodNames = ClassAccessV2::buildMethodNames;
		SerializableFunction<ClassAccessV2, Class[][][]> buildMethodParamTypes = ClassAccessV2::buildMethodParamTypes;

		SerializableFunction<ClassAccessV2, Map<String, Tuple2<Class[], BiFunction<Object, Object[], Object>>[]>> buildMethods = ClassAccessV2::buildMethods;

		Map<String, List<Method>> declaredMethods = getDeclaredMethods(type);
		Map.Entry<String, List<Method>>[] methodEntryArray = declaredMethods.entrySet().toArray(new Map.Entry[0]);

		final String lambdaPrefixOfMethods = "lambda$" + buildMethods.serialized().getImplMethodName() + "$";

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
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED | ACC_VARARGS, invokeIndexMethod.serialized().getImplMethodName(), "(Ljava/lang/Object;II[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
			methodVisitor.visitCode();
			// switch 方法名分支
			{
				methodVisitor.visitVarInsn(ILOAD, 2);

				Label[] labels = new Label[methodEntryArray.length];
				for (int iName = 0; iName < methodEntryArray.length; iName++) {
					labels[iName] = new Label();
				}
				Label labelDefault = new Label();
				methodVisitor.visitTableSwitchInsn(0, methodEntryArray.length - 1, labelDefault, labels);
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
						methodVisitor.visitLdcInsn("找不到指定的非私有方法");
						methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
						methodVisitor.visitInsn(ATHROW);
					} else {
						// switch 重载参数分支
						{
							Label[] labelsInner = new Label[count];
							if (count > 1) {
								for (int idxOverload = 0; idxOverload < count; idxOverload++) {
									labelsInner[idxOverload] = new Label();
								}
							}
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

								MethodVisitor mv = methodVisitor;
								// 方法调用
								{
									Label labelStart = new Label();
									Label labelEnd = new Label();
									Label labelCatch = new Label();
									if (hasThrows) {
										mv.visitTryCatchBlock(labelStart, labelEnd, labelCatch, "java/lang/Throwable");
										mv.visitLabel(labelStart);
									}
									// try
									methodVisitor.visitVarInsn(ALOAD, 1);
									mv.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
									if (Modifier.isStatic(method.getModifiers())) {
										mv.visitInsn(POP);
									}
									// param convert
									for (int idxParams = 0; idxParams < paramCount; idxParams++) {
										Class<?> parameterType = parameterTypes[idxParams];
										methodVisitor.visitVarInsn(ALOAD, 4);
										mv.visitIntInsn(BIPUSH, idxParams);
										mv.visitInsn(AALOAD);
										Type paramType = Type.getType(parameterType);
										switch (paramType.getSort()) {
											case Type.BOOLEAN:
												mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
												mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
												break;
											case Type.BYTE:
												mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
												mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
												break;
											case Type.CHAR:
												mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
												mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
												break;
											case Type.SHORT:
												mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
												mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
												break;
											case Type.INT:
												mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
												mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
												break;
											case Type.FLOAT:
												mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
												mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
												break;
											case Type.LONG:
												mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
												mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
												break;
											case Type.DOUBLE:
												mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
												mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
												break;
											case Type.ARRAY:
												mv.visitTypeInsn(CHECKCAST, paramType.getDescriptor());
												break;
											case Type.OBJECT:
												mv.visitTypeInsn(CHECKCAST, paramType.getInternalName());
												break;
										}
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
									mv.visitMethodInsn(invokeOpcode, Type.getInternalName(declaringClass), methodName, Type.getMethodDescriptor(method), isInterface);
									Class<?> returnType = method.getReturnType();
									switch (Type.getType(returnType).getSort()) {
										case Type.VOID:
											mv.visitInsn(ACONST_NULL);
											break;
										case Type.BOOLEAN:
											mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
											break;
										case Type.BYTE:
											mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
											break;
										case Type.CHAR:
											mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
											break;
										case Type.SHORT:
											mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
											break;
										case Type.INT:
											mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
											break;
										case Type.FLOAT:
											mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
											break;
										case Type.LONG:
											mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
											break;
										case Type.DOUBLE:
											mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
											break;
									}
									if (hasThrows) {
										mv.visitLabel(labelEnd);
									}
									mv.visitInsn(ARETURN);
									// catch
									if (hasThrows) {
										mv.visitLabel(labelCatch);
										mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
										mv.visitVarInsn(ASTORE, 5);
										mv.visitTypeInsn(NEW, Type.getInternalName(IllegalArgumentException.class));
										mv.visitInsn(DUP);
										mv.visitVarInsn(ALOAD, 5);
										mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(IllegalArgumentException.class), "<init>", "(Ljava/lang/Throwable;)V", false);
										mv.visitInsn(ATHROW);
									}
								}
							}
							if (count > 1) {
								methodVisitor.visitLabel(labelInnerDefault);
								methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
								methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
								methodVisitor.visitInsn(DUP);
								methodVisitor.visitLdcInsn("找不到指定的非私有方法");
								methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
								methodVisitor.visitInsn(ATHROW);
							}
						}
					}
				}
				methodVisitor.visitLabel(labelDefault);
				methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
				methodVisitor.visitInsn(DUP);
				methodVisitor.visitLdcInsn("找不到指定的非私有方法");
				methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
				methodVisitor.visitInsn(ATHROW);
			}

			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}

		// 生成各方法的lambda方法的调用方法列表
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, buildMethods.serialized().getImplMethodName()
				, "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;[L" +
					Type.getInternalName(Tuple2.class) + "<[Ljava/lang/Class;Ljava/util/function/BiFunction<Ljava/lang/Object;[Ljava/lang/Object;Ljava/lang/Object;>;>;>;", null);
			methodVisitor.visitCode();
			methodVisitor.visitTypeInsn(NEW, "java/util/HashMap");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
			methodVisitor.visitVarInsn(ASTORE, 1);

			int nameIdx = 0;
			for (Map.Entry<String, List<Method>> entry : declaredMethods.entrySet()) {
				List<Method> methods = entry.getValue();
				String methodName = entry.getKey();
				int methodCount = methods.size();

				methodVisitor.visitLdcInsn(Integer.valueOf(methodCount));
				methodVisitor.visitTypeInsn(ANEWARRAY, Type.getInternalName(Tuple2.class));
				methodVisitor.visitVarInsn(ASTORE, 2);


				for (int iMethod = 0; iMethod < methodCount; iMethod++) {
					Method method = methods.get(iMethod);
					Class<?>[] parameterTypes = method.getParameterTypes();
					int len = parameterTypes.length;
					String lambdaName = lambdaPrefixOfMethods + methodName + "$" + iMethod;

					methodVisitor.visitIntInsn(BIPUSH, len);
					methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Class");

					for (int j = 0; j < len; j++) {
						Class<?> parameterType = parameterTypes[j];
						methodVisitor.visitInsn(DUP);
						methodVisitor.visitIntInsn(BIPUSH, j);
						if (parameterType.isPrimitive()) {
							methodVisitor.visitFieldInsn(GETSTATIC, Type.getInternalName(Types.getWrapperClass(parameterType)), "TYPE", "Ljava/lang/Class;");
						} else {
							methodVisitor.visitLdcInsn(Type.getType(parameterType));
						}
						methodVisitor.visitInsn(AASTORE);
					}
					methodVisitor.visitVarInsn(ASTORE, 3);

					methodVisitor.visitInvokeDynamicInsn("apply", "()Ljava/util/function/BiFunction;"
						, new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory",
							"metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false)
						, new Object[]{
							Type.getType("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
							new Handle(Opcodes.H_INVOKESTATIC, accessClassNameInternal,
								lambdaName,
								"(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false), Type.getType("(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;")
						}
					);

					methodVisitor.visitVarInsn(ASTORE, 4);
					methodVisitor.visitVarInsn(ALOAD, 2);
					methodVisitor.visitIntInsn(SIPUSH, iMethod);
					methodVisitor.visitTypeInsn(NEW, Type.getInternalName(Tuple2.class));
					methodVisitor.visitInsn(DUP);
					methodVisitor.visitVarInsn(ALOAD, 3);
					methodVisitor.visitVarInsn(ALOAD, 4);
					methodVisitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(Tuple2.class)
						, "<init>", "(Ljava/lang/Object;Ljava/lang/Object;)V", false);
					methodVisitor.visitInsn(AASTORE);
				}
				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitLdcInsn(methodName);
				methodVisitor.visitVarInsn(ALOAD, 2);
				methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
				methodVisitor.visitInsn(POP);

				nameIdx++;
			}


			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitInsn(ARETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}

		// 生成各实例方法的lambda方法
		{
			int nameIdx = 0;
			for (Map.Entry<String, List<Method>> entry : declaredMethods.entrySet()) {
				List<Method> methods = entry.getValue();
				String methodName = entry.getKey();
				int methodCount = methods.size();
				for (int i = 0; i < methodCount; i++) {
					Method method = methods.get(i);
					Class<?>[] parameterTypes = method.getParameterTypes();
					int len = parameterTypes.length;
					String lambdaName = lambdaPrefixOfMethods + methodName + "$" + i;
					boolean hasThrows = method.getExceptionTypes().length > 0;

					MethodVisitor mv = cw.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC, lambdaName, "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
					mv.visitCode();
					Label label0 = new Label();
					Label label1 = new Label();
					Label label2 = new Label();
					if (hasThrows) {
						mv.visitTryCatchBlock(label0, label1, label2, "java/lang/Throwable");
						mv.visitLabel(label0);
					}

					mv.visitVarInsn(ALOAD, 0);
					mv.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
					if (Modifier.isStatic(method.getModifiers())) {
						mv.visitInsn(POP);
					}
					for (int j = 0; j < len; j++) {
						Class<?> parameterType = parameterTypes[j];
						mv.visitVarInsn(ALOAD, 1);
						mv.visitIntInsn(BIPUSH, j);
						mv.visitInsn(AALOAD);

						Type paramType = Type.getType(parameterType);
						switch (paramType.getSort()) {
							case Type.BOOLEAN:
								mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
								mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
								break;
							case Type.BYTE:
								mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
								mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
								break;
							case Type.CHAR:
								mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
								mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
								break;
							case Type.SHORT:
								mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
								mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
								break;
							case Type.INT:
								mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
								mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
								break;
							case Type.FLOAT:
								mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
								mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
								break;
							case Type.LONG:
								mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
								mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
								break;
							case Type.DOUBLE:
								mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
								mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
								break;
							case Type.ARRAY:
								mv.visitTypeInsn(CHECKCAST, paramType.getDescriptor());
								break;
							case Type.OBJECT:
								mv.visitTypeInsn(CHECKCAST, paramType.getInternalName());
								break;
						}
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
					mv.visitMethodInsn(invokeOpcode, Type.getInternalName(declaringClass), methodName, Type.getMethodDescriptor(method), isInterface);
					Class<?> returnType = method.getReturnType();
					switch (Type.getType(returnType).getSort()) {
						case Type.VOID:
							mv.visitInsn(ACONST_NULL);
							break;
						case Type.BOOLEAN:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
							break;
						case Type.BYTE:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
							break;
						case Type.CHAR:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
							break;
						case Type.SHORT:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
							break;
						case Type.INT:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
							break;
						case Type.FLOAT:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
							break;
						case Type.LONG:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
							break;
						case Type.DOUBLE:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
							break;
					}

					if (hasThrows) {
						mv.visitLabel(label1);
					}
					mv.visitInsn(ARETURN);
					if (hasThrows) {
						mv.visitLabel(label2);
						mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
						mv.visitVarInsn(ASTORE, 2);
						mv.visitTypeInsn(NEW, Type.getInternalName(IllegalArgumentException.class));
						mv.visitInsn(DUP);
						mv.visitVarInsn(ALOAD, 2);
						mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(IllegalArgumentException.class), "<init>", "(Ljava/lang/Throwable;)V", false);
						mv.visitInsn(ATHROW);
					}

					mv.visitMaxs(0, 0);
					mv.visitEnd();
				}
				nameIdx++;
			}
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
		while (nextClass != Object.class) {
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

		SerializableFunction<ClassAccessV2, String[]> buildFieldNames = ClassAccessV2::buildFieldNames;
		SerializableFunction<ClassAccessV2, Class[]> buildFieldTypes = ClassAccessV2::buildFieldTypes;
		SerializableTriFunction<ClassAccessV2, Object,Integer,Object> getIndexField = ClassAccessV2::getIndexField;
		SerializableConsumerWithArgs4<ClassAccessV2, Object,Integer,Object> setIndexField = ClassAccessV2::setIndexField;
		Map.Entry<String, Field>[] fieldEntryArray = fields.entrySet().toArray(new Map.Entry[0]);

		SerializableFunction<ClassAccessV2, Map<String, Tuple2<Function<Object, Object>, BiConsumer<Object, Object>>>> buildFields = ClassAccessV2::buildFields;
		final String lambdaPrefixOfFields = "lambda$" + buildFields.serialized().getImplMethodName() + "$";

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
		{
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
				switch (fieldType.getSort()) {
					case Type.BOOLEAN:
						methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
						break;
					case Type.BYTE:
						methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
						break;
					case Type.CHAR:
						methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
						break;
					case Type.SHORT:
						methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
						break;
					case Type.INT:
						methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
						break;
					case Type.FLOAT:
						methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
						break;
					case Type.LONG:
						methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
						break;
					case Type.DOUBLE:
						methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
						break;
				}
				methodVisitor.visitInsn(ARETURN);
			}
			methodVisitor.visitLabel(labelDefault);
			methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitLdcInsn("找不到指定的非私有成员");
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
			methodVisitor.visitInsn(ATHROW);

			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}
		// setIndexField
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, setIndexField.serialized().getImplMethodName(),  "(Ljava/lang/Object;ILjava/lang/Object;)V", null, null);
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
				switch (fieldType.getSort()) {
					case Type.BOOLEAN:
						methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
						methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
						break;
					case Type.BYTE:
						methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Byte");
						methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
						break;
					case Type.CHAR:
						methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Character");
						methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
						break;
					case Type.SHORT:
						methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Short");
						methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
						break;
					case Type.INT:
						methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
						methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
						break;
					case Type.FLOAT:
						methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Float");
						methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
						break;
					case Type.LONG:
						methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Long");
						methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
						break;
					case Type.DOUBLE:
						methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Double");
						methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
						break;
					case Type.ARRAY:
						methodVisitor.visitTypeInsn(CHECKCAST, fieldType.getDescriptor());
						break;
					case Type.OBJECT:
						methodVisitor.visitTypeInsn(CHECKCAST, fieldType.getInternalName());
						break;
				}
				if (Modifier.isStatic(field.getModifiers())) {
					methodVisitor.visitFieldInsn(PUTSTATIC, Type.getInternalName(field.getDeclaringClass()), fieldName, fieldType.getDescriptor());
				} else {
					methodVisitor.visitFieldInsn(PUTFIELD, Type.getInternalName(field.getDeclaringClass()), fieldName, fieldType.getDescriptor());
				}
				methodVisitor.visitInsn(RETURN);
			}
			methodVisitor.visitLabel(labelDefault);
			methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitLdcInsn("找不到指定的非私有成员");
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
			methodVisitor.visitInsn(ATHROW);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();

		}

		// buildFields方法
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, buildFields.serialized().getImplMethodName()
				, "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;Lcom/jcfc/stdk/core/tuple/Tuple2<Ljava/util/function/Function<Ljava/lang/Object;Ljava/lang/Object;>;Ljava/util/function/BiConsumer<Ljava/lang/Object;Ljava/lang/Object;>;>;>;", null);

			methodVisitor.visitCode();
			methodVisitor.visitTypeInsn(NEW, "java/util/HashMap");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
			methodVisitor.visitVarInsn(ASTORE, 1);

			for (Map.Entry<String, Field> entry : fields.entrySet()) {
				String fieldName = entry.getKey();
				Field field = entry.getValue();

				methodVisitor.visitInvokeDynamicInsn("apply", "()Ljava/util/function/Function;"
					, new Handle(
						Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory",
						"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false)
					, new Object[]{
						Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;"),
						new Handle(Opcodes.H_INVOKESTATIC, accessClassNameInternal,
							lambdaPrefixOfFields + fieldName + "Getter",
							"(Ljava/lang/Object;)Ljava/lang/Object;", false),
						Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;")
					}
				);
				methodVisitor.visitVarInsn(ASTORE, 2);
				methodVisitor.visitInvokeDynamicInsn("accept", "()Ljava/util/function/BiConsumer;"
					, new Handle(
						Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory",
						"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false)
					, new Object[]{
						Type.getType("(Ljava/lang/Object;Ljava/lang/Object;)V"),
						new Handle(Opcodes.H_INVOKESTATIC, accessClassNameInternal,
							lambdaPrefixOfFields + fieldName + "Setter",
							"(Ljava/lang/Object;Ljava/lang/Object;)V", false),
						Type.getType("(Ljava/lang/Object;Ljava/lang/Object;)V")
					}
				);
				methodVisitor.visitVarInsn(ASTORE, 3);
				methodVisitor.visitTypeInsn(NEW, Type.getInternalName(Tuple2.class));
				methodVisitor.visitInsn(DUP);
				methodVisitor.visitVarInsn(ALOAD, 2);
				methodVisitor.visitVarInsn(ALOAD, 3);
				methodVisitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(Tuple2.class), "<init>", "(Ljava/lang/Object;Ljava/lang/Object;)V", false);
				methodVisitor.visitVarInsn(ASTORE, 4);
				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitLdcInsn(fieldName);
				methodVisitor.visitVarInsn(ALOAD, 4);
				methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
				methodVisitor.visitInsn(POP);
			}
			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitInsn(ARETURN);
			methodVisitor.visitMaxs(4, 5);
			methodVisitor.visitEnd();
		}

		// lambda方法
		{
			for (Map.Entry<String, Field> entry : fields.entrySet()) {
				String fieldName = entry.getKey();
				Field field = entry.getValue();

				{
					MethodVisitor mv = cw.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC,
						lambdaPrefixOfFields + fieldName + "Setter",
						"(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
					mv.visitCode();
					mv.visitVarInsn(ALOAD, 0);
					mv.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
					if (Modifier.isStatic(field.getModifiers())) {
						mv.visitInsn(POP);
					}
					mv.visitVarInsn(ALOAD, 1);
					Type fieldType = Type.getType(field.getType());
					switch (fieldType.getSort()) {
						case Type.BOOLEAN:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
							break;
						case Type.BYTE:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
							break;
						case Type.CHAR:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
							break;
						case Type.SHORT:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
							break;
						case Type.INT:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
							break;
						case Type.FLOAT:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
							break;
						case Type.LONG:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
							break;
						case Type.DOUBLE:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
							break;
						case Type.ARRAY:
							mv.visitTypeInsn(CHECKCAST, fieldType.getDescriptor());
							break;
						case Type.OBJECT:
							mv.visitTypeInsn(CHECKCAST, fieldType.getInternalName());
							break;
					}
					if (Modifier.isStatic(field.getModifiers())) {
						mv.visitFieldInsn(PUTSTATIC, Type.getInternalName(field.getDeclaringClass()), fieldName, fieldType.getDescriptor());
					} else {
						mv.visitFieldInsn(PUTFIELD, Type.getInternalName(field.getDeclaringClass()), fieldName, fieldType.getDescriptor());
					}
					mv.visitInsn(RETURN);
					mv.visitMaxs(2, 2);
					mv.visitEnd();
				}


				{
					MethodVisitor mv = cw.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC,
						lambdaPrefixOfFields + fieldName + "Getter",
						"(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
					mv.visitCode();
					mv.visitVarInsn(ALOAD, 0);
					mv.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
					if (Modifier.isStatic(field.getModifiers())) {
						mv.visitInsn(POP);
						mv.visitFieldInsn(GETSTATIC, Type.getInternalName(field.getDeclaringClass()), fieldName, Type.getDescriptor(field.getType()));
					} else {
						mv.visitFieldInsn(GETFIELD, Type.getInternalName(type), fieldName, Type.getDescriptor(field.getType()));
					}
					Type fieldType = Type.getType(field.getType());
					switch (fieldType.getSort()) {
						case Type.BOOLEAN:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
							break;
						case Type.BYTE:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
							break;
						case Type.CHAR:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
							break;
						case Type.SHORT:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
							break;
						case Type.INT:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
							break;
						case Type.FLOAT:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
							break;
						case Type.LONG:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
							break;
						case Type.DOUBLE:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
							break;
					}
					mv.visitInsn(ARETURN);
					mv.visitMaxs(1, 1);
					mv.visitEnd();
				}
			}
		}
	}
}
