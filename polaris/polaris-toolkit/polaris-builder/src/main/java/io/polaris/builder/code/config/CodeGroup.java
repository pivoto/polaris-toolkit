package io.polaris.builder.code.config;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Qt
 */
@Data
@XStreamAlias("group")
@NoArgsConstructor
public class CodeGroup {

	/** 需忽略的表名前缀，多个间逗号分隔 */
	@XStreamAlias("table-prefix")
	private String tablePrefix;
	/** 需忽略的表名后缀，多个间逗号分隔 */
	@XStreamAlias("table-suffix")
	private String tableSuffix;
	/** 需忽略的列名前缀，多个间逗号分隔 */
	@XStreamAlias("column-prefix")
	private String columnPrefix;
	/** 需忽略的列名后缀，多个间逗号分隔 */
	@XStreamAlias("column-suffix")
	private String columnSuffix;
	/** 组扩展属性配置 */
	@XStreamAlias("property")
	@XStreamConverter(ConfigParser.PropertyConverter.class)
	private Map<String, String> property;

	/** 模板 */
	@XStreamAlias("templates")
	private List<CodeTemplate> templates = new ArrayList<>();

	/** 待生成代码的表 */
	@XStreamAlias("tables")
	private List<CodeTable> tables = new ArrayList<>();
}
