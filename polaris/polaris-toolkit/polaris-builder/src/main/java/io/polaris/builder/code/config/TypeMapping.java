package io.polaris.builder.code.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Qt
 * @since 1.8
 */
@Data
@XStreamAlias("mapping")
@NoArgsConstructor
public class TypeMapping {
	@XStreamAsAttribute
	@XStreamAlias("jdbcType")
	private String jdbcType; // 列的JDBC类型
	@XStreamAsAttribute
	@XStreamAlias("javaType")
	private String javaType; // 列的映射类型
}
