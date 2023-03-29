package io.polaris.builder.code.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import io.polaris.builder.code.JdbcTypes;
import io.polaris.dbv.toolkit.StringKit;
import lombok.Data;

import java.util.function.Function;

/**
 * @author Qt
 */
@SuppressWarnings({"AlibabaAvoidCommentBehindStatement"})
@Data
@XStreamAlias("column")
public class ColumnDto {
	@XStreamAsAttribute
	private String name; // 列名
	@XStreamAsAttribute
	@XStreamOmitField
	private Integer type; // 列类型 java.sql.Types
	@XStreamAsAttribute
	private String comment; // 注释
	@XStreamAsAttribute
	private String defaultValue; // 默认值
	@XStreamAsAttribute
	private boolean primary = false; // 是否主键列
	@XStreamAsAttribute
	private boolean nullable = true; // 是否可为空值
	@XStreamAsAttribute
	private int columnSize;
	@XStreamAsAttribute
	private int decimalDigits;
	@XStreamAsAttribute
	private boolean generated = false;
	@XStreamAsAttribute
	private boolean autoincrement = false;

	@XStreamAsAttribute
	@XStreamAlias("jdbcType")
	private String jdbcType; // 列的JDBC类型
	@XStreamAsAttribute
	@XStreamAlias("javaType")
	private String javaType; // 列的映射类型

	@XStreamOmitField
	private String javaTypeSimpleName;
	@XStreamOmitField
	private String javaClassName; // 列的映射类名,一般列只作为类的字段,不会用到此值
	@XStreamOmitField
	private String javaVariableName; // 列的映射变量名
	@XStreamOmitField
	private String xmlName;


	public boolean isNotNull() {
		return !nullable;
	}

	public void setNotNull(boolean notNull) {
		this.nullable = !notNull;
	}

	/**
	 * 预处理,对列的映射变量名、映射类型等的处理
	 */
	public void prepare4Java(Function<String, String> columnNameTrimmer) {
		String name = columnNameTrimmer.apply(this.name);
		char[] nameChars = name.toCharArray();
		StringBuilder sb = new StringBuilder();
		boolean flag = false;
		for (int i = 1; i < nameChars.length; i++) {
			if (nameChars[i] == '_') {
				flag = true;
			} else {
				if (flag) {
					sb.append(Character.toUpperCase(nameChars[i]));
					flag = false;
				} else {
					sb.append(Character.toLowerCase(nameChars[i]));
				}
			}
		}
		if (StringKit.isEmpty(this.javaClassName)) {
			this.javaClassName = Character.toUpperCase(nameChars[0]) + sb.toString();
		}
		if (StringKit.isEmpty(this.javaVariableName)) {
			this.javaVariableName = Character.toLowerCase(nameChars[0]) + sb.toString();
		}

		if (StringKit.isNotEmpty(this.jdbcType)) {
			this.type = JdbcTypes.getTypeValue(this.jdbcType);
		} else {
			if (this.type == null) {
				throw new IllegalArgumentException("未知JdbcType");
			}
			this.jdbcType = JdbcTypes.getTypeName(this.type);
		}
		if (StringKit.isEmpty(this.javaType)) {
			Class c = JdbcTypes.getJavaType(this.type);
			if (c == null) {
				throw new IllegalArgumentException("不支持的JdbcType：" + this.jdbcType);
			}
			this.javaType = c.getName();
		}

		if (this.javaType.matches("java\\.lang\\.\\w+")) {
			this.javaType = this.javaType.replace("java.lang.", "");
			this.javaTypeSimpleName = this.javaType;
		} else {
			int i = this.javaType.lastIndexOf(".");
			if (i >= 0) {
				this.javaTypeSimpleName = this.javaType.substring(i + 1);
			} else {
				this.javaTypeSimpleName = this.javaType;
			}
		}
		this.xmlName = name.toLowerCase().replace("_", "-");
	}

}
