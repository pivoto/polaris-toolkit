package io.polaris.core.asm.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.polaris.core.asm.internal.AsmUtils;
import io.polaris.core.collection.Iterables;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_VARARGS;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;

/**
 * ASM 方法访问器
 * <p>通过 ASM 字节码生成技术，为 Java 方法提供高性能的调用能力</p>
 * <p>支持通过索引快速调用类的所有公共方法，包括接口方法</p>
 * <p>参考：<a href="https://github.com/EsotericSoftware/reflectasm">https://github.com/EsotericSoftware/reflectasm</a></p>
 *
 * @author Qt
 * @see <a href="https://github.com/EsotericSoftware/reflectasm">https://github.com/EsotericSoftware/reflectasm</a>
 * @since Aug 04, 2023
 */
@SuppressWarnings("rawtypes")
public abstract class MethodAccess {
	/**
	 * 访问类缓存池
	 */
	private static final AccessClassPool<Class, MethodAccess> pool = new AccessClassPool<>();
	/**
	 * 方法名称数组
	 */
	private String[] methodNames;
	/**
	 * 方法参数类型二维数组
	 */
	private Class[][] parameterTypes;
	/**
	 * 方法返回值类型数组
	 */
	private Class[] returnTypes;
	/**
	 * 方法泛型参数类型三维数组
	 */
	private java.lang.reflect.Type[][] genericParameterTypes;
	/**
	 * 方法泛型返回值类型数组
	 */
	private java.lang.reflect.Type[] genericReturnTypes;

	/**
	 * 通过索引调用方法
	 * <p>子类必须实现此方法以提供具体的方法调用逻辑</p>
	 *
	 * @param object 目标对象
	 * @param methodIndex 方法索引
	 * @param args 方法参数
	 * @return 方法返回值
	 */
	public abstract Object invoke(Object object, int methodIndex, Object... args);


	/**
	 * 通过方法名和参数类型调用方法
	 *
	 * @param object 目标对象
	 * @param methodName 方法名
	 * @param paramTypes 参数类型数组
	 * @param args 参数值数组
	 * @return 方法返回值
	 */
	public Object invoke(Object object, String methodName, Class[] paramTypes, Object... args) {
		return invoke(object, getIndex(methodName, paramTypes), args);
	}


	/**
	 * 通过方法名和参数值自动匹配并调用方法
	 *
	 * @param object 目标对象
	 * @param methodName 方法名
	 * @param args 参数值数组
	 * @return 方法返回值
	 */
	public Object invoke(Object object, String methodName, Object... args) {
		return invoke(object, getIndex(methodName, args == null ? 0 : args.length), args);
	}

	/**
	 * 获取指定名称方法的索引（返回第一个匹配的方法）
	 *
	 * @param methodName 方法名
	 * @return 方法索引
	 * @throws IllegalArgumentException 当方法不存在时抛出
	 */
	public int getIndex(String methodName) {
		for (int i = 0, n = methodNames.length; i < n; i++) {
			if (methodNames[i].equals(methodName)) {
				return i;
			}
		}
		throw new IllegalArgumentException("Unable to find non-private method: " + methodName);
	}

	/**
	 * 获取指定名称和参数类型的方法索引
	 *
	 * @param methodName 方法名
	 * @param paramTypes 参数类型数组
	 * @return 方法索引
	 * @throws IllegalArgumentException 当方法不存在时抛出
	 */
	public int getIndex(String methodName, Class... paramTypes) {
		for (int i = 0, n = methodNames.length; i < n; i++) {
			if (methodNames[i].equals(methodName) && Arrays.equals(paramTypes, parameterTypes[i])) {
				return i;
			}
		}
		throw new IllegalArgumentException("Unable to find non-private method: " + methodName + " " + Arrays.toString(paramTypes));
	}

	/**
	 * 获取指定名称和参数数量的方法索引
	 *
	 * @param methodName 方法名
	 * @param paramsCount 参数数量
	 * @return 方法索引
	 * @throws IllegalArgumentException 当方法不存在时抛出
	 */
	public int getIndex(String methodName, int paramsCount) {
		for (int i = 0, n = methodNames.length; i < n; i++) {
			if (methodNames[i].equals(methodName) && parameterTypes[i].length == paramsCount) {
				return i;
			}
		}
		throw new IllegalArgumentException(
			"Unable to find non-private method: " + methodName + " with " + paramsCount + " params.");
	}

	/**
	 * 获取所有方法名称列表
	 *
	 * @return 方法名称列表
	 */
	public List<String> getMethodNames() {
		return Iterables.asList(methodNames);
	}

	/**
	 * 获取所有方法的参数类型列表
	 *
	 * @return 参数类型列表
	 */
	public List<Class[]> getParameterTypes() {
		return Iterables.asList(parameterTypes);
	}

	/**
	 * 获取所有方法的泛型参数类型列表
	 *
	 * @return 泛型参数类型列表
	 */
	public List<java.lang.reflect.Type[]> getGenericParameterTypes() {
		return Iterables.asList(genericParameterTypes);
	}

	/**
	 * 获取所有方法的返回值类型列表
	 *
	 * @return 返回值类型列表
	 */
	public List<Class> getReturnTypes() {
		return Iterables.asList(returnTypes);
	}

	/**
	 * 获取所有方法的泛型返回值类型列表
	 *
	 * @return 泛型返回值类型列表
	 */
	public List<java.lang.reflect.Type> getGenericReturnTypes() {
		return Iterables.asList(genericReturnTypes);
	}

	/**
	 * 获取或创建方法访问器实例
	 *
	 * @param type 要访问的类型
	 * @return 方法访问器实例
	 */
	public static MethodAccess get(Class type) {
		return pool.computeIfAbsent(type, MethodAccess::create);
	}

	/**
	 * Creates a new MethodAccess for the specified type.
	 *
	 * @param type Must not be a primitive type, or void.
	 */
	public static MethodAccess create(Class type) {
		boolean isInterface = type.isInterface();
		if (!isInterface && type.getSuperclass() == null && type != Object.class) {
			throw new IllegalArgumentException("不支持基本数据类型或void类型");
		}

		ArrayList<Method> methods = new ArrayList<>();
		if (!isInterface) {
			Class nextClass = type;
			while (nextClass != null && nextClass != Object.class) {
				addDeclaredMethodsToList(nextClass, methods);
				nextClass = nextClass.getSuperclass();
			}
		} else {
			recursiveAddInterfaceMethodsToList(type, methods);
		}

		int n = methods.size();
		String[] methodNames = new String[n];
		Class[][] parameterTypes = new Class[n][];
		java.lang.reflect.Type[][] genericParameterTypes = new java.lang.reflect.Type[n][];
		java.lang.reflect.Type[] genericReturnTypes = new java.lang.reflect.Type[n];
		Class[] returnTypes = new Class[n];
		for (int i = 0; i < n; i++) {
			Method method = methods.get(i);
			methodNames[i] = method.getName();
			genericParameterTypes[i] = method.getGenericParameterTypes();
			parameterTypes[i] = method.getParameterTypes();
			genericReturnTypes[i] = method.getGenericReturnType();
			returnTypes[i] = method.getReturnType();
		}

		String accessClassName = AccessClassLoader.buildAccessClassName(type, MethodAccess.class);

		Class accessClass;
		AccessClassLoader loader = AccessClassLoader.get(type);
		synchronized (loader) {
			accessClass = loader.loadAccessClass(accessClassName);
			if (accessClass == null) {
				String accessClassNameInternal = accessClassName.replace('.', '/');
				String classNameInternal = type.getName().replace('.', '/');

				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
				MethodVisitor mv;
				cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal, null,
					MethodAccess.class.getName().replace('.', '/'),
					null);
				{
					mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
					mv.visitCode();
					mv.visitVarInsn(ALOAD, 0);
					mv.visitMethodInsn(INVOKESPECIAL, MethodAccess.class.getName().replace('.', '/'), "<init>", "()V");
					mv.visitInsn(RETURN);
					mv.visitMaxs(0, 0);
					mv.visitEnd();
				}
				{
					mv = cw.visitMethod(ACC_PUBLIC + ACC_VARARGS, "invoke",
						"(Ljava/lang/Object;I[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
					mv.visitCode();

					if (!methods.isEmpty()) {
						mv.visitVarInsn(ALOAD, 1);
						mv.visitTypeInsn(CHECKCAST, classNameInternal);
						mv.visitVarInsn(ASTORE, 4);

						mv.visitVarInsn(ILOAD, 2);
						Label[] labels = new Label[n];
						for (int i = 0; i < n; i++) {
							labels[i] = new Label();
						}
						Label defaultLabel = new Label();
						mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

						StringBuilder buffer = new StringBuilder(128);
						for (int i = 0; i < n; i++) {
							mv.visitLabel(labels[i]);
							if (i == 0) {
								mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{classNameInternal}, 0, null);
							} else {
								mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
							}
							mv.visitVarInsn(ALOAD, 4);

							buffer.setLength(0);
							buffer.append('(');

							Class[] paramTypes = parameterTypes[i];
							Class returnType = returnTypes[i];
							for (int paramIndex = 0; paramIndex < paramTypes.length; paramIndex++) {
								mv.visitVarInsn(ALOAD, 3);
								mv.visitIntInsn(BIPUSH, paramIndex);
								mv.visitInsn(AALOAD);
								Type paramType = Type.getType(paramTypes[paramIndex]);
								AsmUtils.autoUnBoxing(mv, paramType);

								buffer.append(paramType.getDescriptor());
							}

							buffer.append(')');
							buffer.append(Type.getDescriptor(returnType));
							int invoke;
							if (isInterface) {
								invoke = INVOKEINTERFACE;
							} else if (Modifier.isStatic(methods.get(i).getModifiers())) {
								invoke = INVOKESTATIC;
							} else {
								invoke = INVOKEVIRTUAL;
							}
							mv.visitMethodInsn(invoke, classNameInternal, methodNames[i], buffer.toString());

							AsmUtils.autoBoxingForReturn(mv, Type.getType(returnType));
							mv.visitInsn(ARETURN);
						}

						mv.visitLabel(defaultLabel);
						mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
					}
					mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
					mv.visitInsn(DUP);
					mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
					mv.visitInsn(DUP);
					mv.visitLdcInsn("Method not found: ");
					mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
					mv.visitVarInsn(ILOAD, 2);
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
					mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
					mv.visitInsn(ATHROW);
					mv.visitMaxs(0, 0);
					mv.visitEnd();
				}
				cw.visitEnd();
				byte[] data = cw.toByteArray();
				accessClass = loader.defineAccessClass(accessClassName, data);
			}
		}
		try {
			MethodAccess access = (MethodAccess) accessClass.newInstance();
			access.methodNames = methodNames;
			access.parameterTypes = parameterTypes;
			access.genericParameterTypes = genericParameterTypes;
			access.returnTypes = returnTypes;
			access.genericReturnTypes = genericReturnTypes;
			return access;
		} catch (Throwable t) {
			throw new IllegalStateException("Error constructing method access class: " + accessClassName, t);
		}
	}

	private static void addDeclaredMethodsToList(Class type, ArrayList<Method> methods) {
		Method[] declaredMethods = type.getDeclaredMethods();
		for (int i = 0, n = declaredMethods.length; i < n; i++) {
			Method method = declaredMethods[i];
			int modifiers = method.getModifiers();
			// if (Modifier.isStatic(modifiers)) continue;
			if (Modifier.isPrivate(modifiers)) {
				continue;
			}
			methods.add(method);
		}
	}

	private static void recursiveAddInterfaceMethodsToList(Class interfaceType, ArrayList<Method> methods) {
		addDeclaredMethodsToList(interfaceType, methods);
		for (Class nextInterface : interfaceType.getInterfaces()) {
			recursiveAddInterfaceMethodsToList(nextInterface, methods);
		}
	}
}
