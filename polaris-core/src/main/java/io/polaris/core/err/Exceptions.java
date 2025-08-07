package io.polaris.core.err;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import io.polaris.core.function.Executable;

/**
 * 异常处理工具类，提供各种处理异常的便捷方法。
 *
 * <p>该类包含以下功能：</p>
 * <ul>
 *   <li>异常类型查找和转换</li>
 *   <li>异常根因分析</li>
 *   <li>静默执行方法（忽略异常）</li>
 *   <li>异常堆栈跟踪信息获取</li>
 *   <li>异常匹配检查</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>
 * // 获取异常的根因
 * Throwable rootCause = Exceptions.getRootCause(exception);
 *
 * // 静默执行代码，忽略可能的异常
 * String result = Exceptions.getQuietly(() -> someMethodThatMayThrow());
 *
 * // 检查异常是否属于指定类型
 * boolean isIOException = Exceptions.matches(exception, IOException.class);
 * </pre>
 *
 * @author Qt
 * @since 1.8
 */
public class Exceptions {


	/**
	 * 从Throwable中查找指定类型的异常，如果找不到则使用Function构建器创建新的异常实例
	 *
	 * @param <T> 异常类型
	 * @param t 要查找的Throwable对象，可以为null
	 * @param type 要查找的异常类型
	 * @param builder 当找不到指定类型异常时用于创建新异常的函数
	 * @return 找到的指定类型异常或新创建的异常实例
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Exception> T of(Throwable t, Class<T> type, Function<Throwable, T> builder) {
		if (t == null) {
			return builder.apply(t);
		}
		if (type.isAssignableFrom(t.getClass())) {
			return (T) t;
		}
		while (t.getCause() != null) {
			t = t.getCause();
			if (type.isAssignableFrom(t.getClass())) {
				return (T) t;
			}
		}
		return builder.apply(t);
	}

	/**
	 * 从Throwable中查找指定类型的异常，如果找不到则使用Supplier构建器创建新的异常实例
	 *
	 * @param <T> 异常类型
	 * @param t 要查找的Throwable对象，可以为null
	 * @param type 要查找的异常类型
	 * @param builder 当找不到指定类型异常时用于创建新异常的供应器
	 * @return 找到的指定类型异常或新创建的异常实例
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Exception> T of(Throwable t, Class<T> type, Supplier<T> builder) {
		if (t == null) {
			return builder.get();
		}
		if (type.isAssignableFrom(t.getClass())) {
			return (T) t;
		}
		while (t.getCause() != null) {
			t = t.getCause();
			if (type.isAssignableFrom(t.getClass())) {
				return (T) t;
			}
		}
		return builder.get();
	}

	/**
	 * 获取Throwable的根因异常，直到满足指定条件为止
	 *
	 * @param t Throwable对象
	 * @param predicate 判断条件
	 * @return 满足条件的Throwable对象或最后一个根因异常
	 */
	public static Throwable getRootCauseUntil(@Nonnull Throwable t, Predicate<Throwable> predicate) {
		while (t.getCause() != null) {
			t = t.getCause();
			if (predicate.test(t)) {
				return t;
			}
		}
		return t;
	}

	/**
	 * 安全地获取Throwable的根因异常，直到满足指定条件为止，防止循环引用
	 *
	 * @param t Throwable对象
	 * @param predicate 判断条件
	 * @return 满足条件的Throwable对象或最后一个根因异常
	 */
	public static Throwable getRootCauseSafelyUntil(@Nonnull Throwable t, Predicate<Throwable> predicate) {
		Set<Throwable> set = new HashSet<>();
		while (t.getCause() != null && !set.contains(t)) {
			set.add(t);
			t = t.getCause();
			if (predicate.test(t)) {
				return t;
			}
		}
		return t;
	}

	/**
	 * 获取Throwable的根因异常
	 *
	 * @param t Throwable对象
	 * @return 根因异常
	 */
	public static Throwable getRootCause(@Nonnull Throwable t) {
		while (t.getCause() != null) {
			t = t.getCause();
		}
		return t;
	}

	/**
	 * 安全地获取Throwable的根因异常，防止循环引用
	 *
	 * @param t Throwable对象
	 * @return 根因异常
	 */
	public static Throwable getRootCauseSafely(@Nonnull Throwable t) {
		Set<Throwable> set = new HashSet<>();
		while (t.getCause() != null && !set.contains(t)) {
			set.add(t);
			t = t.getCause();
		}
		return t;
	}

	/**
	 * 获取Throwable的因果链路径
	 *
	 * @param t Throwable对象
	 * @return 包含所有因果关系异常的集合
	 */
	public static Set<Throwable> getCausePath(@Nonnull Throwable t) {
		Set<Throwable> set = new LinkedHashSet<>();
		while (t != null && !set.contains(t)) {
			set.add(t);
			t = t.getCause();
		}
		return set;
	}

	/**
	 * 检查Throwable或其因果链中是否存在满足条件的异常
	 *
	 * @param t Throwable对象
	 * @param predicate 判断条件
	 * @return 满足条件的Throwable对象，如果不存在则返回null
	 */
	public static Throwable hasCause(@Nonnull Throwable t, Predicate<Throwable> predicate) {
		while (t != null) {
			if (predicate.test(t)) {
				return t;
			}
			t = t.getCause();
		}
		return t;
	}

	/**
	 * 检查Throwable或其因果链中是否存在指定类型的异常
	 *
	 * @param t Throwable对象
	 * @param type 要检查的异常类型
	 * @return 找到的指定类型异常，如果不存在则返回null
	 */
	public static Throwable hasCause(@Nonnull Throwable t, Class<? extends Throwable> type) {
		while (t != null) {
			if (type.isAssignableFrom(t.getClass())) {
				return t;
			}
			t = t.getCause();
		}
		return t;
	}

	/**
	 * 获取Throwable的堆栈跟踪信息
	 *
	 * @param t Throwable对象
	 * @return 堆栈跟踪信息字符串
	 */
	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		return sw.toString();
	}

	/**
	 * 静默关闭AutoCloseable资源，忽略可能发生的异常
	 *
	 * @param closeable 可关闭的资源
	 */
	public static void quietlyClose(AutoCloseable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Throwable e) {
				// ignore
			}
		}
	}

	/**
	 * 静默关闭AutoCloseable资源，将可能发生的异常添加为指定Throwable的抑制异常
	 *
	 * @param closeable 可关闭的资源
	 * @param t 用于添加抑制异常的Throwable
	 */
	public static void quietlyClose(AutoCloseable closeable, Throwable t) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Throwable e) {
				t.addSuppressed(e);
			}
		}
	}

	/**
	 * 静默执行Runnable，忽略可能发生的异常
	 *
	 * @param runnable 要执行的Runnable
	 */
	public static void runQuietly(Runnable runnable) {
		if (runnable != null) {
			try {
				runnable.run();
			} catch (Throwable e) {
				// ignore
			}
		}
	}

	/**
	 * 静默执行Runnable，使用指定的消费者处理可能发生的异常
	 *
	 * @param runnable 要执行的Runnable
	 * @param consumer 用于处理异常的消费者
	 */
	public static void runQuietly(Runnable runnable, Consumer<Throwable> consumer) {
		if (runnable != null) {
			try {
				runnable.run();
			} catch (Throwable e) {
				consumer.accept(e);
			}
		}
	}

	/**
	 * 静默执行Runnable，将可能发生的异常添加为指定Throwable的抑制异常
	 *
	 * @param runnable 要执行的Runnable
	 * @param t 用于添加抑制异常的Throwable
	 */
	public static void runQuietly(Runnable runnable, Throwable t) {
		if (runnable != null) {
			try {
				runnable.run();
			} catch (Throwable e) {
				t.addSuppressed(e);
			}
		}
	}

	/**
	 * 静默获取Supplier的值，忽略可能发生的异常
	 *
	 * @param <T> 返回值类型
	 * @param supplier 提供值的Supplier
	 * @return Supplier提供的值，如果发生异常则返回null
	 */
	public static <T> T getQuietly(Supplier<T> supplier) {
		if (supplier != null) {
			try {
				return supplier.get();
			} catch (Throwable e) {
				// ignore
			}
		}
		return null;
	}

	/**
	 * 静默获取Supplier的值，使用指定的消费者处理可能发生的异常
	 *
	 * @param <T> 返回值类型
	 * @param supplier 提供值的Supplier
	 * @param consumer 用于处理异常的消费者
	 * @return Supplier提供的值，如果发生异常则返回null
	 */
	public static <T> T getQuietly(Supplier<T> supplier, Consumer<Throwable> consumer) {
		if (supplier != null) {
			try {
				return supplier.get();
			} catch (Throwable e) {
				consumer.accept(e);
			}
		}
		return null;
	}

	/**
	 * 静默获取Supplier的值，将可能发生的异常添加为指定Throwable的抑制异常
	 *
	 * @param <T> 返回值类型
	 * @param supplier 提供值的Supplier
	 * @param t 用于添加抑制异常的Throwable
	 * @return Supplier提供的值，如果发生异常则返回null
	 */
	public static <T> T getQuietly(Supplier<T> supplier, Throwable t) {
		if (supplier != null) {
			try {
				return supplier.get();
			} catch (Throwable e) {
				t.addSuppressed(e);
			}
		}
		return null;
	}

	/**
	 * 静默执行Executable，忽略可能发生的异常
	 *
	 * @param executable 要执行的Executable
	 */
	public static void executeQuietly(Executable executable) {
		if (executable != null) {
			try {
				executable.execute();
			} catch (Throwable e) {
				// ignore
			}
		}
	}

	/**
	 * 静默执行Executable，使用指定的消费者处理可能发生的异常
	 *
	 * @param executable 要执行的Executable
	 * @param consumer 用于处理异常的消费者
	 */
	public static void executeQuietly(Executable executable, Consumer<Throwable> consumer) {
		if (executable != null) {
			try {
				executable.execute();
			} catch (Throwable e) {
				consumer.accept(e);
			}
		}
	}

	/**
	 * 静默执行Executable，将可能发生的异常添加为指定Throwable的抑制异常
	 *
	 * @param executable 要执行的Executable
	 * @param t 用于添加抑制异常的Throwable
	 */
	public static void executeQuietly(Executable executable, Throwable t) {
		if (executable != null) {
			try {
				executable.execute();
			} catch (Throwable e) {
				t.addSuppressed(e);
			}
		}
	}


	/**
	 * 静默执行Callable，忽略可能发生的异常
	 *
	 * @param <T> 返回值类型
	 * @param callable 要执行的Callable
	 * @return Callable的执行结果，如果发生异常则返回null
	 */
	public static <T> T callQuietly(Callable<T> callable) {
		if (callable != null) {
			try {
				return callable.call();
			} catch (Throwable e) {
				// ignore
			}
		}
		return null;
	}

	/**
	 * 静默执行Callable，使用指定的消费者处理可能发生的异常
	 *
	 * @param <T> 返回值类型
	 * @param callable 要执行的Callable
	 * @param consumer 用于处理异常的消费者
	 * @return Callable的执行结果，如果发生异常则返回null
	 */
	public static <T> T callQuietly(Callable<T> callable, Consumer<Throwable> consumer) {
		if (callable != null) {
			try {
				return callable.call();
			} catch (Throwable e) {
				consumer.accept(e);
			}
		}
		return null;
	}

	/**
	 * 静默执行Callable，将可能发生的异常添加为指定Throwable的抑制异常
	 *
	 * @param <T> 返回值类型
	 * @param callable 要执行的Callable
	 * @param t 用于添加抑制异常的Throwable
	 * @return Callable的执行结果，如果发生异常则返回null
	 */
	public static <T> T callQuietly(Callable<T> callable, Throwable t) {
		if (callable != null) {
			try {
				return callable.call();
			} catch (Throwable e) {
				t.addSuppressed(e);
			}
		}
		return null;
	}

	/**
	 * 检查抛出的异常是否与目标异常类型列表中的任何一个匹配
	 *
	 * @param throwable 要检查的抛出异常
	 * @param targets   目标异常类型列表
	 * @return 如果throwable是targets中任何一种类型的实例则返回true，否则返回false
	 */
	@SafeVarargs
	public static boolean matches(Throwable throwable, Class<? extends Throwable>... targets) {
		return matches(throwable, true, targets);
	}

	/**
	 * 检查抛出的异常是否与目标异常类型列表中的任何一个匹配
	 *
	 * @param throwable    要检查的抛出异常
	 * @param matchIfEmpty 当targets为空时的默认返回值
	 * @param targets      目标异常类型列表
	 * @return 如果throwable是targets中任何一种类型的实例则返回true，否则返回false
	 */
	@SafeVarargs
	public static boolean matches(Throwable throwable, boolean matchIfEmpty, Class<? extends Throwable>... targets) {
		boolean matched = matchIfEmpty;
		if (targets != null && targets.length > 0) {
			matched = false;
			for (Class<? extends Throwable> target : targets) {
				if (target.isInstance(throwable)) {
					matched = true;
					break;
				}
			}
		}
		return matched;
	}

}
