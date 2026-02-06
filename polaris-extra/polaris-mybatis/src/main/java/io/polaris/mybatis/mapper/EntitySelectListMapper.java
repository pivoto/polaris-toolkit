package io.polaris.mybatis.mapper;

/**
 * 实体查询列表映射器接口
 * 继承自基础实体映射器和其他特定功能的映射器接口
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectListMapper<E> extends EntityMapper<E>
	, EntitySelectListDefaultMapper<E>
	, EntitySelectListDirectMapper<E>
	, EntitySelectListExceptLogicDeletedMapper<E> {

}
