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
public class Schema {

	@DbvColumn("TABLE_SCHEM")
	private String value;

	/*@DbvColumn("TABLE_CATALOG")
	public String catalog;*/

}
