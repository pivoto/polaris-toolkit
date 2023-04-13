package io.polaris.builder.code.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Qt
 */
@SuppressWarnings("ALL")
@Data
@XStreamAlias("table")
public class TableDto implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 表名 */
	@XStreamAsAttribute
	private String name;
	/** 注释 */
	@XStreamAsAttribute
	private String comment;

	/** 所有列 */
	@ToString.Exclude
	@XStreamAlias(("columns"))
	@XStreamImplicit(itemFieldName = "column", keyFieldName = "name")
	private List<ColumnDto> columns = new ArrayList<>();


	/** 主键列 */
	@ToString.Exclude
	@XStreamOmitField
	private List<ColumnDto> pkColumns = new ArrayList<>();
	/** 非主键列 */
	@ToString.Exclude
	@XStreamOmitField
	private List<ColumnDto> normalColumns = new ArrayList<ColumnDto>();
	@XStreamOmitField
	private Set<String> columnJavaTypes = new LinkedHashSet<>();

	@XStreamOmitField
	private Map<String, String> property;
	/** 代码生成时对应的包名 */
	@XStreamOmitField
	private String javaPackageName = "";
	/** 代码生成时对应的包名 */
	@XStreamOmitField
	private String javaPackageDir = "";

	/** 表名映射后的类名 */
	@XStreamOmitField
	private String javaClassName;
	/** 表名映射后的类的实例变量名 */
	@XStreamOmitField
	private String javaVariableName;
	@XStreamOmitField
	private String xmlName;

	/**
	 * 预处理,对类名、变量名、主键列、非主键列等的处理
	 */
	public void prepare4Java(Function<String, String> tableNameTrimmer, Function<String, String> columnNameTrimmer) {
		if (columns == null) {
			columns = new ArrayList<>();
		}
		if (pkColumns == null) {
			pkColumns = new ArrayList<>();
		}
		if (normalColumns == null) {
			normalColumns = new ArrayList<>();
		}
		if (columnJavaTypes == null) {
			columnJavaTypes = new LinkedHashSet<>();
		}

		for (ColumnDto col : columns) {
			col.prepare4Java(columnNameTrimmer);
			if (col.getJavaType().contains(".")) {
				columnJavaTypes.add(col.getJavaType());
			}
		}

		if (pkColumns.isEmpty()) {
			for (ColumnDto col : columns) {
				if (col.isPrimary()) {
					pkColumns.add(col);
				}
			}
		} else {
			for (ColumnDto col : pkColumns) {
				col.prepare4Java(columnNameTrimmer);
				if (col.getJavaType().contains(".")) {
					columnJavaTypes.add(col.getJavaType());
				}
			}
		}

		if (normalColumns.isEmpty()) {
			for (ColumnDto col : columns) {
				if (!col.isPrimary()) {
					normalColumns.add(col);
				}
			}
		} else {
			for (ColumnDto col : normalColumns) {
				col.prepare4Java(columnNameTrimmer);
				if (col.getJavaType().contains(".")) {
					columnJavaTypes.add(col.getJavaType());
				}
			}
		}
		javaPackageDir = javaPackageName.replace('.', '/');

		String name = tableNameTrimmer.apply(this.name);
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
		javaClassName = Character.toUpperCase(nameChars[0]) + sb.toString();
		javaVariableName = Character.toLowerCase(nameChars[0]) + sb.toString();
		xmlName = name.toLowerCase().replace("_", "-");
	}

	@Override
	public TableDto clone() {
		return SerializationUtils.clone(this);
	}

}
