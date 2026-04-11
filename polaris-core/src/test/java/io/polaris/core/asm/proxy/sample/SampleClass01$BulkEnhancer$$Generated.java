//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.polaris.core.asm.proxy.sample;

import io.polaris.core.asm.proxy.BulkInvocation;
import io.polaris.core.asm.proxy.Interceptor;
import io.polaris.core.asm.proxy.Invoker;
import io.polaris.core.err.InvocationException;

import java.lang.reflect.Method;
import java.util.function.Function;

public class SampleClass01$BulkEnhancer$$Generated extends SampleClass01 {
	private static final Class GENERATED$ENHANCED;
	private static Method[] GENERATED$TARGET_METHODS;
	private static Function[] GENERATED$OVERRIDE_FUNCS;
	private static Function[] GENERATED$DEFAULT_FUNCS;
	private static final ThreadLocal GENERATED$THREAD_INTERCEPTOR;
	private static final Invoker GENERATED$SUPER_INVOKER;
	private static final Invoker GENERATED$RAW_INVOKER;
	private static final Object[] GENERATED$EMPTY_ARGS;
	private static Interceptor[][] GENERATED$STATIC_INTERCEPTOR;
	private Interceptor[][] GENERATED$INTERCEPTORS_MATRIX;

	static   {
		GENERATED$ENHANCED = SampleClass01.class;
		GENERATED$THREAD_INTERCEPTOR = new ThreadLocal();
		GENERATED$EMPTY_ARGS = new Object[0];
		GENERATED$RAW_INVOKER = SampleClass01$BulkEnhancer$$Generated::GENERATED$INVOKE_RAW;
		GENERATED$SUPER_INVOKER = SampleClass01$BulkEnhancer$$Generated::GENERATED$INVOKE_SUPER;
	}

	public static void GENERATED$SET_TARGET_METHODS(Method[] var0) {
		GENERATED$TARGET_METHODS = var0;
	}

	public static void GENERATED$SET_OVERRIDE_FUNCS(Function[] var0) {
		GENERATED$OVERRIDE_FUNCS = var0;
	}

	public static void GENERATED$SET_DEFAULT_FUNCS(Function[] var0) {
		GENERATED$DEFAULT_FUNCS = var0;
	}

	public static void GENERATED$SET_STATIC_INTERCEPTOR(Interceptor[][] var0) {
		GENERATED$STATIC_INTERCEPTOR = var0;
	}

	public static void GENERATED$SET_THREAD_INTERCEPTOR(Interceptor[][] var0) {
		GENERATED$THREAD_INTERCEPTOR.set(var0);
	}

	public static void GENERATED$DEL_THREAD_INTERCEPTOR() {
		GENERATED$THREAD_INTERCEPTOR.remove();
	}

	private static void GENERATED$BIND_INTERCEPTOR(Object var0) {
		SampleClass01$BulkEnhancer$$Generated var1 = (SampleClass01$BulkEnhancer$$Generated)var0;
		Object var10000 = GENERATED$THREAD_INTERCEPTOR.get();
		if (var10000 == null) {
			var10000 = GENERATED$STATIC_INTERCEPTOR;
			if (var10000 == null) {
				return;
			}
		}

		var1.GENERATED$INTERCEPTORS_MATRIX = (Interceptor[][])var10000;
	}

	private static final Object GENERATED$INVOKE_RAW(int var0, Object var1, Object var2, Object[] var3) throws Throwable {
		switch (var0) {
			case 0:
				if (GENERATED$OVERRIDE_FUNCS != null) {
					Function var4 = GENERATED$OVERRIDE_FUNCS[0];
					if (var4 != null) {
						return var4.apply(var3);
					}
				}

				if (var2 instanceof Object) {
					return new Boolean(var2.equals(var3[0]));
				}

				return new Boolean(((SampleClass01$BulkEnhancer$$Generated)var1).GENERATED$SUPER_equals(var3[0]));
			case 1:
				if (GENERATED$OVERRIDE_FUNCS != null) {
					Function var6 = GENERATED$OVERRIDE_FUNCS[1];
					if (var6 != null) {
						return var6.apply(var3);
					}
				}

				if (var2 instanceof Object) {
					return var2.toString();
				}

				return ((SampleClass01$BulkEnhancer$$Generated)var1).GENERATED$SUPER_toString();
			case 2:
				if (GENERATED$OVERRIDE_FUNCS != null) {
					Function var8 = GENERATED$OVERRIDE_FUNCS[2];
					if (var8 != null) {
						return var8.apply(var3);
					}
				}

				if (var2 instanceof Object) {
					return new Integer(var2.hashCode());
				}

				return new Integer(((SampleClass01$BulkEnhancer$$Generated)var1).GENERATED$SUPER_hashCode());
			default:
				throw new IllegalArgumentException("Cannot find matching method");
		}
	}

	private static final Object GENERATED$INVOKE_SUPER(int var0, Object var1, Object var2, Object[] var3) throws Throwable {
		switch (var0) {
			case 0:
				return new Boolean(((SampleClass01$BulkEnhancer$$Generated)var2).GENERATED$SUPER_equals(var3[0]));
			case 1:
				return ((SampleClass01$BulkEnhancer$$Generated)var2).GENERATED$SUPER_toString();
			case 2:
				return new Integer(((SampleClass01$BulkEnhancer$$Generated)var2).GENERATED$SUPER_hashCode());
			default:
				throw new IllegalArgumentException("Cannot find matching method");
		}
	}

	public SampleClass01$BulkEnhancer$$Generated() {
		GENERATED$BIND_INTERCEPTOR(this);
	}

	public final boolean equals(Object var1) {
		if (this.GENERATED$INTERCEPTORS_MATRIX != null) {
			Interceptor[] var2 = this.GENERATED$INTERCEPTORS_MATRIX[0];
			if (var2 != null && var2.length >= 1) {
				try {
					BulkInvocation var3 = new BulkInvocation(0, this, GENERATED$TARGET_METHODS[0], GENERATED$RAW_INVOKER, GENERATED$SUPER_INVOKER, var2);
					Object var4 = var3.invoke(this, new Object[]{var1});
					return (Boolean)var4;
				} catch (Error | RuntimeException var5) {
					throw var5;
				} catch (Throwable var6) {
					throw new InvocationException(var6);
				}
			}
		}

		return this.GENERATED$SUPER_equals(var1);
	}

	public final String toString() {
		if (this.GENERATED$INTERCEPTORS_MATRIX != null) {
			Interceptor[] var1 = this.GENERATED$INTERCEPTORS_MATRIX[1];
			if (var1 != null && var1.length >= 1) {
				try {
					BulkInvocation var2 = new BulkInvocation(1, this, GENERATED$TARGET_METHODS[1], GENERATED$RAW_INVOKER, GENERATED$SUPER_INVOKER, var1);
					Object var3 = var2.invoke(this, GENERATED$EMPTY_ARGS);
					return (String)var3;
				} catch (Error | RuntimeException var4) {
					throw var4;
				} catch (Throwable var5) {
					throw new InvocationException(var5);
				}
			}
		}

		return this.GENERATED$SUPER_toString();
	}

	public final int hashCode() {
		if (this.GENERATED$INTERCEPTORS_MATRIX != null) {
			Interceptor[] var1 = this.GENERATED$INTERCEPTORS_MATRIX[2];
			if (var1 != null && var1.length >= 1) {
				try {
					BulkInvocation var2 = new BulkInvocation(2, this, GENERATED$TARGET_METHODS[2], GENERATED$RAW_INVOKER, GENERATED$SUPER_INVOKER, var1);
					Object var3 = var2.invoke(this, GENERATED$EMPTY_ARGS);
					return ((Number)var3).intValue();
				} catch (Error | RuntimeException var4) {
					throw var4;
				} catch (Throwable var5) {
					throw new InvocationException(var5);
				}
			}
		}

		return this.GENERATED$SUPER_hashCode();
	}

	private final boolean GENERATED$SUPER_equals(Object var1) {
		if (GENERATED$OVERRIDE_FUNCS != null) {
			Function var2 = GENERATED$OVERRIDE_FUNCS[0];
			if (var2 != null) {
				return (Boolean)var2.apply(new Object[]{var1});
			}
		}

		return super.equals(var1);
	}

	private final String GENERATED$SUPER_toString() {
		if (GENERATED$OVERRIDE_FUNCS != null) {
			Function var1 = GENERATED$OVERRIDE_FUNCS[1];
			if (var1 != null) {
				return (String)var1.apply(GENERATED$EMPTY_ARGS);
			}
		}

		return super.toString();
	}

	private final int GENERATED$SUPER_hashCode() {
		if (GENERATED$OVERRIDE_FUNCS != null) {
			Function var1 = GENERATED$OVERRIDE_FUNCS[2];
			if (var1 != null) {
				return ((Number)var1.apply(GENERATED$EMPTY_ARGS)).intValue();
			}
		}

		return super.hashCode();
	}

	static {
	}
}
