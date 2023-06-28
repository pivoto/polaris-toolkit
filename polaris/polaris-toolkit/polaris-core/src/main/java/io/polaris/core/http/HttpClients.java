package io.polaris.core.http;

import io.polaris.core.consts.StdConsts;
import io.polaris.core.io.IO;
import io.polaris.core.string.Strings;

import javax.net.ssl.*;
import java.io.*;
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
	public static final String POST = "POST";


	public static String doFormPost(String url, String params) throws IOException, GeneralSecurityException {
		return doRequest(RequestSettings.builder().url(url)
			.contentType(ContentType.FORM_URLENCODED).requestMethod(POST).content(params).build());
	}

	public static String doPost(String url, String content) throws IOException, GeneralSecurityException {
		return doRequest(RequestSettings.builder().url(url).content(content).requestMethod(POST).build());
	}


	public static String doHttpsPost(String url, String keyStorePath, String keyStorePassword, String content)
		throws IOException, GeneralSecurityException {
		return doRequest(RequestSettings.builder().url(url).requestMethod(POST).keyStorePassword(keyStorePassword)
			.keyStorePath(keyStorePath).content(content).build());
	}

	public static String doRequest(RequestSettings settings)
		throws IOException, GeneralSecurityException {
		// region settings
		String requestMethod = settings.getRequestMethod();
		if (Strings.isBlank(requestMethod)) {
			requestMethod = POST;
		}
		String requestUrl = settings.getUrl();
		boolean https = requestUrl.startsWith("https://");

		String charset = settings.getCharset();
		if (Strings.isBlank(charset)) {
			charset = StdConsts.UTF_8;
		}

		ContentType contentType = settings.getContentType();
		if (contentType==null) {
			contentType = ContentType.JSON;
		}
		// endregion

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
		conn.setRequestProperty("accept", "*/*");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("Content-Type", contentType.toString(charset));
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.connect();

		String content = settings.getContent();
		if (Strings.isNotBlank(content)) {
			OutputStream os = conn.getOutputStream();
			os.write(content.getBytes(charset));
			os.flush();
			os.close();
		}

		// 读取服务器端返回的内容
		InputStream is = conn.getInputStream();
		return IO.toString(is, charset);
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
	public static void initHttpsURLConnection(String password,
																						String keyStorePath, String trustStorePath) throws Exception {
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
