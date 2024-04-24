package io.polaris.web.mock;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockRequestDispatcher implements RequestDispatcher {
	private final String resource;

	public MockRequestDispatcher(String resource) {
		this.resource = resource;
	}

	@Override
	public void forward(ServletRequest request, ServletResponse response) {
		if (response.isCommitted()) {
			throw new IllegalStateException("Cannot perform forward - response is already committed");
		}
		getMockHttpServletResponse(response).setForwardedUrl(this.resource);
		if (log.isDebugEnabled()) {
			log.debug("MockRequestDispatcher: forwarding to [" + this.resource + "]");
		}
	}

	@Override
	public void include(ServletRequest request, ServletResponse response) {
		getMockHttpServletResponse(response).addIncludedUrl(this.resource);
		if (log.isDebugEnabled()) {
			log.debug("MockRequestDispatcher: including [" + this.resource + "]");
		}
	}


	protected MockHttpServletResponse getMockHttpServletResponse(ServletResponse response) {
		if (response instanceof MockHttpServletResponse) {
			return (MockHttpServletResponse) response;
		}
		if (response instanceof HttpServletResponseWrapper) {
			return getMockHttpServletResponse(((HttpServletResponseWrapper) response).getResponse());
		}
		throw new IllegalArgumentException("MockRequestDispatcher requires MockHttpServletResponse");
	}

}
