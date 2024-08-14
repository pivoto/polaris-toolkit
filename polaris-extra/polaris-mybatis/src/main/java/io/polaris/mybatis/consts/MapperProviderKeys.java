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

	String updateEntityById = "updateEntityById";
	String updateEntityByAny = "updateEntityByAny";

	String existsEntityById = "existsEntityById";
	String existsEntity = "existsEntity";

	String selectEntityById = "selectEntityById";
	String selectEntity = "selectEntity";

	String countEntity = "countEntity";



}
