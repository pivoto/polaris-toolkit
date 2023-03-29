package io.polaris.builder.code.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import io.polaris.dbv.toolkit.StringKit;
import lombok.Data;

import java.sql.Types;
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

	// for java
	@XStreamOmitField
	private String javaClassName; // 列的映射类名,一般列只作为类的字段,不会用到此值
	@XStreamOmitField
	private String javaVariableName; // 列的映射变量名
	@XStreamOmitField
	private String javaClassType; // 列的映射类型
	@XStreamOmitField
	private String javaJdbcType; // 列的JDBC类型
	@XStreamOmitField
	private String xmlName;

	static String javaClassType(int dataType) {
		switch (dataType) {
			case Types.BIT:
				return Boolean.class.getSimpleName();
			case Types.TINYINT:
				return Byte.class.getSimpleName();
			case Types.SMALLINT:
				return Short.class.getSimpleName();
			case Types.INTEGER:
				return Integer.class.getSimpleName();
			case Types.BIGINT:
				return Long.class.getSimpleName();
			case Types.FLOAT:
				return Double.class.getSimpleName();
			case Types.REAL:
				return Float.class.getSimpleName();
			case Types.DOUBLE:
				return Double.class.getSimpleName();
			case Types.NUMERIC:
				return java.math.BigDecimal.class.getName();
			case Types.DECIMAL:
				return java.math.BigDecimal.class.getName();
			case Types.CHAR:
				return String.class.getSimpleName();
			case Types.VARCHAR:
				return String.class.getSimpleName();
			case Types.LONGVARCHAR:
				return String.class.getSimpleName();
			case Types.DATE:
				return java.util.Date.class.getName();
			case Types.TIME:
				return java.sql.Timestamp.class.getName();
			case Types.TIMESTAMP:
				return java.sql.Timestamp.class.getName();
			case Types.BINARY:
				return byte[].class.getSimpleName();
			case Types.VARBINARY:
				return byte[].class.getSimpleName();
			case Types.LONGVARBINARY:
				return byte[].class.getSimpleName();
			case Types.NULL:
				throw new IllegalArgumentException("" + dataType);
			case Types.OTHER:
				throw new IllegalArgumentException("" + dataType);
			case Types.JAVA_OBJECT:
				throw new IllegalArgumentException("" + dataType);
			case Types.DISTINCT:
				throw new IllegalArgumentException("" + dataType);
			case Types.STRUCT:
				throw new IllegalArgumentException("" + dataType);
			case Types.ARRAY:
				throw new IllegalArgumentException("" + dataType);
			case Types.BLOB:
				return byte[].class.getSimpleName();
			case Types.CLOB:
				return String.class.getSimpleName();
			case Types.REF:
				throw new IllegalArgumentException("" + dataType);
			case Types.DATALINK:
				throw new IllegalArgumentException("" + dataType);
			case Types.BOOLEAN:
				return Boolean.class.getSimpleName();
			case Types.ROWID:
				throw new IllegalArgumentException("" + dataType);
			case Types.NCHAR:
				return String.class.getSimpleName();
			case Types.NVARCHAR:
				return String.class.getSimpleName();
			case Types.LONGNVARCHAR:
				return String.class.getSimpleName();
			case Types.NCLOB:
				return String.class.getSimpleName();
			case Types.SQLXML:
				throw new IllegalArgumentException("" + dataType);
			default:
				return null;
		}
	}

	static String javaJdbcType(int dataType) {
		switch (dataType) {
			case Types.BIT:
				return "BIT";
			case Types.TINYINT:
				return "TINYINT";
			case Types.SMALLINT:
				return "SMALLINT";
			case Types.INTEGER:
				return "INTEGER";
			case Types.BIGINT:
				return "BIGINT";
			case Types.FLOAT:
				return "FLOAT";
			case Types.REAL:
				return "REAL";
			case Types.DOUBLE:
				return "DOUBLE";
			case Types.NUMERIC:
				return "NUMERIC";
			case Types.DECIMAL:
				return "DECIMAL";
			case Types.CHAR:
				return "CHAR";
			case Types.VARCHAR:
				return "VARCHAR";
			case Types.LONGVARCHAR:
				return "LONGVARCHAR";
			case Types.DATE:
				return "DATE";
			case Types.TIME:
				return "TIME";
			case Types.TIMESTAMP:
				return "TIMESTAMP";
			case Types.BINARY:
				return "BINARY";
			case Types.VARBINARY:
				return "VARBINARY";
			case Types.LONGVARBINARY:
				return "LONGVARBINARY";
			case Types.NULL:
				return "NULL";
			case Types.OTHER:
				return "OTHER";
			case Types.JAVA_OBJECT:
				return "JAVA_OBJECT";
			case Types.DISTINCT:
				return "DISTINCT";
			case Types.STRUCT:
				return "STRUCT";
			case Types.ARRAY:
				return "ARRAY";
			case Types.BLOB:
				return "BLOB";
			case Types.CLOB:
				return "CLOB";
			case Types.REF:
				return "REF";
			case Types.DATALINK:
				return "DATALINK";
			case Types.BOOLEAN:
				return "BOOLEAN";
			case Types.ROWID:
				return "ROWID";
			case Types.NCHAR:
				return "NCHAR";
			case Types.NVARCHAR:
				return "NVARCHAR";
			case Types.LONGNVARCHAR:
				return "LONGNVARCHAR";
			case Types.NCLOB:
				return "NCLOB";
			case Types.SQLXML:
				return "SQLXML";
			default:
				return null;
		}
	}

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
		name = columnNameTrimmer.apply(name);
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
		if (StringKit.isEmpty(javaClassName)) {
			javaClassName = Character.toUpperCase(nameChars[0]) + sb.toString();
		}
		if (StringKit.isEmpty(javaVariableName)) {
			javaVariableName = Character.toLowerCase(nameChars[0]) + sb.toString();
		}
		if (type != null) {
			if (StringKit.isEmpty(javaJdbcType)) {
				javaJdbcType = javaJdbcType(type);
			}
			if (StringKit.isEmpty(javaClassType)) {
				javaClassType = javaClassType(type);
			}
		} else {
			if (StringKit.isNotEmpty(javaJdbcType)) {
				try {
					type = (int) Types.class.getField(javaJdbcType.toUpperCase()).get(null);
				} catch (Exception e) {
				}
			}
		}
		xmlName = name.toLowerCase().replace("_", "-");
	}

}
