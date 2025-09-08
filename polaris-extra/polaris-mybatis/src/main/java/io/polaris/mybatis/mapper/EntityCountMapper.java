package io.polaris.mybatis.mapper;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityCountMapper<E> extends EntityMapper<E>
	, EntityCountDefaultMapper<E>
	, EntityCountDirectMapper<E>
	, EntityCountExceptLogicDeletedMapper<E> {

}
