package io.awesome.dbv.model;

import io.awesome.dbv.annotation.ColumnName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Qt
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TableType {

	/**
	 * 典型的类型是“TABLE”，“VIEW”，“SYSTEM TABLE”，“GLOBAL TEMPORARY”，“LOCAL TEMPORARY”，“ALIAS”，“SYNONYM”
	 */
	@ColumnName("TABLE_TYPE")
	private String value;

}
