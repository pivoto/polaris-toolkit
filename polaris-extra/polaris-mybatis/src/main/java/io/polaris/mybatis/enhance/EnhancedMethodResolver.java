package io.polaris.mybatis.enhance;

import java.lang.reflect.Method;

import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.builder.annotation.MethodResolver;

/**
 * @author Qt
 * @since Mar 30, 2026
 */
public class EnhancedMethodResolver extends MethodResolver {
	private final EnhancedMapperAnnotationBuilder annotationBuilder;
	private final Method method;

	public EnhancedMethodResolver(EnhancedMapperAnnotationBuilder annotationBuilder, Method method) {
		super(annotationBuilder, method);
		this.annotationBuilder = annotationBuilder;
		this.method = method;
	}

	@Override
	public void resolve() {
		annotationBuilder.parseIncompleteStatement(method);
	}
}
