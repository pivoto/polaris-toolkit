package io.polaris.mybatis.mapper;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityDeleteByAnyMapper<E> extends EntityMapper<E>
	, EntityDeleteDefaultByAnyMapper<E>
	, EntityDeleteDirectByAnyMapper<E>
	, EntityDeleteLogicByAnyMapper<E> {

}
