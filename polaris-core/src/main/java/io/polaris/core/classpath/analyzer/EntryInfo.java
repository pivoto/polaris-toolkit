package io.polaris.core.classpath.analyzer;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Qt
 * @since Aug 12, 2025
 */
@Getter
@EqualsAndHashCode
@ToString
@NotThreadSafe
public class EntryInfo {
	/** 如：`rpc:{RpcCode}`, `mq:{QueueName}`, `job:{JobName}`,`sql:{SqlId}`, */
	private final String key;
	private final Class<?> entryClass;
	private final String className;
	private final String methodName;
	private final String methodDesc;

	private final Set<MethodInfo> traces;
	@Setter
	private ClassInfo classInfo;

	public EntryInfo(String key, Class<?> entryClass, String methodName, String methodDesc) {
		this.key = key;
		this.entryClass = entryClass;
		this.className = entryClass.getName();
		this.methodName = methodName;
		this.methodDesc = methodDesc;
		this.traces = new LinkedHashSet<>();
	}

	public EntryInfo(String key, String className, String methodName, String methodDesc) throws ClassNotFoundException {
		this(key, Class.forName(className), methodName, methodDesc);
	}

	public String dump() {
		return dump(0);
	}

	public String dump(int level) {
		char[] tab = new char[level];
		Arrays.fill(tab, '\t');
		StringBuilder sb = new StringBuilder();
		sb.append("key(").append(key).append(") ");
		sb.append(className).append(".").append(methodName).append(methodDesc);
		if (classInfo != null) {
			sb.append("\n\t").append(tab).append("classInfo: ").append(classInfo.dump(level + 1));
		}
		if (traces != null && !traces.isEmpty()) {
			sb.append("\n\t").append(tab).append("traces: ");
			for (MethodInfo trace : traces) {
				sb.append("\n\t").append(tab).append('\t').append(trace.dump(level + 2));
			}
		}
		return sb.toString();
	}

	public void addTraces(MethodInfo... methodInfos) {
		this.traces.addAll(Arrays.asList(methodInfos));
	}

	public void addTraces(Collection<MethodInfo> methodInfos) {
		this.traces.addAll(methodInfos);
	}

}
