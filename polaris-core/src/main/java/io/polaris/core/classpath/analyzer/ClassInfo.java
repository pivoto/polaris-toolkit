package io.polaris.core.classpath.analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Qt
 * @since Aug 12, 2025
 */
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class ClassInfo {

	/** classVersion */
	private int version;
	/** classAccess */
	private int access;
	/** classInternalName */
	private String name;
	/** classSignature */
	private String signature;
	private String superName;
	private String[] interfaces;

	private List<AnnotationInfo> annotations;

	public String dump() {
		return dump(0);
	}

	public String dump(int level) {
		char[] tab = new char[level];
		Arrays.fill(tab, '\t');
		StringBuilder sb = new StringBuilder();
		sb.append("class: ");
		sb.append(name);
		sb.append("access(").append(Integer.toHexString(access)).append(")");
		sb.append(" version(").append(version).append(")");
		sb.append("\n\t").append(tab).append("signature: ").append(signature);
		if (superName != null) {
			sb.append("\n\t").append(tab).append("superName: ").append(superName);
		}
		if (interfaces != null && interfaces.length > 0) {
			sb.append("\n\t").append(tab).append("interfaces: ").append(Arrays.toString(interfaces));
		}
		if (annotations != null && !annotations.isEmpty()) {
			sb.append("\n\t").append(tab).append("annotations: ");
			for (AnnotationInfo annotation : annotations) {
				sb.append("\n\t\t").append(tab).append(annotation.dump());
			}
		}
		return sb.toString();
	}

	public void addAnnotation(AnnotationInfo annotationInfo) {
		if (annotations == null) {
			annotations = new ArrayList<>();
		}
		annotations.add(annotationInfo);
	}
}
