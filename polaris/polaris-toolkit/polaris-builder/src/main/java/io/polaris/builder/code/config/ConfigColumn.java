package io.polaris.builder.code.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.*;

/**
 * @author Qt
 * @since 1.8
 */
@Data
@XStreamAlias("column")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ConfigColumn {
	@XStreamAsAttribute
	@XStreamAlias("name")
	private String name;
	@XStreamAsAttribute
	@XStreamAlias("javaType")
	private String javaType; // 列的映射类型
}
