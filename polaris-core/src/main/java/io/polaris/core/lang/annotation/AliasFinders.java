package io.polaris.core.lang.annotation;

import java.lang.reflect.AnnotatedElement;
import java.util.Set;

/**
 * @author Qt
 * @since Oct 07, 2025
 */
public class AliasFinders {

	private static AliasFinder defaultAliasFinder = new CompositeAliasFinder(DefaultAliasFinder.INSTANCE);

	public static AliasFinder getDefaultAliasFinder() {
		return defaultAliasFinder;
	}

	public static void setDefaultAliasFinder(AliasFinder aliasFinder) {
		AliasFinders.defaultAliasFinder = aliasFinder;
	}

	public static void appendDefaultAliasFinder(AliasFinder aliasFinder) {
		if (defaultAliasFinder instanceof CompositeAliasFinder) {
			((CompositeAliasFinder) defaultAliasFinder).append(aliasFinder);
		} else {
			AliasFinders.defaultAliasFinder = new CompositeAliasFinder(defaultAliasFinder, aliasFinder);
		}
	}

	public static void prependDefaultAliasFinder(AliasFinder aliasFinder) {
		if (defaultAliasFinder instanceof CompositeAliasFinder) {
			((CompositeAliasFinder) defaultAliasFinder).prepend(aliasFinder);
		} else {
			AliasFinders.defaultAliasFinder = new CompositeAliasFinder(aliasFinder, defaultAliasFinder);
		}
	}

	public static void removeDefaultAliasFinder(AliasFinder aliasFinder) {
		if (defaultAliasFinder instanceof CompositeAliasFinder) {
			((CompositeAliasFinder) defaultAliasFinder).remove(aliasFinder);
		}
	}

	public static Set<AliasAttribute> findAliasAttributes(AnnotatedElement element) {
		return defaultAliasFinder.findAliasAttributes(element);
	}

	public static Set<AliasAttribute> findAliasAttributes(AliasFinder aliasFinder, AnnotatedElement element) {
		return aliasFinder.findAliasAttributes(element);
	}

}
