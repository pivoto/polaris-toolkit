package io.polaris.core.jdbc.table;

import io.polaris.core.jdbc.annotation.Column;
import io.polaris.core.jdbc.annotation.Id;
import io.polaris.core.jdbc.annotation.Table;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

/**
 * Oracle的dual伪表
 *
 * @author Qt
 * @since 1.8,  Aug 31, 2023
 */
@Table(value = "DUAL", sqlGenerated = false)
@FieldNameConstants
@Data
public class DualEntity {

	@Id
	@Column(value = "DUMMY")
	private String dummy;

}
