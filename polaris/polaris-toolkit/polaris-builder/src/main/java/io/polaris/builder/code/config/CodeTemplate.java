package io.polaris.builder.code.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Qt
 */
@Data
@XStreamAlias("template")
@NoArgsConstructor
public class CodeTemplate {
	/** 模板路径*/
	@XStreamAlias("path")
	private String path;
	/** 输出目录路径*/
	@XStreamAlias("outdir")
	private String outdir;
	/** 输出文件名*/
	@XStreamAlias("filename")
	private String filename;
	/** 模板扩展属性*/
	@XStreamAlias("property")
	@XStreamConverter(ConfigParser.PropertyConverter.class)
	private Map<String, String> property;

}
