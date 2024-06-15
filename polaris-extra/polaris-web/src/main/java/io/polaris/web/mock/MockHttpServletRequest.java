package io.polaris.web.mock;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import io.polaris.core.map.Maps;
import io.polaris.core.string.Strings;


public class MockHttpServletRequest implements HttpServletRequest {

	/**
	 * The default protocol: 'http'.
	 */
	public static final String DEFAULT_PROTOCOL = "http";

	/**
	 * The default server address: '127.0.0.1'.
	 */
	public static final String DEFAULT_SERVER_ADDR = "127.0.0.1";

	/**
	 * The default server name: 'localhost'.
	 */
	public static final String DEFAULT_SERVER_NAME = "localhost";

	/**
	 * The default server port: '80'.
	 */
	public static final int DEFAULT_SERVER_PORT = 80;

	/**
	 * The default remote address: '127.0.0.1'.
	 */
	public static final String DEFAULT_REMOTE_ADDR = "127.0.0.1";

	/**
	 * The default remote host: 'localhost'.
	 */
	public static final String DEFAULT_REMOTE_HOST = "localhost";

	private static final String CONTENT_TYPE_HEADER = "Content-Type";

	private static final String CHARSET_PREFIX = "charset=";
	private final Map<String, Object> attributes = new LinkedHashMap<String, Object>();


	// ---------------------------------------------------------------------
	// ServletRequest properties
	// ---------------------------------------------------------------------
	private final Map<String, String[]> parameters = new LinkedHashMap<String, String[]>(16);
	/** List of locales in descending order */
	private final List<Locale> locales = new LinkedList<Locale>();
	private final ServletContext servletContext;
	private final Map<String, HeaderValueHolder> headers = Maps.newUpperCaseLinkedHashMap();
	private final Set<String> userRoles = new HashSet<String>();
	private boolean active = true;
	private String characterEncoding;
	private byte[] content;
	private String contentType;
	private String protocol = DEFAULT_PROTOCOL;
	private String scheme = DEFAULT_PROTOCOL;
	private String serverName = DEFAULT_SERVER_NAME;
	private int serverPort = DEFAULT_SERVER_PORT;
	private String remoteAddr = DEFAULT_REMOTE_ADDR;
	private String remoteHost = DEFAULT_REMOTE_HOST;
	private boolean secure = false;
	private int remotePort = DEFAULT_SERVER_PORT;
	private String localName = DEFAULT_SERVER_NAME;


	// ---------------------------------------------------------------------
	// HttpServletRequest properties
	// ---------------------------------------------------------------------
	private String localAddr = DEFAULT_SERVER_ADDR;
	private int localPort = DEFAULT_SERVER_PORT;
	private String authType;
	private Cookie[] cookies;
	private String method;
	private String pathInfo;
	private String contextPath = "";
	private String queryString;
	private String remoteUser;
	private Principal userPrincipal;

	private String requestedSessionId;

	private String requestURI;

	private String servletPath = "";

	private HttpSession session;

	private boolean requestedSessionIdValid = true;

	private boolean requestedSessionIdFromCookie = true;

	private boolean requestedSessionIdFromURL = false;


	// ---------------------------------------------------------------------
	// Constructors
	// ---------------------------------------------------------------------

	public MockHttpServletRequest() {
		this(null, "", "");
	}

	public MockHttpServletRequest(String method, String requestURI) {
		this(null, method, requestURI);
	}

	public MockHttpServletRequest(ServletContext servletContext) {
		this(servletContext, "", "");
	}

	public MockHttpServletRequest(ServletContext servletContext, String method, String requestURI) {
		this.servletContext = (servletContext != null ? servletContext : new MockServletContext());
		this.method = method;
		this.requestURI = requestURI;
		this.locales.add(Locale.ENGLISH);
	}


	// ---------------------------------------------------------------------
	// Lifecycle methods
	// ---------------------------------------------------------------------

	public ServletContext getServletContext() {
		return this.servletContext;
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		return null;
	}

	@Override
	public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
		return null;
	}

	@Override
	public boolean isAsyncStarted() {
		return false;
	}

	@Override
	public boolean isAsyncSupported() {
		return false;
	}

	@Override
	public AsyncContext getAsyncContext() {
		return null;
	}

	@Override
	public DispatcherType getDispatcherType() {
		return null;
	}

	public boolean isActive() {
		return this.active;
	}

	public void close() {
		this.active = false;
	}

	public void invalidate() {
		close();
		clearAttributes();
	}

	protected void checkActive() throws IllegalStateException {
		if (!this.active) {
			throw new IllegalStateException("Request is not active anymore");
		}
	}


	// ---------------------------------------------------------------------
	// ServletRequest interface
	// ---------------------------------------------------------------------

	@Override
	public Object getAttribute(String name) {
		checkActive();
		return this.attributes.get(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		checkActive();
		return Collections.enumeration(new LinkedHashSet<String>(this.attributes.keySet()));
	}

	@Override
	public String getCharacterEncoding() {
		return this.characterEncoding;
	}

	@Override
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
		updateContentTypeHeader();
	}

	private void updateContentTypeHeader() {
		if (Strings.isNotEmpty(this.contentType)) {
			StringBuilder sb = new StringBuilder(this.contentType);
			if (!this.contentType.toLowerCase().contains(CHARSET_PREFIX) &&
				Strings.isNotEmpty(this.characterEncoding)) {
				sb.append(";").append(CHARSET_PREFIX).append(this.characterEncoding);
			}
			doAddHeaderValue(CONTENT_TYPE_HEADER, sb.toString(), true);
		}
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	@Override
	public int getContentLength() {
		return (this.content != null ? this.content.length : -1);
	}

	@Override
	public long getContentLengthLong() {
		return 0;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
		if (contentType != null) {
			int charsetIndex = contentType.toLowerCase().indexOf(CHARSET_PREFIX);
			if (charsetIndex != -1) {
				this.characterEncoding = contentType.substring(charsetIndex + CHARSET_PREFIX.length());
			}
			updateContentTypeHeader();
		}
	}

	@Override
	public ServletInputStream getInputStream() {
		if (this.content != null) {
			return new DelegatingServletInputStream(new ByteArrayInputStream(this.content));
		} else {
			return null;
		}
	}

	/**
	 * Set a single value for the specified HTTP parameter.
	 * <p>If there are already one or more values registered for the given
	 * parameter name, they will be replaced.
	 */
	public void setParameter(String name, String value) {
		setParameter(name, new String[]{value});
	}

	/**
	 * Set an array of values for the specified HTTP parameter.
	 * <p>If there are already one or more values registered for the given
	 * parameter name, they will be replaced.
	 */
	public void setParameter(String name, String[] values) {
		this.parameters.put(name, values);
	}

	public void setParameters(Map params) {
		for (Object key : params.keySet()) {
			Object value = params.get(key);
			if (value instanceof String) {
				this.setParameter((String) key, (String) value);
			} else if (value instanceof String[]) {
				this.setParameter((String) key, (String[]) value);
			} else {
				throw new IllegalArgumentException(
					"Parameter map value must be single value " + " or array of type [" + String.class.getName() + "]");
			}
		}
	}

	public void addParameter(String name, String value) {
		addParameter(name, new String[]{value});
	}

	public void addParameter(String name, String[] values) {
		String[] oldArr = this.parameters.get(name);
		if (oldArr != null) {
			String[] newArr = new String[oldArr.length + values.length];
			System.arraycopy(oldArr, 0, newArr, 0, oldArr.length);
			System.arraycopy(values, 0, newArr, oldArr.length, values.length);
			this.parameters.put(name, newArr);
		} else {
			this.parameters.put(name, values);
		}
	}

	public void addParameters(Map params) {
		for (Object key : params.keySet()) {
			Object value = params.get(key);
			if (value instanceof String) {
				this.addParameter((String) key, (String) value);
			} else if (value instanceof String[]) {
				this.addParameter((String) key, (String[]) value);
			} else {
				throw new IllegalArgumentException("Parameter map value must be single value " +
					" or array of type [" + String.class.getName() + "]");
			}
		}
	}

	public void removeParameter(String name) {
		this.parameters.remove(name);
	}

	public void removeAllParameters() {
		this.parameters.clear();
	}

	@Override
	public String getParameter(String name) {
		String[] arr = (name != null ? this.parameters.get(name) : null);
		return (arr != null && arr.length > 0 ? arr[0] : null);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(this.parameters.keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		return (name != null ? this.parameters.get(name) : null);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return Collections.unmodifiableMap(this.parameters);
	}

	@Override
	public String getProtocol() {
		return this.protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Override
	public String getScheme() {
		return this.scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	@Override
	public String getServerName() {
		return this.serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@Override
	public int getServerPort() {
		return this.serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	@Override
	public BufferedReader getReader() throws UnsupportedEncodingException {
		if (this.content != null) {
			InputStream sourceStream = new ByteArrayInputStream(this.content);
			Reader sourceReader = (this.characterEncoding != null) ?
				new InputStreamReader(sourceStream, this.characterEncoding) : new InputStreamReader(sourceStream);
			return new BufferedReader(sourceReader);
		} else {
			return null;
		}
	}

	@Override
	public String getRemoteAddr() {
		return this.remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	@Override
	public String getRemoteHost() {
		return this.remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	@Override
	public void setAttribute(String name, Object value) {
		checkActive();
		if (value != null) {
			this.attributes.put(name, value);
		} else {
			this.attributes.remove(name);
		}
	}

	@Override
	public void removeAttribute(String name) {
		checkActive();
		this.attributes.remove(name);
	}

	public void clearAttributes() {
		this.attributes.clear();
	}

	public void addPreferredLocale(Locale locale) {
		this.locales.add(0, locale);
	}

	public void setPreferredLocales(List<Locale> locales) {
		this.locales.clear();
		this.locales.addAll(locales);
	}

	@Override
	public Locale getLocale() {
		return this.locales.get(0);
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return Collections.enumeration(this.locales);
	}

	@Override
	public boolean isSecure() {
		return this.secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return new MockRequestDispatcher(path);
	}

	@Override
	public String getRealPath(String path) {
		return this.servletContext.getRealPath(path);
	}

	@Override
	public int getRemotePort() {
		return this.remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	@Override
	public String getLocalName() {
		return this.localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	@Override
	public String getLocalAddr() {
		return this.localAddr;
	}

	public void setLocalAddr(String localAddr) {
		this.localAddr = localAddr;
	}

	@Override
	public int getLocalPort() {
		return this.localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}


	// ---------------------------------------------------------------------
	// HttpServletRequest interface
	// ---------------------------------------------------------------------

	@Override
	public String getAuthType() {
		return this.authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	@Override
	public Cookie[] getCookies() {
		return this.cookies;
	}

	public void setCookies(Cookie... cookies) {
		this.cookies = cookies;
	}

	/**
	 * Add a header entry for the given name.
	 * <p>If there was no entry for that header name before, the value will be used
	 * as-is. In case of an existing entry, a String array will be created,
	 * adding the given value (more specifically, its toString representation)
	 * as further element.
	 * <p>Multiple values can only be stored as list of Strings, following the
	 * Servlet spec (see {@code getHeaders} accessor). As alternative to
	 * repeated {@code addHeader} calls for individual elements, you can
	 * use a single call with an entire array or Collection of values as
	 * parameter.
	 *
	 * @see #getHeaderNames
	 * @see #getHeader
	 * @see #getHeaders
	 * @see #getDateHeader
	 * @see #getIntHeader
	 */
	public void addHeader(String name, Object value) {
		if (CONTENT_TYPE_HEADER.equalsIgnoreCase(name)) {
			setContentType((String) value);
			return;
		}
		doAddHeaderValue(name, value, false);
	}

	@SuppressWarnings("rawtypes")
	private void doAddHeaderValue(String name, Object value, boolean replace) {
		HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
		if (header == null || replace) {
			header = new HeaderValueHolder();
			this.headers.put(name, header);
		}
		if (value instanceof Collection) {
			header.addValues((Collection) value);
		} else if (value.getClass().isArray()) {
			header.addValueArray(value);
		} else {
			header.addValue(value);
		}
	}

	@Override
	public long getDateHeader(String name) {
		HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
		Object value = (header != null ? header.getValue() : null);
		if (value instanceof Date) {
			return ((Date) value).getTime();
		} else if (value instanceof Number) {
			return ((Number) value).longValue();
		} else if (value != null) {
			throw new IllegalArgumentException(
				"Value for header '" + name + "' is neither a Date nor a Number: " + value);
		} else {
			return -1L;
		}
	}

	@Override
	public String getHeader(String name) {
		HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
		return (header != null ? header.getStringValue() : null);
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
		return Collections.enumeration(header != null ? header.getStringValues() : new LinkedList<String>());
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return Collections.enumeration(this.headers.keySet());
	}

	@Override
	public int getIntHeader(String name) {
		HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
		Object value = (header != null ? header.getValue() : null);
		if (value instanceof Number) {
			return ((Number) value).intValue();
		} else if (value instanceof String) {
			return Integer.parseInt((String) value);
		} else if (value != null) {
			throw new NumberFormatException("Value for header '" + name + "' is not a Number: " + value);
		} else {
			return -1;
		}
	}

	@Override
	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public String getPathInfo() {
		return this.pathInfo;
	}

	public void setPathInfo(String pathInfo) {
		this.pathInfo = pathInfo;
	}

	@Override
	public String getPathTranslated() {
		return (this.pathInfo != null ? getRealPath(this.pathInfo) : null);
	}

	@Override
	public String getContextPath() {
		return this.contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	@Override
	public String getQueryString() {
		return this.queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	@Override
	public String getRemoteUser() {
		return this.remoteUser;
	}

	public void setRemoteUser(String remoteUser) {
		this.remoteUser = remoteUser;
	}

	public void addUserRole(String role) {
		this.userRoles.add(role);
	}

	@Override
	public boolean isUserInRole(String role) {
		return (this.userRoles.contains(role) || (this.servletContext instanceof MockServletContext &&
			((MockServletContext) this.servletContext).getDeclaredRoles().contains(role)));
	}

	@Override
	public Principal getUserPrincipal() {
		return this.userPrincipal;
	}

	public void setUserPrincipal(Principal userPrincipal) {
		this.userPrincipal = userPrincipal;
	}

	@Override
	public String getRequestedSessionId() {
		return this.requestedSessionId;
	}

	public void setRequestedSessionId(String requestedSessionId) {
		this.requestedSessionId = requestedSessionId;
	}

	@Override
	public String getRequestURI() {
		return this.requestURI;
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	@Override
	public StringBuffer getRequestURL() {
		StringBuffer url = new StringBuffer(this.scheme);
		url.append("://").append(this.serverName).append(':').append(this.serverPort);
		url.append(getRequestURI());
		return url;
	}

	@Override
	public String getServletPath() {
		return this.servletPath;
	}

	public void setServletPath(String servletPath) {
		this.servletPath = servletPath;
	}

	@Override
	public HttpSession getSession(boolean create) {
		checkActive();
		// Reset session if invalidated.
		if (this.session instanceof MockHttpSession && ((MockHttpSession) this.session).isInvalid()) {
			this.session = null;
		}
		// Create new session if necessary.
		if (this.session == null && create) {
			this.session = new MockHttpSession(this.servletContext);
		}
		return this.session;
	}

	@Override
	public HttpSession getSession() {
		return getSession(true);
	}

	@Override
	public String changeSessionId() {
		return null;
	}

	public void setSession(HttpSession session) {
		this.session = session;
		if (session instanceof MockHttpSession) {
			MockHttpSession mockSession = ((MockHttpSession) session);
			mockSession.access();
		}
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return this.requestedSessionIdValid;
	}

	public void setRequestedSessionIdValid(boolean requestedSessionIdValid) {
		this.requestedSessionIdValid = requestedSessionIdValid;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return this.requestedSessionIdFromCookie;
	}

	public void setRequestedSessionIdFromCookie(boolean requestedSessionIdFromCookie) {
		this.requestedSessionIdFromCookie = requestedSessionIdFromCookie;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return this.requestedSessionIdFromURL;
	}

	public void setRequestedSessionIdFromURL(boolean requestedSessionIdFromURL) {
		this.requestedSessionIdFromURL = requestedSessionIdFromURL;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return isRequestedSessionIdFromURL();
	}

	public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
		return (this.userPrincipal != null && this.remoteUser != null && this.authType != null);
	}

	public void login(String username, String password) throws ServletException {
		throw new ServletException("Username-password authentication not supported - override the login method");
	}

	public void logout() throws ServletException {
		this.userPrincipal = null;
		this.remoteUser = null;
		this.authType = null;
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		return null;
	}

	@Override
	public Part getPart(String name) throws IOException, ServletException {
		return null;
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
		return null;
	}

}
