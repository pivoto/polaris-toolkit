package io.polaris.core.asm.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.polaris.core.asm.internal.*;
import io.polaris.core.err.BytecodeOperationException;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since May 11, 2024
 */
public class BulkEnhancer extends AbstractEnhancer {
	protected static final Type TYPE_BULK_ENHANCER = Type.getType(BulkEnhancer.class);
	protected static final Type TYPE_BULK_INVOCATION = Type.getType(BulkInvocation.class);

//	protected static final Signature TO_INTERCEPTOR_MATRIX =
//		new Signature("toInterceptorMatrix", TYPE_INTERCEPTOR_ARRAY_ARRAY, new Type[]{
//			TYPE_MATCHED_INTERCEPTOR_ARRAY, TYPE_METHOD_ARRAY
//		});
	protected static final Signature CSTRUCT_BULK_INVOCATION = AsmTypes.parseConstructor(new Type[]{
		Type.INT_TYPE, AsmConsts.TYPE_OBJECT, TYPE_METHOD,
		TYPE_INVOKER, TYPE_INVOKER, TYPE_INTERCEPTOR_ARRAY,
	});
	protected static final Signature METHOD_INVOCATION_INVOKE =
		new Signature("invoke", AsmConsts.TYPE_OBJECT, new Type[]{
			AsmConsts.TYPE_OBJECT, AsmConsts.TYPE_OBJECT_ARRAY
		});


	protected static final Signature GENERATED$SET_STATIC_INTERCEPTOR =
		new Signature("GENERATED$SET_STATIC_INTERCEPTOR", Type.VOID_TYPE, new Type[]{
			TYPE_INTERCEPTOR_ARRAY_ARRAY
		});
	protected static final Signature GENERATED$SET_THREAD_INTERCEPTOR =
		new Signature("GENERATED$SET_THREAD_INTERCEPTOR", Type.VOID_TYPE, new Type[]{
			TYPE_INTERCEPTOR_ARRAY_ARRAY
		});
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


	private final List<MatchedInterceptor> interceptors = new ArrayList<>();
	private Interceptor[][] matrix;

	public BulkEnhancer() {
		super();
	}

	@Override
	public BulkEnhancer serialVersionUID(Long serialVersionUID) {
		return (BulkEnhancer) super.serialVersionUID(serialVersionUID);
	}

	@Override
	public BulkEnhancer superclass(Class<?> superclass) {
		return (BulkEnhancer) super.superclass(superclass);
	}

	@Override
	public BulkEnhancer withFinal(boolean withFinal) {
		return (BulkEnhancer) super.withFinal(withFinal);
	}

	@Override
	public BulkEnhancer interfaces(Class<?>[] interfaces) {
		return (BulkEnhancer) super.interfaces(interfaces);
	}

	public BulkEnhancer interceptors(Interceptor... interceptors) {
		checkState();
		for (Interceptor interceptor : interceptors) {
			this.interceptors.add(new MatchedInterceptor(null, interceptor));
		}
		return this;
	}

	public BulkEnhancer interceptors(Predicate<Method> predicate, Interceptor... interceptors) {
		checkState();
		this.interceptors.add(new MatchedInterceptor(predicate, interceptors));
		return this;
	}

	public BulkEnhancer clear() {
		checkState();
		this.interceptors.clear();
		return this;
	}

	@Override
	protected Object generateKey() {
		return new Object[]{superclass, withFinal, interfaces, interceptors, serialVersion};
	}


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

	@Override
	protected void saveTargetMethods(Method[] targetMethods){
		Interceptor[][] matrix = new Interceptor[targetMethods.length][];
		for (int i = 0; i < targetMethods.length; i++) {
			List<Interceptor> list = new ArrayList<>();
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


	@Override
	protected void emitSpecialFields(ClassEmitter ce) {
		ce.declare_field(AsmConsts.ACC_PRIVATE | AsmConsts.ACC_STATIC, GENERATED$STATIC_INTERCEPTOR, TYPE_INTERCEPTOR_ARRAY_ARRAY, null);
		ce.declare_field(AsmConsts.ACC_PRIVATE, GENERATED$INTERCEPTORS_MATRIX, TYPE_INTERCEPTOR_ARRAY_ARRAY, null);
	}

	@Override
	protected Signature getSetStaticInterceptorSignature() {
		return GENERATED$SET_STATIC_INTERCEPTOR;
	}

	@Override
	protected Signature getSetThreadInterceptorSignature() {
		return GENERATED$SET_THREAD_INTERCEPTOR;
	}


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
		e.checkcast(TYPE_INTERCEPTOR_ARRAY_ARRAY);
		e.load_local(me);
		e.swap();
		e.putfield(GENERATED$INTERCEPTORS_MATRIX);
		////e.invoke_virtual(ce.getClassType(), GENERATED$SET_INTERCEPTOR);

		e.mark(end);
		e.return_value();
		e.end_method();
	}


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
				// 调用本类生成的GENERATED$SUPER_方法以兼容接口方法
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
