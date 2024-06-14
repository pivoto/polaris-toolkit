package io.polaris.mybatis.mapper;

import java.lang.reflect.Type;

import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.statement.ColumnPredicate;
import io.polaris.core.jdbc.sql.statement.MergeStatement;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityMergeMapper<E> extends MergeStatementMapper, EntityMapper<E> {

	@SuppressWarnings({"unchecked"})
	default int mergeBySql(E entity) {
		Type actualType = JavaType.of(getClass()).getActualType(EntityMapper.class, 0);
		Class<?> entityClass = Types.getClass(actualType);
		if (entityClass == Object.class) {
			throw new IllegalStateException("未知实体类型！");
		}
		return mergeBySql((Class<E>) entityClass, entity);
	}

	@SuppressWarnings({"unchecked"})
	default int mergeBySql(E entity, boolean includeAllEmpty, boolean updateWhenMatched, boolean insertWhenNotMatched) {
		Type actualType = JavaType.of(getClass()).getActualType(EntityMapper.class, 0);
		Class<?> entityClass = Types.getClass(actualType);
		if (entityClass == Object.class) {
			throw new IllegalStateException("未知实体类型！");
		}
		return mergeBySql((Class<E>) entityClass, entity, includeAllEmpty, updateWhenMatched, insertWhenNotMatched);
	}

	@SuppressWarnings({"unchecked"})
	default int mergeBySql(E entity, boolean updateWhenMatched, boolean insertWhenNotMatched) {
		Type actualType = JavaType.of(getClass()).getActualType(EntityMapper.class, 0);
		Class<?> entityClass = Types.getClass(actualType);
		if (entityClass == Object.class) {
			throw new IllegalStateException("未知实体类型！");
		}
		return mergeBySql((Class<E>) entityClass, entity, updateWhenMatched, insertWhenNotMatched);
	}

}
