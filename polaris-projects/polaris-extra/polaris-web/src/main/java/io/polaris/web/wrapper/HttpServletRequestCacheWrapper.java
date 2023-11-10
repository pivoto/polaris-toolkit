package io.polaris.web.wrapper;

import io.polaris.core.io.IO;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author Qt
 * @since 1.8
 */
public class HttpServletRequestCacheWrapper extends HttpServletRequestWrapper implements HttpServletRequest {
	private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";
	private static final String METHOD_POST = "POST";
	public static final String ISO_8859_1 = "ISO-8859-1";
	private final ByteArrayOutputStream paramCache;
	private byte[] contentData;
	private HttpServletRequest request;
	private boolean cached = false;

	private HttpServletRequestCacheWrapper(HttpServletRequest request) {
		super(request);
		this.request = request;
		int contentLength = request.getContentLength();
		this.paramCache = new ByteArrayOutputStream(contentLength >= 0 ? contentLength : 1024);
		// parse(request);
	}

	public static HttpServletRequest wrap(HttpServletRequest request) {
		if (request instanceof HttpServletRequestCacheWrapper) {
			return (HttpServletRequestCacheWrapper) request;
		}
		return new HttpServletRequestCacheWrapper(request);
	}


	@Override
	public String getParameter(String name) {
		cacheRequestParameters();
		return super.getParameter(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		cacheRequestParameters();
		return super.getParameterMap();
	}

	@Override
	public Enumeration<String> getParameterNames() {
		cacheRequestParameters();
		return super.getParameterNames();
	}

	@Override
	public String[] getParameterValues(String name) {
		cacheRequestParameters();
		return super.getParameterValues(name);
	}


	private void parse(HttpServletRequest request) {
		if (cached) {
			return;
		}
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ServletInputStream in = request.getInputStream();
			if (in != null) {
				IO.copy(in, bos, 4096);
				contentData = bos.toByteArray();
			} else {
				contentData = null;
			}
		} catch (IOException e) {
		}
		cached = true;
	}

	public byte[] getContentData() {
		parse(this.request);
		return contentData == null ? paramCache.toByteArray() : contentData;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		parse(this.request);
		if (contentData == null) {
			return null;
		}
		return new ServletInputStream() {
			ByteArrayInputStream bis = new ByteArrayInputStream(contentData);

			public int read() throws IOException {
				return bis.read();
			}

			public boolean isReady() {
				return true;
			}

			public boolean isFinished() {
				return bis.available() == 0;
			}

			public void setReadListener(ReadListener listener) {
			}
		};
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
	}

	@Override
	public String getCharacterEncoding() {
		String enc = super.getCharacterEncoding();
		return (enc != null ? enc : ISO_8859_1);
	}

	private void cacheRequestParameters() {
		if (this.paramCache.size() == 0 && isFormPost()) {
			writeRequestParametersToCachedContent();
		}
	}

	private boolean isFormPost() {
		String contentType = getContentType();
		return (contentType != null && contentType.contains(FORM_CONTENT_TYPE) &&
			METHOD_POST.equalsIgnoreCase(getMethod()));
	}

	private void writeRequestParametersToCachedContent() {
		try {
			if (this.paramCache.size() == 0) {
				String requestEncoding = getCharacterEncoding();
				Map<String, String[]> form = super.getParameterMap();
				for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext(); ) {
					String name = nameIterator.next();
					List<String> values = Arrays.asList(form.get(name));
					for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext(); ) {
						String value = valueIterator.next();
						this.paramCache.write(URLEncoder.encode(name, requestEncoding).getBytes());
						if (value != null) {
							this.paramCache.write('=');
							this.paramCache.write(URLEncoder.encode(value, requestEncoding).getBytes());
							if (valueIterator.hasNext()) {
								this.paramCache.write('&');
							}
						}
					}
					if (nameIterator.hasNext()) {
						this.paramCache.write('&');
					}
				}
			}
		} catch (IOException ex) {
			throw new IllegalStateException("Failed to write request parameters to cached content", ex);
		}
	}
}
