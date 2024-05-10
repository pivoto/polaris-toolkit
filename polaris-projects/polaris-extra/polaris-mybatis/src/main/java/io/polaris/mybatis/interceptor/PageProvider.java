package io.polaris.mybatis.interceptor;

/**
 * @author Qt
 * @since  Aug 28, 2023
 */
public interface PageProvider<P> {


	P getCtxPage();


	void clearCtxPage();


	void setCtxPage(P page);

}
