package io.polaris.mybatis.mapper;

/**
 * 实体查询映射器接口，继承多个查询相关的映射器接口。
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectMapper<E> extends EntityMapper<E>
	, EntitySelectOneDefaultMapper<E>
	, EntitySelectOneDirectMapper<E>
	, EntitySelectOneExceptLogicDeletedMapper<E> {

}
