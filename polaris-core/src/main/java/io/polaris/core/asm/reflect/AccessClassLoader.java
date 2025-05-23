package io.polaris.core.asm.reflect;

import java.io.File;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import io.polaris.core.consts.StdKeys;
import io.polaris.core.env.GlobalStdEnv;
import io.polaris.core.io.IO;
import io.polaris.core.map.Maps;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @see <a href="https://github.com/EsotericSoftware/reflectasm">https://github.com/EsotericSoftware/reflectasm</a>
 * @since Aug 04, 2023
 */
@SuppressWarnings("all")
public class AccessClassLoader extends ClassLoader {
	private static final Map<ClassLoader, AccessClassLoader> accessClassLoaders = Maps.newSoftMap(new ConcurrentHashMap<>());
	// Fast-path for classes loaded in the same ClassLoader as this class.
	private static final ClassLoader selfContextParentClassLoader = getParentClassLoader(AccessClassLoader.class);
	private static volatile AccessClassLoader selfContextAccessClassLoader = new AccessClassLoader(selfContextParentClassLoader);
	private static volatile Method defineClassMethod;

	private final Set<String> localClassNames = Collections.newSetFromMap(new ConcurrentHashMap<>());
	private static final Map<String, Class<?>> baseClasses = new ConcurrentHashMap<>();
	private static final String classBytesCacheDir;
	private static final boolean classBytesCacheEnabled;

	static {
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
		String tmpdir = GlobalStdEnv.get(StdKeys.JAVA_CLASS_BYTES_TMPDIR);
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

	/** Returns null if the access class has not yet been defined. */
	Class loadAccessClass(String name) {
		// No need to check the parent class loader if the access class hasn't been defined yet.
		if (localClassNames.contains(name)) {
			try {
				return loadClass(name, false);
			} catch (ClassNotFoundException ex) {
				throw new IllegalArgumentException(ex); // Should not happen, since we know the class has been defined.
			}
		}
		return null;
	}

	Class defineAccessClass(String name, byte[] bytes) throws ClassFormatError {
		localClassNames.add(name);
		if (classBytesCacheEnabled) {
			try {
				IO.writeBytes(new File(classBytesCacheDir + "/" + name.replace(".", "/") + ".class"), bytes);
			} catch (Throwable ignored) {
			}
		}
		return defineClass(name, bytes);
	}

	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		// These classes come from the classloader that loaded AccessClassLoader.
		Class<?> c = baseClasses.get(name);
		if (c != null) {
			return c;
		}
		// All other classes come from the classloader that loaded the type we are accessing.
		return super.loadClass(name, resolve);
	}

	Class<?> defineClass(String name, byte[] bytes) throws ClassFormatError {
		try {
			// Attempt to load the access class in the same loader, which makes protected and default access members accessible.
			return (Class<?>) getDefineClassMethod().invoke(getParent(),
				new Object[]{name, bytes, Integer.valueOf(0), Integer.valueOf(bytes.length), getClass().getProtectionDomain()});
		} catch (Exception ignored) {
			// continue with the definition in the current loader (won't have access to protected and package-protected members)
		}
		return defineClass(name, bytes, 0, bytes.length, getClass().getProtectionDomain());
	}


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

	public static void registerBaseClass(Class<?>... classes) {
		for (Class<?> c : classes) {
			baseClasses.put(c.getName(), c);
		}
	}

	public static String buildAccessClassName(Class<?> type, Class<?> baseAccessClass) {
		if (type.isArray()) {
			throw new IllegalArgumentException("不支持数组类型：" + type.getCanonicalName());
		}
		String className = type.getName();
		String accessClassName = className + "$$" + baseAccessClass.getSimpleName() + "$";
		if (accessClassName.startsWith("java.")) {
			accessClassName = "javax." + accessClassName.substring(5);
		}
		return accessClassName;
	}

	private static ClassLoader getParentClassLoader(Class type) {
		ClassLoader parent = type.getClassLoader();
		if (parent == null) {
			parent = ClassLoader.getSystemClassLoader();
		}
		return parent;
	}


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

	public static AccessClassLoader get(Class type) {
		ClassLoader parent = getParentClassLoader(type);
		// 1. fast-path:
		if (selfContextParentClassLoader.equals(parent)) {
			if (selfContextAccessClassLoader == null) {
				synchronized (accessClassLoaders) { // DCL with volatile semantics
					if (selfContextAccessClassLoader == null) {
						selfContextAccessClassLoader = new AccessClassLoader(selfContextParentClassLoader);
					}
				}
			}
			return selfContextAccessClassLoader;
		}
		// 2. normal search:
		return accessClassLoaders.computeIfAbsent(parent, k -> new AccessClassLoader(parent));
	}


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

	public static int activeAccessClassLoaders() {
		int sz = accessClassLoaders.size();
		if (selfContextAccessClassLoader != null) {
			sz++;
		}
		return sz;
	}
}
