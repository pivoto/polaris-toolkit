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
 * 自动生成Mapper方法的配置注解。通过 {@link AutoMapperType} 指定生成SQL类型
 * <p>
 * 可同时配合以下注解调整配置规则：
 * <ul>
 *   <li>
 *   {@link MapperEntity} 声明实体类型与别名
 *   </li>
 *   <li>
 *   {@link MapperParam} 配置参数中的特定类型值的绑定键名
 *   </li>
 *   <li>
 *   {@link SupportLogicDeleted} 是否处理逻辑删除
 *   </li>
 *   <li>
 *   {@link AutoResultMapping} 自动生成列映射配置
 *   </li>
 *   <li>
 *   {@link AutoUseGeneratedKeys} 检测目标实体的生成列信息，动态修改MappedStatement中`useGeneratedKeys`等属性
 *   </li>
 *   <li>
 *   {@link ExistsByCount} 是否通过Count(*)查询存在性，否则表示使用游标查询或通过分页插件处理，接收返回值为布尔列表
 *   </li>
 * </ul>
 *
 * @author Qt
 * @see MapperEntity
 * @see MapperParam
 * @see SupportLogicDeleted
 * @see AutoResultMapping
 * @see AutoUseGeneratedKeys
 * @see ExistsByCount
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
