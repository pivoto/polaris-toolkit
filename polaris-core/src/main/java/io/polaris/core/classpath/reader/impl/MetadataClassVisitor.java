package io.polaris.core.classpath.reader.impl;

import java.util.Map;
import java.util.function.BiConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.asm.internal.AsmConsts;
import io.polaris.core.classpath.reader.AnnotationMetadata;
import io.polaris.core.classpath.reader.annotation.AnnotationValue;
import io.polaris.core.string.Strings;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since Jan 26, 2026
 */
class MetadataClassVisitor extends ClassVisitor {

	@Nonnull
	private final ClassLoader classLoader;
	@Nonnull
	private final Map<String, DefaultClassMetadata> visitedClasses;
	@Nonnull
	private final DefaultClassMetadata classMetadata;


	MetadataClassVisitor(@Nonnull ClassLoader classLoader, @Nonnull DefaultClassMetadata classMetadata, @Nonnull Map<String, DefaultClassMetadata> visitedClasses) {
		super(AsmConsts.ASM_API);
		this.classLoader = classLoader;
		this.visitedClasses = visitedClasses;
		this.classMetadata = classMetadata;
	}

	@Nonnull
	public DefaultClassMetadata getClassMetadata() {
		return classMetadata;
	}

	@Override
	public void visit(int version, int access, String name, String signature,
		String superName, String[] interfaces) {
		this.classMetadata.setVersion(version);
		this.classMetadata.setAccess(access);
		this.classMetadata.setClassName(name);
		this.classMetadata.setSignature(signature);
		if (superName != null && !isInterface(access)) {
			this.classMetadata.setSuperClassName(superName);
		}
		this.classMetadata.setInterfaceNames(interfaces);
		// visited
		this.visitedClasses.put(name, this.classMetadata);
	}

	@Override
	public void visitOuterClass(String owner, String name, String descriptor) {
		this.classMetadata.setEnclosingClassName(owner);
	}

	@Override
	public void visitInnerClass(String name, @Nullable String outerName, String innerName,
		int access) {
		if (outerName != null) {
			String className = toClassName(name);
			String outerClassName = toClassName(outerName);
			if (Strings.equals(this.classMetadata.getClassName(), className)) {
				this.classMetadata.setEnclosingClassName(outerName);
				this.classMetadata.setIndependentInnerClass((access & Opcodes.ACC_STATIC) != 0);
			} else if (Strings.equals(this.classMetadata.getClassName(), outerClassName)) {
				this.classMetadata.addMemberClassName(className);
			}
		}
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

		AnnotationValue annotation = new AnnotationValue(annotationClassMetadata, descriptor, visible);
		classMetadata.addAnnotation(annotation);

		BiConsumer<String, AnnotationMetadata> consumer = annotation::setValue;

		return new MetadataAnnotationVisitor(this.classLoader, this.visitedClasses, consumer);
	}

	@Override
	@Nullable
	public MethodVisitor visitMethod(
		int access, String name, String descriptor, String signature, String[] exceptions) {

		// Skip bridge methods - we're only interested in original
		// annotation-defining user methods. On JDK 8, we'd otherwise run into
		// double detection of the same annotated method...
		if (isBridge(access)) {
			return null;
		}
		DefaultMethodMetadata methodInfo = new DefaultMethodMetadata(classMetadata,  access, name, descriptor, signature, exceptions );
		this.classMetadata.addMethod(methodInfo);
		return new MetadataMethodVisitor(this.classLoader, this.visitedClasses, methodInfo);
	}

	@Override
	public void visitEnd() {
		super.visitEnd();
	}

	private String toClassName(String name) {
		return name.replace('/', '.');
	}

	private boolean isBridge(int access) {
		return (access & Opcodes.ACC_BRIDGE) != 0;
	}

	private boolean isInterface(int access) {
		return (access & Opcodes.ACC_INTERFACE) != 0;
	}


}
