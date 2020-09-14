package io.polaris.core.string;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
public class StringReplacerChain implements StringReplacer, Iterable<StringReplacer> {
	private final List<StringReplacer> replacers = new LinkedList<>();

	public StringReplacerChain(StringReplacer... args) {
		for (StringReplacer arg : args) {
			replacers.add(arg);
		}
	}

	public StringReplacerChain add(StringReplacer element) {
		replacers.add(element);
		return this;
	}

	@Override
	public Iterator<StringReplacer> iterator() {
		return replacers.iterator();
	}

	@Override
	public String replace(String s) {
		for (StringReplacer strReplacer : replacers) {
			s = strReplacer.replace(s);
		}
		return s;
	}
}
