package io.polaris.core.asm.proxy.sample;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.polaris.core.asm.proxy.BulkInvocation;
import io.polaris.core.asm.proxy.Interceptor;
import io.polaris.core.asm.proxy.Invoker;
import io.polaris.core.err.InvocationException;

public class Source01BulkEnhancerSample extends Source01 {
	private static final Class GENERATED$ENHANCED = Source01.class;
	private static Method[] GENERATED$TARGET_METHODS;
	private static final ThreadLocal GENERATED$THREAD_INTERCEPTOR = new ThreadLocal<>();
	private static final Invoker GENERATED$SUPER_INVOKER;
	private static final Invoker GENERATED$RAW_INVOKER;
	private static final Object[] GENERATED$EMPTY_ARGS;
	private static Interceptor[][] GENERATED$STATIC_INTERCEPTOR;
	private Interceptor[][] GENERATED$INTERCEPTORS_MATRIX;

	static {
		GENERATED$EMPTY_ARGS = new Object[0];
		GENERATED$RAW_INVOKER = Source01BulkEnhancerSample::GENERATED$INVOKE_RAW;
		GENERATED$SUPER_INVOKER = Source01BulkEnhancerSample::GENERATED$INVOKE_SUPER;
	}

	public static void GENERATED$SET_TARGET_METHODS(Method[] methods) {
		GENERATED$TARGET_METHODS = methods;
	}

	public static void GENERATED$SET_STATIC_INTERCEPTOR(Interceptor[][] interceptors) {
		GENERATED$STATIC_INTERCEPTOR = interceptors;
	}

	public static void GENERATED$SET_THREAD_INTERCEPTOR(Interceptor[][] interceptors) {
		GENERATED$THREAD_INTERCEPTOR.set(interceptors);
	}

	public static void GENERATED$DEL_THREAD_INTERCEPTOR() {
		GENERATED$THREAD_INTERCEPTOR.remove();
	}

	private static void GENERATED$BIND_INTERCEPTOR(Object target) {
		Source01BulkEnhancerSample obj = (Source01BulkEnhancerSample) target;
		Object interceptors = GENERATED$THREAD_INTERCEPTOR.get();
		if (interceptors == null) {
			interceptors = GENERATED$STATIC_INTERCEPTOR;
			if (interceptors == null) {
				return;
			}
		}
		obj.GENERATED$INTERCEPTORS_MATRIX = (Interceptor[][]) interceptors;
	}

	private static Object GENERATED$INVOKE_RAW(int index, Object obj, Object[] args) throws InvocationTargetException {
		Source01 o = (Source01) obj;
		try {
			switch (index) {
				case 0:
					return o.apply((Source01) args[0]);
				case 1:
					return null;
				// ...
			}
		} catch (Throwable e) {
			throw new InvocationTargetException(e);
		}
		throw new IllegalArgumentException("Cannot find matching method");
	}

	private static Object GENERATED$INVOKE_SUPER(int index, Object obj, Object[] args) throws InvocationTargetException {
		Source01BulkEnhancerSample o = (Source01BulkEnhancerSample) obj;
		try {
			switch (index) {
				case 0:
					return o.GENERATED$SUPER_apply((Source01) args[0]);
				case 1:
					return null;
				// ...
			}
		} catch (Throwable e) {
			throw new InvocationTargetException(e);
		}
		throw new IllegalArgumentException("Cannot find matching method");
	}

	public Source01BulkEnhancerSample() {
		super();
		GENERATED$BIND_INTERCEPTOR(this);
	}

	@Override
	public final Source01 apply(Source01 arg0) {
		if (this.GENERATED$INTERCEPTORS_MATRIX != null) {
			Interceptor[] interceptors = this.GENERATED$INTERCEPTORS_MATRIX[0];
			if (interceptors != null && interceptors.length >= 1) {
				try {
					BulkInvocation invocation = new BulkInvocation(0, this, GENERATED$TARGET_METHODS[0], GENERATED$RAW_INVOKER, GENERATED$SUPER_INVOKER, interceptors);
					Object rs = invocation.invoke(this, new Object[]{arg0});
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

	private final Source01 GENERATED$SUPER_apply(Source01 var1) {
		return super.apply(var1);
	}

}
