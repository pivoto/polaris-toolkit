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
	String deleteEntityByAny = "deleteEntityByAny";
	String logicDeleteEntityById = "logicDeleteEntityById";
	String logicDeleteEntityByAny = "logicDeleteEntityByAny";

	String updateEntityById = "updateEntityById";
	String updateEntityByAny = "updateEntityByAny";

	String existsEntityById = "existsEntityById";
	String existsEntityByIdExceptLogicDeleted = "existsEntityByIdExceptLogicDeleted";
	String existsEntity = "existsEntity";
	String existsEntityExceptLogicDeleted = "existsEntityExceptLogicDeleted";

	String selectEntityById = "selectEntityById";
	String selectEntityByIdExceptLogicDeleted = "selectEntityByIdExceptLogicDeleted";
	String selectEntity = "selectEntity";
	String selectEntityExceptLogicDeleted = "selectEntityExceptLogicDeleted";

	String countEntity = "countEntity";
	String countEntityExceptLogicDeleted = "countEntityExceptLogicDeleted";



}
