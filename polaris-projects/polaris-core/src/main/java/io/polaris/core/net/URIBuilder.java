package io.polaris.core.net;

import io.polaris.core.string.Strings;
import io.polaris.core.tuple.Pair;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Qt
 * @since 1.8
 */
public class URIBuilder {
	private String scheme;
	private String encodedSchemeSpecificPart;
	private String encodedAuthority;
	private String userInfo;
	private String encodedUserInfo;
	private String host;
	private int port;
	private String encodedPath;
	private List<String> pathSegments;
	private String encodedQuery;
	private List<Pair<String, String>> queryParams;
	private String query;
	private Charset charset;
	private String fragment;
	private String encodedFragment;

	public URIBuilder() {
		super();
		this.port = -1;
	}

	public URIBuilder(final String string) throws URISyntaxException {
		this(new URI(string), null);
	}

	public URIBuilder(final URI uri) {
		this(uri, null);
	}

	public URIBuilder(final String string, final Charset charset) throws URISyntaxException {
		this(new URI(string), charset);
	}

	public URIBuilder(final URI uri, final Charset charset) {
		super();
		setCharset(charset);
		digestURI(uri);
	}

	public URIBuilder setCharset(final Charset charset) {
		this.charset = charset;
		return this;
	}

	public Charset getCharset() {
		return charset;
	}

	private List<Pair<String, String>> parseQuery(final String query, final Charset charset) {
		if (query != null && !query.isEmpty()) {
			return URLEncoders.parseQuery(query, charset);
		}
		return null;
	}

	private List<String> parsePath(final String path, final Charset charset) {
		if (path != null && !path.isEmpty()) {
			return URLEncoders.parsePathSegments(path, charset);
		}
		return null;
	}

	public URI build() throws URISyntaxException {
		return new URI(buildString());
	}

	private String buildString() {
		final StringBuilder sb = new StringBuilder();
		if (this.scheme != null) {
			sb.append(this.scheme).append(':');
		}
		if (this.encodedSchemeSpecificPart != null) {
			sb.append(this.encodedSchemeSpecificPart);
		} else {
			if (this.encodedAuthority != null) {
				sb.append("//").append(this.encodedAuthority);
			} else if (this.host != null) {
				sb.append("//");
				if (this.encodedUserInfo != null) {
					sb.append(this.encodedUserInfo).append("@");
				} else if (this.userInfo != null) {
					sb.append(encodeUserInfo(this.userInfo)).append("@");
				}
				if (Nets.isIPv6Address(this.host)) {
					sb.append("[").append(this.host).append("]");
				} else {
					sb.append(this.host);
				}
				if (this.port >= 0) {
					sb.append(":").append(this.port);
				}
			}
			if (this.encodedPath != null) {
				sb.append(normalizePath(this.encodedPath, sb.length() == 0));
			} else if (this.pathSegments != null) {
				sb.append(encodePath(this.pathSegments));
			}
			if (this.encodedQuery != null) {
				sb.append("?").append(this.encodedQuery);
			} else if (this.queryParams != null && !this.queryParams.isEmpty()) {
				sb.append("?").append(encodeUrlForm(this.queryParams));
			} else if (this.query != null) {
				sb.append("?").append(encodeUric(this.query));
			}
		}
		if (this.encodedFragment != null) {
			sb.append("#").append(this.encodedFragment);
		} else if (this.fragment != null) {
			sb.append("#").append(encodeUric(this.fragment));
		}
		return sb.toString();
	}

	private static String normalizePath(final String path, final boolean relative) {
		String s = path;
		if (Strings.isBlank(s)) {
			return "";
		}
		if (!relative && !s.startsWith("/")) {
			s = "/" + s;
		}
		return s;
	}

	private void digestURI(final URI uri) {
		this.scheme = uri.getScheme();
		this.encodedSchemeSpecificPart = uri.getRawSchemeSpecificPart();
		this.encodedAuthority = uri.getRawAuthority();
		this.host = uri.getHost();
		this.port = uri.getPort();
		this.encodedUserInfo = uri.getRawUserInfo();
		this.userInfo = uri.getUserInfo();
		this.encodedPath = uri.getRawPath();
		this.pathSegments = parsePath(uri.getRawPath(), this.charset != null ? this.charset : StandardCharsets.UTF_8);
		this.encodedQuery = uri.getRawQuery();
		this.queryParams = parseQuery(uri.getRawQuery(), this.charset != null ? this.charset : StandardCharsets.UTF_8);
		this.encodedFragment = uri.getRawFragment();
		this.fragment = uri.getFragment();
	}

	private String encodeUserInfo(final String userInfo) {
		return URLEncoders.encodeUserInfo(userInfo, charset);
	}

	private String encodePath(final List<String> pathSegments) {
		return URLEncoders.formatSegments(pathSegments, charset);
	}

	private String encodeUrlForm(final List<Pair<String, String>> params) {
		return URLEncoders.format(params, this.charset);
	}

	private String encodeUric(final String fragment) {
		return URLEncoders.encodeUric(fragment, this.charset != null ? this.charset : StandardCharsets.UTF_8);
	}

	public URIBuilder setScheme(final String scheme) {
		this.scheme = scheme;
		return this;
	}

	public URIBuilder setUserInfo(final String userInfo) {
		this.userInfo = userInfo;
		this.encodedSchemeSpecificPart = null;
		this.encodedAuthority = null;
		this.encodedUserInfo = null;
		return this;
	}

	public URIBuilder setUserInfo(final String username, final String password) {
		return setUserInfo(username + ':' + password);
	}

	public URIBuilder setHost(final String host) {
		this.host = host;
		this.encodedSchemeSpecificPart = null;
		this.encodedAuthority = null;
		return this;
	}

	public URIBuilder setPort(final int port) {
		this.port = port < 0 ? -1 : port;
		this.encodedSchemeSpecificPart = null;
		this.encodedAuthority = null;
		return this;
	}

	public URIBuilder setPath(final String path) {
		return setPathSegments(path != null ? URLEncoders.parsePathSegments(path, charset) : null);
	}

	public URIBuilder setPathSegments(final String... pathSegments) {
		this.pathSegments = pathSegments.length > 0 ? Arrays.asList(pathSegments) : null;
		this.encodedSchemeSpecificPart = null;
		this.encodedPath = null;
		return this;
	}

	public URIBuilder setPathSegments(final List<String> pathSegments) {
		this.pathSegments = pathSegments != null && pathSegments.size() > 0 ? new ArrayList<>(pathSegments) : null;
		this.encodedSchemeSpecificPart = null;
		this.encodedPath = null;
		return this;
	}

	public URIBuilder removeQuery() {
		this.queryParams = null;
		this.query = null;
		this.encodedQuery = null;
		this.encodedSchemeSpecificPart = null;
		return this;
	}

	public URIBuilder setQuery(final String query) {
		this.queryParams = parseQuery(query, this.charset);
		this.query = null;
		this.encodedQuery = null;
		this.encodedSchemeSpecificPart = null;
		return this;
	}

	public URIBuilder setParameters(final List<Pair<String, String>> params) {
		if (this.queryParams == null) {
			this.queryParams = new ArrayList<>();
		} else {
			this.queryParams.clear();
		}
		this.queryParams.addAll(params);
		this.encodedQuery = null;
		this.encodedSchemeSpecificPart = null;
		this.query = null;
		return this;
	}

	public URIBuilder addParameters(final List<Pair<String, String>> params) {
		if (this.queryParams == null) {
			this.queryParams = new ArrayList<>();
		}
		this.queryParams.addAll(params);
		this.encodedQuery = null;
		this.encodedSchemeSpecificPart = null;
		this.query = null;
		return this;
	}

	public URIBuilder setParameters(final Pair<String, String>... params) {
		if (this.queryParams == null) {
			this.queryParams = new ArrayList<>();
		} else {
			this.queryParams.clear();
		}
		for (final Pair<String, String> nvp : params) {
			this.queryParams.add(nvp);
		}
		this.encodedQuery = null;
		this.encodedSchemeSpecificPart = null;
		this.query = null;
		return this;
	}

	public URIBuilder addParameter(final String param, final String value) {
		if (this.queryParams == null) {
			this.queryParams = new ArrayList<>();
		}
		this.queryParams.add(Pair.of(param, value));
		this.encodedQuery = null;
		this.encodedSchemeSpecificPart = null;
		this.query = null;
		return this;
	}

	public URIBuilder setParameter(final String param, final String value) {
		if (this.queryParams == null) {
			this.queryParams = new ArrayList<>();
		}
		if (!this.queryParams.isEmpty()) {
			for (final Iterator<Pair<String, String>> it = this.queryParams.iterator(); it.hasNext(); ) {
				final Pair<String, String> nvp = it.next();
				if (nvp.getKey().equals(param)) {
					it.remove();
				}
			}
		}
		this.queryParams.add(new Pair<>(param, value));
		this.encodedQuery = null;
		this.encodedSchemeSpecificPart = null;
		this.query = null;
		return this;
	}

	public URIBuilder clearParameters() {
		this.queryParams = null;
		this.encodedQuery = null;
		this.encodedSchemeSpecificPart = null;
		return this;
	}

	public URIBuilder setCustomQuery(final String query) {
		this.query = query;
		this.encodedQuery = null;
		this.encodedSchemeSpecificPart = null;
		this.queryParams = null;
		return this;
	}

	public URIBuilder setFragment(final String fragment) {
		this.fragment = fragment;
		this.encodedFragment = null;
		return this;
	}

	public boolean isAbsolute() {
		return this.scheme != null;
	}

	public boolean isOpaque() {
		return this.pathSegments == null && this.encodedPath == null;
	}

	public String getScheme() {
		return this.scheme;
	}

	public String getUserInfo() {
		return this.userInfo;
	}

	public String getHost() {
		return this.host;
	}

	public int getPort() {
		return this.port;
	}

	public boolean isPathEmpty() {
		return (this.pathSegments == null || this.pathSegments.isEmpty()) &&
			(this.encodedPath == null || this.encodedPath.isEmpty());
	}

	public List<String> getPathSegments() {
		return this.pathSegments != null ? new ArrayList<>(this.pathSegments) : Collections.emptyList();
	}

	public String getPath() {
		if (this.pathSegments == null) {
			return null;
		}
		final StringBuilder result = new StringBuilder();
		for (final String segment : this.pathSegments) {
			result.append('/').append(segment);
		}
		return result.toString();
	}

	public boolean isQueryEmpty() {
		return (this.queryParams == null || this.queryParams.isEmpty()) && this.encodedQuery == null;
	}

	public List<Pair<String, String>> getQueryParams() {
		return this.queryParams != null ? new ArrayList<>(this.queryParams) : Collections.emptyList();
	}

	public String getFragment() {
		return this.fragment;
	}

	@Override
	public String toString() {
		return buildString();
	}
}
