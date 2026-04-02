package io.polaris.mybatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.mybatis.consts.AutoMapperType;
import io.polaris.mybatis.mapper.EntityMapper;

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

	/**
	 * 自动Mapper方法生成使用的实体类，不配置则默认从方法或接口的注解及泛型参数推断
	 *
	 * @see MapperEntity
	 * @see EntityMapper
	 */
	Class<?> entity() default Object.class;

	/**
	 * 自动Mapper方法生成类型
	 */
	AutoMapperType type();

	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface List {
		AutoMapper[] value();
	}
}
