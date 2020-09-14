package io.polaris.core.msg;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qt
 * @since 1.8
 */
public class MessageResources {

	public final static String DEFAULT_MESSAGE_BASE_NAME = "msg,i18n,error,ValidationMessages";
	private final static Map<String, MessageResource> resources = new ConcurrentHashMap<>();
	private static MessageResource defaultMessageResource = getMessageResource(DEFAULT_MESSAGE_BASE_NAME);

	public static MessageResource getMessageResource(String baseName) {
		return resources.computeIfAbsent(baseName, k -> new MessageResource(baseName));
	}

	public static MessageResource getDefaultMessageResource() {
		return defaultMessageResource;
	}

	public static void setDefaultMessageResource(MessageResource defaultMessageResource) {
		if (defaultMessageResource == null) {
			throw new IllegalArgumentException();
		}
		MessageResources.defaultMessageResource = defaultMessageResource;
	}
}
