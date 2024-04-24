package io.polaris.web.mock;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import io.polaris.core.collection.ObjectArrays;


public class MockFilterChain implements FilterChain {

	private final List<Filter> filters;
	private ServletRequest request;
	private ServletResponse response;
	private Iterator<Filter> iterator;


	public MockFilterChain() {
		this.filters = Collections.emptyList();
	}


	public MockFilterChain(Servlet servlet) {
		this.filters = initFilterList(servlet);
	}


	public MockFilterChain(Servlet servlet, Filter... filters) {
		this.filters = initFilterList(servlet, filters);
	}

	private static List<Filter> initFilterList(Servlet servlet, Filter... filters) {
		Filter[] allFilters = ObjectArrays.addAll(filters, new Filter[]{new ServletFilterProxy(servlet)});
		;
		return Arrays.asList(allFilters);
	}


	public ServletRequest getRequest() {
		return this.request;
	}


	public ServletResponse getResponse() {
		return this.response;
	}


	@Override
	public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {

		if (this.request != null) {
			throw new IllegalStateException("This FilterChain has already been called!");
		}

		if (this.iterator == null) {
			this.iterator = this.filters.iterator();
		}

		if (this.iterator.hasNext()) {
			Filter nextFilter = this.iterator.next();
			nextFilter.doFilter(request, response, this);
		}

		this.request = request;
		this.response = response;
	}


	public void reset() {
		this.request = null;
		this.response = null;
		this.iterator = null;
	}


	private static class ServletFilterProxy implements Filter {

		private final Servlet delegateServlet;

		private ServletFilterProxy(Servlet servlet) {
			this.delegateServlet = servlet;
		}

		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

			this.delegateServlet.service(request, response);
		}

		@Override
		public void init(FilterConfig filterConfig) throws ServletException {
		}

		@Override
		public void destroy() {
		}

		@Override
		public String toString() {
			return this.delegateServlet.toString();
		}
	}

}
