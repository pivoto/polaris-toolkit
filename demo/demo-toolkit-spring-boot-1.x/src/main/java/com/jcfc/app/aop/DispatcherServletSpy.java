package com.jcfc.app.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
@Slf4j
public class DispatcherServletSpy {
	private DispatcherServlet delegate;

	@Autowired
	public DispatcherServletSpy(DispatcherServlet delegate) {
		this.delegate = delegate;
	}

	public void doSpecifiedDispatch(HttpServletRequest request, HttpServletResponse response, String path) throws Exception {
		request = copy(request, path);
		doDispatch(request, response);
	}

	private HandlerExecutionChain doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Method doDispatch = DispatcherServlet.class.getDeclaredMethod("doDispatch", HttpServletRequest.class, HttpServletResponse.class);
		doDispatch.setAccessible(true);
		return (HandlerExecutionChain) doDispatch.invoke(delegate, request, response);
	}

	public void doSpecifiedHandler(HttpServletRequest request, HttpServletResponse response, String path) throws Exception {
		request = copy(request, path);
		HandlerExecutionChain mappedHandler = getHandler(request);
		HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
		ModelAndView mv = ha.handle(request, response, mappedHandler.getHandler());
		log.info("{}", mv);
	}


	private HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		Method getHandler = DispatcherServlet.class.getDeclaredMethod("getHandler", HttpServletRequest.class);
		getHandler.setAccessible(true);
		return (HandlerExecutionChain) getHandler.invoke(delegate, request);
	}

	private HandlerAdapter getHandlerAdapter(Object handler) throws Exception {
		Method getHandlerAdapter = DispatcherServlet.class.getDeclaredMethod("getHandlerAdapter", Object.class);
		getHandlerAdapter.setAccessible(true);
		return (HandlerAdapter) getHandlerAdapter.invoke(delegate, handler);
	}

	public HttpServletRequest copy(HttpServletRequest request, String path) {
		final String contextPath = request.getContextPath();
		final String servletPath;
		if (!path.startsWith("/")) {
			servletPath = "/" + path;
		} else {
			servletPath = path;
		}

		HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
			@Override
			public String getRequestURI() {
				return contextPath + servletPath;
			}

			@Override
			public String getServletPath() {
				return servletPath;
			}

			@Override
			public StringBuffer getRequestURL() {
				return new StringBuffer().append(contextPath).append(servletPath);
			}
		};


		return wrapper;
		/*return (HttpServletRequest) Proxy.newProxyInstance(request.getClass().getClassLoader(),
			new Class[]{HttpServletRequest.class},
			(proxy, method, args) -> {
				Object rs = method.invoke(request, args);
				return rs;
			});*/
	}
}
