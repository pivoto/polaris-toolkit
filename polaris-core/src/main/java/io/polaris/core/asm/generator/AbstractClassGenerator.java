package io.polaris.core.asm.generator;

import java.lang.ref.WeakReference;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import io.polaris.core.asm.internal.AsmReflects;
import io.polaris.core.asm.internal.ClassNameReader;
import io.polaris.core.env.Config;
import io.polaris.core.err.BytecodeOperationException;
import io.polaris.core.map.Maps;
import io.polaris.core.tuple.Tuple2;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

/**
 * 类生成器抽象基类
 * <p>提供类生成的通用功能和缓存机制，支持自定义命名策略和生成策略</p>
 *
 * @author Qt
 * @since May 10, 2024
 */
public abstract class AbstractClassGenerator implements ClassGenerator {

	/** 类加载器数据缓存，使用弱引用避免内存泄漏 */
	private static volatile Map<ClassLoader, ClassLoaderData> CACHE = new WeakHashMap<>();

	/** 是否默认启用缓存，从配置中读取 */
	private static final boolean DEFAULT_USE_CACHE = Config.INSTANCE.bytecode().generator().cache();
	/** 生成器是否已冻结（不可修改） */
	private volatile boolean frozen = false;
	/** 生成策略 */
	private GeneratorStrategy strategy = DefaultGeneratorStrategy.INSTANCE;
	/** 命名策略 */
	private NamingPolicy namingPolicy = DefaultNamingPolicy.INSTANCE;
	/** 类加载器 */
	private ClassLoader classLoader;
	/** 包名 */
	private String packageName;
	/** 基础类名 */
	private String baseName;
	/** 生成器的键值，用于缓存识别 */
	private Tuple2<AbstractClassGenerator, Object> key;
	/** 是否使用缓存 */
	private boolean useCache = DEFAULT_USE_CACHE;
	/** 生成的类名 */
	private String className;
	/** 是否尝试直接加载已存在的类 */
	private boolean attemptLoad;

	/**
	 * 默认构造函数
	 * <p>初始化包名和基础类名为当前类的信息</p>
	 */
	protected AbstractClassGenerator() {
		setPackageName(getClass().getPackage().getName());
		setBaseName(getClass().getSimpleName());
	}

	/**
	 * 检查生成器是否已冻结
	 *
	 * @return 如果已冻结返回 true，否则返回 false
	 */
	protected boolean isFrozen() {
		return frozen;
	}

	/**
	 * 检查生成器是否可编辑
	 *
	 * @return 如果可编辑返回 true，否则返回 false
	 */
	protected boolean isEditable() {
		return !frozen;
	}

	/**
	 * 检查生成器状态，确保未冻结
	 * <p>如果已冻结则抛出 IllegalStateException</p>
	 *
	 * @throws IllegalStateException 当生成器已冻结时抛出
	 */
	protected void checkState() {
		if (frozen) {
			throw new IllegalStateException();
		}
	}

	/**
	 * 设置包名
	 *
	 * @param packageName 包名
	 * @throws IllegalStateException 当生成器已冻结时抛出
	 */
	protected void setPackageName(String packageName) {
		checkState();
		this.packageName = packageName;
	}

	/**
	 * 设置基础类名
	 *
	 * @param baseName 基础类名
	 * @throws IllegalStateException 当生成器已冻结时抛出
	 */
	protected void setBaseName(String baseName) {
		checkState();
		this.baseName = baseName;
	}

	/**
	 * 设置生成器的键值
	 * <p>用于缓存识别，将当前生成器和键值对象包装成 Tuple2</p>
	 *
	 * @param key 键值对象
	 * @throws IllegalStateException 当生成器已冻结时抛出
	 */
	protected void setKey(Object key) {
		checkState();
		this.key = Tuple2.of(this, key);
	}

	/**
	 * 获取生成的类名
	 *
	 * @return 生成的类全名
	 */
	protected final String getClassName() {
		return className;
	}

	/**
	 * 设置类名（内部使用）
	 *
	 * @param className 类名
	 * @throws IllegalStateException 当生成器已冻结时抛出
	 */
	private void setClassName(String className) {
		checkState();
		this.className = className;
	}

	/**
	 * 生成类名
	 * <p>使用命名策略生成唯一的类名</p>
	 *
	 * @param dupChecker 重复类名检查器
	 * @return 生成的类全名
	 */
	private String generateClassName(Predicate<String> dupChecker) {
		return namingPolicy.getClassName(packageName, baseName, key.getSecond(), dupChecker);
	}

	/**
	 * 设置类加载器
	 *
	 * @param classLoader 类加载器
	 * @throws IllegalStateException 当生成器已冻结时抛出
	 */
	public void setClassLoader(ClassLoader classLoader) {
		checkState();
		this.classLoader = classLoader;
	}


	/**
	 * 设置命名策略
	 *
	 * @param namingPolicy 命名策略，为 null 时使用默认策略
	 * @throws IllegalStateException 当生成器已冻结时抛出
	 */
	public void namingPolicy(NamingPolicy namingPolicy) {
		checkState();
		if (namingPolicy == null)
			namingPolicy = DefaultNamingPolicy.INSTANCE;
		this.namingPolicy = namingPolicy;
	}

	/**
	 * 获取当前使用的命名策略
	 *
	 * @return 命名策略实例
	 */
	public NamingPolicy namingPolicy() {
		return namingPolicy;
	}

	/**
	 * 设置是否使用缓存
	 *
	 * @param useCache true-启用缓存，false-禁用缓存
	 * @throws IllegalStateException 当生成器已冻结时抛出
	 */
	public void useCache(boolean useCache) {
		checkState();
		this.useCache = useCache;
	}

	/**
	 * 检查是否启用缓存
	 *
	 * @return true-启用缓存，false-禁用缓存
	 */
	public boolean useCache() {
		return useCache;
	}

	/**
	 * 设置是否尝试直接加载已存在的类
	 *
	 * @param attemptLoad true-尝试加载，false-不尝试
	 * @throws IllegalStateException 当生成器已冻结时抛出
	 */
	public void attemptLoad(boolean attemptLoad) {
		checkState();
		this.attemptLoad = attemptLoad;
	}

	/**
	 * 检查是否启用了尝试加载
	 *
	 * @return true-尝试加载，false-不尝试
	 */
	public boolean attemptLoad() {
		return attemptLoad;
	}

	/**
	 * 设置生成策略
	 *
	 * @param strategy 生成策略，为 null 时使用默认策略
	 * @throws IllegalStateException 当生成器已冻结时抛出
	 */
	protected void strategy(GeneratorStrategy strategy) {
		checkState();
		if (strategy == null)
			strategy = DefaultGeneratorStrategy.INSTANCE;
		this.strategy = strategy;
	}

	/**
	 * 获取当前使用的生成策略
	 *
	 * @return 生成策略实例
	 */
	protected GeneratorStrategy strategy() {
		return strategy;
	}


	/**
	 * 获取保护域
	 * <p>默认为 null，子类可以重写以提供特定的 ProtectionDomain</p>
	 *
	 * @return ProtectionDomain 实例，默认为 null
	 */
	protected ProtectionDomain getProtectionDomain() {
		return null;
	}

	/**
	 * 获取目标类型（父类或接口）
	 * <p>用于 JDK9+ 通过 {@code MethodHandles.privateLookupIn} 将生成的类定义到目标类型的加载器，
	 * 无需 --add-opens 且生成的类与目标同运行时包，可访问 protected/package 成员。默认为 null，子类可重写。</p>
	 *
	 * @return 目标类型，默认为 null
	 */
	protected Class<?> getTargetClass() {
		return null;
	}

	/**
	 * 获取默认的类加载器
	 * <p>子类实现此方法以提供特定的类加载器</p>
	 *
	 * @return 默认类加载器，可为 null
	 */
	protected abstract ClassLoader getDefaultClassLoader();

	/**
	 * {@inheritDoc}
	 *
	 * @param v 类访问者，用于接收类结构信息
	 * @throws Exception
	 */
	@Override
	public abstract void generate(ClassVisitor v) throws Exception;

	/**
	 * 生成类成功时调用
	 * <p>子类可以覆盖此方法以执行额外的操作</p>
	 *
	 * @param clazz 生成的 Class 对象
	 */
	protected void onAfterCreateClass(Class<?> clazz) {
	}

	/**
	 * 生成类开始前调用
	 * <p>子类可以覆盖此方法以执行额外的操作</p>
	 */
	protected void onBeforeCreateClass() {
	}

	/**
	 * 实例化类的对象开始前调用
	 *
	 * @param type 类类型
	 */
	protected void onBeforeCreateInstance(Class<?> type) {

	}

	/**
	 * 实例化类的对象完成后调用
	 *
	 * @param type 类类型
	 */
	protected void onAfterCreateInstance(Class<?> type) {

	}

	/**
	 * 生成类并实例化对象
	 *
	 * @return 新创建的类的实例
	 */
	public Object create() {
		Class<?> type = createClass();
		try {
			onBeforeCreateInstance(type);
			return AsmReflects.newInstance(type);
		} finally {
			onAfterCreateInstance(type);
		}
	}


	/**
	 * 先执行生成类前置处理（校验和设置配置参数等）
	 * 然后根据字节码生成类 Class 对象
	 *
	 * @return 生成的 Class 对象
	 */
	public Class<?> createClass() {
		onBeforeCreateClass();
		return generate();
	}

	/**
	 * 生成类
	 * <p>使用缓存机制管理生成的类，生成后将当前生成器冻结</p>
	 *
	 * @return 生成的 Class 对象
	 */
	private Class<?> generate() {
		ClassLoader loader = getClassLoader();
		Map<ClassLoader, ClassLoaderData> cache = CACHE;
		ClassLoaderData data = cache.get(loader);
		if (data == null) {
			synchronized (AbstractClassGenerator.class) {
				cache = CACHE;
				data = cache.get(loader);
				if (data == null) {
					// 创建新的缓存映射并添加 ClassLoaderData
					Map<ClassLoader, ClassLoaderData> newCache = new WeakHashMap<>(cache);
					data = new ClassLoaderData();
					newCache.put(loader, data);
					CACHE = newCache;
				}
			}
		}
		// 从 ClassLoaderData 获取或生成类
		Class<?> clazz = data.generate(this, loader, useCache());
		// 冻结生成器，防止后续修改
		this.frozen = true;
		return clazz;
	}

	/**
	 * 获取类加载器
	 * <p>按照以下顺序获取：
	 * <br>1.设置的 classLoader
	 * <br>2.getDefaultClassLoader()
	 * <br>3.线程上下文 ClassLoader
	 * <br>4.当前类的 ClassLoader
	 * </p>
	 *
	 * @return 非空的 ClassLoader 实例
	 * @throws IllegalStateException 当无法确定任何 ClassLoader 时抛出
	 */
	private ClassLoader getClassLoader() {
		ClassLoader t = classLoader;
		if (t == null) {
			t = getDefaultClassLoader();
		}
		if (t == null) {
			t = Thread.currentThread().getContextClassLoader();
		}
		if (t == null) {
			t = getClass().getClassLoader();
		}
		if (t == null) {
			throw new IllegalStateException("Cannot determine classloader");
		}
		return t;
	}

	/**
	 * 执行类生成
	 * <p>生成类名、检查重复、定义类到 ClassLoader，支持异常处理</p>
	 *
	 * @param classLoader 类加载器
	 * @return 生成的 Class 对象
	 * @throws BytecodeOperationException 当字节码操作失败时抛出
	 */
	private Class<?> doGenerate(@Nonnull ClassLoader classLoader) {
		Class<?> clazz = $doGenerate(classLoader);
		onAfterCreateClass(clazz);
		return clazz;
	}

	private Class<?> $doGenerate(@Nonnull ClassLoader classLoader) {
		try {
			Class<?> clazz;
			if (attemptLoad) {
				try {
					// 尝试直接加载已存在的类
					clazz = classLoader.loadClass(getClassName());
					return clazz;
				} catch (ClassNotFoundException e) {
					// 忽略，继续生成新类
				}
			}
			// 使用策略生成字节码
			byte[] b = strategy.generate(this);
			// 从字节码中读取类名进行验证
			String className = ClassNameReader.getClassName(new ClassReader(b));
			ProtectionDomain protectionDomain = getProtectionDomain();
			// 目标类型用于 JDK9+ privateLookupIn，将生成的类定义到目标加载器，无需 --add-opens
			Class<?> targetClass = getTargetClass();
			synchronized (classLoader) { // just in case
				// 统一使用带 targetClass 的 defineClass：protectionDomain 为 null 时走 privateLookupIn 或反射默认保护域
				clazz = AsmReflects.defineClass(className, b, classLoader, protectionDomain, targetClass);
			}
			return clazz;
		} catch (RuntimeException | Error e) {
			// 运行时错误和错误直接抛出
			throw e;
		} catch (Exception e) {
			// 其他异常包装为 BytecodeOperationException
			throw new BytecodeOperationException(e);
		}
	}


	/**
	 * 类加载器数据管理类
	 * <p>管理每个 ClassLoader 的已保留类名集合和已生成的类缓存</p>
	 */
	private static class ClassLoaderData {
		/** 已保留的类名集合，防止重复 */
		private final Set<String> reservedClassNames = ConcurrentHashMap.newKeySet();
		/** 已生成的类缓存，使用弱引用避免内存泄漏 */
		private final Map<Tuple2<AbstractClassGenerator, Object>, WeakReference<Class<?>>> generatedClasses = Maps.newSoftMap(new ConcurrentHashMap<>());

		/**
		 * 获取或生成类
		 * <p>如果启用缓存则从缓存中获取，否则直接生成新类</p>
		 *
		 * @param generator 类生成器
		 * @param classLoader 类加载器
		 * @param useCache 是否使用缓存
		 * @return 生成的 Class 对象
		 */
		public Class<?> generate(AbstractClassGenerator generator, ClassLoader classLoader, boolean useCache) {
			if (!useCache || generator.key == null) {
				// 不使用缓存，直接生成新类
				return $generate(generator, classLoader);
			} else {
				// 使用缓存，不存在时生成新类并缓存
				WeakReference<Class<?>> ref = generatedClasses.get(generator.key);
				if (ref != null) {
					Class<?> clazz = ref.get();
					if (clazz != null) {
						return clazz;
					}
				}
				synchronized (generatedClasses) {
					ref = generatedClasses.get(generator.key);
					if (ref != null) {
						Class<?> clazz = ref.get();
						if (clazz != null) {
							return clazz;
						}
					}
					Class<?> clazz = $generate(generator, classLoader);
					generatedClasses.put(generator.key, new WeakReference<>(clazz));
					return clazz;
				}
			}
		}

		private Class<?> $generate(AbstractClassGenerator generator, ClassLoader classLoader) {
			if (classLoader == null) {
				throw new IllegalArgumentException("ClassLoader is null ");
			}
			//noinspection SynchronizationOnLocalVariableOrMethodParameter
			synchronized (classLoader) {
				// 生成唯一的类名并注册到数据中
				String name = generator.generateClassName(reservedClassNames::contains);
				reservedClassNames.add(name);
				generator.setClassName(name);
			}
			return generator.doGenerate(classLoader);
		}

	}

}
