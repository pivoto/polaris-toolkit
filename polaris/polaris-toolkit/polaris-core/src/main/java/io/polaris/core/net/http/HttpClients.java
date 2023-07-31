package io.polaris.core.net.http;

import io.polaris.core.io.IO;
import io.polaris.core.string.Strings;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author Qt
 * @since 1.8
 */
public class HttpClients {


	public static Response doFormPost(String url, String params) throws IOException, GeneralSecurityException {
		return doRequest(new RequestSettings().withUrl(url)
			.withContentType(ContentType.FORM_URLENCODED).withRequestMethod(RequestSettings.POST).withContent(params));
	}

	public static Response doPost(String url, String content) throws IOException, GeneralSecurityException {
		return doRequest(new RequestSettings().withUrl(url).withContent(content).withRequestMethod(RequestSettings.POST));
	}


	public static Response doHttpsPost(String url, String keyStorePath, String keyStorePassword, String content)
		throws IOException, GeneralSecurityException {
		return doRequest(new RequestSettings().withUrl(url).withRequestMethod(RequestSettings.POST).withKeyStorePassword(keyStorePassword)
			.withKeyStorePath(keyStorePath).withContent(content));
	}

	public static Response doRequest(RequestSettings settings) throws IOException, GeneralSecurityException {
		String requestMethod = settings.getRequestMethod();
		String requestUrl = settings.getUrl();
		boolean https = requestUrl.startsWith("https://");
		String charset = settings.getCharset();
		ContentType contentType = settings.getContentType();

		URL url = new URL(requestUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		if (https) {
			SSLContext sslContext = settings.getSslContext();
			if (sslContext == null) {
				String keyStorePassword = settings.getKeyStorePassword();
				String keyStorePath = settings.getKeyStorePath();
				if (Strings.isNotBlank(keyStorePassword) && Strings.isNotBlank(keyStorePath)) {
					sslContext = getSSLContext(keyStorePassword, keyStorePath);
				} else {
					sslContext = getNoopSSLContext();
				}
			}
			((HttpsURLConnection) conn).setSSLSocketFactory(sslContext.getSocketFactory());
			((HttpsURLConnection) conn).setHostnameVerifier(getNoopHostnameVerifier());
		}

		conn.setRequestMethod(requestMethod);
		if (settings.getConnectTimeout() > 0) {
			conn.setConnectTimeout(settings.getConnectTimeout());
		}
		if (settings.getReadTimeout() > 0) {
			conn.setReadTimeout(settings.getReadTimeout());
		}

		conn.setRequestProperty("accept", "*/*");
		conn.setRequestProperty("Connection", "Keep-Alive");
		if (settings.getHeaders() != null) {
			settings.getHeaders().forEach(conn::setRequestProperty);
		}
		conn.setRequestProperty("User-Agent", settings.getUserAgent());
		conn.setRequestProperty("Content-Type", contentType.toString(charset));

		conn.setUseCaches(true);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.connect();

		String content = settings.getContent();
		if (Strings.isNotBlank(content)) {
			try (OutputStream out = conn.getOutputStream();) {
				out.write(content.getBytes(charset));
				out.flush();
			}
		}
		Response response = new Response();
		response.setResponseCode(conn.getResponseCode());
		response.setResponseMessage(conn.getResponseMessage());
		response.setResponseHeaders(conn.getHeaderFields());
		response.setContentLength(conn.getContentLengthLong());
		response.setContentType(conn.getContentType());

		// 读取服务器端返回的内容
		try (InputStream in = conn.getInputStream();) {
			if (settings.isReadBytes()) {
				response.setContentBytes(IO.toBytes(in));
			} else {
				response.setContent(IO.toString(in, charset));
			}
		}
		return response;
	}

	public static HostnameVerifier getNoopHostnameVerifier() {
		HostnameVerifier noop = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		return noop;
	}

	public static X509TrustManager getNoopX509TrustManager() {
		return new X509TrustManager() {

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

		};
	}

	public static SSLContext getNoopSSLContext() throws GeneralSecurityException {
		SSLContext ctx = SSLContext.getInstance("SSL");
		ctx.init(null, new TrustManager[]{getNoopX509TrustManager()}, new java.security.SecureRandom());
		return ctx;
	}

	public static SSLContext getSSLContext(String password, String keyStorePath)
		throws GeneralSecurityException, IOException {
		return getSSLContext(password, keyStorePath, keyStorePath);
	}

	/**
	 * 获得SSLSocketFactory.
	 *
	 * @param password       密码
	 * @param keyStorePath   密钥库路径
	 * @param trustStorePath 信任库路径
	 * @return SSLSocketFactory
	 * @throws Exception
	 */
	public static SSLContext getSSLContext(String password, String keyStorePath, String trustStorePath)
		throws GeneralSecurityException, IOException {
		// 实例化密钥库   KeyManager选择证书证明自己的身份
		KeyManagerFactory keyManagerFactory = KeyManagerFactory
			.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		// 获得密钥库
		KeyStore keyStore = getKeyStore(password, keyStorePath);
		// 初始化密钥工厂
		keyManagerFactory.init(keyStore, password.toCharArray());

		// 实例化信任库    TrustManager决定是否信任对方的证书
		TrustManagerFactory trustManagerFactory = TrustManagerFactory
			.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		// 获得信任库
		KeyStore trustStore = getKeyStore(password, trustStorePath);
		// 初始化信任库
		trustManagerFactory.init(trustStore);
		// 实例化SSL上下文
		SSLContext ctx = SSLContext.getInstance("TLS");
		// 初始化SSL上下文
		ctx.init(keyManagerFactory.getKeyManagers(),
			trustManagerFactory.getTrustManagers(), null);
		// 获得SSLSocketFactory
		return ctx;
	}

	/**
	 * 获得KeyStore.
	 *
	 * @param keyStorePath 密钥库路径
	 * @param password     密码
	 * @return 密钥库
	 * @throws Exception
	 */
	public static KeyStore getKeyStore(String password, String keyStorePath)
		throws GeneralSecurityException, IOException {
		// 实例化密钥库 KeyStore用于存放证书，创建对象时 指定交换数字证书的加密标准
		//指定交换数字证书的加密标准
		KeyStore ks = KeyStore.getInstance("JKS");
		// 获得密钥库文件流
		InputStream is = IO.getInputStream(keyStorePath, HttpClients.class);
		// 加载密钥库
		ks.load(is, password.toCharArray());
		// 关闭密钥库文件流
		is.close();
		return ks;
	}

	/**
	 * 初始化HttpsURLConnection.
	 *
	 * @param password       密码
	 * @param keyStorePath   密钥库路径
	 * @param trustStorePath 信任库路径
	 * @throws Exception
	 */
	public static void initHttpsDefaultConfig(String password
		, String keyStorePath, String trustStorePath) throws Exception {
		// 声明SSL上下文
		SSLContext sslContext = null;
		// 实例化主机名验证接口
		HostnameVerifier noop = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		try {
			sslContext = getSSLContext(password, keyStorePath, trustStorePath);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		if (sslContext != null) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		}
		HttpsURLConnection.setDefaultHostnameVerifier(noop);
	}


}
