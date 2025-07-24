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

	public static String format(String msg, String defaultMsg, Object[] args) {
		if (msg == null || msg.isEmpty()) {
			msg = defaultMsg;
			defaultMsg = null;
		}
		if (msg == null || msg.isEmpty()) {
			return "";
		}
		if (!msg.contains("{") && !msg.contains("}")) {
			return msg;
		}
		if (defaultMsg != null) {
			return MessageFormat.formatWithEmpty(msg, defaultMsg, args);
		}
		return MessageFormat.format(msg, args);
	}

}
