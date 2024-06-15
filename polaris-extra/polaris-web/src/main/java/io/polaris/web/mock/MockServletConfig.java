package io.polaris.web.mock;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;


public class MockServletConfig implements ServletConfig {

	private final ServletContext servletContext;
	private final String servletName;
	private final Map<String, String> initParameters = new LinkedHashMap<String, String>();

	public MockServletConfig() {
		this(null, "");
	}

	public MockServletConfig(String servletName) {
		this(null, servletName);
	}

	public MockServletConfig(ServletContext servletContext) {
		this(servletContext, "");
	}

	public MockServletConfig(ServletContext servletContext, String servletName) {
		this.servletContext = (servletContext != null ? servletContext : new MockServletContext());
		this.servletName = servletName;
	}

	@Override
	public String getServletName() {
		return this.servletName;
	}

	@Override
	public ServletContext getServletContext() {
		return this.servletContext;
	}

	public void addInitParameter(String name, String value) {
		this.initParameters.put(name, value);
	}

	@Override
	public String getInitParameter(String name) {
		return this.initParameters.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(this.initParameters.keySet());
	}

}
