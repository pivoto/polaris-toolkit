package io.polaris.core.asm.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.polaris.core.lang.Types;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.reflect.SerializableFunction;
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
 * @since  Aug 05, 2023
 */
@SuppressWarnings("all")
public abstract class ClassAccessV1<T> {
	private static ILogger log = ILoggers.of(ClassAccessV1.class);
	private int constructorIndex = -1;
	private Class[][] constructorParamTypes;
	private Function<Object[], Object>[] constructors;
	private Map<String, Tuple2<Class[], BiFunction<Object, Object[], Object>>[]> methods;
	private Map<String, Tuple2<Function<Object, Object>, BiConsumer<Object, Object>>> fields;

	static {
		AccessClassLoader.registerBaseClass(ClassAccessV1.class);
	}

	protected ClassAccessV1() {
		constructorParamTypes = this.buildConstructorParamTypes();
		constructors = this.buildConstructors();
		methods = this.buildMethods();
		fields = this.buildFields();
		for (int i = 0; i < constructorParamTypes.length; i++) {
			if (constructorParamTypes[i].length == 0) {
				constructorIndex = i;
				break;
			}
		}
	}

	protected Map<String, Tuple2<Function<Object, Object>, BiConsumer<Object, Object>>> buildFields() {
		return Collections.emptyMap();
	}

	protected Map<String, Tuple2<Class[], BiFunction<Object, Object[], Object>>[]> buildMethods() {
		return Collections.emptyMap();
	}

	protected Class[][] buildConstructorParamTypes() {
		return new Class[0][0];
	}

	protected Function<Object[], Object>[] buildConstructors() {
		return new Function[0];
	}

	public Set<String> getMethodNames() {
		return new LinkedHashSet<>(methods.keySet());
	}

	public Set<String> getFieldNames() {
		return new LinkedHashSet<>(fields.keySet());
	}

	public boolean containsDefaultConstructor() {
		return constructorIndex >= 0;
	}

	public boolean containsConstructor(Class... types) {
		if (types == null || types.length == 0) {
			return containsDefaultConstructor();
		}
		for (int i = 0; i < constructorParamTypes.length; i++) {
			Class[] constructorParamType = constructorParamTypes[i];
			// 完全匹配
			if (Types.isEquals(constructorParamType, types)) {
				return true;
			}
		}
		for (int i = 0; i < constructorParamTypes.length; i++) {
			Class[] constructorParamType = constructorParamTypes[i];
			// 匹配子类型
			if (Types.isAssignable(constructorParamType, types)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsMethod(String methodName) {
		return methods.containsKey(methodName);
	}

	public boolean containsMethod(String methodName, Class... paramTypes) {
		Tuple2<Class[], BiFunction<Object, Object[], Object>>[] tuples = this.methods.get(methodName);
		if (tuples == null) {
			return false;
		}
		for (Tuple2<Class[], BiFunction<Object, Object[], Object>> tuple : tuples) {
			Class[] definedTypes = tuple.getFirst();
			if (Types.isEquals(definedTypes, paramTypes)) {
				return true;
			}
		}
		for (Tuple2<Class[], BiFunction<Object, Object[], Object>> tuple : tuples) {
			Class[] definedTypes = tuple.getFirst();
			if (Types.isAssignable(definedTypes, paramTypes)) {
				return true;
			}
		}
		return true;
	}

	public boolean containsField(String fieldName) {
		return fields.containsKey(fieldName);
	}

	public T newInstance() {
		if (constructorIndex >= 0) {
			try {
				return (T) constructors[constructorIndex].apply(new Object[0]);
			} catch (Throwable e) {
				throw new IllegalArgumentException(e);
			}
		}
		throw new IllegalArgumentException("找不到指定的非私有构造方法");
	}

	public T newInstance(Class<?>[] types, Object... args) {
		if (types == null || types.length == 0) {
			return newInstance();
		}
		for (int i = 0; i < constructorParamTypes.length; i++) {
			Class[] constructorParamType = constructorParamTypes[i];
			// 完全匹配
			if (Types.isEquals(constructorParamType, types)) {
				try {
					return (T) constructors[i].apply(args);
				} catch (Throwable e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
		for (int i = 0; i < constructorParamTypes.length; i++) {
			Class[] constructorParamType = constructorParamTypes[i];
			// 匹配子类型
			if (Types.isAssignable(constructorParamType, types)) {
				try {
					return (T) constructors[i].apply(args);
				} catch (Throwable e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
		throw new IllegalArgumentException("找不到指定的非私有构造方法");
	}

	public T newInstance(Object... args) {
		if (args.length == 0) {
			return newInstance();
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
					try {
						return (T) constructors[i].apply(args);
					} catch (Throwable e) {
						throw new IllegalArgumentException(e);
					}
				}
			}
		}
		throw new IllegalArgumentException("找不到指定的非私有构造方法");
	}

	public Object invokeMethod(Object object, String methodName, Class[] paramTypes, Object... args) {
		Tuple2<Class[], BiFunction<Object, Object[], Object>>[] tuples = this.methods.get(methodName);
		if (tuples == null) {
			throw new IllegalArgumentException("找不到指定的非私有方法：" + methodName + " " + Arrays.toString(paramTypes));
		}
		for (Tuple2<Class[], BiFunction<Object, Object[], Object>> tuple : tuples) {
			Class[] definedTypes = tuple.getFirst();
			if (Types.isEquals(definedTypes, paramTypes)) {
				try {
					return tuple.getSecond().apply(object, args);
				} catch (Throwable e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
		for (Tuple2<Class[], BiFunction<Object, Object[], Object>> tuple : tuples) {
			Class[] definedTypes = tuple.getFirst();
			if (Types.isAssignable(definedTypes, paramTypes)) {
				try {
					return tuple.getSecond().apply(object, args);
				} catch (Throwable e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
		throw new IllegalArgumentException("找不到指定的非私有方法：" + methodName + " " + Arrays.toString(paramTypes));
	}

	public Object invokeMethod(Object object, String methodName, Object... args) {
		Tuple2<Class[], BiFunction<Object, Object[], Object>>[] tuples = this.methods.get(methodName);
		if (tuples == null) {
			throw new IllegalArgumentException("找不到指定的非私有方法：" + methodName);
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
				try {
					return tuple.getSecond().apply(object, args);
				} catch (Throwable e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
		throw new IllegalArgumentException("找不到指定的非私有方法：" + methodName);
	}

	public void setField(Object object, String fieldName, Object value) {
		Tuple2<Function<Object, Object>, BiConsumer<Object, Object>> tuple = this.fields.get(fieldName);
		if (tuple == null) {
			throw new IllegalArgumentException("找不到指定的非私有字段：" + fieldName);
		}
		tuple.getSecond().accept(object, value);
	}

	public Object getField(Object object, String fieldName) {
		Tuple2<Function<Object, Object>, BiConsumer<Object, Object>> tuple = this.fields.get(fieldName);
		if (tuple == null) {
			throw new IllegalArgumentException("找不到指定的非私有字段：" + fieldName);
		}
		return tuple.getFirst().apply(object);
	}

	public static <T> ClassAccessV1<T> get(Class<T> type) {
		String accessClassName = AccessClassLoader.buildAccessClassName(type, ClassAccessV1.class);
		Class accessClass;
		AccessClassLoader loader = AccessClassLoader.get(type);
		synchronized (loader) {
			accessClass = loader.loadAccessClass(accessClassName);
			if (accessClass == null) {
				accessClass = buildAccessClass(loader, accessClassName, type);
			}
		}
		ClassAccessV1<T> access;
		try {
			access = (ClassAccessV1<T>) accessClass.newInstance();
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
		String superclassNameInternal = ClassAccessV1.class.getName().replace('.', '/');
		cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal, null,
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
		SerializableFunction<ClassAccessV1, Function<Object[], Object>[]> buildConstructors = ClassAccessV1::buildConstructors;
		final String lambdaPrefixOfConstructors = "lambda$" + buildConstructors.serialized().getImplMethodName() + "$";

		// 生成各构造的参数类型列表
		{
			SerializableFunction<ClassAccessV1, Class[][]> fetchConstructorParamTypes = ClassAccessV1::buildConstructorParamTypes;
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, fetchConstructorParamTypes.serialized().getImplMethodName(), "()[[Ljava/lang/Class;", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitLdcInsn(Integer.valueOf(size));
			methodVisitor.visitTypeInsn(ANEWARRAY, "[Ljava/lang/Class;");
			methodVisitor.visitVarInsn(ASTORE, 1);

			for (int i = 0; i < size; i++) {
				Constructor<T> constructor = constructorList.get(i);
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				int len = parameterTypes.length;
				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitIntInsn(BIPUSH, i);
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
				methodVisitor.visitInsn(AASTORE);
			}

			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitInsn(ARETURN);
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
			for (int i = 0; i < size; i++) {
				Constructor<T> constructor = constructorList.get(i);
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				int len = parameterTypes.length;

				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitIntInsn(BIPUSH, i);
				methodVisitor.visitInvokeDynamicInsn("apply", "()Ljava/util/function/Function;"
					, new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory",
						"metafactory",
						"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false)
					, new Object[]{
						Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;")
						, new Handle(Opcodes.H_INVOKESTATIC, accessClassNameInternal, lambdaPrefixOfConstructors + i, "([Ljava/lang/Object;)Ljava/lang/Object;", false)
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
			for (int i = 0; i < size; i++) {
				Constructor<T> constructor = constructorList.get(i);
				boolean hasThrows = constructor.getExceptionTypes().length > 0;

				MethodVisitor mv = cw.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC,
					lambdaPrefixOfConstructors + i, "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
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
				for (int j = 0; j < len; j++) {
					Class<?> parameterType = parameterTypes[j];
					mv.visitVarInsn(ALOAD, 0);
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
		SerializableFunction<ClassAccessV1, Map<String, Tuple2<Class[], BiFunction<Object, Object[], Object>>[]>> buildMethods = ClassAccessV1::buildMethods;

		Map<String, List<Method>> declaredMethods = getDeclaredMethods(type);
		final String lambdaPrefixOfMethods = "lambda$" + buildMethods.serialized().getImplMethodName() + "$";

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


				for (int i = 0; i < methodCount; i++) {
					Method method = methods.get(i);
					Class<?>[] parameterTypes = method.getParameterTypes();
					int len = parameterTypes.length;
					String lambdaName = lambdaPrefixOfMethods + methodName + "$" + i;

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
					methodVisitor.visitIntInsn(SIPUSH, i);
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

	private	static Map<String, List<Method>> getDeclaredMethods(Class type) {
		Map<String, Method> allMethods = new HashMap<>();
		boolean isInterface = type.isInterface();
		if (!isInterface) {
			Class nextClass = type;
			while (nextClass != null) { // 也包含Object类的方法
				ClassAccessV1.recursiveAddMethodsToMap(nextClass, allMethods);
				nextClass = nextClass.getSuperclass();
			}
		} else {
			ClassAccessV1.recursiveAddMethodsToMap(type, allMethods);
		}
		Map<String, List<Method>> overloadMethods = new HashMap<>();
		allMethods.forEach((k, v) -> {
			List<Method> list = overloadMethods.computeIfAbsent(v.getName(), name -> new ArrayList<>());
			list.add(v);
		});
		return overloadMethods;
	}

	private	static void recursiveAddMethodsToMap(Class interfaceType, Map<String, Method> methods) {
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
		Map<String, Field> fields = new HashMap<>();
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

		SerializableFunction<ClassAccessV1, Map<String, Tuple2<Function<Object, Object>, BiConsumer<Object, Object>>>> buildFields = ClassAccessV1::buildFields;
		final String lambdaPrefixOfFields = "lambda$" + buildFields.serialized().getImplMethodName() + "$";
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
