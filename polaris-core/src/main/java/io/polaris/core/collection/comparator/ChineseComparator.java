package io.polaris.core.collection.comparator;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * @author Qt
 * @since 1.8
 */
public class ChineseComparator implements Comparator<String>, Serializable {
	private static final long serialVersionUID = 1L;

	private final Collator collator;

	public ChineseComparator() {
		collator = Collator.getInstance(Locale.CHINESE);
	}

	@Override
	public int compare(String o1, String o2) {
		return collator.compare(o1, o2);
	}

}
