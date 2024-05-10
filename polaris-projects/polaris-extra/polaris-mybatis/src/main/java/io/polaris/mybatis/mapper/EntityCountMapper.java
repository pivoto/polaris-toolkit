package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.Map;

/**
 * @author Qt
 * @since  Aug 25, 2023
 */
public interface EntityCountMapper<E> extends EntityMapper<E> {

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntity)
	int countEntity(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) Boolean includeWhereEmptyVal);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntity)
	int countEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) Boolean includeWhereEmptyVal);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntity)
	int countEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) Boolean includeWhereEmptyVal);


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
