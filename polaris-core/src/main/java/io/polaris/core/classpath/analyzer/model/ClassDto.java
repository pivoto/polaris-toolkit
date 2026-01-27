package io.polaris.core.classpath.analyzer.model;

import io.polaris.core.classpath.analyzer.ClassInfo;
import lombok.Data;


/**
 * @author Qt
 * @since Aug 21, 2025
 */
@Data
public class ClassDto {
	/** classInternalName */
	private String name;
	/** classSignature */
	private String signature;
	private String superName;
	private String[] interfaces;

	public static ClassDto from(ClassInfo classInfo) {
		ClassDto classDto = new ClassDto();
		classDto.name = classInfo.getName();
		classDto.signature = classInfo.getSignature();
		classDto.superName = classInfo.getSuperName();
		classDto.interfaces = classInfo.getInterfaces();
		return classDto;
	}
}
