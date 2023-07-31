package io.polaris.core.net.http;

import io.polaris.core.consts.StdConsts;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.Strings;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.net.ssl.SSLContext;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
@NoArgsConstructor
@Getter
@Setter
public class RequestSettings {
	public static final String POST = "POST";
	public static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.1774.50";
	private String url;
	private SSLContext sslContext;
	private String keyStorePassword;
	private String keyStorePath;
	private String requestMethod;
	private String content;
	private String charset;
	private ContentType contentType;
	private Map<String, String> headers;
	private String userAgent;
	private int connectTimeout;
	private int readTimeout;
	private boolean readBytes = false;

	public String getRequestMethod() {
		return Strings.isNotBlank(requestMethod) ? requestMethod : POST;
	}

	public ContentType getContentType() {
		return contentType != null ? contentType : ContentType.JSON;
	}

	public String getCharset() {
		return Strings.isNotBlank(charset) ? charset : StdConsts.UTF_8;
	}

	public String getUserAgent() {
		return Strings.isNotBlank(userAgent) ? userAgent : DEFAULT_USER_AGENT;
	}

	public RequestSettings withHeader(String name, String value) {
		(this.headers == null ? this.headers = new HashMap<>() : this.headers).put(name, value);
		return this;
	}

	public RequestSettings withUrl(java.lang.String url) {
		this.url = url;
		return this;
	}

	public RequestSettings withSslContext(javax.net.ssl.SSLContext sslContext) {
		this.sslContext = sslContext;
		return this;
	}

	public RequestSettings withKeyStorePassword(java.lang.String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
		return this;
	}

	public RequestSettings withKeyStorePath(java.lang.String keyStorePath) {
		this.keyStorePath = keyStorePath;
		return this;
	}

	public RequestSettings withRequestMethod(java.lang.String requestMethod) {
		this.requestMethod = requestMethod;
		return this;
	}

	public RequestSettings withContent(java.lang.String content) {
		this.content = content;
		return this;
	}

	public RequestSettings withCharset(java.lang.String charset) {
		this.charset = charset;
		return this;
	}

	public RequestSettings withContentType(io.polaris.core.net.http.ContentType contentType) {
		this.contentType = contentType;
		return this;
	}

	public RequestSettings withHeaders(java.util.Map<java.lang.String, java.lang.String> headers) {
		this.headers = headers;
		return this;
	}

	public RequestSettings withUserAgent(java.lang.String userAgent) {
		this.userAgent = userAgent;
		return this;
	}

	public RequestSettings withConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public RequestSettings withReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}
	public RequestSettings withReadBytes(boolean readBytes) {
		this.readBytes = readBytes;
		return this;
	}

	public static void main(String[] args) {
		Field[] fields = Reflects.getFields(RequestSettings.class, field -> !Modifier.isStatic(field.getModifiers()));
		for (Field field : fields) {
			String methodName = "with" + Strings.capitalize(field.getName());
			Method method = Reflects.getPublicMethod(RequestSettings.class, methodName, field.getType());
			if (method != null) {
				continue;
			}
			System.out.printf("public %s %s(%s %s){\n\tthis.%s = %s;\n\treturn this;\n}\n",
				RequestSettings.class.getSimpleName(), methodName, field.getGenericType().getTypeName(),
				field.getName(), field.getName(), field.getName()
			);
		}
	}
}
