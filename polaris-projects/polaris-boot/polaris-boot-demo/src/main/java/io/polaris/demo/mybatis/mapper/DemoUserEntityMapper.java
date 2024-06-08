package io.polaris.demo.mybatis.mapper;

import io.polaris.demo.mybatis.entity.DemoUserEntity;
import io.polaris.mybatis.mapper.*;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Qt
 * @since  Aug 25, 2023
 */
@Mapper
public interface DemoUserEntityMapper extends
	EntityInsertMapper<DemoUserEntity>,
	EntityDeleteByIdMapper<DemoUserEntity>,
	EntityDeleteByAnyMapper<DemoUserEntity>,
	EntityUpdateByIdMapper<DemoUserEntity>,
	EntityUpdateByAnyMapper<DemoUserEntity>,
	EntitySelectMapper<DemoUserEntity>,
	EntitySelectListMapper<DemoUserEntity>,
	InsertStatementMapper,
	DeleteStatementMapper,
	UpdateStatementMapper,
	MergeStatementMapper,
	SelectStatementMapper<DemoUserEntity> {

}
