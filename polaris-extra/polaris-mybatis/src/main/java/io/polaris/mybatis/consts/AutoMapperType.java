package io.polaris.mybatis.consts;

import java.util.Map;
import java.util.function.BiFunction;

import io.polaris.mybatis.enhance.EnhancedAutoMappers;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.mapping.SqlCommandType;

/**
 * @author Qt
 * @since Mar 29, 2026
 */
public enum AutoMapperType {

	/** 支持通过 @EntitySelect、@SqlSelect、@SqlSelectSet、@SqlRawSimple、@SqlRaw 等注解以生成自动语句 */
	selectAny(SqlCommandType.SELECT, EnhancedAutoMappers::selectAny),
	/** 支持通过 @EntityInsert、@EntityDelete、@EntityUpdate、@EntityMerge、@SqlInsert、@SqlDelete、@SqlUpdate、@SqlRawSimple、@SqlRaw 等注解以生成自动语句 */
	updateAny(SqlCommandType.UPDATE, EnhancedAutoMappers::updateAny),
	/** 支持通过参数指定任意查询语句以直接使用 */
	selectAnySql(SqlCommandType.SELECT, EnhancedAutoMappers::executeAnySql),
	/** 支持通过参数指定任意DML语句以直接使用 */
	updateAnySql(SqlCommandType.UPDATE, EnhancedAutoMappers::executeAnySql),
	/** 支持通过参数指定任意 InsertStatement 以直接使用 */
	insertBySql(SqlCommandType.INSERT, EnhancedAutoMappers::insertBySql),
	/** 支持通过参数指定任意 DeleteStatement 以直接使用 */
	deleteBySql(SqlCommandType.DELETE, EnhancedAutoMappers::deleteBySql),
	/** 支持通过参数指定任意 UpdateStatement 以直接使用 */
	updateBySql(SqlCommandType.UPDATE, EnhancedAutoMappers::updateBySql),
	/** 支持通过参数指定任意 SelectStatement 以直接使用 */
	selectBySql(SqlCommandType.SELECT, EnhancedAutoMappers::selectBySql),
	/** 支持通过参数指定任意 SelectStatement 以直接使用 */
	selectBySqlDirect(SqlCommandType.SELECT, EnhancedAutoMappers::selectBySqlDirect),
	/** 支持通过参数指定任意 SelectStatement 以直接使用 */
	selectBySqlExceptLogicDeleted(SqlCommandType.SELECT, EnhancedAutoMappers::selectBySqlExceptLogicDeleted),
	/** 支持通过参数指定任意 SelectStatement 以直接使用 */
	countBySql(SqlCommandType.SELECT, EnhancedAutoMappers::countBySql),
	/** 支持通过参数指定任意 SelectStatement 以直接使用 */
	countBySqlDirect(SqlCommandType.SELECT, EnhancedAutoMappers::countBySqlDirect),
	/** 支持通过参数指定任意 SelectStatement 以直接使用 */
	countBySqlExceptLogicDeleted(SqlCommandType.SELECT, EnhancedAutoMappers::countBySqlExceptLogicDeleted),
	/** 支持通过参数指定任意 SelectStatement 以直接使用 */
	existsBySql(SqlCommandType.SELECT, EnhancedAutoMappers::existsBySql),
	/** 支持通过参数指定任意 SelectStatement 以直接使用 */
	existsBySqlDirect(SqlCommandType.SELECT, EnhancedAutoMappers::existsBySqlDirect),
	/** 支持通过参数指定任意 SelectStatement 以直接使用 */
	existsBySqlExceptLogicDeleted(SqlCommandType.SELECT, EnhancedAutoMappers::existsBySqlExceptLogicDeleted),
	/** 支持通过参数指定任意 MergeStatement 以直接使用 */
	mergeBySql(SqlCommandType.UPDATE, EnhancedAutoMappers::mergeBySql),

	/** 支持自动生成实体的 Insert 语句 */
	insertEntity(SqlCommandType.INSERT, EnhancedAutoMappers::insertEntity),

	/** 支持自动生成实体的 Delete 语句，按主键条件删除 */
	deleteEntityById(SqlCommandType.DELETE, EnhancedAutoMappers::deleteEntityById),
	/** 支持自动生成实体的 Delete 语句，按主键条件删除 */
	directDeleteEntityById(SqlCommandType.DELETE, EnhancedAutoMappers::directDeleteEntityById),
	/** 支持自动生成实体的 Delete 语句，按主键条件删除 */
	logicDeleteEntityById(SqlCommandType.UPDATE, EnhancedAutoMappers::logicDeleteEntityById),
	/** 支持自动生成实体的 Delete 语句，按任意条件删除 */
	deleteEntityByAny(SqlCommandType.DELETE, EnhancedAutoMappers::deleteEntityByAny),
	/** 支持自动生成实体的 Delete 语句，按任意条件删除 */
	directDeleteEntityByAny(SqlCommandType.DELETE, EnhancedAutoMappers::directDeleteEntityByAny),
	/** 支持自动生成实体的 Delete 语句，按任意条件删除 */
	logicDeleteEntityByAny(SqlCommandType.UPDATE, EnhancedAutoMappers::logicDeleteEntityByAny),

	/** 支持自动生成实体的 Update 语句，按主键条件更新 */
	updateEntityById(SqlCommandType.UPDATE, EnhancedAutoMappers::updateEntityById),
	/** 支持自动生成实体的 Update 语句，按任意条件更新 */
	updateEntityByAny(SqlCommandType.UPDATE, EnhancedAutoMappers::updateEntityByAny),

	/** 支持自动生成实体的 Select 语句，按任意条件查询，根据方法注解或传入条件决定是否排除逻辑删除数据 */
	existsEntity(SqlCommandType.SELECT, EnhancedAutoMappers::existsEntity),
	/** 支持自动生成实体的 Select 语句，按任意条件查询，不排除逻辑删除 */
	existsEntityDirect(SqlCommandType.SELECT, EnhancedAutoMappers::existsEntityDirect),
	/** 支持自动生成实体的 Select 语句，按任意条件查询，排除逻辑删除 */
	existsEntityExceptLogicDeleted(SqlCommandType.SELECT, EnhancedAutoMappers::existsEntityExceptLogicDeleted),
	/** 支持自动生成实体的 Select 语句，按主键条件查询，根据方法注解或传入条件决定是否排除逻辑删除数据 */
	existsEntityById(SqlCommandType.SELECT, EnhancedAutoMappers::existsEntityById),
	/** 支持自动生成实体的 Select 语句，按主键条件查询，不排除逻辑删除 */
	existsEntityByIdDirect(SqlCommandType.SELECT, EnhancedAutoMappers::existsEntityByIdDirect),
	/** 支持自动生成实体的 Select 语句，按主键条件查询，排除逻辑删除 */
	existsEntityByIdExceptLogicDeleted(SqlCommandType.SELECT, EnhancedAutoMappers::existsEntityByIdExceptLogicDeleted),

	/** 支持自动生成实体的 Select 语句，按任意条件查询，根据方法注解或传入条件决定是否排除逻辑删除数据 */
	selectEntity(SqlCommandType.SELECT, EnhancedAutoMappers::selectEntity),
	/** 支持自动生成实体的 Select 语句，按任意条件查询，不排除逻辑删除 */
	selectEntityDirect(SqlCommandType.SELECT, EnhancedAutoMappers::selectEntityDirect),
	/** 支持自动生成实体的 Select 语句，按任意条件查询，排除逻辑删除 */
	selectEntityExceptLogicDeleted(SqlCommandType.SELECT, EnhancedAutoMappers::selectEntityExceptLogicDeleted),
	/** 支持自动生成实体的 Select 语句，按主键条件查询，根据方法注解或传入条件决定是否排除逻辑删除数据 */
	selectEntityById(SqlCommandType.SELECT, EnhancedAutoMappers::selectEntityById),
	/** 支持自动生成实体的 Select 语句，按主键条件查询，不排除逻辑删除 */
	selectEntityByIdDirect(SqlCommandType.SELECT, EnhancedAutoMappers::selectEntityByIdDirect),
	/** 支持自动生成实体的 Select 语句，按主键条件查询，排除逻辑删除 */
	selectEntityByIdExceptLogicDeleted(SqlCommandType.SELECT, EnhancedAutoMappers::selectEntityByIdExceptLogicDeleted),

	/** 支持自动生成实体的 Select 语句，按任意条件查询，根据方法注解或传入条件决定是否排除逻辑删除数据 */
	countEntity(SqlCommandType.SELECT, EnhancedAutoMappers::countEntity),
	/** 支持自动生成实体的 Select 语句，按任意条件查询，不排除逻辑删除 */
	countEntityDirect(SqlCommandType.SELECT, EnhancedAutoMappers::countEntityDirect),
	/** 支持自动生成实体的 Select 语句，按任意条件查询，排除逻辑删除 */
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
