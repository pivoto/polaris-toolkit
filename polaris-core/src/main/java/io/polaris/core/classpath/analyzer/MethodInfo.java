package io.polaris.core.classpath.analyzer;

import java.util.*;

import javax.annotation.concurrent.NotThreadSafe;

import io.polaris.core.collection.ObjectArrays;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Qt
 * @since Aug 12, 2025
 */
@Setter
@Getter
@ToString
@NotThreadSafe
public class MethodInfo {

	private MethodKey key;
	private ClassInfo classInfo;
	private int access;
	private String name;
	private String descriptor;
	private String signature;
	private String[] exceptions;

	private String sha1;

	private Set<String> entryKeys;
	private Set<MethodInfo> traces;

	private List<String> parameterNames;
	private List<AnnotationInfo> methodAnnotations;
	private List<AnnotationInfo> returnAnnotations;
	private Map<Integer, List<AnnotationInfo>> formalParameterAnnotations;
	private Map<Integer, List<AnnotationInfo>> parameterAnnotations;

	public String calcAndSetSha1(MethodCodeAnalyzer analyzer) {
		String sha1 = EntryAnalyzer.calcBase32Sha1(dump(analyzer));
		setSha1(sha1);
		return sha1;
	}

	public String calcAndSetSha1(String code) {
		String sha1 = EntryAnalyzer.calcBase32Sha1(dump() + code);
		setSha1(sha1);
		return sha1;
	}

	public String dump() {
		return dump(0);
	}

	public String dump(int level) {
		return dump(level, null);
	}

	public String dump(MethodCodeAnalyzer analyzer) {
		return dump(0, analyzer);
	}

	public String dump(int level, MethodCodeAnalyzer analyzer) {
		Set<String> recorder = new HashSet<>();
		return dump(recorder, level, analyzer);
	}

	private String dump(Set<String> recorder, int level) {
		return dump(recorder, level, null);
	}

	private String dump(Set<String> recorder, int level, MethodCodeAnalyzer analyzer) {
		char[] tab = new char[level];
		Arrays.fill(tab, '\t');
		StringBuilder sb = new StringBuilder();
		sb.append(tab);
		sb.append("method: ");
		sb.append(classInfo.getName()).append(".").append(name).append(descriptor);
		sb.append(" access(").append(Integer.toHexString(access)).append(")");
		sb.append("\n\t").append(tab).append("signature: ").append(signature);
		sb.append("\n\t").append(tab).append("exceptions: ").append(Arrays.toString(exceptions));
		if (parameterNames != null && !parameterNames.isEmpty()) {
			sb.append("\n\t").append(tab).append("parameterNames: ").append(ObjectArrays.toString(parameterNames));
		}
		if (methodAnnotations != null && !methodAnnotations.isEmpty()) {
			sb.append("\n\t").append(tab).append("methodAnnotations: ");
			for (AnnotationInfo annotation : methodAnnotations) {
				sb.append("\n\t\t").append(tab).append(annotation.dump());
			}
		}
		if (returnAnnotations != null && !returnAnnotations.isEmpty()) {
			sb.append("\n\t").append(tab).append("returnAnnotations: ");
			for (AnnotationInfo annotation : returnAnnotations) {
				sb.append("\n\t\t").append(tab).append(annotation.dump());
			}
		}
		if (parameterAnnotations != null && !parameterAnnotations.isEmpty()) {
			sb.append("\n\t").append(tab).append("parameterAnnotations: ");
			parameterAnnotations.forEach((parameterIndex, annotations) -> {
				sb.append("\n\t\t").append(tab).append("param[").append(parameterIndex).append("]: ");
				for (AnnotationInfo annotation : annotations) {
					sb.append("\n\t\t\t").append(tab).append(annotation.dump());
				}
			});
		}
		if (analyzer != null) {
			sb.append("\n\t").append(tab).append("analyzer: ");
			sb.append("\n\t").append(tab).append('\t').append(analyzer.dump(level + 1));
		}
		if (entryKeys != null && !entryKeys.isEmpty()) {
			sb.append("\n\t").append(tab).append("entryKeys: ").append(entryKeys);
		}
		if (traces != null && !traces.isEmpty()) {
			sb.append("\n\t").append(tab).append("traces: ");
			// 记录key，防止递归
			recorder.add(this.key.dump());
			List<MethodInfo> keep = new ArrayList<>(traces.size());
			for (MethodInfo trace : traces) {
				String key = trace.getKey().dump();
				if (recorder.contains(key)) {
					sb.append("\n\t").append(tab).append('\t').append("method(recursive): ");
					sb.append(trace.getClassInfo().getName()).append(".").append(trace.getName()).append(trace.getDescriptor());
					continue;
				}
				keep.add(trace);
				// 记录key，防止递归
				recorder.add(key);
			}
			for (MethodInfo trace : keep) {
				sb.append("\n\t").append(tab).append('\t').append(trace.dump(recorder, level + 2));
			}
		}
		return sb.toString();
	}

	public void addParameterName(String name) {
		if (parameterNames == null) {
			parameterNames = new ArrayList<>();
		}
		parameterNames.add(name);
	}

	public void addMethodAnnotation(AnnotationInfo annotationInfo) {
		if (methodAnnotations == null) {
			methodAnnotations = new ArrayList<>();
		}
		methodAnnotations.add(annotationInfo);
	}

	public void addReturnAnnotation(AnnotationInfo annotationInfo) {
		if (returnAnnotations == null) {
			returnAnnotations = new ArrayList<>();
		}
		returnAnnotations.add(annotationInfo);
	}

	public void addFormalParameterAnnotation(int parameterIndex, AnnotationInfo annotationInfo) {
		if (formalParameterAnnotations == null) {
			formalParameterAnnotations = new HashMap<>();
		}
		List<AnnotationInfo> annotations = formalParameterAnnotations.computeIfAbsent(parameterIndex, k -> new ArrayList<>());
		annotations.add(annotationInfo);
	}

	public void addParameterAnnotation(int parameterIndex, AnnotationInfo annotationInfo) {
		if (parameterAnnotations == null) {
			parameterAnnotations = new HashMap<>();
		}
		List<AnnotationInfo> annotations = parameterAnnotations.computeIfAbsent(parameterIndex, k -> new ArrayList<>());
		annotations.add(annotationInfo);
	}

	public void addEntryKeys(String... entryKeys) {
		if (this.entryKeys == null) {
			this.entryKeys = new LinkedHashSet<>();
		}
		this.entryKeys.addAll(Arrays.asList(entryKeys));
	}

	public void addEntryKeys(Collection<String> entryKeys) {
		if (this.entryKeys == null) {
			this.entryKeys = new LinkedHashSet<>();
		}
		this.entryKeys.addAll(entryKeys);
	}

	public void addTraces(MethodInfo... methodInfos) {
		if (this.traces == null) {
			this.traces = new LinkedHashSet<>();
		}
		this.traces.addAll(Arrays.asList(methodInfos));
	}

	public void addTraces(Collection<MethodInfo> methodInfos) {
		if (this.traces == null) {
			this.traces = new LinkedHashSet<>();
		}
		this.traces.addAll(methodInfos);
	}


	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MethodInfo)) return false;
		MethodInfo that = (MethodInfo) o;
		return getAccess() == that.getAccess() && Objects.equals(getKey(), that.getKey()) && Objects.equals(getClassInfo(), that.getClassInfo()) && Objects.equals(getName(), that.getName()) && Objects.equals(getDescriptor(), that.getDescriptor()) && Objects.equals(getSignature(), that.getSignature()) && Objects.deepEquals(getExceptions(), that.getExceptions());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getKey(), getClassInfo(), getAccess(), getName(), getDescriptor(), getSignature(), Arrays.hashCode(getExceptions()));
	}
}
