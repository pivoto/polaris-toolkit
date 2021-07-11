package io.awesome.builder.bean;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Data;

import java.util.Map;

/**
 * @author Qt
 */
@Data
@XStreamAlias("table")
public class CodeTable {
	@XStreamAlias("name")
	@XStreamAsAttribute
	private String table;
	@XStreamAlias("catalog")
	@XStreamAsAttribute
	private String catalog;
	@XStreamAlias("schema")
	@XStreamAsAttribute
	private String schema;
	@XStreamAlias("package")
	@XStreamAsAttribute
	private String javaPackageName;
	@XStreamAlias("classify")
	@XStreamAsAttribute
	private String classify;
	@XStreamAlias("property")
	@XStreamAsAttribute
	private Map<String, String> property;

}
