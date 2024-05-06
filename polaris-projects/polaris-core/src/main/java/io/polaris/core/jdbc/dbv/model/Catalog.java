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
public class Catalog {

	@DbvColumn("TABLE_CAT")
	private String value;
}
