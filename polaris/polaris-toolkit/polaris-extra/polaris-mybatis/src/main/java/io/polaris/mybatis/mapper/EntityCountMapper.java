package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.mybatis.consts.EntityMapperKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
public interface EntityCountMapper<E> extends EntityMapper<E> {

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntity)
	int countEntity(@Param(EntityMapperKeys.WHERE) E entity
		, @Param(EntityMapperKeys.WHERE_NULLS_INCLUDE) Boolean includeWhereNulls);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntity)
	int countEntityByMap(@Param(EntityMapperKeys.WHERE) Map<String, Object> entity
		, @Param(EntityMapperKeys.WHERE_NULLS_INCLUDE) Boolean includeWhereNulls);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntity)
	int countEntityByCriteria(@Param(EntityMapperKeys.WHERE) Criteria criteria
		, @Param(EntityMapperKeys.WHERE_NULLS_INCLUDE) Boolean includeWhereNulls);


	default int countEntity(E entity) {
		return countEntity(entity, false);
	}

	default int countEntityByMap(Map<String, Object> entity) {
		return countEntityByMap(entity, false);
	}

	default int countEntityByCriteria(Criteria criteria) {
		return countEntityByCriteria(criteria, false);
	}


}
