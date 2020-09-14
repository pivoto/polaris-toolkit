package io.polaris.mybatis.interceptor;

/**
 * @author Qt
 * @since 1.8,  Aug 28, 2023
 */
public interface PageProvider<P> {


	P getCtxPage();


	void clearCtxPage();


	void setCtxPage(P page);

}
