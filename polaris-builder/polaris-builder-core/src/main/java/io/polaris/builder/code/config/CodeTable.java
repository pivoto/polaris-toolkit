package io.polaris.builder.code.config;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
	private String name;
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
	private String javaPackage;
	/** 表扩展属性 */
	@XStreamAlias("property")
	@XStreamConverter(ConfigParser.PropertyConverter.class)
	private Map<String, String> property;
	/** jdbc类型映射 */
	@XStreamAlias("mappings")
	private Set<TypeMapping> mappings = new LinkedHashSet<>();
	@XStreamAlias("columns")
	private Set<ConfigColumn> columns = new LinkedHashSet<>();

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
	/** 需忽略的列名，支持正则表达式 */
	@XStreamAlias("ignored-columns")
	private Set<String> ignoredColumns = new LinkedHashSet<>();
}
