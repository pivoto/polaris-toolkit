package io.awesome.builder.bean;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Qt
 */
@Data
@XStreamAlias("group")
public class CodeGroup {

	@XStreamAlias("templates")
	private List<CodeTemplate> templates = new ArrayList<>();

	@XStreamAlias("tables")
	private List<CodeTable> tables = new ArrayList<>();

	@XStreamAlias("property")
	private Map<String, String> property;

}
