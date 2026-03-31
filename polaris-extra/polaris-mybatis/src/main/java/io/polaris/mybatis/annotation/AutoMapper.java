package io.polaris.mybatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.mybatis.consts.AutoMapperType;
import org.apache.ibatis.annotations.Select;

/**
 * @author Qt
 * @since Mar 29, 2026
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(AutoMapper.List.class)
public @interface AutoMapper {

	String databaseId() default "";

	Class<?> entity() default Object.class;

	AutoMapperType type();

	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface List {
		AutoMapper[] value();
	}
}
