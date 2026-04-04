package io.polaris.core.asm.internal;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.polaris.core.reflect.Reflects;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since May 10, 2024
 */
public class AsmReflects {
	/** 私有构造器，防止实例化 */
	private AsmReflects() {
	}

	/** 默认的类加载器，使用当前类的类加载器 */
	private static final ClassLoader defaultLoader = AsmReflects.class.getClassLoader();
	/** 定义类的方法反射对象（标准方式） */
	private static final Method DEFINE_CLASS;
	/** 定义类的方法反射对象（使用 Unsafe） */
	private static final Method DEFINE_CLASS_UNSAFE;
	/** 保护域，用于定义类时的安全控制 */
	private static final ProtectionDomain PROTECTION_DOMAIN;
	/** Unsafe 实例，用于绕过 JVM 安全检查 */
	private static final Object UNSAFE;
	/** 初始化过程中可能抛出的异常 */
	private static final Throwable THROWABLE;

	/** Object 类的所有可访问方法列表 */
	/** Object 类的所有可访问方法列表 */
	private static final List<Method> OBJECT_METHODS = new ArrayList<>();

	static {
		ProtectionDomain protectionDomain;
		Method defineClass, defineClassUnsafe;
		Object unsafe;
		Throwable throwable = null;
		try {
			// 获取当前类的保护域
			protectionDomain = getProtectionDomain(AsmReflects.class);
			try {
				// 通过特权操作获取 ClassLoader 的 defineClass 方法
				defineClass = AccessController.doPrivileged((PrivilegedExceptionAction<Method>) () -> {
					Class<?> loader = Class.forName("java.lang.ClassLoader"); // JVM crash w/o this
					Method defineClass1 = loader.getDeclaredMethod("defineClass",
						String.class,
						byte[].class,
						Integer.TYPE,
						Integer.TYPE,
						ProtectionDomain.class);
					// 设置方法可访问
					defineClass1.setAccessible(true);
					return defineClass1;
				});
				defineClassUnsafe = null;
				unsafe = null;
			} catch (Throwable t) {
				// 在 Jigsaw 模块系统中 defineClass 不可用，回退到 Unsafe 方式
				throwable = t;
				defineClass = null;
				// 通过特权操作获取 Unsafe 实例
				unsafe = AccessController.doPrivileged((PrivilegedExceptionAction<?>) () -> {
					Class<?> u = Class.forName("sun.misc.Unsafe");
					Field theUnsafe = u.getDeclaredField("theUnsafe");
					theUnsafe.setAccessible(true);
					return theUnsafe.get(null);
				});
				Class<?> u = Class.forName("sun.misc.Unsafe");
				// 获取 Unsafe 的 defineClass 方法
				defineClassUnsafe = u.getMethod("defineClass",
					String.class,
					byte[].class,
					Integer.TYPE,
					Integer.TYPE,
					ClassLoader.class,
					ProtectionDomain.class);
			}
			// 收集 Object 类的所有非 final、非静态方法（除了 finalize）
			AccessController.doPrivileged((PrivilegedExceptionAction<?>) () -> {
				Method[] methods = Object.class.getDeclaredMethods();
				for (Method method : methods) {
					if ("finalize".equals(method.getName())
						|| (method.getModifiers() & (Modifier.FINAL | Modifier.STATIC)) > 0) {
						continue;
					}
					OBJECT_METHODS.add(method);
				}
				return null;
			});
		} catch (Throwable t) {
			if (throwable == null) {
				throwable = t;
			}
			protectionDomain = null;
			defineClass = null;
			defineClassUnsafe = null;
			unsafe = null;
		}
		// 初始化静态常量
		PROTECTION_DOMAIN = protectionDomain;
		DEFINE_CLASS = defineClass;
		DEFINE_CLASS_UNSAFE = defineClassUnsafe;
		UNSAFE = unsafe;
		THROWABLE = throwable;
	}

	/** 默认搜索的包名数组，用于辅助类查找 */
	/** 默认搜索的包名数组，用于辅助类查找 */
	private static final String[] DEFAULT_PACKAGES = {
		"java.lang",
	};

	/**
	 * 根据名称获取基本类型对应的 Class 对象
	 *
	 * @param name 基本类型名称（如 "int", "boolean" 等）
	 * @return 对应的基本类型 Class 对象，如果不是基本类型则返回 null
	 */
	/**
	 * 根据名称获取基本类型对应的 Class 对象
	 *
	 * @param name 基本类型名称（如 "int", "boolean" 等）
	 * @return 对应的基本类型 Class 对象，如果不是基本类型则返回 null
	 */
	private static Class<?> getPrimitiveClassByName(String name) {
		switch (name) {
			case "void":
				return Void.TYPE;
			case "boolean":
				return Boolean.TYPE;
			case "char":
				return Character.TYPE;
			case "byte":
				return Byte.TYPE;
			case "short":
				return Short.TYPE;
			case "int":
				return Integer.TYPE;
			case "float":
				return Float.TYPE;
			case "long":
				return Long.TYPE;
			case "double":
				return Double.TYPE;
		}
		return null;
	}

	/**
	 * 根据名称获取基本类型的描述符
	 *
	 * @param name 基本类型名称（如 "int", "boolean" 等）
	 * @return 对应的基本类型描述符（如 "I", "Z" 等），如果不是基本类型则返回 null
	 */
	/**
	 * 根据名称获取基本类型的描述符
	 *
	 * @param name 基本类型名称（如 "int", "boolean" 等）
	 * @return 对应的基本类型描述符（如 "I", "Z" 等），如果不是基本类型则返回 null
	 */
	private static String getPrimitiveDescriptorByName(String name) {
		switch (name) {
			case "void":
				return "V";
			case "boolean":
				return "Z";
			case "char":
				return "C";
			case "byte":
				return "B";
			case "short":
				return "S";
			case "int":
				return "I";
			case "float":
				return "F";
			case "long":
				return "J";
			case "double":
				return "D";
		}
		return null;
	}


	/**
	 * 定义类，使用默认保护域
	 *
	 * @param className 类名
	 * @param b 字节码数组
	 * @param loader 类加载器
	 * @return 定义的 Class 对象
	 * @throws Exception 定义类过程中可能抛出的异常
	 */
	public static Class<?> defineClass(String className, byte[] b, ClassLoader loader) throws Exception {
		return defineClass(className, b, loader, PROTECTION_DOMAIN);
	}

	/**
	 * 定义类，可指定保护域
	 *
	 * @param className 类名
	 * @param b 字节码数组
	 * @param loader 类加载器
	 * @param protectionDomain 保护域
	 * @return 定义的 Class 对象
	 * @throws Exception 定义类过程中可能抛出的异常
	 */
	public static Class<?> defineClass(String className, byte[] b, ClassLoader loader, ProtectionDomain protectionDomain) throws Exception {
		Class<?> c;
		if (DEFINE_CLASS != null) {
			// 使用标准方式定义类
			Object[] args = new Object[]{className, b, 0, b.length, protectionDomain};
			c = (Class<?>) DEFINE_CLASS.invoke(loader, args);
		} else if (DEFINE_CLASS_UNSAFE != null) {
			// 使用 Unsafe 方式定义类
			Object[] args = new Object[]{className, b, 0, b.length, loader, protectionDomain};
			c = (Class<?>) DEFINE_CLASS_UNSAFE.invoke(UNSAFE, args);
		} else {
			// 两种方式都不可用，抛出异常
			throw new RuntimeException(THROWABLE);
		}
		// 强制运行静态初始化器
		Class.forName(className, true, loader);
		return c;
	}

	/**
	 * 获取类的所有可访问方法（包括父类和接口）
	 *
	 * @param type 目标类
	 * @return 方法 Map，键为方法签名，值为 Method 对象
	 */
	public static Map<String, Method> getAllAccessibleMethods(Class<?> type) {
		Map<String, Method> allMethods = new LinkedHashMap<>();
		boolean isInterface = type.isInterface();
		if (!isInterface) {
			// 非接口类，递归收集所有父类的方法
			Class<?> nextClass = type;
			while (nextClass != null) { // 也包含 Object 类的方法
				recursiveAddMethodsToMap(nextClass, allMethods);
				nextClass = nextClass.getSuperclass();
			}
		} else {
			// 接口类，只收集接口本身的方法
			recursiveAddMethodsToMap(type, allMethods);
		}
		return allMethods;
	}

	/**
	 * 递归添加接口或类的方法到 Map 中
	 *
	 * @param interfaceType 目标接口或类
	 * @param methods 方法 Map，用于存储收集到的方法
	 */
	@SuppressWarnings("all")
	private static void recursiveAddMethodsToMap(Class<?> interfaceType, Map<String, Method> methods) {
		Method[] declaredMethods = interfaceType.getDeclaredMethods();
		for (int i = 0, n = declaredMethods.length; i < n; i++) {
			Method method = declaredMethods[i];
			int modifiers = method.getModifiers();
			// 跳过私有方法
			if (Modifier.isPrivate(modifiers)) {
				continue;
			}
			// 忽略几下 Object 方法
			if (Reflects.isFinalizeMethod(method) ||
				Reflects.isNotifyMethod(method) || Reflects.isNotifyAllMethod(method) ||
				Reflects.isWaitMethod(method)) {
				continue;
			}
			// 忽略非覆写的 clone 方法
			if (Reflects.isCloneMethod(method) && method.getDeclaringClass() == Object.class) {
				continue;
			}
			// 使用方法名和描述符作为键存入 Map
			methods.putIfAbsent(method.getName() + Type.getMethodDescriptor(method), method);
		}
		// 递归处理父接口
		for (Class<?> nextInterface : interfaceType.getInterfaces()) {
			recursiveAddMethodsToMap(nextInterface, methods);
		}
	}


	/**
	 * 获取类的保护域
	 *
	 * @param source 源类
	 * @return 保护域对象
	 */
	public static ProtectionDomain getProtectionDomain(final Class<?> source) {
		if (source == null) {
			return null;
		}
		// 通过特权操作获取保护域
		return AccessController.doPrivileged((PrivilegedAction<ProtectionDomain>) source::getProtectionDomain);
	}

	/**
	 * 获取成员（方法或构造器）的异常类型数组
	 *
	 * @param member 成员对象（Method 或 Constructor）
	 * @return ASM Type 数组，表示异常类型
	 * @throws IllegalArgumentException 如果传入的是 Field
	 */
	public static Type[] getExceptionTypes(Member member) {
		if (member instanceof Method) {
			return AsmTypes.getTypes(((Method) member).getExceptionTypes());
		} else if (member instanceof Constructor) {
			return AsmTypes.getTypes(((Constructor<?>) member).getExceptionTypes());
		} else {
			throw new IllegalArgumentException("Cannot get exception types of a field");
		}
	}

	/**
	 * 获取成员（方法或构造器）的签名
	 *
	 * @param member 成员对象（Method 或 Constructor）
	 * @return Signature 对象，包含方法名和描述符
	 * @throws IllegalArgumentException 如果传入的是 Field
	 */
	public static Signature getSignature(Member member) {
		if (member instanceof Method) {
			return new Signature(member.getName(), Type.getMethodDescriptor((Method) member));
		} else if (member instanceof Constructor) {
			Type[] types = AsmTypes.getTypes(((Constructor<?>) member).getParameterTypes());
			return new Signature(AsmConsts.CONSTRUCTOR_NAME,
				Type.getMethodDescriptor(Type.VOID_TYPE, types));
		} else {
			throw new IllegalArgumentException("Cannot get signature of a field");
		}
	}

	/**
	 * 查找构造器
	 *
	 * @param desc 构造器描述符字符串
	 * @return 找到的 Constructor 对象
	 * @throws IllegalArgumentException 如果找不到对应的构造器
	 */
	public static Constructor<?> findConstructor(String desc) {
		return findConstructor(desc, defaultLoader);
	}

	/**
	 * 查找构造器，可指定类加载器
	 *
	 * @param desc 构造器描述符字符串
	 * @param loader 类加载器
	 * @return 找到的 Constructor 对象
	 * @throws IllegalArgumentException 如果找不到对应的构造器
	 */
	public static Constructor<?> findConstructor(String desc, ClassLoader loader) {
		try {
			int lparen = desc.indexOf('(');
			// 提取类名
			String className = desc.substring(0, lparen).trim();
			// 解析参数类型并获取构造器
			return getClass(className, loader).getConstructor(parseTypes(desc, loader));
		} catch (ReflectiveOperationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Method findMethod(String desc) {
		return findMethod(desc, defaultLoader);
	}

	public static Method findMethod(String desc, ClassLoader loader) {
		try {
			int lparen = desc.indexOf('(');
			int dot = desc.lastIndexOf('.', lparen);
			String className = desc.substring(0, dot).trim();
			String methodName = desc.substring(dot + 1, lparen).trim();
			return getClass(className, loader).getDeclaredMethod(methodName, parseTypes(desc, loader));
		} catch (ReflectiveOperationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static Class<?>[] parseTypes(String desc, ClassLoader loader) throws ClassNotFoundException {
		int lparen = desc.indexOf('(');
		int rparen = desc.indexOf(')', lparen);
		List<String> params = new ArrayList<>();
		int start = lparen + 1;
		for (; ; ) {
			int comma = desc.indexOf(',', start);
			if (comma < 0) {
				break;
			}
			params.add(desc.substring(start, comma).trim());
			start = comma + 1;
		}
		if (start < rparen) {
			params.add(desc.substring(start, rparen).trim());
		}
		Class<?>[] types = new Class[params.size()];
		for (int i = 0; i < types.length; i++) {
			types[i] = getClass(params.get(i), loader);
		}
		return types;
	}

	public static Class<?> getClass(String className) throws ClassNotFoundException {
		return getClass(className, Thread.currentThread().getContextClassLoader(), DEFAULT_PACKAGES);
	}

	public static Class<?> getClass(String className, ClassLoader loader) throws ClassNotFoundException {
		return getClass(className, loader, DEFAULT_PACKAGES);
	}

	public static Class<?> getClass(String className, String[] packages) throws ClassNotFoundException {
		return getClass(className, Thread.currentThread().getContextClassLoader(), packages);
	}

	public static Class<?> getClass(String className, ClassLoader loader, String[] packages) throws ClassNotFoundException {
		String save = className;
		int dimensions = 0;
		int index = 0;
		while ((index = className.indexOf("[]", index) + 1) > 0) {
			dimensions++;
		}
		StringBuilder brackets = new StringBuilder(className.length() - dimensions);
		for (int i = 0; i < dimensions; i++) {
			brackets.append('[');
		}
		className = className.substring(0, className.length() - 2 * dimensions);

		String prefix = (dimensions > 0) ? brackets + "L" : "";
		String suffix = (dimensions > 0) ? ";" : "";
		try {
			return Class.forName(prefix + className + suffix, false, loader);
		} catch (ClassNotFoundException ignore) {}
		for (int i = 0; i < packages.length; i++) {
			try {
				return Class.forName(prefix + packages[i] + '.' + className + suffix, false, loader);
			} catch (ClassNotFoundException ignore) {}
		}
		if (dimensions == 0) {
			Class<?> c = getPrimitiveClassByName(className);
			if (c != null) {
				return c;
			}
		} else {
			String transform = getPrimitiveDescriptorByName(className);
			if (transform != null) {
				try {
					return Class.forName(brackets + transform, false, loader);
				} catch (ClassNotFoundException ignore) {}
			}
		}
		throw new ClassNotFoundException(save);
	}


	public static Object newInstance(Class<?> type) {
		return newInstance(type, AsmConsts.EMPTY_CLASS_ARRAY, null);
	}

	public static Object newInstance(Class<?> type, Class<?>[] parameterTypes, Object[] args) {
		return newInstance(getConstructor(type, parameterTypes), args);
	}

	public static Object newInstance(final Constructor<?> cstruct, final Object[] args) {
		boolean flag = cstruct.isAccessible();
		try {
			if (!flag) {
				cstruct.setAccessible(true);
			}
			return cstruct.newInstance(args);
		} catch (ReflectiveOperationException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (!flag) {
				cstruct.setAccessible(false);
			}
		}
	}

	public static <T> Constructor<T> getConstructor(Class<T> type, Class<?>[] parameterTypes) {
		try {
			Constructor<T> constructor = type.getDeclaredConstructor(parameterTypes);
			constructor.setAccessible(true);
			return constructor;
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String[] getNames(Class<?>[] classes) {
		if (classes == null)
			return null;
		String[] names = new String[classes.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = classes[i].getName();
		}
		return names;
	}

	public static Class<?>[] getClasses(Object[] objects) {
		Class<?>[] classes = new Class[objects.length];
		for (int i = 0; i < objects.length; i++) {
			classes[i] = objects[i].getClass();
		}
		return classes;
	}

	public static Method findNewInstance(Class<?> iface) {
		Method m = findInterfaceMethod(iface);
		if (!m.getName().equals("newInstance")) {
			throw new IllegalArgumentException(iface + " missing newInstance method");
		}
		return m;
	}

	public static Method[] getPropertyMethods(PropertyDescriptor[] properties, boolean read, boolean write) {
		Set<Method> methods = new HashSet<>();
		for (int i = 0; i < properties.length; i++) {
			PropertyDescriptor pd = properties[i];
			if (read) {
				methods.add(pd.getReadMethod());
			}
			if (write) {
				methods.add(pd.getWriteMethod());
			}
		}
		methods.remove(null);
		return methods.toArray(new Method[0]);
	}

	public static PropertyDescriptor[] getBeanProperties(Class<?> type) {
		return getPropertiesHelper(type, true, true);
	}

	public static PropertyDescriptor[] getBeanGetters(Class<?> type) {
		return getPropertiesHelper(type, true, false);
	}

	public static PropertyDescriptor[] getBeanSetters(Class<?> type) {
		return getPropertiesHelper(type, false, true);
	}

	private static PropertyDescriptor[] getPropertiesHelper(Class<?> type, boolean read, boolean write) {
		try {
			BeanInfo info = Introspector.getBeanInfo(type, Object.class);
			PropertyDescriptor[] all = info.getPropertyDescriptors();
			if (read && write) {
				return all;
			}
			List<PropertyDescriptor> properties = new ArrayList<>(all.length);
			for (int i = 0; i < all.length; i++) {
				PropertyDescriptor pd = all[i];
				if ((read && pd.getReadMethod() != null) ||
					(write && pd.getWriteMethod() != null)) {
					properties.add(pd);
				}
			}
			return properties.toArray(new PropertyDescriptor[0]);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}


	public static Method findDeclaredMethod(final Class<?> type,
		final String methodName, final Class<?>[] parameterTypes)
		throws NoSuchMethodException {

		Class<?> cl = type;
		while (cl != null) {
			try {
				return cl.getDeclaredMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException e) {
				cl = cl.getSuperclass();
			}
		}
		throw new NoSuchMethodException(methodName);

	}

	public static List<Method> addAllMethods(final Class<?> type, final List<Method> list) {
		if (type == Object.class) {
			list.addAll(OBJECT_METHODS);
		} else
			list.addAll(java.util.Arrays.asList(type.getDeclaredMethods()));

		Class<?> superclass = type.getSuperclass();
		if (superclass != null) {
			addAllMethods(superclass, list);
		}
		Class<?>[] interfaces = type.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			addAllMethods(interfaces[i], list);
		}
		return list;
	}

	public static List<Class<?>> addAllInterfaces(Class<?> type, List<Class<?>> list) {
		Class<?> superclass = type.getSuperclass();
		if (superclass != null) {
			list.addAll(Arrays.asList(type.getInterfaces()));
			addAllInterfaces(superclass, list);
		}
		return list;
	}


	public static Method findInterfaceMethod(Class<?> iface) {
		if (!iface.isInterface()) {
			throw new IllegalArgumentException(iface + " is not an interface");
		}
		Method[] methods = iface.getDeclaredMethods();
		if (methods.length != 1) {
			throw new IllegalArgumentException("expecting exactly 1 method in " + iface);
		}
		return methods[0];
	}

	public static int findPackageProtected(Class<?>[] classes) {
		for (int i = 0; i < classes.length; i++) {
			if (!Modifier.isPublic(classes[i].getModifiers())) {
				return i;
			}
		}
		return 0;
	}

	public static MethodInfo getMethodInfo(final Member member, final int modifiers) {
		final Signature sig = getSignature(member);
		return new MethodInfo() {
			private ClassInfo ci;

			@Override
			public ClassInfo getClassInfo() {
				if (ci == null)
					ci = AsmReflects.getClassInfo(member.getDeclaringClass());
				return ci;
			}

			@Override
			public int getModifiers() {
				return modifiers;
			}

			@Override
			public Signature getSignature() {
				return sig;
			}

			@Override
			public Type[] getExceptionTypes() {
				return AsmReflects.getExceptionTypes(member);
			}

			public Attribute getAttribute() {
				return null;
			}
		};
	}

	public static MethodInfo getMethodInfo(Member member) {
		return getMethodInfo(member, member.getModifiers());
	}

	public static ClassInfo getClassInfo(final Class<?> clazz) {
		final Type type = Type.getType(clazz);
		final Type sc = (clazz.getSuperclass() == null) ? null : Type.getType(clazz.getSuperclass());
		return new ClassInfo() {
			@Override
			public Type getType() {
				return type;
			}

			@Override
			public Type getSuperType() {
				return sc;
			}

			@Override
			public Type[] getInterfaces() {
				return AsmTypes.getTypes(clazz.getInterfaces());
			}

			@Override
			public int getModifiers() {
				return clazz.getModifiers();
			}
		};
	}


}
