package io.polaris.core.classpath.reader.impl;

import java.util.Map;
import java.util.function.BiConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.asm.internal.AsmConsts;
import io.polaris.core.classpath.reader.AnnotationMetadata;
import io.polaris.core.classpath.reader.annotation.AnnotationValue;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since Jan 26, 2026
 */
class MetadataMethodVisitor extends MethodVisitor {

	@Nonnull
	private final ClassLoader classLoader;
	@Nonnull
	private final Map<String, DefaultClassMetadata> visitedClasses;
	@Nonnull
	private final DefaultMethodMetadata methodMetadata;


	MetadataMethodVisitor(@Nonnull ClassLoader classLoader, @Nonnull Map<String, DefaultClassMetadata> visitedClasses, DefaultMethodMetadata methodMetadata) {
		super(AsmConsts.ASM_API);
		this.classLoader = classLoader;
		this.visitedClasses = visitedClasses;
		this.methodMetadata = methodMetadata;
	}


	@Override
	@Nullable
	public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
		String resource = Type.getType(descriptor).getInternalName();
		DefaultClassMetadata annotationClassMetadata = null;
		try {
			DefaultMetadataReader reader = new DefaultMetadataReader(resource, this.classLoader, this.visitedClasses);
			annotationClassMetadata = reader.getClassMetadata();
		} catch (Exception ignored) {
		}

		AnnotationValue methodAnnotationInfo = new AnnotationValue(annotationClassMetadata, descriptor, visible);

		this.methodMetadata.addMethodAnnotation(methodAnnotationInfo);

		BiConsumer<String, AnnotationMetadata> consumer = methodAnnotationInfo::setValue;
		// 解析方法注解
		return new MetadataAnnotationVisitor(this.classLoader, this.visitedClasses, consumer);
	}

	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		BiConsumer<String, AnnotationMetadata> consumer = (name, annotation) -> this.methodMetadata.setDefaultValue(annotation);
		return new MetadataAnnotationVisitor(this.classLoader, this.visitedClasses, consumer);
	}

	@Override
	public void visitEnd() {
		super.visitEnd();
	}

}
