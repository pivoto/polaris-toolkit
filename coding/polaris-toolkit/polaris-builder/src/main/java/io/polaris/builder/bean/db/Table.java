package io.polaris.builder.bean.db;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Qt
 */
@SuppressWarnings("ALL")
@Data
@XStreamAlias("table")
public class Table {

	@XStreamAsAttribute
	private String name; // 表名
	@XStreamAsAttribute
	private String comment; // 注释

	@ToString.Exclude
	@XStreamAlias(("columns"))
	@XStreamImplicit(itemFieldName = "column", keyFieldName = "name")
	private List<Column> columns = new ArrayList<Column>(); // 所有列

	@ToString.Exclude
	@XStreamOmitField
	@XStreamAsAttribute
	private List<Column> pkColumns = new ArrayList<Column>(); // 主键列
	@ToString.Exclude
	@XStreamOmitField
	@XStreamAsAttribute
	private List<Column> normalColumns = new ArrayList<Column>(); // 非主键列

	@XStreamOmitField
	private String classify;
	@XStreamOmitField
	private Map<String, String> property;
	@XStreamOmitField
	private String javaPackageName = ""; // 代码生成时对应的包名
	@XStreamOmitField
	private String javaPackageDir = ""; // 代码生成时对应的包名

	// for java
	@XStreamOmitField
	private String javaClassName; // 表名映射后的类名
	@XStreamOmitField
	private String javaVariableName; // 表名映射后的类的实例变量名
	@XStreamOmitField
	private String xmlName;

	/**
	 * 预处理,对类名、变量名、主键列、非主键列等的处理
	 */
	public void prepare4Java() {
		if (columns == null) {
			columns = new ArrayList<>();
		}
		if (pkColumns == null) {
			pkColumns = new ArrayList<>();
		}
		if (normalColumns == null) {
			normalColumns = new ArrayList<>();
		}

		for (Column col : columns) {
			col.prepare4Java();
		}

		if (pkColumns.isEmpty()) {
			for (Column col : columns) {
				if (col.isPrimary()) {
					pkColumns.add(col);
				}
			}
		} else {
			for (Column col : pkColumns) {
				col.prepare4Java();
			}
		}

		if (normalColumns.isEmpty()) {
			for (Column col : columns) {
				if (!col.isPrimary()) {
					normalColumns.add(col);
				}
			}
		} else {
			for (Column col : normalColumns) {
				col.prepare4Java();
			}
		}
		javaPackageDir = javaPackageName.replace('.', '/');

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

}
