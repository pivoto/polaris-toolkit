package com.jcfc.app.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
@Slf4j
@Component
public class StdHandlerInterceptor implements HandlerInterceptor {
	@Autowired
	private DispatcherServletSpy dispatcherServletSpy;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		log.info("preHandle: {}", handler);
		String contextPath = request.getContextPath();
		String servletPath = request.getServletPath();
		String pathInfo = request.getPathInfo();
		log.info("contextPath:'{}', servletPath:'{}', pathInfo:'{}'",contextPath, servletPath, pathInfo);
		if(pathInfo.equalsIgnoreCase("/info/hello")){
			dispatcherServletSpy.doSpecifiedHandler(request, response,"/hello");
			return false;
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		log.info("postHandle: {}", handler);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		log.info("afterCompletion: {}", handler);
	}
}

