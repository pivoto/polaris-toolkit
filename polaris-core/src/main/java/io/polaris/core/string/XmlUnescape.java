package io.polaris.core.string;

/**
 * @author Qt
 */
public class XmlUnescape extends StringReplacerChain {

	public XmlUnescape() {
		for (String[] escape : XmlEscape.BASIC_ESCAPE) {
			add(s -> s.replace(escape[1], escape[0]));
		}
	}
}
