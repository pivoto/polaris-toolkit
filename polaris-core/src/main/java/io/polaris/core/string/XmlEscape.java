package io.polaris.core.string;

/**
 * @author Qt
 * @since 1.8
 */
public class XmlEscape extends StringReplacerChain {
	public static final String[][] BASIC_ESCAPE = { //
		{"'", "&apos;"}, // " - single-quote
		{"\"", "&quot;"}, // " - double-quote
		{"&", "&amp;"}, // & - ampersand
		{"<", "&lt;"}, // < - less-than
		{">", "&gt;"}, // > - greater-than
	};

	public XmlEscape() {
		for (String[] escape : BASIC_ESCAPE) {
			add(s -> s.replace(escape[0], escape[1]));
		}
	}
}
