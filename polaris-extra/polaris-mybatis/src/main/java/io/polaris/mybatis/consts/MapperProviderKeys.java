package io.polaris.mybatis.consts;

/**
 * @author Qt
 * @since  Aug 25, 2023
 */
public interface MapperProviderKeys {

	String executeAnySql = "executeAnySql";

	String insertBySql = "insertBySql";
	String deleteBySql = "deleteBySql";
	String updateBySql = "updateBySql";
	String selectBySql = "selectBySql";
	String countBySql = "countBySql";
	String existsBySql = "existsBySql";
	String mergeBySql = "mergeBySql";


	String insertEntity = "insertEntity";

	String deleteEntityById = "deleteEntityById";
	String deleteEntityDirectById = "deleteEntityDirectById";
	String deleteEntityLogicById = "logicDeleteEntityById";
	String deleteEntityByAny = "deleteEntityByAny";
	String deleteEntityDirectByAny = "deleteEntityDirectByAny";
	String deleteEntityLogicByAny = "logicDeleteEntityByAny";

	String updateEntityById = "updateEntityById";
	String updateEntityByAny = "updateEntityByAny";

	String existsEntityById = "existsEntityById";
	String existsEntityDirectById = "existsEntityDirectById";
	String existsEntityExceptLogicDeletedById = "existsEntityExceptLogicDeletedById";
	String existsEntity = "existsEntity";
	String existsEntityDirect = "existsEntityDirect";
	String existsEntityExceptLogicDeleted = "existsEntityExceptLogicDeleted";

	String selectEntityById = "selectEntityById";
	String selectEntityDirectById = "selectEntityDirectById";
	String selectEntityExceptLogicDeletedById = "selectEntityExceptLogicDeletedById";
	String selectEntity = "selectEntity";
	String selectEntityDirect = "selectEntityDirect";
	String selectEntityExceptLogicDeleted = "selectEntityExceptLogicDeleted";

	String countEntity = "countEntity";
	String countEntityDirect = "countEntityDirect";
	String countEntityExceptLogicDeleted = "countEntityExceptLogicDeleted";



}
