package io.polaris.core.jdbc.entity;

import io.polaris.core.jdbc.annotation.Id;
import io.polaris.core.jdbc.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * @author Qt
 * @since  Feb 08, 2024
 */
@Data
@Table("DEMO_TEST01")
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemoTest01Entity {
	@Id
	private Long id;
	private String name;
}
