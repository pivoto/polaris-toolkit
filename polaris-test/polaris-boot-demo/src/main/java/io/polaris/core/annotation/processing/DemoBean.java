package io.polaris.core.annotation.processing;

import java.util.Map;
import java.util.Set;

import io.polaris.core.annotation.Access;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qt
 * @since Aug 12, 2023
 */
@Data
@Access(map = true, getters = true, setters = true)
public class DemoBean extends BaseDemoBean {
	private long id;
	private String name;
	private double score;
	private Map<String, Object> map;
	private Set<String> set;


	@Setter(AccessLevel.NONE)
	@Access.ExcludeSetter
	private Object noSetter;

	@Getter(AccessLevel.NONE)
	@Access.ExcludeGetter
	private Object noGetter;
}
