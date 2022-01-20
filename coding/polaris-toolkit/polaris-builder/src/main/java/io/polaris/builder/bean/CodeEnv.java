package io.polaris.builder.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Qt
 */
@Data
@XStreamAlias("env")
public class CodeEnv {

	@XStreamAlias("outdir")
	private String outdir;
	@XStreamAlias("groups")
	private List<CodeGroup> groups;
	@XStreamAlias("property")
	private Map<String, String> property;

}
