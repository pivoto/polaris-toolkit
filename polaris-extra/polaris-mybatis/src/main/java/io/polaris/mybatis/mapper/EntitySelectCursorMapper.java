package io.polaris.mybatis.mapper;

/**
 * 实体查询游标映射器接口
 * <p>
 * 该接口继承了多个游标查询相关的映射器接口，提供了完整的实体游标查询功能。
 * 包括默认游标查询、直接游标查询以及排除逻辑删除数据的游标查询。
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectCursorMapper<E> extends EntityMapper<E>
	, EntitySelectCursorDefaultMapper<E>
	, EntitySelectCursorDirectMapper<E>
	, EntitySelectCursorExceptLogicDeletedMapper<E> {

}
