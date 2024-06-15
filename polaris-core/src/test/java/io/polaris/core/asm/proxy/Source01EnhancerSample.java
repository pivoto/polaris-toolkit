//package io.polaris.core.asm.proxy;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//
///**
// * @author Qt
// * @since May 11, 2024
// */
//public abstract class Source01EnhancerSample extends Source01 {
//	private static final ThreadLocal GENERATED$THREAD_INTERCEPTOR = new ThreadLocal<>();
//	private static Interceptor[] GENERATED$STATIC_INTERCEPTOR;
//	private static final Method[] GENERATED$TARGET_METHODS;
//	private static final Invoker GENERATED$SUPER_INVOKER;
//	private static final Invoker GENERATED$RAW_INVOKER;
//
//	static {
//		GENERATED$TARGET_METHODS = Enhancer.findMethods(new String[]{}, Source01EnhancerSample.class.getDeclaredMethods());
//		GENERATED$SUPER_INVOKER = Source01EnhancerSample::GENERATED$INVOKE_SUPER;
//		GENERATED$RAW_INVOKER = Source01EnhancerSample::GENERATED$INVOKE_RAW;
//	}
//
//	private Interceptor[] GENERATED$INTERCEPTORS_MATRIX;
//
//	public Source01EnhancerSample() {
//		GENERATED$BIND_INTERCEPTOR(this);
//	}
//
//	private static void GENERATED$SET_STATIC_INTERCEPTOR(Interceptor[] args) {
//		GENERATED$STATIC_INTERCEPTOR = args;
//	}
//
//	public static void GENERATED$SET_THREAD_INTERCEPTOR(Interceptor[] args) {
//		GENERATED$THREAD_INTERCEPTOR.set(args);
//	}
//
//	public static void GENERATED$DEL_THREAD_INTERCEPTOR() {
//		GENERATED$THREAD_INTERCEPTOR.remove();
//	}
//
//	private static void GENERATED$BIND_INTERCEPTOR(Object o) {
//		Source01EnhancerSample target = (Source01EnhancerSample) o;
//		Interceptor[] matrix = (Interceptor[]) GENERATED$THREAD_INTERCEPTOR.get();
//		if (matrix == null) {
//			matrix = GENERATED$STATIC_INTERCEPTOR;
//			if (matrix == null) {
//				return;
//			}
//		}
//		target.GENERATED$INTERCEPTORS_MATRIX = matrix;
//	}
//
//
//
//	void GENERATED$abstractMethod() {
//		throw new AbstractMethodError();
//	}
//
//	Object abstractMethod(int a,byte b,char c) throws InvocationTargetException{
//		try {
//			int index = 1;
//			Interceptor interceptor = GENERATED$INTERCEPTORS_MATRIX[index];
//			if (interceptor == null ) {
//
//			}
//			Invocation invocation = new DefaultInvocation(index, this, GENERATED$TARGET_METHODS[index],
//				GENERATED$RAW_INVOKER, GENERATED$SUPER_INVOKER);
//			Object[] args = {a, b, c};
//			Object rs = interceptor.intercept(this, GENERATED$TARGET_METHODS[index], args, invocation);
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
//		Source01EnhancerSample o = (Source01EnhancerSample) obj;
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
