package io.polaris.core.jdbc.entity.merge;

import io.polaris.core.jdbc.annotation.ColumnProperty;
import io.polaris.core.jdbc.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * @author Qt
 * @since Oct 08, 2025
 */
@TableX2(table = "table_x2")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class TableX2Entity {
	@Id
	@ColumnPropertyX1
	@ColumnProperty(key = "string", type = ColumnProperty.Type.STRING, stringValue = "stringValue")
	private Long id;
	private String name;
	@ColumnX2(skip = true)
	private Double score;
	@ColumnX2("FIELD_STR")
	@ColumnPropertyX1
	@ColumnProperty(key = "string", type = ColumnProperty.Type.STRING, stringValue = "stringValue")
	private String fieldStr1;
}
