package io.polaris.core.asm.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import io.polaris.core.asm.internal.AsmConsts;
import io.polaris.core.asm.internal.AsmTypes;
import io.polaris.core.asm.internal.Block;
import io.polaris.core.asm.internal.ClassEmitter;
import io.polaris.core.asm.internal.CodeEmitter;
import io.polaris.core.asm.internal.Emitters;
import io.polaris.core.asm.internal.Local;
import io.polaris.core.asm.internal.MethodInfo;
import io.polaris.core.asm.internal.Signature;
import io.polaris.core.err.BytecodeOperationException;
import io.polaris.core.tuple.Tuple2;
import lombok.RequiredArgsConstructor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

/**
 * 批量增强器，作为{@link Enhancer} 的变种，支持添加多个拦截器
 * <p>
 * 可通过传入方法匹配器参数以决定目标方法对应的实际生效的拦截器，允许多个拦截器按顺序应用于同一个方法
 *
 * @author Qt
 * @since May 11, 2024
 */
public class BulkEnhancer extends AbstractEnhancer {
	/** 批量增强器类的 ASM 类型 */
	protected static final Type TYPE_BULK_ENHANCER = Type.getType(BulkEnhancer.class);
	/** 批量调用上下文的 ASM 类型 */
	protected static final Type TYPE_BULK_INVOCATION = Type.getType(BulkInvocation.class);

	//	protected static final Signature TO_INTERCEPTOR_MATRIX =
//		new Signature("toInterceptorMatrix", TYPE_INTERCEPTOR_ARRAY_ARRAY, new Type[]{
//			TYPE_MATCHED_INTERCEPTOR_ARRAY, TYPE_METHOD_ARRAY
//		});
	/** 批量调用上下文构造方法签名 */
	protected static final Signature CSTRUCT_BULK_INVOCATION = AsmTypes.parseConstructor(new Type[]{
		Type.INT_TYPE, AsmConsts.TYPE_OBJECT, TYPE_METHOD,
		TYPE_INVOKER, TYPE_INVOKER, TYPE_INTERCEPTOR_ARRAY,
	});
	/** 调用上下文的 invoke 方法签名 */
	protected static final Signature METHOD_INVOCATION_INVOKE =
		new Signature("invoke", AsmConsts.TYPE_OBJECT, new Type[]{
			AsmConsts.TYPE_OBJECT, AsmConsts.TYPE_OBJECT_ARRAY
		});


	/** 设置静态拦截器矩阵的方法签名 */
	protected static final Signature GENERATED$SET_STATIC_INTERCEPTOR =
		new Signature("GENERATED$SET_STATIC_INTERCEPTOR", Type.VOID_TYPE, new Type[]{
			TYPE_INTERCEPTOR_ARRAY_ARRAY
		});
	/** 设置线程拦截器矩阵的方法签名 */
	protected static final Signature GENERATED$SET_THREAD_INTERCEPTOR =
		new Signature("GENERATED$SET_THREAD_INTERCEPTOR", Type.VOID_TYPE, new Type[]{
			TYPE_INTERCEPTOR_ARRAY_ARRAY
		});
	/** 设置拦截器矩阵的方法签名 */
	protected static final Signature GENERATED$SET_INTERCEPTOR =
		new Signature("GENERATED$SET_INTERCEPTOR", Type.VOID_TYPE, new Type[]{
			TYPE_INTERCEPTOR_ARRAY_ARRAY
		});

//	/** @see #TO_INTERCEPTOR_MATRIX */
//	public static Interceptor[][] toInterceptorMatrix(MatchedInterceptor[] groups, Method[] targetMethods) {
//		Interceptor[][] matrix = new Interceptor[targetMethods.length][];
//		for (int i = 0; i < targetMethods.length; i++) {
//			List<Interceptor> list = new ArrayList<>(groups.length);
//			for (MatchedInterceptor group : groups) {
//				Interceptor[] interceptors = group.getInterceptors();
//				if (interceptors != null && group.accept(targetMethods[i])) {
//					list.addAll(Arrays.asList(interceptors));
//				}
//			}
//			matrix[i] = list.toArray(new Interceptor[0]);
//		}
//		return matrix;
//	}


	/** 匹配的拦截器组列表，每个组包含匹配条件和对应的拦截器 */
	private final List<MatchedInterceptor> interceptors = new ArrayList<>();
	/** 拦截器矩阵，存储每个目标方法对应的所有拦截器 */
	private Interceptor[][] matrix;

	public BulkEnhancer() {
		super();
	}

	@Override
	public BulkEnhancer serialVersionUID(Long serialVersionUID) {
		super.serialVersionUID(serialVersionUID);
		return this;
	}

	@Override
	public BulkEnhancer superclass(Class<?> superclass) {
		super.superclass(superclass);
		return this;
	}

	@Override
	public BulkEnhancer withFinal(boolean withFinal) {
		super.withFinal(withFinal);
		return this;
	}

	@Override
	public BulkEnhancer interfaces(Class<?>[] interfaces) {
		super.interfaces(interfaces);
		return this;
	}


	@Override
	public BulkEnhancer override(Function<Method, Function<Object[], Object>> overrideGenerator) {
		super.override(overrideGenerator);
		return this;
	}

	@Override
	public BulkEnhancer override(Method method, Function<Object[], Object> overrideFunction) {
		super.override(method, overrideFunction);
		return this;
	}

	@Override
	public BulkEnhancer override(Map<Method, Function<Object[], Object>> overrideMapper) {
		super.override(overrideMapper);
		return this;
	}

	@Override
	public BulkEnhancer defaults(Function<Method, Function<Object[], Object>> defaultGenerator) {
		super.defaults(defaultGenerator);
		return this;
	}

	@Override
	public BulkEnhancer defaults(Method method, Function<Object[], Object> defaultFunction) {
		super.defaults(method, defaultFunction);
		return this;
	}

	@Override
	public BulkEnhancer defaults(Map<Method, Function<Object[], Object>> defaultMapper) {
		super.defaults(defaultMapper);
		return this;
	}

	/**
	 * 添加无条件的拦截器，应用于所有方法
	 *
	 * @param interceptors 要应用的拦截器数组
	 * @return 当前增强器实例，支持链式调用
	 */
	public BulkEnhancer interceptors(Interceptor... interceptors) {
		checkState();
		for (Interceptor interceptor : interceptors) {
			this.interceptors.add(new MatchedInterceptor(null, interceptor));
		}
		return this;
	}

	/**
	 * 添加条件拦截器组，仅应用于匹配指定条件的方法
	 *
	 * @param predicate 方法匹配条件
	 * @param interceptors 要应用的拦截器数组
	 * @return 当前增强器实例，支持链式调用
	 */
	public BulkEnhancer interceptors(Predicate<Method> predicate, Interceptor... interceptors) {
		checkState();
		this.interceptors.add(new MatchedInterceptor(predicate, interceptors));
		return this;
	}

	/**
	 * 清空所有已配置的拦截器
	 *
	 * @return 当前增强器实例，支持链式调用
	 */
	public BulkEnhancer clear() {
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
		return new EnhancerKey(superclass, withFinal, interfaces, interceptors, serialVersion);
	}

	@RequiredArgsConstructor
	private static class EnhancerKey {
		private final Class<?> superclass;
		private final boolean withFinal;
		private final Class<?>[] interfaces;
		private final List<MatchedInterceptor> interceptors;
		private final Long serialVersion;

		@Override
		public int hashCode() {
			return Objects.hash(superclass, withFinal, Arrays.hashCode(interfaces), interceptors, serialVersion);
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof EnhancerKey)) return false;
			EnhancerKey that = (EnhancerKey) o;
			return withFinal == that.withFinal
				&& Objects.equals(superclass, that.superclass)
				&& Objects.deepEquals(interfaces, that.interfaces)
				&& Objects.equals(interceptors, that.interceptors)
				&& Objects.equals(serialVersion, that.serialVersion);
		}
	}

	/**
	 * 注册静态拦截器矩阵到生成的增强类
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
			Method setter = type.getDeclaredMethod(GENERATED$SET_STATIC_INTERCEPTOR.getName(), Interceptor[][].class);
			setter.invoke(null, new Object[]{this.matrix});
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(type + " is not an enhanced class");
		} catch (ReflectiveOperationException e) {
			throw new BytecodeOperationException(e);
		}
	}

	/**
	 * 绑定线程拦截器矩阵到生成的增强类
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
			Method setter = type.getDeclaredMethod(GENERATED$SET_THREAD_INTERCEPTOR.getName(), Interceptor[][].class);
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
	 * 为每个目标方法收集所有匹配的拦截器，形成二维拦截器矩阵
	 *
	 * @param targetMethods 目标方法数组
	 */
	@Override
	protected void saveTargetMethods(Method[] targetMethods) {
		Interceptor[][] matrix = new Interceptor[targetMethods.length][];
		for (int i = 0; i < targetMethods.length; i++) {
			List<Interceptor> list = new ArrayList<>();
			// 遍历所有拦截器组，收集匹配的拦截器
			for (MatchedInterceptor group : this.interceptors) {
				Interceptor[] interceptors = group.getInterceptors();
				if (interceptors != null && group.accept(targetMethods[i])) {
					list.addAll(Arrays.asList(interceptors));
				}
			}
			matrix[i] = list.toArray(new Interceptor[0]);
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
		ce.declare_field(AsmConsts.ACC_PRIVATE | AsmConsts.ACC_STATIC, GENERATED$STATIC_INTERCEPTOR, TYPE_INTERCEPTOR_ARRAY_ARRAY, null);
		ce.declare_field(AsmConsts.ACC_PRIVATE, GENERATED$INTERCEPTORS_MATRIX, TYPE_INTERCEPTOR_ARRAY_ARRAY, null);
	}

	/**
	 * 获取设置静态拦截器矩阵的方法签名
	 *
	 * @return 设置静态拦截器的方法签名
	 */
	@Override
	protected Signature getSetStaticInterceptorSignature() {
		return GENERATED$SET_STATIC_INTERCEPTOR;
	}

	/**
	 * 获取设置线程拦截器矩阵的方法签名
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
		Interceptor[][] var = (Interceptor[][]) GENERATED$THREAD_INTERCEPTOR.get();
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

		// 获取线程级别的拦截器
		e.getfield(GENERATED$THREAD_INTERCEPTOR);
		e.invoke_virtual(TYPE_THREAD_LOCAL, THREAD_LOCAL_GET);
		e.dup();
		Label found_callback = e.make_label();
		e.ifnonnull(found_callback);
		e.pop();

		// 使用静态拦截器
		e.getfield(GENERATED$STATIC_INTERCEPTOR);
		e.dup();
		e.ifnonnull(found_callback);
		e.pop();
		e.goTo(end);

		e.mark(found_callback);
		e.checkcast(TYPE_INTERCEPTOR_ARRAY_ARRAY);
		e.load_local(me);
		e.swap();
		e.putfield(GENERATED$INTERCEPTORS_MATRIX);
		////e.invoke_virtual(ce.getClassType(), GENERATED$SET_INTERCEPTOR);

		e.mark(end);
		e.return_value();
		e.end_method();
	}


	/**
	 * 发射生成所有目标方法的拦截逻辑
	 * <p>
	 * 为每个目标方法生成代理方法，检查是否存在拦截器数组，如果存在则执行拦截链逻辑，否则直接调用父类方法
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
				Interceptor[] interceptors = GENERATED$INTERCEPTORS_MATRIX[$index];
				if (interceptors == null || interceptors.length == 0) {
					return super.xxxx(args)
				}
				try {
				Invocation invocation = new Invocation($index, this, GENERATED$TARGET_METHODS[$index],
					GENERATED$RAW_INVOKER, GENERATED$SUPER_INVOKER,
					GENERATED$INTERCEPTORS_MATRIX[$index]);
				Object[] args = {...$args};
				Object rs = invocation.invoke(this, args);
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
				e.getfield(thisType, GENERATED$INTERCEPTORS_MATRIX, TYPE_INTERCEPTOR_ARRAY_ARRAY);
				e.ifnull(labelSuper);

				Local interceptors = e.make_local(TYPE_INTERCEPTOR_ARRAY);
				e.load_this();
				e.getfield(thisType, GENERATED$INTERCEPTORS_MATRIX, TYPE_INTERCEPTOR_ARRAY_ARRAY);
				e.push(i);
				e.aaload();
				e.store_local(interceptors);

				e.load_local(interceptors);
				e.ifnull(labelSuper);

				e.load_local(interceptors);
				e.arraylength();
				e.push(1);
				e.if_icmp(AsmConsts.IFLT, labelSuper);

				e.mark(labelInterceptor);
				Block handler = e.begin_block();
				Local invocation = e.make_local(TYPE_BULK_INVOCATION);
				// 创建批量调用上下文
				e.new_instance(TYPE_BULK_INVOCATION);
				e.dup();
				e.push(i);
				e.load_this();
				e.getstatic(thisType, GENERATED$TARGET_METHODS, TYPE_METHOD_ARRAY);
				e.push(i);
				e.aaload();
				e.getstatic(thisType, GENERATED$RAW_INVOKER, TYPE_INVOKER);
				e.getstatic(thisType, GENERATED$SUPER_INVOKER, TYPE_INVOKER);
				e.load_local(interceptors);
				e.invoke_constructor(TYPE_BULK_INVOCATION, CSTRUCT_BULK_INVOCATION);
				e.store_local(invocation);

				e.load_local(invocation);
				e.load_this();
				Type[] argumentTypes = method.getSignature().getArgumentTypes();
				// 处理参数数组：无参方法使用空数组，有参方法创建参数数组
				if (argumentTypes.length == 0) {
					e.getfield(GENERATED$EMPTY_ARGS);
				} else {
					e.create_arg_array();
				}
				e.invoke_virtual(TYPE_BULK_INVOCATION, METHOD_INVOCATION_INVOKE);
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

	/* @Override
	protected void emitSpecialMethod(ClassEmitter ce, List<MethodInfo> methods, Map<String, Method> undeclaredMethods) {
		emitSetInterceptors(ce, methods);
	}


	private void emitSetInterceptors(ClassEmitter ce, List<MethodInfo> methods) {
		Type thisType = ce.getClassType();
		CodeEmitter e = ce.begin_method(AsmConsts.ACC_PUBLIC, GENERATED$SET_INTERCEPTOR, null);
		 *//*public void GENERATED$SET_INTERCEPTORS(MatchedInterceptor[] groups) {
			Interceptor[][] matrix = Enhancer.toInterceptorMatrix(groups, GENERATED$TARGET_METHODS);
			this.GENERATED$INTERCEPTORS_MATRIX = matrix;
		}*//*
		Local matrix = e.make_local(TYPE_INTERCEPTOR_ARRAY_ARRAY);
		e.load_arg(0);
		e.getfield(GENERATED$TARGET_METHODS);
		e.invoke_static(TYPE_BULK_ENHANCER, TO_INTERCEPTOR_MATRIX, false);
		e.store_local(matrix);
		e.load_this();
		e.load_local(matrix);
		e.checkcast(TYPE_INTERCEPTOR_ARRAY_ARRAY);
		e.putfield(thisType, GENERATED$INTERCEPTORS_MATRIX, TYPE_INTERCEPTOR_ARRAY_ARRAY);
//		e.pop();
		e.return_value();
		e.end_method();
	} */

}
