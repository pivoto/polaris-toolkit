package io.polaris.web.mock;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.activation.FileTypeMap;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import io.polaris.core.consts.StdKeys;
import io.polaris.core.string.Strings;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MockServletContext implements ServletContext {
	private static final String COMMON_DEFAULT_SERVLET_NAME = "default";
	private static final String TEMP_DIR_SYSTEM_PROPERTY = "java.io.tmpdir";

	private final Map<String, ServletContext> contexts = new HashMap<String, ServletContext>();

	private final Map<String, String> initParameters = new LinkedHashMap<String, String>();

	private final Map<String, Object> attributes = new LinkedHashMap<String, Object>();

	private final Set<String> declaredRoles = new HashSet<String>();

	private final Map<String, RequestDispatcher> namedRequestDispatchers = new HashMap<String, RequestDispatcher>();

	private final String resourceBasePath;

	private String contextPath = "";

	private int majorVersion = 2;

	private int minorVersion = 5;

	private int effectiveMajorVersion = 2;

	private int effectiveMinorVersion = 5;

	private String servletContextName = "MockServletContext";

	private String defaultServletName = COMMON_DEFAULT_SERVLET_NAME;

	public MockServletContext() {
		this(System.getProperty(StdKeys.USER_DIR));
	}

	public MockServletContext(String resourceBasePath) {
		this.resourceBasePath = Strings.coalesce(resourceBasePath, ".");
		// Use JVM temp dir as ServletContext temp dir.
		String tempDir = System.getProperty(TEMP_DIR_SYSTEM_PROPERTY);
		registerNamedDispatcher(this.defaultServletName, new MockRequestDispatcher(this.defaultServletName));
	}


	@Override
	public String getContextPath() {
		return this.contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = (contextPath != null ? contextPath : "");
	}

	public void registerContext(String contextPath, ServletContext context) {
		this.contexts.put(contextPath, context);
	}

	@Override
	public ServletContext getContext(String contextPath) {
		if (this.contextPath.equals(contextPath)) {
			return this;
		}
		return this.contexts.get(contextPath);
	}

	@Override
	public int getMajorVersion() {
		return this.majorVersion;
	}

	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}

	@Override
	public int getMinorVersion() {
		return this.minorVersion;
	}

	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}

	public int getEffectiveMajorVersion() {
		return this.effectiveMajorVersion;
	}

	public void setEffectiveMajorVersion(int effectiveMajorVersion) {
		this.effectiveMajorVersion = effectiveMajorVersion;
	}

	public int getEffectiveMinorVersion() {
		return this.effectiveMinorVersion;
	}

	public void setEffectiveMinorVersion(int effectiveMinorVersion) {
		this.effectiveMinorVersion = effectiveMinorVersion;
	}

	@Override
	public String getMimeType(String filePath) {
		String mimeType = MimeTypeResolver.getMimeType(filePath);
		return ("application/octet-stream".equals(mimeType)) ? null : mimeType;
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		try {
			File[] files = new File(this.resourceBasePath, path).listFiles();
			Set<String> resourcePaths = new LinkedHashSet<String>(files.length);
			for (File file : files) {
				String resultPath = file.getName();
				if (file.isDirectory()) {
					resultPath += "/";
				}
				resourcePaths.add(resultPath);
			}
			return resourcePaths;
		} catch (Exception ex) {
			log.warn("Couldn't get resource paths for " + path, ex);
			return null;
		}
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		return Thread.currentThread().getContextClassLoader().getResource(path);
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		if (!path.startsWith("/")) {
			throw new IllegalArgumentException("RequestDispatcher path at ServletContext level must start with '/'");
		}
		return new MockRequestDispatcher(path);
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String path) {
		return this.namedRequestDispatchers.get(path);
	}


	public void registerNamedDispatcher(String name, RequestDispatcher requestDispatcher) {
		this.namedRequestDispatchers.put(name, requestDispatcher);
	}


	public void unregisterNamedDispatcher(String name) {
		this.namedRequestDispatchers.remove(name);
	}


	public String getDefaultServletName() {
		return this.defaultServletName;
	}


	public void setDefaultServletName(String defaultServletName) {
		unregisterNamedDispatcher(this.defaultServletName);
		this.defaultServletName = defaultServletName;
		registerNamedDispatcher(this.defaultServletName, new MockRequestDispatcher(this.defaultServletName));
	}

	@Override
	public Servlet getServlet(String name) {
		return null;
	}

	@Override
	public Enumeration<Servlet> getServlets() {
		return Collections.enumeration(new HashSet<Servlet>());
	}

	@Override
	public Enumeration<String> getServletNames() {
		return Collections.enumeration(new HashSet<String>());
	}

	@Override
	public void log(String message) {
		log.info(message);
	}

	@Override
	public void log(Exception ex, String message) {
		log.info(message, ex);
	}

	@Override
	public void log(String message, Throwable ex) {
		log.info(message, ex);
	}

	@Override
	public String getRealPath(String path) {
		return new File(this.resourceBasePath, path).getAbsolutePath();
	}

	@Override
	public String getServerInfo() {
		return "MockServletContext";
	}

	@Override
	public String getInitParameter(String name) {
		return this.initParameters.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(this.initParameters.keySet());
	}

	public boolean setInitParameter(String name, String value) {
		if (this.initParameters.containsKey(name)) {
			return false;
		}
		this.initParameters.put(name, value);
		return true;
	}

	public void addInitParameter(String name, String value) {
		this.initParameters.put(name, value);
	}

	@Override
	public Object getAttribute(String name) {
		return this.attributes.get(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return Collections.enumeration(new LinkedHashSet<String>(this.attributes.keySet()));
	}

	@Override
	public void setAttribute(String name, Object value) {
		if (value != null) {
			this.attributes.put(name, value);
		} else {
			this.attributes.remove(name);
		}
	}

	@Override
	public void removeAttribute(String name) {
		this.attributes.remove(name);
	}

	@Override
	public String getServletContextName() {
		return this.servletContextName;
	}

	@Override
	public ServletRegistration.Dynamic addServlet(String servletName, String className) {
		return null;
	}

	@Override
	public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
		return null;
	}

	@Override
	public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
		return null;
	}

	@Override
	public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
		return null;
	}

	@Override
	public ServletRegistration getServletRegistration(String servletName) {
		return null;
	}

	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		return null;
	}

	@Override
	public FilterRegistration.Dynamic addFilter(String filterName, String className) {
		return null;
	}

	@Override
	public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
		return null;
	}

	@Override
	public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
		return null;
	}

	@Override
	public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
		return null;
	}

	@Override
	public FilterRegistration getFilterRegistration(String filterName) {
		return null;
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		return null;
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		return null;
	}

	@Override
	public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {

	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		return null;
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		return null;
	}

	@Override
	public void addListener(String className) {

	}

	@Override
	public <T extends EventListener> void addListener(T t) {

	}

	@Override
	public void addListener(Class<? extends EventListener> listenerClass) {

	}

	@Override
	public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
		return null;
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		return null;
	}

	public void setServletContextName(String servletContextName) {
		this.servletContextName = servletContextName;
	}

	public ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	public void declareRoles(String... roleNames) {
		for (String roleName : roleNames) {
			this.declaredRoles.add(roleName);
		}
	}

	@Override
	public String getVirtualServerName() {
		return null;
	}

	public Set<String> getDeclaredRoles() {
		return Collections.unmodifiableSet(this.declaredRoles);
	}


	private static class MimeTypeResolver {

		public static String getMimeType(String filePath) {
			return FileTypeMap.getDefaultFileTypeMap().getContentType(filePath);
		}
	}

}
