package io.polaris.core.classpath.analyzer.model;

import java.util.List;
import java.util.Map;

import lombok.Data;


/**
 * @author Qt
 * @since Aug 21, 2025
 */
@Data
public class ContextDto {

	private List<EntryDto> entryList;
	private Map<String, String> methodSha1;
	private Map<String, MethodDto> methodMap;


}
