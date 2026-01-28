package io.polaris.core.classpath.reader.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import io.polaris.core.classpath.reader.AnnotationMetadata;
import io.polaris.core.classpath.reader.ClassMetadata;
import io.polaris.core.classpath.reader.MethodMetadata;
import io.polaris.core.classpath.reader.annotation.AnnotationValue;
import io.polaris.core.consts.StdConsts;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.objectweb.asm.Opcodes;

/**
 * @author Qt
 * @since Jan 27, 2026
 */
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class DefaultClassMetadata implements ClassMetadata {

	/** class version */
	private int version;
	/** class access */
	private int access;
	/** class name */
	private String className;
	/** class signature */
	private String signature;
	/** class super name */
	private String superClassName;
	/** class interface names */
	private String[] interfaceNames;

	private String enclosingClassName;
	private boolean independentInnerClass;
	@Setter(AccessLevel.NONE)
	private List<String> memberClassNames;

	/** class annotations */
	@Setter(AccessLevel.NONE)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<AnnotationMetadata> annotations;
	/** class methods */
	@Setter(AccessLevel.NONE)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<MethodMetadata> methods;


	public void setClassName(String name) {
		this.className = name.replace('/', '.');
	}

	public void setSuperClassName(String superName) {
		this.superClassName = superName.replace('/', '.');
	}

	public void setInterfaceNames(String[] interfaces) {
		this.interfaceNames = new String[interfaces.length];
		for (int i = 0; i < interfaces.length; i++) {
			this.interfaceNames[i] = interfaces[i].replace('/', '.');
		}
	}

	public void setEnclosingClassName(String enclosingName) {
		this.enclosingClassName = enclosingName.replace('/', '.');
	}

	public void addMemberClassName(String name) {
		if (memberClassNames == null) {
			memberClassNames = new ArrayList<>();
		}
		memberClassNames.add(name.replace('/', '.'));
	}

	public void addAnnotation(AnnotationValue annotationInfo) {
		if (annotations == null) {
			annotations = new ArrayList<>();
		}
		annotations.add(annotationInfo);
	}

	public void addMethod(DefaultMethodMetadata methodInfo) {
		if (methods == null) {
			methods = new ArrayList<>();
		}
		methods.add(methodInfo);
	}

	@Override
	public List<AnnotationMetadata> getAnnotations() {
		return annotations == null ? Collections.emptyList() : Collections.unmodifiableList(annotations);
	}

	@Override
	public List<MethodMetadata> getMethods() {
		return methods == null ? Collections.emptyList() : Collections.unmodifiableList(methods);
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
		sb.append("class: ").append(className);
		sb.append(" access(").append(Integer.toHexString(access)).append(")");
		sb.append(" version(").append(version).append(")");
		sb.append("\n\t").append(tab).append("signature: ").append(signature);
		if (superClassName != null) {
			sb.append("\n\t").append(tab).append("super: ").append(superClassName);
		}
		if (interfaceNames != null && interfaceNames.length > 0) {
			sb.append("\n\t").append(tab).append("interfaces: ").append(Arrays.toString(interfaceNames));
		}
		if (enclosingClassName != null) {
			sb.append("\n\t").append(tab).append("enclosing: ").append(enclosingClassName);
		}
		if (memberClassNames != null && !memberClassNames.isEmpty()) {
			sb.append("\n\t").append(tab).append("member: ").append(memberClassNames);
		}
		sb.append("\n\t").append(tab).append("independentInnerClass: ").append(independentInnerClass);

		if (annotations != null && !annotations.isEmpty()) {
			sb.append("\n\t").append(tab).append("annotations: ");
			for (AnnotationMetadata annotation : annotations) {
				sb.append("\n\t\t").append(tab).append(annotation.dump());
			}
		}
		if (methods != null && !methods.isEmpty()) {
			sb.append("\n\t").append(tab).append("methods: ");
			for (MethodMetadata method : methods) {
				sb.append("\n").append(method.dump(level + 2));
			}
		}
		return sb.toString();
	}


	@Override
	public String getClassName() {
		return this.className;
	}

	@Override
	public boolean isInterface() {
		return (this.access & Opcodes.ACC_INTERFACE) != 0;
	}

	@Override
	public boolean isAnnotation() {
		return (this.access & Opcodes.ACC_ANNOTATION) != 0;
	}

	@Override
	public boolean isAbstract() {
		return (this.access & Opcodes.ACC_ABSTRACT) != 0;
	}

	@Override
	public boolean isFinal() {
		return (this.access & Opcodes.ACC_FINAL) != 0;
	}

	@Override
	public boolean isIndependent() {
		return (this.enclosingClassName == null || this.independentInnerClass);
	}

	@Override
	@Nullable
	public String getEnclosingClassName() {
		return this.enclosingClassName;
	}

	@Override
	@Nullable
	public String getSuperClassName() {
		return this.superClassName;
	}

	@Override
	public String[] getInterfaceNames() {
		return this.interfaceNames.clone();
	}

	@Override
	public String[] getMemberClassNames() {
		return this.memberClassNames == null ? StdConsts.EMPTY_STRING_ARRAY : this.memberClassNames.toArray(StdConsts.EMPTY_STRING_ARRAY);
	}


}
