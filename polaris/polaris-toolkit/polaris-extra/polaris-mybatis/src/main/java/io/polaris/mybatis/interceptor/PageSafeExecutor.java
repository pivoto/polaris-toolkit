package io.polaris.mybatis.interceptor;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 *
 * @author Qt
 * @since 1.8,  Aug 28, 2023
 */
public class PageSafeExecutor {
	/**
	 * 拦截器中执行其他查询方法，容易受原查询方法的分页组件对象影响, 导致发生分页并在返回结果时清空分页组件使原查询的分页处理报空指针异常，此方法用于在执行查询前保存分页状态并执行查询后恢复
	 */
	public static void execSafely(Runnable task) {
		PageProvider provider = PageProviders.getProvider();
		if (provider == null) {
			task.run();
		} else {
			Object page = provider.getCtxPage();
			try {
				provider.clearCtxPage();
				task.run();
			} finally {
				if (page != null) {
					provider.setCtxPage(page);
				}
			}
		}
	}
	public static <V>V  execSafely(Supplier<V> task) {
		PageProvider provider = PageProviders.getProvider();
		if (provider == null) {
			return task.get();
		} else {
			Object page = provider.getCtxPage();
			try {
				provider.clearCtxPage();
				return task.get();
			} finally {
				if (page != null) {
					provider.setCtxPage(page);
				}
			}
		}
	}
}
