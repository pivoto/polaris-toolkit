package io.polaris.core.asm.proxy;

import java.lang.reflect.Method;
import java.util.function.Predicate;

import lombok.Getter;

/**
 * 匹配的拦截器，封装了方法匹配条件和对应的拦截器数组
 * 
 * @author Qt
 * @since May 12, 2024
 */
@Getter
public class MatchedInterceptor {

	/**
	 * 方法匹配器，用于判断拦截器是否适用于某个方法
	 */
	private final Predicate<Method> predicate;
	
	/**
	 * 拦截器数组，包含需要应用的所有拦截器
	 */
	private final Interceptor[] interceptors;

	/**
	 * 构造匹配的拦截器
	 *
	 * @param predicate 方法匹配条件，为 null 时表示匹配所有方法
	 * @param interceptors 要应用的拦截器数组
	 */
	public MatchedInterceptor(Predicate<Method> predicate, Interceptor... interceptors) {
		this.predicate = predicate;
		this.interceptors = interceptors;
	}

	/**
	 * 判断指定方法是否被此匹配器接受
	 *
	 * @param method 待检测的方法
	 * @return 如果匹配器为空或方法满足匹配条件则返回 true
	 */
	public boolean accept(Method method) {
		return predicate == null || predicate.test(method);
	}
}
