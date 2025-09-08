package io.polaris.mybatis.mapper;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectCursorMapper<E> extends EntityMapper<E>
	, EntitySelectCursorDefaultMapper<E>
	, EntitySelectCursorDirectMapper<E>
	, EntitySelectCursorExceptLogicDeletedMapper<E> {

}
