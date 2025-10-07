package io.polaris.core.lang.annotation;

import java.lang.reflect.AnnotatedElement;
import java.util.Set;


/**
 * @author Qt
 * @since Oct 07, 2025
 */
public interface AliasFinder {

	Set<AliasAttribute> findAliasAttributes(AnnotatedElement element);

}
