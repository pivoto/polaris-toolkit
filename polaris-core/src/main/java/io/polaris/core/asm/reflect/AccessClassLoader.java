package io.polaris.core.asm.reflect;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import io.polaris.core.env.Config;
import io.polaris.core.io.IO;
import io.polaris.core.lang.Types;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.map.Maps;
import io.polaris.core.string.Strings;

/**
 * ASM 访问类加载器
 * <p>用于动态生成和加载 ASM 字节码访问类，支持反射操作的性能优化</p>
 * <p>参考：<a href="https://github.com/EsotericSoftware/reflectasm">reflectasm 项目</a></p>
 *
 * @author Qt
 * @see <a href="https://github.com/EsotericSoftware/reflectasm">https://github.com/EsotericSoftware/reflectasm</a>
 * @since Aug 04, 2023
 */
@SuppressWarnings("all")
public class AccessClassLoader extends ClassLoader {
	private static final Logger log = Loggers.of(AccessClassLoader.class);
	private static final Map<ClassLoader, AccessClassLoader> accessClassLoaders = Maps.newSoftMap(new ConcurrentHashMap<>());
	// Fast-path for classes loaded in the same ClassLoader as this class.
	private static final ClassLoader selfContextParentClassLoader = getParentClassLoader(AccessClassLoader.class);
	private static volatile AccessClassLoader selfContextAccessClassLoader = new AccessClassLoader(selfContextParentClassLoader);
	private static volatile Method defineClassMethod;
	/** MethodHandles.Lookup.defineClass 方法反射对象（JDK9+，用反射获取以兼容 JDK8 编译）；JDK8 下为 null */
	private static volatile Method lookupDefineClassMethod;
	/** 目标类型的 MethodHandles.Lookup（JDK9+），用于通过 Lookup.defineClass 定义类，避免反射 ClassLoader.defineClass 在 JDK17 强封装下失效 */
	private volatile MethodHandles.Lookup targetLookup;

	private final Set<String> localClassNames = Collections.newSetFromMap(new ConcurrentHashMap<>());
	private static final Map<String, Class<?>> baseClasses = new ConcurrentHashMap<>();
	private static final String classBytesCacheDir;
	private static final boolean classBytesCacheEnabled;

	static {
		try {
			// Lookup.defineClass(byte[]) 仅 JDK9+ 才有，用反射获取以兼容 JDK8 编译
			lookupDefineClassMethod = MethodHandles.Lookup.class.getMethod("defineClass", byte[].class);
		} catch (NoSuchMethodException e) {
			// JDK8 无此方法，保持 null，defineClass 回退反射/protected 方式
			lookupDefineClassMethod = null;
		}
		registerBaseClass(
			ClassAccess.class
			, ClassLambdaAccess.class
			, BeanCopier.class
			, BeanAccess.class
			, BeanLambdaAccess.class
			, MethodAccess.class
			, ConstructorAccess.class
			, PublicConstructorAccess.class
			, FieldAccess.class
		);
		String tmpdir = Config.INSTANCE.bytecode().generator().tmpdir();
		if (Strings.isNotBlank(tmpdir)) {
			File dir = new File(tmpdir.trim());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			classBytesCacheDir = dir.getAbsolutePath();
			classBytesCacheEnabled = dir.exists();
		} else {
			classBytesCacheDir = null;
			classBytesCacheEnabled = false;
		}
	}

	private AccessClassLoader(ClassLoader parent) {
		super(parent);
	}


	/**
	 * 加载或定义访问类
	 * <p>如果类已存在则直接返回，否则使用提供的字节码生成器定义新类</p>
	 *
	 * @param name 类全限定名
	 * @param byteGenerator 字节码生成器，用于在类未定义时生成字节码
	 * @return 加载或定义的 Class 对象
	 */
	public Class loadOrDefineClass(String name, Supplier<byte[]> byteGenerator) {
		Class c = loadAccessClass(name);
		if (c == null) {
			synchronized (this) {
				c = loadAccessClass(name);
				if (c == null) {
					c = defineAccessClass(name, byteGenerator.get());
				}
			}
		}
		return c;
	}

	/**
	 * 加载已定义的访问类
	 * <p>仅检查本地类名集合，不委托给父类加载器</p>
	 *
	 * @param name 类全限定名
	 * @return 如果类已定义则返回 Class 对象，否则返回 null
	 */
	Class loadAccessClass(String name) {
		// 无需检查父类加载器，因为访问类尚未定义
		if (localClassNames.contains(name)) {
			try {
				return loadClass(name, false);
			} catch (ClassNotFoundException ex) {
				throw new IllegalArgumentException(ex); // 不应该发生，因为我们知道类已经定义
			}
		}
		return null;
	}

	/**
	 * 定义访问类
	 * <p>将字节码定义为类，并可选择性地缓存到磁盘</p>
	 *
	 * @param name 类全限定名
	 * @param bytes 类字节码数组
	 * @return 定义的 Class 对象
	 * @throws ClassFormatError 当字节码格式错误时抛出
	 */
	Class defineAccessClass(String name, byte[] bytes) throws ClassFormatError {
		localClassNames.add(name);
		if (classBytesCacheEnabled) {
			try {
				File file = new File(classBytesCacheDir + "/" + name.replace(".", "/") + ".class");
				if (log.isDebugEnabled()) {
					log.debug("缓存动态生成类的字节码：{}", file.getAbsolutePath());
				}
				IO.writeBytes(file, bytes);
			} catch (Throwable ignored) {
			}
		}
		return defineClass(name, bytes);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		// 尝试从基类映射中获取（来自加载 AccessClassLoader 的类加载器）
		Class<?> c = baseClasses.get(name);
		if (c != null) {
			return c;
		}
		// 其他所有类委托给父类加载器（来自加载目标类型的类加载器）
		return super.loadClass(name, resolve);
	}

	/**
	 * 定义类到父类加载器
	 * <p>尝试在父类加载器中定义，使受保护和包级成员可访问</p>
	 *
	 * @param name 类全限定名
	 * @param bytes 类字节码数组
	 * @return 定义的 Class 对象
	 * @throws ClassFormatError 当字节码格式错误时抛出
	 */
	Class<?> defineClass(String name, byte[] bytes) throws ClassFormatError {
		// JDK9+ 优先使用 MethodHandles.Lookup.defineClass（无需 --add-opens），在目标类型所在加载器/模块定义类，可访问受保护和包级成员
		MethodHandles.Lookup lookup = targetLookup;
		if (lookup != null && lookupDefineClassMethod != null) {
			try {
				return (Class<?>) lookupDefineClassMethod.invoke(lookup, bytes);
			} catch (Exception ignored) {
				// 失败则回退到反射或本地 defineClass
			}
		}
		try {
			// 尝试在相同加载器中加载访问类，使受保护和默认访问权限的成员可访问
			return (Class<?>) getDefineClassMethod().invoke(getParent(),
				new Object[]{name, bytes, Integer.valueOf(0), Integer.valueOf(bytes.length), getClass().getProtectionDomain()});
		} catch (Exception ignored) {
			// 继续在当前位置加载器中定义（无法访问受保护和包级成员）
		}
		return defineClass(name, bytes, 0, bytes.length, getClass().getProtectionDomain());
	}


	/**
	 * 判断两个类是否在相同的运行时类加载器中
	 * <p>用于验证访问权限兼容性</p>
	 *
	 * @param type1 第一个类
	 * @param type2 第二个类
	 * @return 如果在相同类加载器中返回 true，否则返回 false
	 */
	public static boolean areInSameRuntimeClassLoader(Class type1, Class type2) {
		if (type1.getPackage() != type2.getPackage()) {
			return false;
		}
		ClassLoader loader1 = type1.getClassLoader();
		ClassLoader loader2 = type2.getClassLoader();
		ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
		if (loader1 == null) {
			return (loader2 == null || loader2 == systemClassLoader);
		}
		if (loader2 == null) {
			return loader1 == systemClassLoader;
		}
		return loader1 == loader2;
	}

	/**
	 * 注册基类
	 * <p>将指定的类注册为基类，这些类将由 AccessClassLoader 直接提供</p>
	 *
	 * @param classes 要注册的类数组
	 */
	public static void registerBaseClass(Class<?>... classes) {
		for (Class<?> c : classes) {
			baseClasses.put(c.getName(), c);
		}
	}

	/**
	 * 构建访问类名称
	 * <p>根据目标类型和基访问类生成标准的访问类名称</p>
	 *
	 * @param type 目标类型
	 * @param baseAccessClass 基访问类
	 * @return 生成的访问类全限定名
	 * @throws IllegalArgumentException 当传入数组类型时抛出
	 */
	public static String buildAccessClassName(Class<?> type, Class<?> baseAccessClass) {
		if (type.isArray()) {
			throw new IllegalArgumentException("不支持数组类型：" + Types.getSafeCanonicalName(type));
		}
		String className = type.getName();
		String accessClassName = className + "$$" + baseAccessClass.getSimpleName() + "$";
		if (accessClassName.startsWith("java.")) {
			accessClassName = "javax." + accessClassName.substring(5);
		}
		return accessClassName;
	}

	/**
	 * 获取父类加载器
	 *
	 * @param type 目标类型
	 * @return 父类加载器，如果为 null 则返回系统类加载器
	 */
	private static ClassLoader getParentClassLoader(Class type) {
		ClassLoader parent = type.getClassLoader();
		if (parent == null) {
			parent = ClassLoader.getSystemClassLoader();
		}
		return parent;
	}


	/**
	 * 获取 defineClass 方法
	 * <p>通过反射获取 ClassLoader 的 defineClass 方法，用于在父加载器中定义类</p>
	 *
	 * @return defineClass 方法对象
	 * @throws Exception 当反射操作失败时抛出
	 */
	private static Method getDefineClassMethod() throws Exception {
		if (defineClassMethod == null) {
			synchronized (accessClassLoaders) {
				if (defineClassMethod == null) {
					defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass",
						new Class[]{String.class, byte[].class, int.class, int.class, ProtectionDomain.class});
					try {
						defineClassMethod.setAccessible(true);
					} catch (Exception ignored) {
					}
				}
			}
		}
		return defineClassMethod;
	}

	/**
	 * 获取指定类型的访问类加载器
	 * <p>使用软引用 Map 缓存类加载器实例，支持快速路径优化</p>
	 *
	 * @param type 目标类型
	 * @return 对应的 AccessClassLoader 实例
	 */
	public static AccessClassLoader get(Class type) {
		ClassLoader parent = getParentClassLoader(type);
		AccessClassLoader rs;
		// 1. fast-path:
		if (selfContextParentClassLoader.equals(parent)) {
			if (selfContextAccessClassLoader == null) {
				synchronized (accessClassLoaders) { // DCL with volatile semantics
					if (selfContextAccessClassLoader == null) {
						selfContextAccessClassLoader = new AccessClassLoader(selfContextParentClassLoader);
					}
				}
			}
			rs = selfContextAccessClassLoader;
		} else {
			// 2. normal search:
			// 防止因对象回收后导致SoftMap结果丢失，尝试多次获取
			while ((rs = accessClassLoaders.computeIfAbsent(parent, k -> new AccessClassLoader(parent))) == null) {
			}
		}
		ensureLookup(rs, type);
		return rs;
	}

	/**
	 * 确保访问类加载器持有目标类型的 MethodHandles.Lookup
	 * <p>JDK9+ 通过 privateLookupIn 获取能访问目标类型私有成员并在其加载器定义类的 Lookup，
	 * 避免反射 ClassLoader.defineClass 在 JDK17 强封装下失效（需 --add-opens）</p>
	 *
	 * @param loader 访问类加载器
	 * @param type 目标类型
	 */
	private static void ensureLookup(AccessClassLoader loader, Class<?> type) {
		if (loader.targetLookup != null) {
			return;
		}
		synchronized (loader) {
			if (loader.targetLookup != null) {
				return;
			}
			try {
				// privateLookupIn(Class, Lookup) 仅 JDK9+ 才有，用反射调用以兼容 JDK8 编译
				Method privateLookupIn = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
				loader.targetLookup = (MethodHandles.Lookup) privateLookupIn.invoke(null, type, MethodHandles.lookup());
			} catch (Exception ignored) {
				// 无法获取私有 lookup（模块限制或 JDK8），保持 null，defineClass 回退反射/protected 方式
			}
		}
	}


	/**
	 * 移除指定类加载器的访问类加载器缓存
	 *
	 * @param parent 父类加载器
	 */
	public static void remove(ClassLoader parent) {
		// 1. fast-path:
		if (selfContextParentClassLoader.equals(parent)) {
			selfContextAccessClassLoader = null;
		} else {
			// 2. normal search:
			synchronized (accessClassLoaders) {
				accessClassLoaders.remove(parent);
			}
		}
	}

	/**
	 * 获取活跃的访问类加载器数量
	 *
	 * @return 活跃的访问类加载器总数（包括快速路径实例）
	 */
	public static int activeAccessClassLoaders() {
		int sz = accessClassLoaders.size();
		if (selfContextAccessClassLoader != null) {
			sz++;
		}
		return sz;
	}
}
