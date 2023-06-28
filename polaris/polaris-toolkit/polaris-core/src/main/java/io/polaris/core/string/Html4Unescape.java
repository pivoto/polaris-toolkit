package io.polaris.core.string;

/**
 * @author Qt
 * @since 1.8
 */
public class Html4Unescape extends XmlEscape {
	public Html4Unescape() {
		super();

		for (String[] escape : Html4Escape.ISO8859_1_ESCAPE) {
			add(s -> s.replace(escape[1], escape[0]));
		}
		for (String[] escape : Html4Escape.HTML40_EXTENDED_ESCAPE) {
			add(s -> s.replace(escape[1], escape[0]));
		}
	}
}
