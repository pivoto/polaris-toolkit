package io.polaris.mybatis.interceptor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Qt
 * @since  Aug 28, 2023
 */
@Slf4j
public class PageProviders {

	private static PageProvider<?> provider;

	public static void setProvider(PageProvider<?> provider) {
		PageProviders.provider = provider;
	}

	public static PageProvider<?> getProvider() {
		return provider;
	}

	static {
		try {
			provider = new PageHelperProvider();
		} catch (Throwable e) {
			log.error("", e);
			provider = new NoopProvider<>();
		}
	}

	static class NoopProvider<T> implements PageProvider<T> {

		@Override
		public T getCtxPage() {
			return null;
		}

		@Override
		public void clearCtxPage() {
		}

		@Override
		public void setCtxPage(T page) {
		}
	}

	static class PageHelperProvider extends com.github.pagehelper.PageHelper implements PageProvider<com.github.pagehelper.Page<?>> {

		@Override
		public com.github.pagehelper.Page<?> getCtxPage() {
			return com.github.pagehelper.PageHelper.getLocalPage();
		}


		@Override
		public void clearCtxPage() {
			com.github.pagehelper.PageHelper.clearPage();
		}

		@Override
		public void setCtxPage(com.github.pagehelper.Page<?> page) {
			com.github.pagehelper.PageHelper.setLocalPage(page);
		}
	}
}
