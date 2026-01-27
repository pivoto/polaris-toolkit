package io.polaris.core.classpath.analyzer.model;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import io.polaris.core.classpath.analyzer.EntryInfo;
import io.polaris.core.classpath.analyzer.MethodInfo;
import lombok.Data;


/**
 * @author Qt
 * @since Aug 21, 2025
 */
@Data
public class FlatEntryDto {
	/** 如：`rpc:{RpcCode}`, `mq:{QueueName}`, `job:{JobName}`,`sql:{SqlId}`, */
	private String key;
	private String className;
	private String methodName;
	private String methodDesc;
	/** 关联的方法调用 */
	private Set<MethodDto> traces;

	private Set<String> entryKeys;

	public static FlatEntryDto from(EntryInfo entry) {
		FlatEntryDto entryDto = new FlatEntryDto();
		entryDto.setKey(entry.getKey());
		entryDto.setClassName(entry.getClassName());
		entryDto.setMethodName(entry.getMethodName());
		entryDto.setMethodDesc(entry.getMethodDesc());
		Set<MethodInfo> methodInfos = entry.getTraces();
		if (methodInfos != null) {
			entryDto.setTraces(new LinkedHashSet<>());
			entryDto.setEntryKeys(new LinkedHashSet<>());

			Set<String> recorder = new HashSet<>();
			for (MethodInfo methodInfo : methodInfos) {
				String key = methodInfo.getKey().dump();
				// 记录key，防止递归
				recorder.add(key);
				entryDto.getTraces().add(MethodDto.from(methodInfo, true));
				if (methodInfo.getEntryKeys() != null) {
					entryDto.getEntryKeys().addAll(methodInfo.getEntryKeys());
				}
			}
			for (MethodInfo methodInfo : methodInfos) {
				walkTraces(recorder, entryDto, methodInfo.getTraces());
			}
		}
		return entryDto;
	}

	private static void walkTraces(Set<String> recorder, FlatEntryDto entryDto, Set<MethodInfo> traces) {
		if (traces != null) {
			for (MethodInfo trace : traces) {
				String key = trace.getKey().dump();
				if (recorder.contains(key)) {
					continue;
				}
				recorder.add(key);
				entryDto.getTraces().add(MethodDto.from(trace));
				if (trace.getEntryKeys() != null) {
					entryDto.getEntryKeys().addAll(trace.getEntryKeys());
				}
				walkTraces(recorder, entryDto, trace.getTraces());
			}
		}
	}
}
