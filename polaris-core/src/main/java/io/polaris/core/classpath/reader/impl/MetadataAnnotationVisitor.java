package io.polaris.core.classpath.reader.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.asm.internal.AsmConsts;
import io.polaris.core.classpath.reader.AnnotationMetadata;
import io.polaris.core.classpath.reader.annotation.AnnotationArrayValue;
import io.polaris.core.classpath.reader.annotation.AnnotationEnumValue;
import io.polaris.core.classpath.reader.annotation.AnnotationSimpleValue;
import io.polaris.core.classpath.reader.annotation.AnnotationValue;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since Jan 26, 2026
 */
class MetadataAnnotationVisitor extends AnnotationVisitor {

	@Nonnull
	private final ClassLoader classLoader;
	@Nonnull
	private final Map<String, DefaultClassMetadata> visitedClasses;
	@Nonnull
	private final BiConsumer<String, AnnotationMetadata> consumer;

	public MetadataAnnotationVisitor(@Nonnull ClassLoader classLoader, @Nonnull Map<String, DefaultClassMetadata> visitedClasses, @Nonnull BiConsumer<String, AnnotationMetadata> consumer) {
		super(AsmConsts.ASM_API);
		this.classLoader = classLoader;
		this.visitedClasses = visitedClasses;
		this.consumer = consumer;
	}


	/**
	 * Visits a primitive value of the annotation.
	 *
	 * @param name  the value name.
	 * @param value the actual value, whose type must be {@link Byte}, {@link Boolean}, {@link
	 *              Character}, {@link Short}, {@link Integer} , {@link Long}, {@link Float}, {@link Double},
	 *              {@link String} or {@link Type} of {@link Type#OBJECT} or {@link Type#ARRAY} sort. This
	 *              value can also be an array of byte, boolean, short, char, int, long, float or double values
	 *              (this is equivalent to using {@link #visitArray} and visiting each array element in turn,
	 *              but is more convenient).
	 */
	@Override
	public void visit(String name, Object value) {
		consumer.accept(name, new AnnotationSimpleValue(value));
		super.visit(name, value);
	}

	@Override
	public void visitEnum(String name, String descriptor, String value) {
		consumer.accept(name, new AnnotationEnumValue(descriptor, value));
		super.visitEnum(name, descriptor, value);
	}

	@Override
	@Nullable
	public AnnotationVisitor visitAnnotation(String name, String descriptor) {

		String resource = Type.getType(descriptor).getInternalName();
		DefaultClassMetadata annotationClassMetadata = null;
		try {
			DefaultMetadataReader reader = new DefaultMetadataReader(resource, this.classLoader, this.visitedClasses);
			annotationClassMetadata = reader.getClassMetadata();
		} catch (Exception ignored) {
		}

		AnnotationValue annotation = new AnnotationValue(annotationClassMetadata, descriptor, true);
		BiConsumer<String, AnnotationMetadata> biConsumer = annotation::setValue;
		consumer.accept(name, annotation);
		return new MetadataAnnotationVisitor(this.classLoader, this.visitedClasses, biConsumer);
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
		List<AnnotationMetadata> array = new ArrayList<>();
		consumer.accept(name, new AnnotationArrayValue(array));
		BiConsumer<String, AnnotationMetadata> biConsumer = (key, value) -> array.add(value);
		return new MetadataAnnotationVisitor(this.classLoader, this.visitedClasses, biConsumer);
	}

	@Override
	public void visitEnd() {
		super.visitEnd();
	}

}
