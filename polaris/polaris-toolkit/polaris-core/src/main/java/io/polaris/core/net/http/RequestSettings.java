package io.polaris.core.net.http;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.net.ssl.SSLContext;

/**
 * @author Qt
 * @since 1.8
 */
@Builder
@Getter
@Setter
public class RequestSettings {
	String url;
	SSLContext sslContext;
	String keyStorePassword;
	String keyStorePath;
	String requestMethod;
	String content;
	String charset;
	ContentType contentType;
}
