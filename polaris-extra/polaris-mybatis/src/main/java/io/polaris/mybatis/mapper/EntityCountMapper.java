package io.polaris.mybatis.mapper;

/**
 * 实体计数映射器接口，继承多个计数相关的映射器接口。
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityCountMapper<E> extends EntityMapper<E>
	, EntityCountDefaultMapper<E>
	, EntityCountDirectMapper<E>
	, EntityCountExceptLogicDeletedMapper<E> {

}
