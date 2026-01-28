package io.polaris.core.classpath.reader.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.polaris.core.classpath.reader.AnnotationMetadata;
import io.polaris.core.classpath.reader.MethodMetadata;
import io.polaris.core.classpath.reader.annotation.AnnotationValue;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since Jan 27, 2026
 */
@EqualsAndHashCode
@ToString
public class DefaultMethodMetadata implements MethodMetadata {
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private final DefaultClassMetadata classMetadata;
	private final int access;
	private final String methodName;
	private final String descriptor;
	private final String signature;
	private final String[] exceptions;
	private final Type returnType;
	private final String returnTypeName;
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private AnnotationMetadata defaultValue;
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<AnnotationMetadata> annotations;

	public DefaultMethodMetadata(DefaultClassMetadata classMetadata, int access, String methodName, String descriptor, String signature, String[] exceptions) {
		this.classMetadata = classMetadata;
		this.access = access;
		this.methodName = methodName;
		this.descriptor = descriptor;
		this.signature = signature;
		this.exceptions = exceptions;
		this.returnType = Type.getReturnType(descriptor);
		this.returnTypeName = this.returnType.getClassName();
	}

	public void setDefaultValue(AnnotationMetadata defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void addMethodAnnotation(AnnotationValue annotationInfo) {
		if (annotations == null) {
			annotations = new ArrayList<>();
		}
		annotations.add(annotationInfo);
	}

	public Type getReturnType() {
		return returnType;
	}

	@Override
	public AnnotationMetadata getDefaultValue() {
		return defaultValue;
	}

	@Override
	public List<AnnotationMetadata> getAnnotations() {
		return annotations == null ? Collections.emptyList() : Collections.unmodifiableList(annotations);
	}

	@Override
	public String dump() {
		return dump(0);
	}

	@Override
	public String dump(int level) {
		char[] tab = new char[level];
		Arrays.fill(tab, '\t');
		StringBuilder sb = new StringBuilder();
		sb.append(tab);
		sb.append("method: ").append(classMetadata.getClassName()).append(".").append(methodName).append(descriptor);
		sb.append(" access(").append(Integer.toHexString(access)).append(")");
		sb.append("\n\t").append(tab).append("signature: ").append(signature);
		sb.append("\n\t").append(tab).append("return: ").append(returnTypeName);
		sb.append("\n\t").append(tab).append("exceptions: ").append(Arrays.toString(exceptions));
		if (defaultValue != null) {
			sb.append("\n\t").append(tab).append("default: ").append(defaultValue.dump());
		}
		if (annotations != null && !annotations.isEmpty()) {
			sb.append("\n\t").append(tab).append("annotations: ");
			for (AnnotationMetadata annotation : annotations) {
				sb.append("\n\t\t").append(tab).append(annotation.dump());
			}
		}
		return sb.toString();
	}


	@Override
	public String getMethodName() {
		return this.methodName;
	}

	@Override
	public String getDeclaringClassName() {
		return this.classMetadata.getClassName();
	}

	@Override
	public String getReturnTypeName() {
		return this.returnTypeName;
	}

	@Override
	public boolean isAbstract() {
		return (this.access & Opcodes.ACC_ABSTRACT) != 0;
	}

	@Override
	public boolean isStatic() {
		return (this.access & Opcodes.ACC_STATIC) != 0;
	}

	@Override
	public boolean isFinal() {
		return (this.access & Opcodes.ACC_FINAL) != 0;
	}

	@Override
	public boolean isOverridable() {
		return !isStatic() && !isFinal() && !isPrivate();
	}

	public boolean isPrivate() {
		return (this.access & Opcodes.ACC_PRIVATE) != 0;
	}


}
