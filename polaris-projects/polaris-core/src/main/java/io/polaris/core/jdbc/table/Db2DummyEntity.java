package io.polaris.core.jdbc.table;

import io.polaris.core.jdbc.annotation.Column;
import io.polaris.core.jdbc.annotation.Id;
import io.polaris.core.jdbc.annotation.Table;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * DB2的伪表SYSIBM.SYSDUMMY1
 *
 * @author Qt
 * @since  Aug 31, 2023
 */
@Table(value = "SYSIBM.SYSDUMMY1", sqlGenerated = false)
@FieldNameConstants
@Data
public class Db2DummyEntity {

	@Id
	@Column(value = "IBMREQD")
	private String dummy;

}
