package io.polaris.core.jdbc.entity.merge;

import io.polaris.core.jdbc.annotation.Column;
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
@TableX1
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class TableX1Entity {
	@Id
	@ColumnPropertyX1
	@ColumnProperty(key = "string", type = ColumnProperty.Type.STRING, stringValue = "stringValue")
	private Long id;
	@ColumnPropertyX1
	@ColumnProperty(key = "string", type = ColumnProperty.Type.STRING, stringValue = "stringValue")
	private String name;
	@ColumnX1(ignored = true)
	private Double score;
	@ColumnX1(name="FIELD_STR", ignored = false)
	@ColumnPropertyX1
	@ColumnProperty(key = "string", type = ColumnProperty.Type.STRING, stringValue = "stringValue")
	private String fieldStr1;
}
