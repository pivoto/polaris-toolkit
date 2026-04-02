package io.polaris.mybatis.consts;

/**
 * @author Qt
 * @see AutoMapperType
 * @since Aug 25, 2023
 */
public interface MapperProviderKeys {

	String provideSql = "provideSql";

	String selectAny = "selectAny";
	String updateAny = "updateAny";
	String executeAnySql = "executeAnySql";
	String selectAnySql = "selectAnySql";
	String updateAnySql = "updateAnySql";

	String insertBySql = "insertBySql";
	String deleteBySql = "deleteBySql";
	String updateBySql = "updateBySql";
	String selectBySql = "selectBySql";
	String selectBySqlDirect = "selectBySqlDirect";
	String selectBySqlExceptLogicDeleted = "selectBySqlExceptLogicDeleted";
	String countBySql = "countBySql";
	String countBySqlDirect = "countBySqlDirect";
	String countBySqlExceptLogicDeleted = "countBySqlExceptLogicDeleted";
	String existsBySql = "existsBySql";
	String existsBySqlDirect = "existsBySqlDirect";
	String existsBySqlExceptLogicDeleted = "existsBySqlExceptLogicDeleted";
	String mergeBySql = "mergeBySql";


	String insertEntity = "insertEntity";

	String deleteEntityById = "deleteEntityById";
	String deleteEntityByAny = "deleteEntityByAny";
	String directDeleteEntityById = "directDeleteEntityById";
	String directDeleteEntityByAny = "directDeleteEntityByAny";
	String logicDeleteEntityById = "logicDeleteEntityById";
	String logicDeleteEntityByAny = "logicDeleteEntityByAny";

	String updateEntityById = "updateEntityById";
	String updateEntityByAny = "updateEntityByAny";

	String existsEntityById = "existsEntityById";
	String existsEntityByIdDirect = "existsEntityByIdDirect";
	String existsEntityByIdExceptLogicDeleted = "existsEntityByIdExceptLogicDeleted";
	String existsEntity = "existsEntity";
	String existsEntityDirect = "existsEntityDirect";
	String existsEntityExceptLogicDeleted = "existsEntityExceptLogicDeleted";

	String selectEntityById = "selectEntityById";
	String selectEntityByIdDirect = "selectEntityByIdDirect";
	String selectEntityByIdExceptLogicDeleted = "selectEntityByIdExceptLogicDeleted";
	String selectEntity = "selectEntity";
	String selectEntityDirect = "selectEntityDirect";
	String selectEntityExceptLogicDeleted = "selectEntityExceptLogicDeleted";

	String countEntity = "countEntity";
	String countEntityDirect = "countEntityDirect";
	String countEntityExceptLogicDeleted = "countEntityExceptLogicDeleted";


}
