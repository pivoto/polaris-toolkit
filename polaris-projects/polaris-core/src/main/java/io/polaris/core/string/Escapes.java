package io.polaris.core.string;

/**
 * @author Qt
 * @since 1.8
 */
public class Escapes {
	private static final XmlEscape XML_ESCAPE = new XmlEscape();
	private static final XmlUnescape XML_UNESCAPE = new XmlUnescape();
	private static final Html4Escape HTML4_ESCAPE = new Html4Escape();
	private static final Html4Unescape HTML4_UNESCAPE = new Html4Unescape();

	public static String escapeXml(String xml) {
		return XML_ESCAPE.replace(xml);
	}
	public static String unescapeXml(String xml) {
		return XML_UNESCAPE.replace(xml);
	}
	public static String escapeHtml4(String html) {
		return HTML4_ESCAPE.replace(html);
	}

	public static String unescapeHtml4(String html) {
		return HTML4_UNESCAPE.replace(html);
	}


}
