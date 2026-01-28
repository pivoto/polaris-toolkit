package io.polaris.core.classpath.reader;

import java.util.List;

import io.polaris.core.classpath.reader.impl.DefaultMethodMetadata;

/**
 * @author Qt
 * @since Jan 26, 2026
 */
public interface AnnotatedElementMetadata {

	List<AnnotationMetadata> getAnnotations();


}
