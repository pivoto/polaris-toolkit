package io.polaris.core.classpath.analyzer.model;

import java.util.HashSet;
import java.util.Set;

import io.polaris.core.classpath.analyzer.EntryInfo;
import io.polaris.core.classpath.analyzer.MethodInfo;
import lombok.Data;


/**
 * @author Qt
 * @since Aug 21, 2025
 */
@Data
public class EntryDto {
	/** 如：`rpc:{RpcCode}`, `mq:{QueueName}`, `job:{JobName}`,`sql:{SqlId}`, */
	private String key;
	private String className;
	private String methodName;
	private String methodDesc;
	/** 关联的方法调用 */
	private Set<String> traces;

	private Set<String> entryKeys;

	public static EntryDto from(EntryInfo entry) {
		EntryDto entryDto = new EntryDto();
		entryDto.setKey(entry.getKey());
		entryDto.setClassName(entry.getClassName());
		entryDto.setMethodName(entry.getMethodName());
		entryDto.setMethodDesc(entry.getMethodDesc());
		Set<MethodInfo> methodInfos = entry.getTraces();
		if (methodInfos != null) {
			entryDto.setTraces(new HashSet<>());
			entryDto.setEntryKeys(new HashSet<String>());

			Set<String> recorder = new HashSet<>();
			for (MethodInfo methodInfo : methodInfos) {
				String key = methodInfo.getKey().dump();
				entryDto.getTraces().add(key);
				// 记录key，防止递归
				recorder.add(key);
				if (methodInfo.getEntryKeys() != null) {
					for (String entryKey : methodInfo.getEntryKeys()) {
						entryDto.getEntryKeys().add(entryKey);
					}
				}
			}
			for (MethodInfo methodInfo : methodInfos) {
				walkTraces(recorder, entryDto, methodInfo.getTraces());
			}
		}
		return entryDto;
	}

	private static void walkTraces(Set<String> recorder, EntryDto entryDto, Set<MethodInfo> traces) {
		if (traces != null) {
			for (MethodInfo trace : traces) {
				String key = trace.getKey().dump();
				if (recorder.contains(key)) {
					continue;
				}
				recorder.add(key);
				if (trace.getEntryKeys() != null) {
					for (String entryKey : trace.getEntryKeys()) {
						entryDto.getEntryKeys().add(entryKey);
					}
				}
				walkTraces(recorder, entryDto, trace.getTraces());
			}
		}
	}
}
