package io.polaris.builder.code.config;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
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
@XStreamAlias("table")
@NoArgsConstructor
public class CodeTable {
	/** 表名 */
	@XStreamAlias("name")
	@XStreamAsAttribute
	private String table;
	/** 库名 */
	@XStreamAlias("catalog")
	@XStreamAsAttribute
	private String catalog;
	/** 模式名/用户名 */
	@XStreamAlias("schema")
	@XStreamAsAttribute
	private String schema;
	/** java包名 */
	@XStreamAlias("package")
	@XStreamAsAttribute
	private String javaPackageName;
	/** 表扩展属性 */
	@XStreamAlias("property")
	@XStreamConverter(ConfigParser.PropertyConverter.class)
	private Map<String, String> property;
	/** jdbc类型映射 */
	@XStreamAlias("mappings")
	private List<TypeMapping> mappings = new ArrayList<>();

	/** 需忽略的表名前缀，多个间逗号分隔 */
	@XStreamAlias("table-prefix")
	@XStreamAsAttribute
	private String tablePrefix;
	/** 需忽略的表名后缀，多个间逗号分隔 */
	@XStreamAlias("table-suffix")
	@XStreamAsAttribute
	private String tableSuffix;
	/** 需忽略的列名前缀，多个间逗号分隔 */
	@XStreamAlias("column-prefix")
	@XStreamAsAttribute
	private String columnPrefix;
	/** 需忽略的列名后缀，多个间逗号分隔 */
	@XStreamAlias("column-suffix")
	@XStreamAsAttribute
	private String columnSuffix;
}
