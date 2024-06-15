package io.polaris.core.jdbc.dbv.model;

import io.polaris.core.jdbc.dbv.annotation.DbvColumn;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
	@DbvColumn("TABLE_TYPE")
	private String value;

}
