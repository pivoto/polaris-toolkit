package io.polaris.core.classpath.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import org.objectweb.asm.AnnotationVisitor;

/**
 * @author Qt
 * @since Aug 17, 2025
 */
public class EntryAnnotationVisitor extends AnnotationVisitor {
	private static final Logger log = Loggers.of(EntryAnnotationVisitor.class);
	private final BiConsumer<String, AnnotationInfo.Value> consumer;

	public EntryAnnotationVisitor(int api, BiConsumer<String, AnnotationInfo.Value> consumer) {
		super(api);
		this.consumer = consumer;
	}

	@Override
	public void visit(final String name, final Object value) {
		log.debug("visit name:{}, value:{}", name, value);
		consumer.accept(name, new AnnotationInfo.SimpleValue(value));
		super.visit(name, value);
	}

	@Override
	public void visitEnum(final String name, final String descriptor, final String value) {
		log.debug("visitEnum name:{}, descriptor:{}, value:{}", name, descriptor, value);
		consumer.accept(name, new AnnotationInfo.EnumValue(descriptor, value));
		super.visitEnum(name, descriptor, value);
	}

	@Override
	public AnnotationVisitor visitAnnotation(final String name, final String descriptor) {
		log.debug("visitAnnotation name:{}, descriptor:{}", name, descriptor);
		AnnotationInfo annotationInfo = new AnnotationInfo(descriptor, true);
		BiConsumer<String, AnnotationInfo.Value> biConsumer = annotationInfo.getValues()::put;
		consumer.accept(name, new AnnotationInfo.AnnotationValue(annotationInfo));
		return new EntryAnnotationVisitor(super.api, biConsumer);
	}

	@Override
	public AnnotationVisitor visitArray(final String name) {
		log.debug("visitArray name:{}", name);
		List<AnnotationInfo.Value> array = new ArrayList<>();
		consumer.accept(name, new AnnotationInfo.ArrayValue(array));
		BiConsumer<String, AnnotationInfo.Value> biConsumer = (key, value) -> array.add(value);
		return new EntryAnnotationVisitor(super.api, biConsumer);
	}

	@Override
	public void visitEnd() {
		log.debug("visitEnd");
		super.visitEnd();
	}
}
