package io.polaris.core.classpath.analyzer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.polaris.core.classpath.analyzer.AnnotationInfo;
import io.polaris.core.classpath.analyzer.MethodInfo;
import lombok.Data;

/**
 * @author Qt
 * @since Aug 21, 2025
 */
@Data
public class MethodDto {
	private String owner;
	private String name;
	private String descriptor;
	private String signature;
	private String[] exceptions;

	private String sha1;

	private Set<String> entryKeys;
	/** 关联的方法调用 */
	private Set<String> traces;

	private List<String> parameterNames;
	private List<AnnotationDto> methodAnnotations;
	private List<AnnotationDto> returnAnnotations;
	private Map<Integer, List<AnnotationDto>> parameterAnnotations;

	public static MethodDto from(MethodInfo methodInfo) {
		return from(methodInfo, false);
	}

	public static MethodDto from(MethodInfo methodInfo, boolean withAnnotation) {
		MethodDto dto = new MethodDto();
		dto.setOwner(methodInfo.getClassInfo().getName());
		dto.setName(methodInfo.getName());
		dto.setDescriptor(methodInfo.getDescriptor());
		dto.setSignature(methodInfo.getSignature());
		dto.setExceptions(methodInfo.getExceptions());
		dto.setSha1(methodInfo.getSha1());
		dto.setEntryKeys(methodInfo.getEntryKeys());
		Set<MethodInfo> methodInfoTraces = methodInfo.getTraces();
		if (methodInfoTraces != null) {
			dto.setTraces(new HashSet<>());
			for (MethodInfo methodInfoTrace : methodInfoTraces) {
				dto.getTraces().add(methodInfoTrace.getKey().dump());
			}
		}
		if (withAnnotation) {
			dto.setParameterNames(methodInfo.getParameterNames());
			List<AnnotationInfo> methodInfoMethodAnnotations = methodInfo.getMethodAnnotations();
			if (methodInfoMethodAnnotations != null) {
				dto.setMethodAnnotations(new ArrayList<>());
				for (AnnotationInfo methodInfoMethodAnnotation : methodInfoMethodAnnotations) {
					dto.getMethodAnnotations().add(AnnotationDto.from(methodInfoMethodAnnotation));
				}
			}
			List<AnnotationInfo> methodInfoReturnAnnotations = methodInfo.getReturnAnnotations();
			if (methodInfoReturnAnnotations != null) {
				dto.setReturnAnnotations(new ArrayList<>());
				for (AnnotationInfo methodInfoReturnAnnotation : methodInfoReturnAnnotations) {
					dto.getReturnAnnotations().add(AnnotationDto.from(methodInfoReturnAnnotation));
				}
			}
			Map<Integer, List<AnnotationInfo>> methodInfoParameterAnnotations = methodInfo.getParameterAnnotations();
			if (methodInfoParameterAnnotations != null) {
				dto.setParameterAnnotations(new HashMap<>());
				for (Map.Entry<Integer, List<AnnotationInfo>> entry : methodInfoParameterAnnotations.entrySet()) {
					List<AnnotationDto> list = new ArrayList<>();
					dto.getParameterAnnotations().put(entry.getKey(), list);
					for (AnnotationInfo methodInfoParameterAnnotation : entry.getValue()) {
						list.add(AnnotationDto.from(methodInfoParameterAnnotation));
					}
				}
			}
		}
		return dto;
	}
}
