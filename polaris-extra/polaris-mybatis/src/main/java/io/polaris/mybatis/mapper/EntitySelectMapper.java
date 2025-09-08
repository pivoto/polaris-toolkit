package io.polaris.mybatis.mapper;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectMapper<E> extends EntityMapper<E>
	, EntitySelectOneDefaultMapper<E>
	, EntitySelectOneDirectMapper<E>
	, EntitySelectOneExceptLogicDeletedMapper<E> {

}
