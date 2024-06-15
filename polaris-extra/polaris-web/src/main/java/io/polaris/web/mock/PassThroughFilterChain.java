package io.polaris.web.mock;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


public class PassThroughFilterChain implements FilterChain {

	private Filter filter;
	private FilterChain nextFilterChain;
	private Servlet servlet;

	public PassThroughFilterChain(Filter filter, FilterChain nextFilterChain) {
		this.filter = filter;
		this.nextFilterChain = nextFilterChain;
	}

	public PassThroughFilterChain(Servlet servlet) {
		this.servlet = servlet;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		if (this.filter != null) {
			this.filter.doFilter(request, response, this.nextFilterChain);
		} else {
			this.servlet.service(request, response);
		}
	}

}
