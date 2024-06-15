package io.polaris.web.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import io.polaris.core.map.Maps;


public class MockHttpServletResponse implements HttpServletResponse {

	private static final String CHARSET_PREFIX = "charset=";
	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	private static final String CONTENT_LENGTH_HEADER = "Content-Length";
	private static final String LOCATION_HEADER = "Location";


	//---------------------------------------------------------------------
	// ServletResponse properties
	//---------------------------------------------------------------------
	private final ByteArrayOutputStream content = new ByteArrayOutputStream(1024);
	private final ServletOutputStream outputStream = new ResponseServletOutputStream(this.content);
	private final List<Cookie> cookies = new ArrayList<Cookie>();
	private final Map<String, HeaderValueHolder> headers = Maps.newUpperCaseLinkedHashMap();
	private final List<String> includedUrls = new ArrayList<String>();
	private boolean outputStreamAccessAllowed = true;
	private boolean writerAccessAllowed = true;
	private String characterEncoding = "ISO-8859-1";
	private boolean charset = false;
	private PrintWriter writer;
	private int contentLength = 0;
	private String contentType;


	//---------------------------------------------------------------------
	// HttpServletResponse properties
	//---------------------------------------------------------------------
	private int bufferSize = 4096;
	private boolean committed;
	private Locale locale = Locale.getDefault();
	private int status = HttpServletResponse.SC_OK;
	private String errorMessage;
	private String forwardedUrl;


	//---------------------------------------------------------------------
	// ServletResponse interface
	//---------------------------------------------------------------------

	public boolean isOutputStreamAccessAllowed() {
		return this.outputStreamAccessAllowed;
	}

	public void setOutputStreamAccessAllowed(boolean outputStreamAccessAllowed) {
		this.outputStreamAccessAllowed = outputStreamAccessAllowed;
	}

	public boolean isWriterAccessAllowed() {
		return this.writerAccessAllowed;
	}

	public void setWriterAccessAllowed(boolean writerAccessAllowed) {
		this.writerAccessAllowed = writerAccessAllowed;
	}

	private void updateContentTypeHeader() {
		if (this.contentType != null) {
			StringBuilder sb = new StringBuilder(this.contentType);
			if (!this.contentType.toLowerCase().contains(CHARSET_PREFIX) && this.charset) {
				sb.append(";").append(CHARSET_PREFIX).append(this.characterEncoding);
			}
			doAddHeaderValue(CONTENT_TYPE_HEADER, sb.toString(), true);
		}
	}

	@Override
	public String getCharacterEncoding() {
		return this.characterEncoding;
	}

	@Override
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
		this.charset = true;
		updateContentTypeHeader();
	}

	@Override
	public ServletOutputStream getOutputStream() {
		if (!this.outputStreamAccessAllowed) {
			throw new IllegalStateException("OutputStream access not allowed");
		}
		return this.outputStream;
	}

	@Override
	public PrintWriter getWriter() throws UnsupportedEncodingException {
		if (!this.writerAccessAllowed) {
			throw new IllegalStateException("Writer access not allowed");
		}
		if (this.writer == null) {
			Writer targetWriter = (this.characterEncoding != null ?
				new OutputStreamWriter(this.content, this.characterEncoding) : new OutputStreamWriter(this.content));
			this.writer = new ResponsePrintWriter(targetWriter);
		}
		return this.writer;
	}

	public byte[] getContentAsByteArray() {
		flushBuffer();
		return this.content.toByteArray();
	}

	public String getContentAsString() throws UnsupportedEncodingException {
		flushBuffer();
		return (this.characterEncoding != null ?
			this.content.toString(this.characterEncoding) : this.content.toString());
	}

	public int getContentLength() {
		return this.contentLength;
	}

	@Override
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
		doAddHeaderValue(CONTENT_LENGTH_HEADER, contentLength, true);
	}

	@Override
	public void setContentLengthLong(long len) {

	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
		if (contentType != null) {
			int charsetIndex = contentType.toLowerCase().indexOf(CHARSET_PREFIX);
			if (charsetIndex != -1) {
				this.characterEncoding = contentType.substring(charsetIndex + CHARSET_PREFIX.length());
				this.charset = true;
			}
			updateContentTypeHeader();
		}
	}

	@Override
	public int getBufferSize() {
		return this.bufferSize;
	}

	@Override
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	@Override
	public void flushBuffer() {
		setCommitted(true);
	}

	@Override
	public void resetBuffer() {
		if (isCommitted()) {
			throw new IllegalStateException("Cannot reset buffer - response is already committed");
		}
		this.content.reset();
	}

	private void setCommittedIfBufferSizeExceeded() {
		int bufSize = getBufferSize();
		if (bufSize > 0 && this.content.size() > bufSize) {
			setCommitted(true);
		}
	}

	@Override
	public boolean isCommitted() {
		return this.committed;
	}

	public void setCommitted(boolean committed) {
		this.committed = committed;
	}

	@Override
	public void reset() {
		resetBuffer();
		this.characterEncoding = null;
		this.contentLength = 0;
		this.contentType = null;
		this.locale = null;
		this.cookies.clear();
		this.headers.clear();
		this.status = HttpServletResponse.SC_OK;
		this.errorMessage = null;
	}

	@Override
	public Locale getLocale() {
		return this.locale;
	}

	@Override
	public void setLocale(Locale locale) {
		this.locale = locale;
	}


	//---------------------------------------------------------------------
	// HttpServletResponse interface
	//---------------------------------------------------------------------

	@Override
	public void addCookie(Cookie cookie) {
		this.cookies.add(cookie);
	}

	public Cookie[] getCookies() {
		return this.cookies.toArray(new Cookie[this.cookies.size()]);
	}

	public Cookie getCookie(String name) {
		for (Cookie cookie : this.cookies) {
			if (name.equals(cookie.getName())) {
				return cookie;
			}
		}
		return null;
	}

	@Override
	public boolean containsHeader(String name) {
		return (HeaderValueHolder.getByName(this.headers, name) != null);
	}


	public Collection<String> getHeaderNames() {
		return this.headers.keySet();
	}


	public String getHeader(String name) {
		HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
		return (header != null ? header.getStringValue() : null);
	}


	public List<String> getHeaders(String name) {
		HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
		if (header != null) {
			return header.getStringValues();
		} else {
			return Collections.emptyList();
		}
	}


	public Object getHeaderValue(String name) {
		HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
		return (header != null ? header.getValue() : null);
	}


	public List<Object> getHeaderValues(String name) {
		HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
		if (header != null) {
			return header.getValues();
		} else {
			return Collections.emptyList();
		}
	}


	@Override
	public String encodeURL(String url) {
		return url;
	}


	@Override
	public String encodeRedirectURL(String url) {
		return encodeURL(url);
	}

	@Override
	public String encodeUrl(String url) {
		return encodeURL(url);
	}

	@Override
	public String encodeRedirectUrl(String url) {
		return encodeRedirectURL(url);
	}

	@Override
	public void sendError(int status, String errorMessage) throws IOException {
		if (isCommitted()) {
			throw new IllegalStateException("Cannot set error status - response is already committed");
		}
		this.status = status;
		this.errorMessage = errorMessage;
		setCommitted(true);
	}

	@Override
	public void sendError(int status) throws IOException {
		if (isCommitted()) {
			throw new IllegalStateException("Cannot set error status - response is already committed");
		}
		this.status = status;
		setCommitted(true);
	}

	@Override
	public void sendRedirect(String url) throws IOException {
		if (isCommitted()) {
			throw new IllegalStateException("Cannot send redirect - response is already committed");
		}
		setHeader(LOCATION_HEADER, url);
		setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		setCommitted(true);
	}

	public String getRedirectedUrl() {
		return getHeader(LOCATION_HEADER);
	}

	@Override
	public void setDateHeader(String name, long value) {
		setHeaderValue(name, value);
	}

	@Override
	public void addDateHeader(String name, long value) {
		addHeaderValue(name, value);
	}

	@Override
	public void setHeader(String name, String value) {
		setHeaderValue(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		addHeaderValue(name, value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		setHeaderValue(name, value);
	}

	@Override
	public void addIntHeader(String name, int value) {
		addHeaderValue(name, value);
	}

	private void setHeaderValue(String name, Object value) {
		if (setSpecialHeader(name, value)) {
			return;
		}
		doAddHeaderValue(name, value, true);
	}

	private void addHeaderValue(String name, Object value) {
		if (setSpecialHeader(name, value)) {
			return;
		}
		doAddHeaderValue(name, value, false);
	}

	private boolean setSpecialHeader(String name, Object value) {
		if (CONTENT_TYPE_HEADER.equalsIgnoreCase(name)) {
			setContentType((String) value);
			return true;
		} else if (CONTENT_LENGTH_HEADER.equalsIgnoreCase(name)) {
			setContentLength(Integer.parseInt((String) value));
			return true;
		} else {
			return false;
		}
	}

	private void doAddHeaderValue(String name, Object value, boolean replace) {
		HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
		if (header == null) {
			header = new HeaderValueHolder();
			this.headers.put(name, header);
		}
		if (replace) {
			header.setValue(value);
		} else {
			header.addValue(value);
		}
	}

	@Override
	public void setStatus(int status, String errorMessage) {
		this.status = status;
		this.errorMessage = errorMessage;
	}

	public int getStatus() {
		return this.status;
	}

	@Override
	public void setStatus(int status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}


	//---------------------------------------------------------------------
	// Methods for MockRequestDispatcher
	//---------------------------------------------------------------------

	public String getForwardedUrl() {
		return this.forwardedUrl;
	}

	public void setForwardedUrl(String forwardedUrl) {
		this.forwardedUrl = forwardedUrl;
	}

	public String getIncludedUrl() {
		int count = this.includedUrls.size();
		if (count > 1) {
			throw new IllegalStateException(
				"More than 1 URL included - check getIncludedUrls instead: " + this.includedUrls);
		}
		return (count == 1 ? this.includedUrls.get(0) : null);
	}

	public void setIncludedUrl(String includedUrl) {
		this.includedUrls.clear();
		if (includedUrl != null) {
			this.includedUrls.add(includedUrl);
		}
	}

	public void addIncludedUrl(String includedUrl) {
		this.includedUrls.add(includedUrl);
	}

	public List<String> getIncludedUrls() {
		return this.includedUrls;
	}


	private class ResponseServletOutputStream extends DelegatingServletOutputStream {

		public ResponseServletOutputStream(OutputStream out) {
			super(out);
		}

		@Override
		public void write(int b) throws IOException {
			super.write(b);
			super.flush();
			setCommittedIfBufferSizeExceeded();
		}

		@Override
		public void flush() throws IOException {
			super.flush();
			setCommitted(true);
		}
	}


	private class ResponsePrintWriter extends PrintWriter {

		public ResponsePrintWriter(Writer out) {
			super(out, true);
		}

		@Override
		public void write(char buf[], int off, int len) {
			super.write(buf, off, len);
			super.flush();
			setCommittedIfBufferSizeExceeded();
		}

		@Override
		public void write(String s, int off, int len) {
			super.write(s, off, len);
			super.flush();
			setCommittedIfBufferSizeExceeded();
		}

		@Override
		public void write(int c) {
			super.write(c);
			super.flush();
			setCommittedIfBufferSizeExceeded();
		}

		@Override
		public void flush() {
			super.flush();
			setCommitted(true);
		}
	}

}
