package com.jcfc.app.demo.controller;

import com.jcfc.app.aop.DispatcherServletSpy;
import com.jcfc.app.demo.service.DemoService;
import io.polaris.toolkit.spring.util.Contexts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
@RestController
@Slf4j
public class HelloController {

	@Autowired
	private DispatcherServlet dispatcherServlet;
	@Autowired
	private DispatcherServletSpy dispatcherServletSpy;

	@PostConstruct
	public void init() {
		log.info("****** HelloController: {}", this);
	}


	@RequestMapping("/hello")
	public Object hello() {
		log.info("hello()");
		Contexts.getApplicationContext().getBean(DemoService.class).doSth();
		return "Hello World";
	}

	@RequestMapping("/info/**")
	public Object info() throws Exception {
		log.info("info()");
		Map<String, Object> map = new LinkedHashMap<>(16);
		dispatcherServlet.getHandlerMappings();


		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		HttpServletResponse response = requestAttributes.getResponse();
		WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());

		map.put("getContextPath", request.getContextPath());
		map.put("getRequestURI", request.getRequestURI());
		map.put("getServletPath", request.getServletPath());
		map.put("getPathInfo", request.getPathInfo());
		map.put("getPathTranslated", request.getPathTranslated());
		map.put("getQueryString", request.getQueryString());


		LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
		map.put("local", localeContext.getLocale());
		map.put("timezone", LocaleContextHolder.getTimeZone());

//		System.out.println(applicationContext.getBean(HelloController.class).hello());
//		dispatcherServletSpy.doSpecifiedHandler(request, response, "/hello");

		return map;
	}


}
