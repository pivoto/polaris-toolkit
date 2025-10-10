package io.polaris.mybatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.annotation.ColumnProperty;
import io.polaris.core.lang.annotation.Alias;
import org.apache.ibatis.type.JdbcType;

/**
 * @author Qt
 * @since Oct 08, 2025
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ColumnProperty(key = ColumnJdbcType.KEY, type = ColumnProperty.Type.STRING)
public @interface ColumnJdbcType {

	String KEY = "jdbcType";

	@Alias(value = "stringValue", annotation = ColumnProperty.class)
	JdbcType value();

}
