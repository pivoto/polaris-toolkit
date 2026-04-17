package io.polaris.mybatis.enhance.example;

import java.math.BigDecimal;
import java.util.Date;

import io.polaris.core.jdbc.annotation.Column;
import io.polaris.core.jdbc.annotation.Id;
import io.polaris.core.jdbc.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Qt
 * @since Apr 17, 2026
 */
@Table("t_example")
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExampleEntity {
	@Id
	private String id;
	@Id
	private String extId;
	private String strCol;
	private Integer intCol;
	private Boolean boolCol;
	private Date dateCol;
	private Long longCol;
	private BigDecimal decimalCol;
	private byte[] bytesCol;
	private ExampleEnum enumCol;
	private ExampleDbEnum enumDbCol;

	@Column(logicDeleted = true)
	private Boolean deleted;
	@Column(createTime = true, updatable = false)
	private Date createDate;
	@Column(updateTime = true)
	private Date updateDate;

}
