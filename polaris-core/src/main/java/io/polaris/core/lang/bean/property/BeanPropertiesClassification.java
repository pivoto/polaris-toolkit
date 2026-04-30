package io.polaris.core.lang.bean.property;

import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since Apr 30, 2026
 */
public class BeanPropertiesClassification {
	public Map<String, BeanPropertyInfo> properties;
	public List<BeanPropertyInfo> setters;
	public List<BeanPropertyInfo> getters;
	public List<BeanPropertyInfo> fields;

	public BeanPropertiesClassification(Map<String, BeanPropertyInfo> properties, List<BeanPropertyInfo> setters, List<BeanPropertyInfo> getters, List<BeanPropertyInfo> fields) {
		this.properties = properties;
		this.setters = setters;
		this.getters = getters;
		this.fields = fields;
	}
}
