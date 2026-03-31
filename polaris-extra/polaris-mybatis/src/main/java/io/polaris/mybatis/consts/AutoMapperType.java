package io.polaris.mybatis.consts;

import java.util.Map;
import java.util.function.BiFunction;

import io.polaris.mybatis.enhance.EnhancedAutoMapperProviders;
import io.polaris.mybatis.provider.*;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.mapping.SqlCommandType;

/**
 * @author Qt
 * @since Mar 29, 2026
 */
public enum AutoMapperType {

	selectAny(SqlCommandType.SELECT, EnhancedAutoMapperProviders::selectAny),
	updateAny(SqlCommandType.UPDATE, EnhancedAutoMapperProviders::updateAny),
	selectAnySql(SqlCommandType.SELECT, EnhancedAutoMapperProviders::executeAnySql),
	updateAnySql(SqlCommandType.UPDATE, EnhancedAutoMapperProviders::executeAnySql),

	insertBySql(SqlCommandType.INSERT, SqlInsertProvider::doProvideSql),
	deleteBySql(SqlCommandType.DELETE, SqlDeleteProvider::doProvideSql),
	updateBySql(SqlCommandType.UPDATE, SqlUpdateProvider::doProvideSql),
	selectBySql(SqlCommandType.SELECT, SqlSelectProvider::doProvideSql),
	selectBySqlDirect(SqlCommandType.SELECT, SqlSelectDirectProvider::doProvideSql),
	selectBySqlExceptLogicDeleted(SqlCommandType.SELECT, SqlSelectExceptLogicDeletedProvider::doProvideSql),
	countBySql(SqlCommandType.SELECT, SqlCountProvider::doProvideSql),


	countBySqlDirect(SqlCommandType.SELECT, SqlCountDirectProvider::doProvideSql),
	countBySqlExceptLogicDeleted(SqlCommandType.SELECT, SqlCountExceptLogicDeletedProvider::doProvideSql),
	existsBySql(SqlCommandType.SELECT, SqlExistsProvider::doProvideSql),
	existsBySqlDirect(SqlCommandType.SELECT, SqlExistsDirectProvider::doProvideSql),
	existsBySqlExceptLogicDeleted(SqlCommandType.SELECT, SqlExistsExceptLogicDeletedProvider::doProvideSql),
	mergeBySql(SqlCommandType.UPDATE, SqlMergeProvider::doProvideSql),

	insertEntity(SqlCommandType.INSERT, EntityInsertProvider::doProvideSql),

	deleteEntityById(SqlCommandType.DELETE, EntityDeleteByIdProvider::doProvideSql),
	directDeleteEntityById(SqlCommandType.DELETE, EntityDeleteDirectByIdProvider::doProvideSql),
	logicDeleteEntityById(SqlCommandType.UPDATE, EntityDeleteLogicByIdProvider::doProvideSql),
	deleteEntityByAny(SqlCommandType.DELETE, EntityDeleteByAnyProvider::doProvideSql),
	directDeleteEntityByAny(SqlCommandType.DELETE, EntityDeleteDirectByAnyProvider::doProvideSql),
	logicDeleteEntityByAny(SqlCommandType.UPDATE, EntityDeleteLogicByAnyProvider::doProvideSql),

	updateEntityById(SqlCommandType.UPDATE, EntityUpdateByIdProvider::doProvideSql),
	updateEntityByAny(SqlCommandType.UPDATE, EntityUpdateByAnyProvider::doProvideSql),

	existsEntity(SqlCommandType.SELECT, EntityExistsByAnyProvider::doProvideSql),
	existsEntityDirect(SqlCommandType.SELECT, EntityExistsByAnyDirectProvider::doProvideSql),
	existsEntityExceptLogicDeleted(SqlCommandType.SELECT, EntityExistsByAnyExceptLogicDeletedProvider::doProvideSql),
	existsEntityById(SqlCommandType.SELECT, EntityExistsByIdProvider::doProvideSql),
	existsEntityByIdDirect(SqlCommandType.SELECT, EntityExistsByIdDirectProvider::doProvideSql),
	existsEntityByIdExceptLogicDeleted(SqlCommandType.SELECT, EntityExistsByIdExceptLogicDeletedProvider::doProvideSql),

	selectEntity(SqlCommandType.SELECT, EntitySelectByAnyProvider::doProvideSql),
	selectEntityDirect(SqlCommandType.SELECT, EntitySelectByAnyDirectProvider::doProvideSql),
	selectEntityExceptLogicDeleted(SqlCommandType.SELECT, EntitySelectByAnyExceptLogicDeletedProvider::doProvideSql),
	selectEntityById(SqlCommandType.SELECT, EntitySelectByIdProvider::doProvideSql),
	selectEntityByIdDirect(SqlCommandType.SELECT, EntitySelectByIdDirectProvider::doProvideSql),
	selectEntityByIdExceptLogicDeleted(SqlCommandType.SELECT, EntitySelectByIdExceptLogicDeletedProvider::doProvideSql),

	countEntity(SqlCommandType.SELECT, EntityCountProvider::doProvideSql),
	countEntityDirect(SqlCommandType.SELECT, EntityCountDirectProvider::doProvideSql),
	countEntityExceptLogicDeleted(SqlCommandType.SELECT, EntityCountExceptLogicDeletedProvider::doProvideSql),

	;

	private final SqlCommandType sqlCommandType;
	private final BiFunction<Map<String, Object>, ProviderContext, String> sqlFunction;

	AutoMapperType(SqlCommandType sqlCommandType, BiFunction<Map<String, Object>, ProviderContext, String> sqlFunction) {
		this.sqlCommandType = sqlCommandType;
		this.sqlFunction = sqlFunction;
	}


	public SqlCommandType sqlCommandType() {
		return sqlCommandType;
	}

	public BiFunction<Map<String, Object>, ProviderContext, String> sqlFunction() {
		return sqlFunction;
	}
}
