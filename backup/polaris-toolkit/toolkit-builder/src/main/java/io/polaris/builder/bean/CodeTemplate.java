package io.polaris.builder.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

import java.util.Map;

/**
 * @author Qt
 */
@Data
@XStreamAlias("template")
public class CodeTemplate {
	@XStreamAlias("path")
	private String path;
	@XStreamAlias("outdir")
	private String outdir;
	@XStreamAlias("filename")
	private String filename;
	@XStreamAlias("property")
	private Map<String, String> property;
}
