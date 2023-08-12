package io.polaris.core.annotation.processing;

import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * @author Qt
 * @since 1.8,  Aug 12, 2023
 */
@Data
@Access(map = true)
public class DemoBean {
	private long id;
	private String name;
	private double score;
	private Map<String,Object> map;
	private Set<String> set;

}
