package io.polaris.core.net.http;

import io.polaris.core.string.Strings;

import java.nio.charset.Charset;

/**
 * @author Qt
 * @since 1.8
 */
public enum ContentType {

	/**
	 * 标准表单编码，当action为get时候，浏览器用x-www-form-urlencoded的编码方式把form数据转换成一个字串（name1=value1&name2=value2…）
	 */
	FORM_URLENCODED("application/x-www-form-urlencoded"),
	/**
	 * 文件上传编码，浏览器会把整个表单以控件为单位分割，并为每个部分加上Content-Disposition，并加上分割符(boundary)
	 */
	MULTIPART("multipart/form-data"),
	/**
	 * Rest请求JSON编码
	 */
	JSON("application/json"),
	/**
	 * Rest请求XML编码
	 */
	XML("application/xml"),
	/**
	 * Rest请求text/xml编码
	 */
	TEXT_XML("text/xml");

	private String value;

	private ContentType(String value) {
		this.value = value;
	}

	public String toString() {
		return value;
	}

	public String toString(String charset) {
		return this.value + ";charset=" + charset;
	}

	public String toString(Charset charset) {
		return this.value + ";charset=" + charset.name();
	}

	public static boolean isFormUrlEncoded(String contentType) {
		return contentType.startsWith(FORM_URLENCODED.value);
	}

	/**
	 * 从请求参数的body中判断请求的Content-Type类型，支持的类型有：
	 * <ul>
	 *   <li>application/json</li>
	 *   <li>application/xml</li>
	 * </ul>
	 */
	public static ContentType get(String body) {
		ContentType contentType = null;
		if (Strings.isNotBlank(body)) {
			char firstChar = body.charAt(0);
			switch (firstChar) {
				case '{':
				case '[':
					// JSON请求体
					contentType = JSON;
					break;
				case '<':
					// XML请求体
					contentType = XML;
					break;

				default:
					break;
			}
		}
		return contentType;
	}


}
