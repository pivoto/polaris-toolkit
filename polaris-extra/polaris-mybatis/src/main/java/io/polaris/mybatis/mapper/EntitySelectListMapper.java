package io.polaris.mybatis.mapper;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectListMapper<E> extends EntityMapper<E>
	, EntitySelectListDefaultMapper<E>
	, EntitySelectListDirectMapper<E>
	, EntitySelectListExceptLogicDeletedMapper<E> {

}
