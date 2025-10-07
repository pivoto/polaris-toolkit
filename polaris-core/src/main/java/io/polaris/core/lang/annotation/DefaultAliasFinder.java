package io.polaris.core.lang.annotation;

import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.Set;

/**
 * @author Qt
 * @since Oct 07, 2025
 */
public class DefaultAliasFinder implements AliasFinder {

	public static final DefaultAliasFinder INSTANCE = new DefaultAliasFinder();

	@Override
	public Set<AliasAttribute> findAliasAttributes(AnnotatedElement element) {
		Alias alias = element.getAnnotation(Alias.class);
		if (alias == null) {
			return Collections.emptySet();
		}
		return Collections.singleton(new AliasAttribute(alias.value(), alias.annotation()));
	}
}
