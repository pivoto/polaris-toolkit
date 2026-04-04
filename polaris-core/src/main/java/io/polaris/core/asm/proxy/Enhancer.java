package io.polaris.core.asm.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.polaris.core.asm.internal.*;
import io.polaris.core.err.BytecodeOperationException;
import io.polaris.core.tuple.Tuple2;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

/**
 * ASM 字节码增强器，支持为类添加方法拦截器
 * <p>
 * 通过生成子类的方式，在目标方法调用前后插入拦截器逻辑
 *
 * @author Qt
 * @since May 11, 2024
 */
public class Enhancer extends AbstractEnhancer {
	/** 增强器类的 ASM 类型 */
	protected static final Type TYPE_ENHANCER = Type.getType(Enhancer.class);
	/** 默认调用上下文的 ASM 类型 */
	protected static final Type TYPE_DEFAULT_INVOCATION = Type.getType(DefaultInvocation.class);

	/** 默认调用上下文构造方法签名 */
	protected static final Signature CSTRUCT_DEFAULT_INVOCATION = AsmTypes.parseConstructor(new Type[]{
		Type.INT_TYPE, AsmConsts.TYPE_OBJECT, TYPE_METHOD,
		TYPE_INVOKER, TYPE_INVOKER
	});
	/** 拦截器 intercept 方法签名 */
	protected static final Signature METHOD_INTERCEPTOR_INTERCEPT =
		new Signature("intercept", AsmConsts.TYPE_OBJECT, new Type[]{
			AsmConsts.TYPE_OBJECT, TYPE_METHOD, AsmConsts.TYPE_OBJECT_ARRAY, Type.getType(Invocation.class)
		});


	/** 设置静态拦截器的方法签名 */
	protected static final Signature GENERATED$SET_STATIC_INTERCEPTOR =
		new Signature("GENERATED$SET_STATIC_INTERCEPTOR", Type.VOID_TYPE, new Type[]{
			TYPE_INTERCEPTOR_ARRAY
		});
	/** 设置线程拦截器的方法签名 */
	protected static final Signature GENERATED$SET_THREAD_INTERCEPTOR =
		new Signature("GENERATED$SET_THREAD_INTERCEPTOR", Type.VOID_TYPE, new Type[]{
			TYPE_INTERCEPTOR_ARRAY
		});

	/** 条件拦截器列表，每个拦截器都关联一个方法匹配条件 */
	private final List<Tuple2<Predicate<Method>, Interceptor>> interceptors = new ArrayList<>();
	/** 默认拦截器，当没有匹配的条件时使用 */
	private Interceptor defaultInterceptor;
	/** 拦截器矩阵，存储每个目标方法对应的拦截器 */
	private Interceptor[] matrix;

	public Enhancer() {
		super();
	}

	@Override
	public Enhancer serialVersionUID(Long serialVersionUID) {
		return (Enhancer) super.serialVersionUID(serialVersionUID);
	}

	@Override
	public Enhancer superclass(Class<?> superclass) {
		return (Enhancer) super.superclass(superclass);
	}

	@Override
	public Enhancer withFinal(boolean withFinal) {
		return (Enhancer) super.withFinal(withFinal);
	}

	@Override
	public Enhancer interfaces(Class<?>[] interfaces) {
		return (Enhancer) super.interfaces(interfaces);
	}

	/**
	 * 设置默认拦截器，应用于所有未匹配的方法
	 *
	 * @param interceptor 默认拦截器实例
	 * @return 当前增强器实例，支持链式调用
	 */
	public Enhancer interceptor(Interceptor interceptor) {
		checkState();
		this.defaultInterceptor = interceptor;
		return this;
	}

	/**
	 * 添加条件拦截器，仅应用于匹配指定条件的方法
	 *
	 * @param predicate 方法匹配条件
	 * @param interceptors 要应用的拦截器数组
	 * @return 当前增强器实例，支持链式调用
	 */
	public Enhancer interceptor(Predicate<Method> predicate, Interceptor... interceptors) {
		checkState();
		for (Interceptor interceptor : interceptors) {
			this.interceptors.add(Tuple2.of(predicate, interceptor));
		}
		return this;
	}

	/**
	 * 清空所有已配置的拦截器
	 *
	 * @return 当前增强器实例，支持链式调用
	 */
	public Enhancer clear() {
		checkState();
		this.interceptors.clear();
		return this;
	}

	/**
	 * 生成缓存键值，用于标识唯一的增强类配置
	 *
	 * @return 包含所有配置信息的对象数组
	 */
	@Override
	protected Object generateKey() {
		return new Object[]{superclass, withFinal, interfaces, defaultInterceptor, interceptors, serialVersion};
	}


	/**
	 * 注册静态拦截器到生成的增强类
	 * <p>
	 * 通过反射调用增强类的 GENERATED$SET_STATIC_INTERCEPTOR 方法设置拦截器矩阵
	 *
	 * @param type 生成的增强类
	 * @throws IllegalArgumentException 如果类型不是增强类
	 * @throws BytecodeOperationException 如果反射操作失败
	 */
	@Override
	protected void registerStaticInterceptors(Class<?> type) {
		try {
			Method setter = type.getDeclaredMethod(GENERATED$SET_STATIC_INTERCEPTOR.getName(), Interceptor[].class);
			setter.invoke(null, new Object[]{this.matrix});
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(type + " is not an enhanced class");
		} catch (ReflectiveOperationException e) {
			throw new BytecodeOperationException(e);
		}
	}

	/**
	 * 绑定线程拦截器到生成的增强类
	 * <p>
	 * 通过反射调用增强类的 GENERATED$SET_THREAD_INTERCEPTOR 方法设置线程级别的拦截器矩阵
	 *
	 * @param type 生成的增强类
	 * @throws IllegalArgumentException 如果类型不是增强类
	 * @throws BytecodeOperationException 如果反射操作失败
	 */
	@Override
	protected void bindThreadInterceptors(Class<?> type) {
		try {
			Method setter = type.getDeclaredMethod(GENERATED$SET_THREAD_INTERCEPTOR.getName(), Interceptor[].class);
			setter.invoke(null, new Object[]{this.matrix});
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(type + " is not an enhanced class");
		} catch (ReflectiveOperationException e) {
			throw new BytecodeOperationException(e);
		}
	}

	/**
	 * 保存目标方法并构建拦截器矩阵
	 * <p>
	 * 为每个目标方法匹配对应的拦截器：优先使用条件匹配的拦截器，若无匹配则使用默认拦截器
	 *
	 * @param targetMethods 目标方法数组
	 */
	@Override
	protected void saveTargetMethods(Method[] targetMethods){
		Interceptor[] matrix = new Interceptor[targetMethods.length];
		for (int i = 0; i < targetMethods.length; i++) {
			Method method = targetMethods[i];
			Interceptor match = null;
			// 遍历所有条件拦截器，找到第一个匹配的拦截器
			for (Tuple2<Predicate<Method>, Interceptor> tuple : interceptors) {
				Predicate<Method> predicate = tuple.getFirst();
				if (predicate == null || predicate.test(method)) {
					match = tuple.getSecond();
					break;
				}
			}
			if (match == null) {
				match = this.defaultInterceptor;
			}
			matrix[i] = match;
		}
		this.matrix = matrix;
	}


	/**
	 * 发射生成特殊的字段，包括静态拦截器和实例拦截器矩阵
	 *
	 * @param ce 类发射器
	 */
	@Override
	protected void emitSpecialFields(ClassEmitter ce) {
		ce.declare_field(AsmConsts.ACC_PRIVATE | AsmConsts.ACC_STATIC, GENERATED$STATIC_INTERCEPTOR, TYPE_INTERCEPTOR_ARRAY, null);
		ce.declare_field(AsmConsts.ACC_PRIVATE, GENERATED$INTERCEPTORS_MATRIX, TYPE_INTERCEPTOR_ARRAY, null);
	}

	/**
	 * 获取设置静态拦截器的方法签名
	 *
	 * @return 设置静态拦截器的方法签名
	 */
	@Override
	protected Signature getSetStaticInterceptorSignature() {
		return GENERATED$SET_STATIC_INTERCEPTOR;
	}

	/**
	 * 获取设置线程拦截器的方法签名
	 *
	 * @return 设置线程拦截器的方法签名
	 */
	@Override
	protected Signature getSetThreadInterceptorSignature() {
		return GENERATED$SET_THREAD_INTERCEPTOR;
	}


	/**
	 * 发射生成绑定拦截器的静态方法
	 * <p>
	 * 生成 GENERATED$BIND_INTERCEPTOR 方法，用于在对象创建时绑定拦截器矩阵。
	 * 优先使用线程级别的拦截器，其次使用静态拦截器
	 *
	 * @param ce 类发射器
	 */
	@Override
	protected void emitStaticBindInterceptor(ClassEmitter ce) {
		CodeEmitter e = ce.begin_method(AsmConsts.ACC_PRIVATE | AsmConsts.ACC_STATIC,
			GENERATED$BIND_INTERCEPTOR,
			null);
		/*
		$T target = ($T) arg0;
		Interceptor[] var = (Interceptor[]) GENERATED$THREAD_INTERCEPTOR.get();
		if (var == null) {
			groups = GENERATED$STATIC_INTERCEPTOR;
			if (var == null) {
				return;
			}
		}
		target.GENERATED$INTERCEPTORS_MATRIX = var;
		*/
		Local me = e.make_local();
		e.load_arg(0);
		e.checkcast_this();
		e.store_local(me);

		Label end = e.make_label();

		e.getfield(GENERATED$THREAD_INTERCEPTOR);
		e.invoke_virtual(TYPE_THREAD_LOCAL, THREAD_LOCAL_GET);
		e.dup();
		Label found_callback = e.make_label();
		e.ifnonnull(found_callback);
		e.pop();

		e.getfield(GENERATED$STATIC_INTERCEPTOR);
		e.dup();
		e.ifnonnull(found_callback);
		e.pop();
		e.goTo(end);

		e.mark(found_callback);
		e.checkcast(TYPE_INTERCEPTOR_ARRAY);
		e.load_local(me);
		e.swap();
		e.putfield(GENERATED$INTERCEPTORS_MATRIX);

		e.mark(end);
		e.return_value();
		e.end_method();
	}


	/**
	 * 发射生成所有目标方法的拦截逻辑
	 * <p>
	 * 为每个目标方法生成代理方法，检查是否存在拦截器，如果存在则执行拦截逻辑，否则直接调用父类方法
	 *
	 * @param ce 类发射器
	 * @param methods 目标方法信息列表
	 * @param undeclaredMethods 未声明的方法映射（接口扩展方法）
	 */
	@Override
	protected void emitMethods(ClassEmitter ce, List<MethodInfo> methods, Map<String, Method> undeclaredMethods) {
		Type thisType = ce.getClassType();
		for (int i = 0; i < methods.size(); i++) {
			MethodInfo method = methods.get(i);
			// invoke with interceptor
			/* try{
				if(GENERATED$INTERCEPTORS_MATRIX == null){
					return super.xxxx(args)
				}
				Interceptor interceptor = GENERATED$INTERCEPTORS_MATRIX[index];
				if (interceptor == null ) {
					return super.xxxx(args)
				}
				try {
				Invocation invocation = new DefaultInvocation($index, this, GENERATED$TARGET_METHODS[$index],
					GENERATED$RAW_INVOKER, GENERATED$SUPER_INVOKER,
					//GENERATED$INTERCEPTORS_MATRIX[$index]
					);
				Object[] args = {...$args};
				Object rs = interceptor.intercept(this, GENERATED$TARGET_METHODS[index], args, invocation);
				return rs;
			} catch (Throwable e) {
				throw new RuntimeException(e);
			} */
			{
				CodeEmitter e = ce.begin_method(method.getModifiers(), method.getSignature(), method.getExceptionTypes());

				Label end = e.make_label();
				Label labelSuper = e.make_label();
				Label labelInterceptor = e.make_label();

				e.load_this();
				e.getfield(thisType, GENERATED$INTERCEPTORS_MATRIX, TYPE_INTERCEPTOR_ARRAY);
				e.ifnull(labelSuper);

				Local interceptor = e.make_local(TYPE_INTERCEPTOR);
				e.load_this();
				e.getfield(thisType, GENERATED$INTERCEPTORS_MATRIX, TYPE_INTERCEPTOR_ARRAY);
				e.push(i);
				e.aaload();
				e.store_local(interceptor);

				e.load_local(interceptor);
				e.ifnull(labelSuper);

				e.mark(labelInterceptor);
				Block handler = e.begin_block();

				Local localMethod = e.make_local(TYPE_METHOD);
				e.getstatic(thisType, GENERATED$TARGET_METHODS, TYPE_METHOD_ARRAY);
				e.push(i);
				e.aaload();
				e.store_local(localMethod);

				Local invocation = e.make_local(TYPE_DEFAULT_INVOCATION);
				e.new_instance(TYPE_DEFAULT_INVOCATION);
				e.dup();
				e.push(i);
				e.load_this();
				e.load_local(localMethod);
				e.getstatic(thisType, GENERATED$RAW_INVOKER, TYPE_INVOKER);
				e.getstatic(thisType, GENERATED$SUPER_INVOKER, TYPE_INVOKER);
				e.invoke_constructor(TYPE_DEFAULT_INVOCATION, CSTRUCT_DEFAULT_INVOCATION);
				e.store_local(invocation);

				e.load_local(interceptor);
				e.load_this();
				e.load_local(localMethod);
				Type[] argumentTypes = method.getSignature().getArgumentTypes();
				if (argumentTypes.length == 0) {
					e.getfield(GENERATED$EMPTY_ARGS);
				} else {
					e.create_arg_array();
				}
				e.load_local(invocation);
				e.invoke_interface(TYPE_INTERCEPTOR, METHOD_INTERCEPTOR_INTERCEPT);
				Local rs = e.make_local(AsmConsts.TYPE_OBJECT);
				e.store_local(rs);
				Type returnType = method.getSignature().getReturnType();
				if (returnType != Type.VOID_TYPE) {
					e.load_local(rs);
					e.unbox(returnType);
				}
				//e.unbox_or_zero(returnType);
				e.return_value();

				handler.end();
//				Emitters.wrap_throwable(handler, TYPE_INVOCATION_EXCEPTION);
				Emitters.wrap_undeclared_throwable(e, handler, method.getExceptionTypes(), TYPE_INVOCATION_EXCEPTION);
				e.goTo(end);

				e.mark(labelSuper);
				e.load_this();
				e.load_args();
				// 调用本类生成的 GENERATED$SUPER_方法以兼容接口方法
				e.invoke_virtual(thisType, new Signature(GENERATED$SUPER_ + method.getSignature().getName(), method.getSignature().getDescriptor()));
				////e.super_invoke(method.getSignature());
				//e.super_invoke();
				e.return_value();
				e.mark(end);
				e.end_method();
			}
		}
	}

}
