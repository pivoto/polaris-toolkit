package io.polaris.core.lang.annotation;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * @author Qt
 * @since Oct 07, 2025
 */
public class CompositeAliasFinder implements AliasFinder {

	private final List<AliasFinder> finders;

	public CompositeAliasFinder(Collection<AliasFinder> finders) {
		this.finders = finders != null ? new ArrayList<>(finders) : new ArrayList<>();
	}

	public CompositeAliasFinder(AliasFinder... finders) {
		this.finders = finders != null ? new ArrayList<>(Arrays.asList(finders)) : new ArrayList<>();
	}

	@Override
	public Set<AliasAttribute> findAliasAttributes(AnnotatedElement element) {
		Set<AliasAttribute> set = new LinkedHashSet<>();
		for (AliasFinder finder : finders) {
			Set<AliasAttribute> attributes = finder.findAliasAttributes(element);
			if (attributes != null) {
				set.addAll(attributes);
			}
		}
		return set;
	}

	public CompositeAliasFinder append(AliasFinder finder) {
		this.finders.add(finder);
		return this;
	}

	public CompositeAliasFinder prepend(AliasFinder finder) {
		this.finders.add(0, finder);
		return this;
	}

	public CompositeAliasFinder remove(AliasFinder aliasFinder) {
		this.finders.remove(aliasFinder);
		return this;
	}

}
