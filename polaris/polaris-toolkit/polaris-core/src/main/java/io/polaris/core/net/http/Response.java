package io.polaris.core.net.http;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
@Getter
@Setter
public class Response {

	private int responseCode;
	private String responseMessage;
	private Map<String, List<String>> responseHeaders;
	private String contentType;
	private long contentLength;

	private String content;
	private byte[] contentBytes;

}
