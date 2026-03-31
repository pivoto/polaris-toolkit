package io.polaris.mybatis.consts;

import java.util.Map;
import java.util.function.BiFunction;

import io.polaris.mybatis.enhance.EnhancedAutoMappers;
import io.polaris.mybatis.provider.*;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.mapping.SqlCommandType;

/**
 * @author Qt
 * @since Mar 29, 2026
 */
public enum AutoMapperType {

	selectAny(SqlCommandType.SELECT, EnhancedAutoMappers::selectAny),
	updateAny(SqlCommandType.UPDATE, EnhancedAutoMappers::updateAny),
	selectAnySql(SqlCommandType.SELECT, EnhancedAutoMappers::executeAnySql),
	updateAnySql(SqlCommandType.UPDATE, EnhancedAutoMappers::executeAnySql),

	insertBySql(SqlCommandType.INSERT, EnhancedAutoMappers::insertBySql),
	deleteBySql(SqlCommandType.DELETE, EnhancedAutoMappers::deleteBySql),
	updateBySql(SqlCommandType.UPDATE, EnhancedAutoMappers::updateBySql),
	selectBySql(SqlCommandType.SELECT, EnhancedAutoMappers::selectBySql),
	selectBySqlDirect(SqlCommandType.SELECT, EnhancedAutoMappers::selectBySqlDirect),
	selectBySqlExceptLogicDeleted(SqlCommandType.SELECT, EnhancedAutoMappers::selectBySqlExceptLogicDeleted),
	countBySql(SqlCommandType.SELECT, EnhancedAutoMappers::countBySql),


	countBySqlDirect(SqlCommandType.SELECT, EnhancedAutoMappers::countBySqlDirect),
	countBySqlExceptLogicDeleted(SqlCommandType.SELECT, EnhancedAutoMappers::countBySqlExceptLogicDeleted),
	existsBySql(SqlCommandType.SELECT, EnhancedAutoMappers::existsBySql),
	existsBySqlDirect(SqlCommandType.SELECT, EnhancedAutoMappers::existsBySqlDirect),
	existsBySqlExceptLogicDeleted(SqlCommandType.SELECT, EnhancedAutoMappers::existsBySqlExceptLogicDeleted),
	mergeBySql(SqlCommandType.UPDATE, EnhancedAutoMappers::mergeBySql),

	insertEntity(SqlCommandType.INSERT, EnhancedAutoMappers::insertEntity),

	deleteEntityById(SqlCommandType.DELETE, EnhancedAutoMappers::deleteEntityById),
	directDeleteEntityById(SqlCommandType.DELETE, EnhancedAutoMappers::directDeleteEntityById),
	logicDeleteEntityById(SqlCommandType.UPDATE, EnhancedAutoMappers::logicDeleteEntityById),
	deleteEntityByAny(SqlCommandType.DELETE, EnhancedAutoMappers::deleteEntityByAny),
	directDeleteEntityByAny(SqlCommandType.DELETE, EnhancedAutoMappers::directDeleteEntityByAny),
	logicDeleteEntityByAny(SqlCommandType.UPDATE, EnhancedAutoMappers::logicDeleteEntityByAny),

	updateEntityById(SqlCommandType.UPDATE, EnhancedAutoMappers::updateEntityById),
	updateEntityByAny(SqlCommandType.UPDATE, EnhancedAutoMappers::updateEntityByAny),

	existsEntity(SqlCommandType.SELECT, EnhancedAutoMappers::existsEntity),
	existsEntityDirect(SqlCommandType.SELECT, EnhancedAutoMappers::existsEntityDirect),
	existsEntityExceptLogicDeleted(SqlCommandType.SELECT, EnhancedAutoMappers::existsEntityExceptLogicDeleted),
	existsEntityById(SqlCommandType.SELECT, EnhancedAutoMappers::existsEntityById),
	existsEntityByIdDirect(SqlCommandType.SELECT, EnhancedAutoMappers::existsEntityByIdDirect),
	existsEntityByIdExceptLogicDeleted(SqlCommandType.SELECT, EnhancedAutoMappers::existsEntityByIdExceptLogicDeleted),

	selectEntity(SqlCommandType.SELECT, EnhancedAutoMappers::selectEntity),
	selectEntityDirect(SqlCommandType.SELECT, EnhancedAutoMappers::selectEntityDirect),
	selectEntityExceptLogicDeleted(SqlCommandType.SELECT, EnhancedAutoMappers::selectEntityExceptLogicDeleted),
	selectEntityById(SqlCommandType.SELECT, EnhancedAutoMappers::selectEntityById),
	selectEntityByIdDirect(SqlCommandType.SELECT, EnhancedAutoMappers::selectEntityByIdDirect),
	selectEntityByIdExceptLogicDeleted(SqlCommandType.SELECT, EnhancedAutoMappers::selectEntityByIdExceptLogicDeleted),

	countEntity(SqlCommandType.SELECT, EnhancedAutoMappers::countEntity),
	countEntityDirect(SqlCommandType.SELECT, EnhancedAutoMappers::countEntityDirect),
	countEntityExceptLogicDeleted(SqlCommandType.SELECT, EnhancedAutoMappers::countEntityExceptLogicDeleted),

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
