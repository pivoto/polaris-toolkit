//package io.polaris.core.asm.proxy;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//
///**
// * @author Qt
// * @since May 11, 2024
// */
//public abstract class Source01BulkEnhancerSample extends Source01 {
//	private static final ThreadLocal GENERATED$THREAD_INTERCEPTOR = new ThreadLocal<>();
//	private static MatchedInterceptor[] GENERATED$STATIC_INTERCEPTOR;
//	private static final Method[] GENERATED$TARGET_METHODS;
//	private static final Invoker GENERATED$SUPER_INVOKER;
//	private static final Invoker GENERATED$RAW_INVOKER;
//
//	static {
//		GENERATED$TARGET_METHODS = BulkEnhancer.findMethods(new String[]{}, Source01BulkEnhancerSample.class.getDeclaredMethods());
//		GENERATED$SUPER_INVOKER = Source01BulkEnhancerSample::GENERATED$INVOKE_SUPER;
//		GENERATED$RAW_INVOKER = Source01BulkEnhancerSample::GENERATED$INVOKE_RAW;
//	}
//
//	private Interceptor[][] GENERATED$INTERCEPTORS_MATRIX;
//
//	public Source01BulkEnhancerSample() {
//		GENERATED$BIND_INTERCEPTOR(this);
//	}
//
//	private static void GENERATED$SET_STATIC_INTERCEPTOR(MatchedInterceptor[] args) {
//		GENERATED$STATIC_INTERCEPTOR = args;
//	}
//
//	public static void GENERATED$SET_THREAD_INTERCEPTOR(MatchedInterceptor[] args) {
//		GENERATED$THREAD_INTERCEPTOR.set(args);
//	}
//
//	public static void GENERATED$DEL_THREAD_INTERCEPTOR() {
//		GENERATED$THREAD_INTERCEPTOR.remove();
//	}
//
//	private static void GENERATED$BIND_INTERCEPTOR(Object o) {
//		Source01BulkEnhancerSample target = (Source01BulkEnhancerSample) o;
//		MatchedInterceptor[] groups = (MatchedInterceptor[]) GENERATED$THREAD_INTERCEPTOR.get();
//		if (groups == null) {
//			groups = GENERATED$STATIC_INTERCEPTOR;
//			if (groups == null) {
//				return;
//			}
//		}
//		target.setInterceptors(groups);
//	}
//
//	public void setInterceptors(MatchedInterceptor[] groups) {
//		Interceptor[][] matrix = BulkEnhancer.toInterceptorMatrix(groups, GENERATED$TARGET_METHODS);
//		this.GENERATED$INTERCEPTORS_MATRIX = matrix;
//	}
//
//
//	void GENERATED$abstractMethod() {
//		throw new AbstractMethodError();
//	}
//
//	Object abstractMethod(int a,byte b,char c) throws InvocationTargetException{
//		try {
//			int index = 1;
//			Interceptor[] interceptors = GENERATED$INTERCEPTORS_MATRIX[index];
//			if (interceptors == null || interceptors.length == 0) {
//
//			}
//			Invocation invocation = new BulkInvocation(index, this, GENERATED$TARGET_METHODS[index],
//				GENERATED$RAW_INVOKER, GENERATED$SUPER_INVOKER,
//				interceptors);
//			Object[] args = {a, b, c};
//			Object rs = invocation.invoke(this, args);
//			return rs;
//		} catch (RuntimeException e) {
//			throw e;
//		} catch (Error e) {
//			throw e;
//		} catch (InvocationTargetException e) {
//			throw e;
//		} catch (Throwable e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//
//	private static Object GENERATED$INVOKE_SUPER(int index, Object obj, Object[] args) throws InvocationTargetException {
//		Source01BulkEnhancerSample o = (Source01BulkEnhancerSample) obj;
//		try {
//			switch (index) {
//				case 1:
//					o.GENERATED$abstractMethod();
//					return null;
//				default:
//					throw new NoSuchMethodException();
//			}
//		} catch (Throwable e) {
//			throw new InvocationTargetException(e);
//		}
//	}
//
//	private static Object GENERATED$INVOKE_RAW(int index, Object obj, Object[] args) throws InvocationTargetException {
//		Source01 o = (Source01) obj;
//		try {
//			switch (index) {
//				case 1:
//					return null;
//				default:
//					throw new NoSuchMethodException();
//			}
//		} catch (Throwable e) {
//			throw new InvocationTargetException(e);
//		}
//	}
//
//}
