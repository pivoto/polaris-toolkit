package io.polaris.demo.mybatis.mapper;

import java.util.List;
import java.util.Map;

import io.polaris.core.jdbc.annotation.EntitySelect;
import io.polaris.demo.mybatis.entity.DemoOrgEntity;
import io.polaris.mybatis.annotation.AutoMapper;
import io.polaris.mybatis.annotation.MapperParam;
import io.polaris.mybatis.consts.AutoMapperType;
import io.polaris.mybatis.mapper.EntityMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * @author Qt
 * @since Apr 01, 2026
 */
@Mapper
public interface DemoOrgAutoMapper extends EntityMapper<DemoOrgEntity> {


	@AutoMapper(type = AutoMapperType.selectAny)
	@SelectProvider
	@EntitySelect(table = DemoOrgEntity.class, byId = false)
	List<DemoOrgEntity> queryAuto(@MapperParam.Where @Param("param") Map<String, Object> param);


	@AutoMapper(type = AutoMapperType.selectAny)
	@SelectProvider
	@EntitySelect(table = DemoOrgEntity.class, byId = false)
	List<DemoOrgEntity> queryAuto(@MapperParam.Where @Param("param") DemoOrgEntity param);


}
