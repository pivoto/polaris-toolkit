package io.polaris.dbv.model;

import io.polaris.dbv.annotation.ColumnName;
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

	@ColumnName("TABLE_CAT")
	private String value;
}