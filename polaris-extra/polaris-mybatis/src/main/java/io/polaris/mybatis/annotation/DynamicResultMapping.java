package io.polaris.mybatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.mybatis.interceptor.DynamicResultMappingInterceptor;

/**
 * 声明此注解后，表示将检测目标实体的生成列信息，动态修改MappedStatement中`useGeneratedKeys`等属性的能力。
 * <p>
 * 由于以插件形式实现，需启用插件{@link DynamicResultMappingInterceptor}
 *
 * @author Qt
 * @since Aug 24, 2023
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicResultMapping {

	/**
	 * @return 指定检测的目标实体类型，默认由Mapper上下文推断
	 */
	Class<?> entity() default void.class;

	/**
	 * @return 是否使用数据库列名映射，而不是使用实体属性名
	 */
	boolean useColumnName() default false;

	/**
	 * @return 列名前缀
	 */
	String columnPrefix() default "";
}
