package io.polaris.core.asm.proxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.polaris.core.asm.generator.AbstractClassGenerator;
import io.polaris.core.asm.internal.*;
import io.polaris.core.collection.PrimitiveArrays;
import io.polaris.core.err.BytecodeOperationException;
import io.polaris.core.err.InvocationException;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

/**
 * 抽象增强器基类，提供 ASM 字节码增强的基础功能
 * <p>
 * 负责生成代理类的核心结构，包括字段、方法和拦截逻辑
 *
 * @author Qt
 * @since May 13, 2024
 */
public abstract class AbstractEnhancer extends AbstractClassGenerator {
	/** 增强类标记字段名 */
	protected static final String GENERATED$ENHANCED = AsmConsts.CLASS_ENHANCED_FIELD;
	/** 静态拦截器字段名 */
	protected static final String GENERATED$STATIC_INTERCEPTOR = "GENERATED$STATIC_INTERCEPTOR";
	/** 线程拦截器字段名 */
	protected static final String GENERATED$THREAD_INTERCEPTOR = "GENERATED$THREAD_INTERCEPTOR";
	/** 目标方法数组字段名 */
	protected static final String GENERATED$TARGET_METHODS = "GENERATED$TARGET_METHODS";
	/** 父类调用器字段名 */
	protected static final String GENERATED$SUPER_INVOKER = "GENERATED$SUPER_INVOKER";
	/** 原始调用器字段名 */
	protected static final String GENERATED$RAW_INVOKER = "GENERATED$RAW_INVOKER";
	/** 空参数数组字段名 */
	protected static final String GENERATED$EMPTY_ARGS = "GENERATED$EMPTY_ARGS";

	/** 父类方法名前缀 */
	protected static final String GENERATED$SUPER_ = "GENERATED$SUPER_";

	/** 拦截器矩阵字段名 */
	protected static final String GENERATED$INTERCEPTORS_MATRIX = "GENERATED$INTERCEPTORS_MATRIX";

	/** 抽象增强器类的 ASM 类型 */
	protected static final Type TYPE_ABSTRACT_ENHANCER = Type.getType(AbstractEnhancer.class);
	/** ThreadLocal 类的 ASM 类型 */
	protected static final Type TYPE_THREAD_LOCAL = Type.getType(ThreadLocal.class);
	/** Method 类的 ASM 类型 */
	protected static final Type TYPE_METHOD = Type.getType(Method.class);
	/** Method 数组的 ASM 类型 */
	protected static final Type TYPE_METHOD_ARRAY = Type.getType(Method[].class);
	/** Invoker 接口的 ASM 类型 */
	protected static final Type TYPE_INVOKER = Type.getType(Invoker.class);
	/** Interceptor 数组的 ASM 类型 */
	protected static final Type TYPE_INTERCEPTOR_ARRAY = Type.getType(Interceptor[].class);
	/** Interceptor 接口的 ASM 类型 */
	protected static final Type TYPE_INTERCEPTOR = Type.getType(Interceptor.class);
	/** InvocationException 类的 ASM 类型 */
	protected static final Type TYPE_INVOCATION_EXCEPTION = Type.getType(InvocationException.class);

//	protected static final Type TYPE_MATCHED_INTERCEPTOR_ARRAY = Type.getType(MatchedInterceptor[].class);
	/** Interceptor 二维数组的 ASM 类型 */
	protected static final Type TYPE_INTERCEPTOR_ARRAY_ARRAY = Type.getType(Interceptor[][].class);



	/** Invoker 接口的 invoke 方法签名 */
	protected static final Signature METHOD_INVOKER__INVOKE =
		new Signature("invoke", AsmConsts.TYPE_OBJECT, new Type[]{
			Type.INT_TYPE, AsmConsts.TYPE_OBJECT, AsmConsts.TYPE_OBJECT_ARRAY
		});

	/** 获取所有声明方法的方法签名 */
	protected static final Signature GET_DECLARED_METHODS =
		AsmTypes.parseSignature("java.lang.reflect.Method[] getDeclaredMethods()");
	/** 获取所有声明构造器的方法签名 */
	protected static final Signature GET_DECLARED_CONSTRUCTORS =
		AsmTypes.parseSignature("java.lang.reflect.Constructor[] getDeclaredConstructors()");
	/** 查找方法数组的方法签名 */
	protected static final Signature FIND_METHODS =
		AsmTypes.parseSignature("java.lang.reflect.Method[] findMethods(String[], java.lang.reflect.Method[])");
	/** 查找构造器数组的方法签名 */
	protected static final Signature FIND_CONSTRUCTORS =
		AsmTypes.parseSignature("java.lang.reflect.Constructor[] findConstructors(String[], java.lang.reflect.Constructor[])");

	/** 调用父类方法的签名 */
	protected static final Signature GENERATED$INVOKE_SUPER =
		new Signature("GENERATED$INVOKE_SUPER", AsmConsts.TYPE_OBJECT, new Type[]{
			Type.INT_TYPE, AsmConsts.TYPE_OBJECT, AsmConsts.TYPE_OBJECT_ARRAY
		});
	/** 调用原始方法的签名 */
	protected static final Signature GENERATED$INVOKE_RAW =
		new Signature("GENERATED$INVOKE_RAW", AsmConsts.TYPE_OBJECT, new Type[]{
			Type.INT_TYPE, AsmConsts.TYPE_OBJECT, AsmConsts.TYPE_OBJECT_ARRAY
		});
	/** 设置目标方法数组的签名 */
	protected static final Signature GENERATED$SET_TARGET_METHODS =
		new Signature("GENERATED$SET_TARGET_METHODS", Type.VOID_TYPE, new Type[]{
			TYPE_METHOD_ARRAY
		});
	/** 删除线程拦截器的签名 */
	protected static final Signature GENERATED$DEL_THREAD_INTERCEPTOR =
		new Signature("GENERATED$DEL_THREAD_INTERCEPTOR", Type.VOID_TYPE, new Type[]{});
	/** 绑定拦截器的签名 */
	protected static final Signature GENERATED$BIND_INTERCEPTOR =
		new Signature("GENERATED$BIND_INTERCEPTOR", Type.VOID_TYPE, new Type[]{
			AsmConsts.TYPE_OBJECT
		});

	/** 无参构造器签名 */
	protected static final Signature CSTRUCT_NULL = AsmTypes.parseConstructor("");
	/** ThreadLocal.get() 方法签名 */
	protected static final Signature THREAD_LOCAL_GET = AsmTypes.parseSignature("Object get()");
	/** ThreadLocal.set() 方法签名 */
	protected static final Signature THREAD_LOCAL_SET = AsmTypes.parseSignature("void set(Object)");
	/** ThreadLocal.remove() 方法签名 */
	protected static final Signature THREAD_LOCAL_REMOVE = AsmTypes.parseSignature("void remove()");


	/**
	 * 根据方法名称和描述符查找方法数组
	 * <p>
	 * 将方法名和描述符映射到实际的方法对象
	 *
	 * @param namesAndDescriptors 方法名称和描述符的字符串数组，格式为 "methodName+descriptor"
	 * @param methods 候选方法数组
	 * @return 匹配的方法数组，顺序与 namesAndDescriptors 对应
	 * @throws IllegalArgumentException 如果找不到匹配的方法
	 * @see #FIND_METHODS
	 */
	public static Method[] findMethods(String[] namesAndDescriptors, Method[] methods) {
		Map<String, Method> map = new HashMap<>();
		// 构建方法映射表：key = 方法名 + 方法描述符
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			map.put(method.getName() + Type.getMethodDescriptor(method), method);
		}
		Method[] result = new Method[namesAndDescriptors.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = map.get(namesAndDescriptors[i]);
			if (result[i] == null) {
				throw new IllegalArgumentException(namesAndDescriptors[i]);
			}
		}
		return result;
	}

	/**
	 * 根据构造器描述符查找构造器数组
	 * <p>
	 * 将构造器描述符映射到实际的构造器对象
	 *
	 * @param descriptors 构造器描述符字符串数组
	 * @param constructors 候选构造器数组
	 * @return 匹配的构造器数组，顺序与 descriptors 对应
	 * @throws IllegalArgumentException 如果找不到匹配的构造器
	 */
	public static Constructor<?>[] findConstructors(String[] descriptors, Constructor<?>[] constructors) {
		Map<String, Constructor<?>> map = new HashMap<>();
		for (int i = 0; i < constructors.length; i++) {
			Constructor<?> constructor = constructors[i];
			map.put(Type.getConstructorDescriptor(constructor), constructor);
		}
		Constructor<?>[] result = new Constructor<?>[descriptors.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = map.get(descriptors[i]);
			if (result[i] == null) {
				throw new IllegalArgumentException(descriptors[i]);
			}
		}
		return result;
	}

	/** 父类类型 */
	protected Class<?> superclass;
	/** 是否生成 final 方法 */
	protected boolean withFinal = true;
	/** 接口类型数组 */
	protected Class<?>[] interfaces;
	/** 序列化版本号 */
	protected Long serialVersion;
	/** 目标方法数组 */
	protected Method[] targetMethods;

	public AbstractEnhancer() {
		super();
	}

	/**
	 * 设置父类类型
	 *
	 * @param superclass 要增强的父类，如果是接口则自动转为 interfaces
	 * @return 当前增强器实例，支持链式调用
	 */
	public AbstractEnhancer superclass(Class<?> superclass) {
		checkState();
		if (superclass != null && superclass.isInterface()) {
			interfaces(new Class[]{superclass});
		} else if (superclass != null && superclass.equals(Object.class)) {
			// affects choice of ClassLoader
			this.superclass = null;
		} else {
			this.superclass = superclass;
		}
		return this;
	}

	/**
	 * 设置是否生成 final 方法
	 *
	 * @param withFinal true 表示生成的代理方法为 final，false 表示不添加final 修饰符
	 * @return 当前增强器实例，支持链式调用
	 */
	public AbstractEnhancer withFinal(boolean withFinal) {
		checkState();
		this.withFinal = withFinal;
		return this;
	}

	/**
	 * 设置要实现的接口数组
	 *
	 * @param interfaces 要实现的接口类型数组
	 * @return 当前增强器实例，支持链式调用
	 */
	public AbstractEnhancer interfaces(Class<?>[] interfaces) {
		checkState();
		this.interfaces = interfaces;
		return this;
	}

	/**
	 * 设置序列化版本号
	 *
	 * @param serialVersionUID 序列化版本 ID
	 * @return 当前增强器实例，支持链式调用
	 */
	public AbstractEnhancer serialVersionUID(Long serialVersionUID) {
		checkState();
		this.serialVersion = serialVersionUID;
		return this;
	}


	/**
	 * 创建增强的代理对象实例
	 * <p>
	 * 生成增强类并创建实例，在创建过程中绑定线程拦截器
	 *
	 * @return 创建的代理对象实例
	 */
	public Object create() {
		Class<?> type = createClass();
		try {
			bindThreadInterceptors(type);
			return AsmReflects.newInstance(type);
		} finally {
			unbindThreadInterceptors(type);
		}
	}

	/**
	 * 创建增强的类
	 * <p>
	 * 检查父类是否可继承，设置包名和类名等基本信息
	 *
	 * @return 生成的增强类
	 * @throws IllegalArgumentException 如果父类是 final 类
	 */
	public Class<?> createClass() {
		Class<?> parent = (superclass == null) ? Object.class : superclass;
		if (Modifier.isFinal(parent.getModifiers())) {
			throw new IllegalArgumentException("Cannot subclass final class: " + parent.getName());
		}
		if (isEditable()) {
			if (superclass != null) {
				setPackageName(superclass.getPackage().getName());
				setBaseName(superclass.getSimpleName() + "$" + getClass().getSimpleName());
			}
			setKey(generateKey());
		}
		return super.generateClass();
	}

	/**
	 * 生成缓存键值，用于标识唯一的增强类配置
	 *
	 * @return 包含所有配置信息的对象数组
	 */
	protected abstract Object generateKey();

	/**
	 * 注册静态拦截器到生成的增强类
	 *
	 * @param type 生成的增强类
	 */
	protected abstract void registerStaticInterceptors(Class<?> type);

	/**
	 * 绑定线程拦截器到生成的增强类
	 *
	 * @param type 生成的增强类
	 */
	protected abstract void bindThreadInterceptors(Class<?> type);

	/**
	 * 解绑线程拦截器
	 * <p>
	 * 通过反射调用增强类的 GENERATED$DEL_THREAD_INTERCEPTOR 方法清除 ThreadLocal 中的拦截器
	 *
	 * @param type 生成的增强类
	 * @throws IllegalArgumentException 如果类型不是增强类
	 * @throws BytecodeOperationException 如果反射操作失败
	 */
	protected void unbindThreadInterceptors(Class<?> type) {
		try {
			Method setter = type.getDeclaredMethod(GENERATED$DEL_THREAD_INTERCEPTOR.getName());
			setter.invoke(null);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(type + " is not an enhanced class");
		} catch (ReflectiveOperationException e) {
			throw new BytecodeOperationException(e);
		}
	}

	/**
	 * 注册目标方法数组到生成的增强类
	 * <p>
	 * 通过反射调用增强类的 GENERATED$SET_TARGET_METHODS 方法设置目标方法
	 *
	 * @param type 生成的增强类
	 * @throws IllegalArgumentException 如果类型不是增强类
	 * @throws BytecodeOperationException 如果反射操作失败
	 */
	protected void registerTargetMethods(Class<?> type) {
		try {
			Method setter = type.getDeclaredMethod(GENERATED$SET_TARGET_METHODS.getName(), Method[].class);
			setter.invoke(null, new Object[]{this.targetMethods});
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(type + " is not an enhanced class");
		} catch (ReflectiveOperationException e) {
			throw new BytecodeOperationException(e);
		}
	}

	/**
	 * 生成增强类并注册目标方法和拦截器
	 *
	 * @param classLoader 用于加载生成类的类加载器
	 * @param data 类加载器数据
	 * @return 生成的增强类
	 */
	@Override
	protected Class<?> generate(ClassLoader classLoader, ClassLoaderData data) {
		Class<?> c = super.generate(classLoader, data);
		registerTargetMethods(c);
		registerStaticInterceptors(c);
		return c;
	}

	/**
	 * 获取默认的类加载器
	 * <p>
	 * 优先使用父类的类加载器，其次使用第一个接口的类加载器
	 *
	 * @return 默认的类加载器，如果无法确定则返回 null
	 */
	@Override
	protected ClassLoader getDefaultClassLoader() {
		if (superclass != null) {
			return superclass.getClassLoader();
		} else if (interfaces != null) {
			return interfaces[0].getClassLoader();
		} else {
			return null;
		}
	}

	/**
	 * 获取保护域
	 * <p>
	 * 使用父类或第一个接口的保护域
	 *
	 * @return 保护域对象，如果无法确定则返回 null
	 */
	@Override
	protected ProtectionDomain getProtectionDomain() {
		if (superclass != null) {
			return AsmReflects.getProtectionDomain(superclass);
		} else if (interfaces != null) {
			return AsmReflects.getProtectionDomain(interfaces[0]);
		} else {
			return null;
		}
	}

	/**
	 * 生成增强类的字节码
	 * <p>
	 * 核心方法，负责生成完整的增强类结构，包括：
	 * - 复制父类构造器
	 * - 实现接口方法
	 * - 添加拦截逻辑
	 * - 生成必要的字段和辅助方法
	 *
	 * @param cv ASM ClassVisitor 对象
	 * @throws Exception 生成过程中可能抛出的异常
	 */
	@Override
	public void generateClass(ClassVisitor cv) throws Exception {
		String className = getClassName();
		Class<?> parent = (superclass == null) ? Object.class : superclass;
		// 收集所有可访问的父类构造器
		List<Constructor<?>> constructors = new ArrayList<>();
		for (Constructor<?> c : parent.getDeclaredConstructors()) {
			int mod = c.getModifiers();
			if (Modifier.isPrivate(mod)) {
				continue;
			}
			if (Modifier.isPublic(mod) || Modifier.isProtected(mod)) {
				constructors.add(c);
			} else {
				// 包级私有构造器，仅在同包时可访问
				if (AsmTypes.getPackageName(className).equals(AsmTypes.getPackageName(Type.getType(parent)))) {
					constructors.add(c);
				}
			}
		}
		if (constructors.isEmpty()) {
			throw new IllegalArgumentException("no accessible constructors: " + parent.getName());
		}

		Map<String, Method> allMethods = AsmReflects.getAllAccessibleMethods(parent);
		// 添加缺失的接口方法
		Map<String, Method> undeclaredMethods = new HashMap<>();
		if (interfaces != null && interfaces.length > 0) {
			// 遍历所有接口，收集未实现的方法
			for (Class<?> anInterface : interfaces) {
				for (Method method : anInterface.getMethods()) {
					Method old = allMethods.putIfAbsent(method.getName() + Type.getMethodDescriptor(method), method);
					if (old == null) {
						// 此接口方法未实现
						undeclaredMethods.put(method.getName() + Type.getMethodDescriptor(method), method);
					}
				}
			}
		}
		// 移除不可继承或实现的方法
		for (Iterator<Map.Entry<String, Method>> it = allMethods.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, Method> entry = it.next();
			Method method = entry.getValue();
			int mod = method.getModifiers();
			// 移除 final 和 static 方法
			if (Modifier.isFinal(mod) || Modifier.isStatic(mod)) {
				it.remove();
			}
			// 移除桥接方法和合成方法
			if (method.isBridge() || method.isSynthetic()) {
				it.remove();
			}

			if (!Modifier.isPublic(mod) && !Modifier.isProtected(mod)
				&& !AsmTypes.getPackageName(className).equals(AsmTypes.getPackageName(Type.getType(method.getDeclaringClass())))) {
				it.remove();
			}
		}

		// 保存目标方法数组
		this.targetMethods = allMethods.values().toArray(new Method[0]);
		// 方法签名列表
		List<MethodInfo> methods = new ArrayList<>();
		for (int i = 0; i < this.targetMethods.length; i++) {
			Method method = this.targetMethods[i];
			int mod = method.getModifiers();
			// 移除 abstract、native、synchronized修饰符，保留其他修饰符
			int modifiers = (mod
				& ~AsmConsts.ACC_ABSTRACT
				& ~AsmConsts.ACC_NATIVE
				& ~AsmConsts.ACC_SYNCHRONIZED);
			if (withFinal) {
				modifiers |= AsmConsts.ACC_FINAL;
			}
			methods.add(AsmReflects.getMethodInfo(method, modifiers));
		}
		saveTargetMethods(this.targetMethods);


		ClassEmitter ce = new ClassEmitter(cv);
		ce.begin_class(AsmConsts.V1_8, AsmConsts.ACC_PUBLIC, className,
			Type.getType(parent),
			AsmTypes.getTypes(interfaces),
			AsmConsts.SOURCE_FILE);

		ce.declare_field(AsmConsts.PRIVATE_FINAL_STATIC, GENERATED$ENHANCED, AsmConsts.TYPE_CLASS, null);
		if (serialVersion != null) {
			ce.declare_field(AsmConsts.PRIVATE_FINAL_STATIC, AsmConsts.SUID_FIELD_NAME, Type.LONG_TYPE, serialVersion);
		}

		{
			CodeEmitter e = ce.getStaticHook();
			e.push(Type.getType(parent));
			e.putstatic(ce.getClassType(),GENERATED$ENHANCED, AsmConsts.TYPE_CLASS);
		}

		emitStandardFields(ce);
		emitSpecialFields(ce);
		emitStaticBlock(ce, constructors, methods);
		emitStaticSetTargetMethods(ce);
		emitStaticSetStaticInterceptor(ce);
		emitStaticSetThreadInterceptor(ce);
		emitStaticDelThreadInterceptor(ce);
		emitStaticBindInterceptor(ce);
		emitStaticInvokeRaw(ce, methods, undeclaredMethods);
		emitStaticInvokeSuper(ce, methods, undeclaredMethods);
		emitConstructors(ce, constructors);
		emitMethods(ce, methods, undeclaredMethods);
		emitSuperMethods(ce, methods, undeclaredMethods);
		emitSpecialMethod(ce, methods, undeclaredMethods);

		ce.end_class();
	}

	/**
	 * 保存目标方法数组，子类可重写此方法以进行额外处理
	 *
	 * @param targetMethods 目标方法数组
	 */
	protected void saveTargetMethods(Method[] targetMethods) {
	}

	/**
	 * 发射生成标准字段
	 * <p>
	 * 生成所有增强类必需的字段，包括目标方法数组、线程拦截器、调用器等
	 *
	 * @param ce 类发射器
	 */
	protected void emitStandardFields(ClassEmitter ce) {
		ce.declare_field(AsmConsts.PRIVATE_STATIC, GENERATED$TARGET_METHODS, TYPE_METHOD_ARRAY, null);
		ce.declare_field(AsmConsts.PRIVATE_FINAL_STATIC, GENERATED$THREAD_INTERCEPTOR, TYPE_THREAD_LOCAL, null);
		ce.declare_field(AsmConsts.PRIVATE_FINAL_STATIC, GENERATED$SUPER_INVOKER, TYPE_INVOKER, null);
		ce.declare_field(AsmConsts.PRIVATE_FINAL_STATIC, GENERATED$RAW_INVOKER, TYPE_INVOKER, null);
		ce.declare_field(AsmConsts.PRIVATE_FINAL_STATIC, GENERATED$EMPTY_ARGS, AsmConsts.TYPE_OBJECT_ARRAY, null);
	}

	/**
	 * 发射生成特殊字段，子类可重写此方法以添加额外字段
	 *
	 * @param ce 类发射器
	 */
	protected void emitSpecialFields(ClassEmitter ce) {
	}


	/**
	 * 发射生成静态代码块
	 * <p>
	 * 初始化所有静态字段，包括 ThreadLocal、调用器实例等
	 *
	 * @param ce 类发射器
	 * @param constructors 构造器列表
	 * @param methods 方法信息列表
	 */
	protected void emitStaticBlock(ClassEmitter ce, List<Constructor<?>> constructors, List<MethodInfo> methods) {
		Type thisType = ce.getClassType();
		CodeEmitter se = ce.getStaticHook();
		// GENERATED$THREAD_INTERCEPTOR = new ThreadLocal<>();
		se.new_instance(TYPE_THREAD_LOCAL);
		se.dup();
		se.invoke_constructor(TYPE_THREAD_LOCAL, CSTRUCT_NULL);
		se.putfield(GENERATED$THREAD_INTERCEPTOR);

		/* 改为初始化类后主动赋值
		// GENERATED$TARGET_METHODS = AbstractEnhancer.findMethods(new String[]{}, $T.class.getDeclaredMethods());
		se.push(methods.size());
		se.newarray(AsmConsts.TYPE_STRING);
		for (int i = 0; i < methods.size(); i++) {
			MethodInfo method = methods.get(i);
			Signature sig = method.getSignature();
			se.dup();
			se.push(i);
			se.push(sig.getName() + sig.getDescriptor());
			se.aastore();
		}
		se.push(thisType);
		se.invoke_virtual(AsmConsts.TYPE_CLASS, GET_DECLARED_METHODS);
		se.invoke_static(TYPE_ABSTRACT_ENHANCER, FIND_METHODS, false);
		se.putstatic(thisType, GENERATED$TARGET_METHODS, TYPE_METHOD_ARRAY); */

		// GENERATED$EMPTY_ARGS = new Object[0];
		se.push(0);
		se.newarray(AsmConsts.TYPE_OBJECT);
		se.putfield(GENERATED$EMPTY_ARGS);

		// GENERATED$RAW_INVOKER = $T::GENERATED$INVOKE_RAW;
		se.invoke_lambda(TYPE_INVOKER, METHOD_INVOKER__INVOKE, thisType, GENERATED$INVOKE_RAW);
		se.putstatic(thisType, GENERATED$RAW_INVOKER, TYPE_INVOKER);
		// GENERATED$SUPER_INVOKER = $T::GENERATED$INVOKE_SUPER;
		se.invoke_lambda(TYPE_INVOKER, METHOD_INVOKER__INVOKE, thisType, GENERATED$INVOKE_SUPER);
		se.putstatic(thisType, GENERATED$SUPER_INVOKER, TYPE_INVOKER);
	}


	/**
	 * 发射生成调用父类方法的静态方法
	 * <p>
	 * 生成 GENERATED$INVOKE_SUPER 方法，根据方法索引调用对应的父类方法
	 *
	 * @param ce 类发射器
	 * @param methods 目标方法信息列表
	 * @param undeclaredMethods 未声明的方法映射（接口扩展方法）
	 */
	protected void emitStaticInvokeSuper(ClassEmitter ce, List<MethodInfo> methods, Map<String, Method> undeclaredMethods) {
		Type thisType = ce.getClassType();
		CodeEmitter e = ce.begin_method(AsmConsts.PRIVATE_FINAL_STATIC, GENERATED$INVOKE_SUPER, new Type[]{Type.getType(InvocationTargetException.class)});
		e.load_arg(1);
		e.checkcast_this();
		Local target = e.make_local();
		e.store_local(target);

		e.load_arg(0);
		final Label illegalArg = e.make_label();
		Block block = e.begin_block();

		e.process_switch(PrimitiveArrays.range(methods.size()), new ProcessSwitchCallback() {
			@Override
			public void processCase(int key, Label end) {
				MethodInfo method = methods.get(key);
				Type[] types = method.getSignature().getArgumentTypes();
				e.load_local(target);
				for (int i = 0; i < types.length; i++) {
					e.load_arg(2);
					e.aaload(i);
					e.unbox(types[i]);
				}
				e.invoke_virtual(thisType, new Signature(GENERATED$SUPER_ + method.getSignature().getName(), method.getSignature().getDescriptor()));
				e.box(method.getSignature().getReturnType());
				e.return_value();
			}

			@Override
			public void processDefault() {
				e.goTo(illegalArg);
			}
		});
		block.end();
		Emitters.wrap_throwable(block, Type.getType(InvocationTargetException.class));
		e.mark(illegalArg);
		e.throw_exception(Type.getType(IllegalArgumentException.class), "Cannot find matching method");
		e.end_method();
	}

	/**
	 * 发射生成调用原始方法的静态方法
	 * <p>
	 * 生成 GENERATED$INVOKE_RAW 方法，根据方法索引直接调用目标方法（不经过父类）
	 *
	 * @param ce 类发射器
	 * @param methods 目标方法信息列表
	 * @param undeclaredMethods 未声明的方法映射（接口扩展方法）
	 */
	protected void emitStaticInvokeRaw(ClassEmitter ce, List<MethodInfo> methods, Map<String, Method> undeclaredMethods) {
		CodeEmitter e = ce.begin_method(AsmConsts.PRIVATE_FINAL_STATIC, GENERATED$INVOKE_RAW, new Type[]{Type.getType(InvocationTargetException.class)});

		e.load_arg(0);
		final Label illegalArg = e.make_label();
		Block block = e.begin_block();

		e.process_switch(PrimitiveArrays.range(methods.size()), new ProcessSwitchCallback() {
			@Override
			public void processCase(int key, Label end) {
				MethodInfo method = methods.get(key);
				Type[] types = method.getSignature().getArgumentTypes();
				e.load_arg(1);
				e.checkcast(method.getClassInfo().getType());
				for (int i = 0; i < types.length; i++) {
					e.load_arg(2);
					e.aaload(i);
					e.unbox(types[i]);
				}
				if (undeclaredMethods.containsKey(method.getSignature().getName() + method.getSignature().getDescriptor())) {
					// 扩展接口方法
					e.invoke_interface(method.getClassInfo().getType(), method.getSignature());
				} else {
					e.invoke_virtual(method.getClassInfo().getType(), method.getSignature());
				}
				e.box(method.getSignature().getReturnType());
				e.return_value();
			}

			@Override
			public void processDefault() {
				e.goTo(illegalArg);
			}
		});
		block.end();
		Emitters.wrap_throwable(block, Type.getType(InvocationTargetException.class));
		e.mark(illegalArg);
		e.throw_exception(Type.getType(IllegalArgumentException.class), "Cannot find matching method");
		e.end_method();
	}

	/**
	 * 发射生成设置目标方法数组的静态方法
	 * <p>
	 * 生成 GENERATED$SET_TARGET_METHODS 方法，用于在类初始化后设置目标方法数组
	 *
	 * @param ce 类发射器
	 */
	protected void emitStaticSetTargetMethods(ClassEmitter ce) {
		CodeEmitter e = ce.begin_method(AsmConsts.ACC_PUBLIC | AsmConsts.ACC_STATIC,
			GENERATED$SET_TARGET_METHODS,
			null);
		e.load_arg(0);
		e.putfield(GENERATED$TARGET_METHODS);
		e.return_value();
		e.end_method();
	}

	/**
	 * 发射生成设置静态拦截器的方法
	 *
	 * @param ce 类发射器
	 */
	protected void emitStaticSetStaticInterceptor(ClassEmitter ce) {
		Signature signature = getSetStaticInterceptorSignature();
		CodeEmitter e = ce.begin_method(AsmConsts.ACC_PUBLIC | AsmConsts.ACC_STATIC,
			signature,
			null);
		e.load_arg(0);
		e.putfield(GENERATED$STATIC_INTERCEPTOR);
		e.return_value();
		e.end_method();
	}

	/**
	 * 获取设置静态拦截器的方法签名，由子类实现
	 *
	 * @return 设置静态拦截器的方法签名
	 */
	protected abstract Signature getSetStaticInterceptorSignature();

	/**
	 * 发射生成设置线程拦截器的方法
	 *
	 * @param ce 类发射器
	 */
	protected void emitStaticSetThreadInterceptor(ClassEmitter ce) {
		Signature signature = getSetThreadInterceptorSignature();
		CodeEmitter e = ce.begin_method(AsmConsts.ACC_PUBLIC | AsmConsts.ACC_STATIC,
			signature,
			null);
		e.getfield(GENERATED$THREAD_INTERCEPTOR);
		e.load_arg(0);
		e.invoke_virtual(TYPE_THREAD_LOCAL, THREAD_LOCAL_SET);
		e.return_value();
		e.end_method();
	}

	/**
	 * 获取设置线程拦截器的方法签名，由子类实现
	 *
	 * @return 设置线程拦截器的方法签名
	 */
	protected abstract Signature getSetThreadInterceptorSignature();


	/**
	 * 发射生成删除线程拦截器的方法
	 *
	 * @param ce 类发射器
	 */
	private void emitStaticDelThreadInterceptor(ClassEmitter ce) {
		CodeEmitter e = ce.begin_method(AsmConsts.ACC_PUBLIC | AsmConsts.ACC_STATIC,
			GENERATED$DEL_THREAD_INTERCEPTOR,
			null);
		e.getfield(GENERATED$THREAD_INTERCEPTOR);
		e.invoke_virtual(TYPE_THREAD_LOCAL, THREAD_LOCAL_REMOVE);
		e.return_value();
		e.end_method();
	}

	/**
	 * 发射生成绑定拦截器的方法，由子类实现具体逻辑
	 *
	 * @param ce 类发射器
	 */
	protected abstract void emitStaticBindInterceptor(ClassEmitter ce);


	/**
	 * 发射生成所有构造器
	 * <p>
	 * 复制父类的所有可访问构造器，并在每个构造器中调用绑定拦截器的方法
	 *
	 * @param ce 类发射器
	 * @param constructors 父类构造器列表
	 */
	protected void emitConstructors(ClassEmitter ce, List<Constructor<?>> constructors) {
		for (Constructor<?> constructor : constructors) {
			MethodInfo methodInfo = AsmReflects.getMethodInfo(constructor);
			CodeEmitter e = Emitters.begin_method(ce, methodInfo,
				AsmConsts.ACC_PUBLIC);
			e.load_this();
			e.dup();
			e.load_args();
			Signature sig = methodInfo.getSignature();
			e.super_invoke_constructor(sig);
			e.invoke_static_this(GENERATED$BIND_INTERCEPTOR);
			e.return_value();
			e.end_method();
		}
	}

	/**
	 * 发射生成所有目标方法的拦截逻辑，由子类实现
	 * <p>
	 * 为每个目标方法生成代理方法，添加拦截器调用逻辑
	 *
	 * @param ce 类发射器
	 * @param methods 目标方法信息列表
	 * @param undeclaredMethods 未声明的方法映射（接口扩展方法）
	 */
	protected abstract void emitMethods(ClassEmitter ce, List<MethodInfo> methods, Map<String, Method> undeclaredMethods);

	/**
	 * 发射生成父类方法调用
	 * <p>
	 * 为每个目标方法生成一个 private final 的 GENERATED$SUPER_方法，用于直接调用父类对应方法
	 *
	 * @param ce 类发射器
	 * @param methods 目标方法信息列表
	 * @param undeclaredMethods 未声明的方法映射（接口扩展方法）
	 */
	protected void emitSuperMethods(ClassEmitter ce, List<MethodInfo> methods, Map<String, Method> undeclaredMethods) {
		Type thisType = ce.getClassType();
		for (int i = 0; i < methods.size(); i++) {
			MethodInfo method = methods.get(i);
			// 生成父类方法调用，添加final/private修饰符以禁止外部访问
			{
				// 继承调用添加final/private修饰
				CodeEmitter e = ce.begin_method(AsmConsts.ACC_FINAL | AsmConsts.ACC_PRIVATE
						| (method.getModifiers() & (~AsmConsts.ACC_PUBLIC) & (~AsmConsts.ACC_PROTECTED)),
					new Signature(GENERATED$SUPER_ + method.getSignature().getName(), method.getSignature().getDescriptor()),
					method.getExceptionTypes());
				e.load_this();
				e.load_args();
				Method undeclaredMethod = undeclaredMethods.get(method.getSignature().getName() + method.getSignature().getDescriptor());
				if (undeclaredMethod != null) {
					// 扩展接口的方法，父类并未实现
					if (undeclaredMethod.isDefault()) {
						e.super_invoke(method.getSignature());
					} else {
						Type returnType = method.getSignature().getReturnType();
						e.zero_or_null(returnType);
					}
				} else {
					// 调用父类的普通方法
					e.super_invoke(method.getSignature());
				}

				e.return_value();
				e.end_method();
			}
		}
	}

	/**
	 * 发射生成特殊方法，子类可重写此方法以添加额外逻辑
	 *
	 * @param ce 类发射器
	 * @param methods 目标方法信息列表
	 * @param undeclaredMethods 未声明的方法映射（接口扩展方法）
	 */
	protected void emitSpecialMethod(ClassEmitter ce, List<MethodInfo> methods, Map<String, Method> undeclaredMethods) {
	}

}
