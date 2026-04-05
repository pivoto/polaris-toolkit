package io.polaris.core.asm.proxy.sample;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.polaris.core.asm.proxy.DefaultInvocation;
import io.polaris.core.asm.proxy.Interceptor;
import io.polaris.core.asm.proxy.Invoker;
import io.polaris.core.err.InvocationException;

/**
 * @author Qt
 * @since May 11, 2024
 */
public class Source01EnhancerSample extends Source01 {
	private static final Class GENERATED$ENHANCED;
	private static Method[] GENERATED$TARGET_METHODS;
	private static final ThreadLocal GENERATED$THREAD_INTERCEPTOR;
	private static final Invoker GENERATED$SUPER_INVOKER;
	private static final Invoker GENERATED$RAW_INVOKER;
	private static final Object[] GENERATED$EMPTY_ARGS;
	private static Interceptor[] GENERATED$STATIC_INTERCEPTOR;
	private Interceptor[] GENERATED$INTERCEPTORS_MATRIX;

	static {
		GENERATED$ENHANCED = Source01.class;
		GENERATED$THREAD_INTERCEPTOR = new ThreadLocal();
		GENERATED$EMPTY_ARGS = new Object[0];
		GENERATED$RAW_INVOKER = Source01EnhancerSample::GENERATED$INVOKE_RAW;
		GENERATED$SUPER_INVOKER = Source01EnhancerSample::GENERATED$INVOKE_SUPER;
	}

	public static void GENERATED$SET_TARGET_METHODS(Method[] methods) {
		GENERATED$TARGET_METHODS = methods;
	}

	public static void GENERATED$SET_STATIC_INTERCEPTOR(Interceptor[] interceptors) {
		GENERATED$STATIC_INTERCEPTOR = interceptors;
	}

	public static void GENERATED$SET_THREAD_INTERCEPTOR(Interceptor[] interceptors) {
		GENERATED$THREAD_INTERCEPTOR.set(interceptors);
	}

	public static void GENERATED$DEL_THREAD_INTERCEPTOR() {
		GENERATED$THREAD_INTERCEPTOR.remove();
	}

	private static void GENERATED$BIND_INTERCEPTOR(Object target) {
		Source01EnhancerSample obj = (Source01EnhancerSample) target;
		Object interceptors = GENERATED$THREAD_INTERCEPTOR.get();
		if (interceptors == null) {
			interceptors = GENERATED$STATIC_INTERCEPTOR;
			if (interceptors == null) {
				return;
			}
		}
		obj.GENERATED$INTERCEPTORS_MATRIX = (Interceptor[]) interceptors;
	}

	private static final Object GENERATED$INVOKE_RAW(int index, Object target, Object[] args) throws InvocationTargetException {
		try {
			switch (index) {
				case 0:
					return ((Source01) target).apply((Source01) args[0]);
				//...
			}
		} catch (Throwable var3) {
			throw new InvocationTargetException(var3);
		}

		throw new IllegalArgumentException("Cannot find matching method");
	}

	private static final Object GENERATED$INVOKE_SUPER(int index, Object target, Object[] args) throws InvocationTargetException {
		Source01EnhancerSample obj = (Source01EnhancerSample) target;
		try {
			switch (index) {
				case 0:
					return obj.GENERATED$SUPER_apply((Source01) args[0]);
				//...
			}
		} catch (Throwable var4) {
			throw new InvocationTargetException(var4);
		}

		throw new IllegalArgumentException("Cannot find matching method");
	}

	public Source01EnhancerSample() {
		GENERATED$BIND_INTERCEPTOR(this);
	}

	public Source01EnhancerSample(int var1) {
		super(var1);
		GENERATED$BIND_INTERCEPTOR(this);
	}

	@Override
	public Source01 apply(Source01 arg0) {
		if (this.GENERATED$INTERCEPTORS_MATRIX != null) {
			Interceptor interceptor = this.GENERATED$INTERCEPTORS_MATRIX[0];
			if (interceptor != null) {
				try {
					Method method = GENERATED$TARGET_METHODS[0];
					DefaultInvocation invocation = new DefaultInvocation(0, this, method, GENERATED$RAW_INVOKER, GENERATED$SUPER_INVOKER);
					Object rs = interceptor.intercept(this, method, new Object[]{arg0}, invocation);
					return (Source01) rs;
				} catch (Error | RuntimeException e) {
					throw e;
				} catch (Throwable e) {
					throw new InvocationException(e);
				}
			}
		}

		return this.GENERATED$SUPER_apply(arg0);
	}

	private final Source01 GENERATED$SUPER_apply(Source01 arg0) {
		return super.apply(arg0);
	}

}
