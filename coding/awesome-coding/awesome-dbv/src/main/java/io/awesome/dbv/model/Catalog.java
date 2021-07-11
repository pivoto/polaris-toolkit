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
public class Catalog {

	@ColumnName("TABLE_CAT")
	private String value;
}
