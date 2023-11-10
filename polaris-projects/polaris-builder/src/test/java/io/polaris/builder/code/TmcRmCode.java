package io.polaris.builder.code;

import io.polaris.builder.code.annotation.Code;
import io.polaris.builder.code.annotation.Column;
import io.polaris.builder.code.annotation.Property;
import io.polaris.builder.code.annotation.Table;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.polaris.builder.code.annotation.Template.*;

/**
 * @author Qt
 * @since 1.8,  Sep 05, 2023
 */
public class TmcRmCode {

	@Test
	public void run() throws IOException {
//		String dir = AppCodeGen.class.getProtectionDomain().getCodeSource().getLocation().getFile();
//		System.setProperty("project.dir", dir.replaceFirst("/target/test-classes/?$", ""));
		System.setProperty("project.dir", "D:/xcode/jcfc/base/basic/tmc/tmc-rm");
		System.out.println("project.dir: " + System.getProperty("project.dir"));
//		Codes.generate(BrmConfig.class);
//		Codes.generate(SrmConfig.class);
		Codes.generate(SrmConfig.class, "SRM_ARCH_SYSTEM");
		Codes.generate(BrmConfig.class, "BRM_TENANT_ROLE_MANAGER");
	}


	@Code(
		outDir = "${sys['project.dir']}",
		jdbcDriver = "oracle.jdbc.OracleDriver",
		jdbcUrl = "jdbc:oracle:thin:@localhost:1521/cmisdb",
		jdbcUsername = "basesv", jdbcPassword = "basesv",
		tables = {
			@Table(schema = "BASESV", name = "SRM_BIT_POS", javaPackage = "io.polaris.tmc.srm"),
			@Table(schema = "BASESV", name = "SRM_RES_APP", javaPackage = "io.polaris.tmc.srm"),
			@Table(schema = "BASESV", name = "SRM_RES_RPC", javaPackage = "io.polaris.tmc.srm"),
			@Table(schema = "BASESV", name = "SRM_RES_RPC_GROUP", javaPackage = "io.polaris.tmc.srm"),
			@Table(schema = "BASESV", name = "SRM_RES_RPC_GROUP_ITEM", javaPackage = "io.polaris.tmc.srm"),
			@Table(schema = "BASESV", name = "SRM_RES_MQ", javaPackage = "io.polaris.tmc.srm"),
			@Table(schema = "BASESV", name = "SRM_RES_DOC", javaPackage = "io.polaris.tmc.srm"),

			@Table(schema = "BASESV", name = "SRM_AUTHZ_APP", javaPackage = "io.polaris.tmc.srm"),
			@Table(schema = "BASESV", name = "SRM_AUTHZ_APP_MQ", javaPackage = "io.polaris.tmc.srm"),
			@Table(schema = "BASESV", name = "SRM_AUTHZ_APP_RPC", javaPackage = "io.polaris.tmc.srm"),
			@Table(schema = "BASESV", name = "SRM_AUTHZ_APP_RPC_GROUP", javaPackage = "io.polaris.tmc.srm"),
			@Table(schema = "BASESV", name = "SRM_LOG_USER_OPER", javaPackage = "io.polaris.tmc.srm"),

			@Table(schema = "BASESV", name = "SRM_ARCH_SYSTEM_DOMAIN", javaPackage = "io.polaris.tmc.srm"),
			@Table(schema = "BASESV", name = "SRM_ARCH_SYSTEM", javaPackage = "io.polaris.tmc.srm"),
			@Table(schema = "BASESV", name = "SRM_ARCH_NET_ZONE", javaPackage = "io.polaris.tmc.srm"),

			@Table(schema = "BASESV", name = "SRM_AUTHZ_RPC_RPC", javaPackage = "io.polaris.tmc.srm"),
			@Table(schema = "BASESV", name = "SRM_AUTHZ_RPC_RPC_GROUP", javaPackage = "io.polaris.tmc.srm"),

			@Table(schema = "BASESV", name = "SRM_OPENDOC_APP", javaPackage = "io.polaris.tmc.srm"),
			@Table(schema = "BASESV", name = "SRM_ARCH_SYSTEM_MODULE", javaPackage = "io.polaris.tmc.srm"),
			@Table(schema = "BASESV", name = "SRM_ARCH_MACHINE", javaPackage = "io.polaris.tmc.srm"),
			@Table(schema = "BASESV", name = "SRM_ARCH_MACHINE_APP", javaPackage = "io.polaris.tmc.srm"),
			@Table(schema = "BASESV", name = "SRM_ARCH_SYSTEM_CATEGORY", javaPackage = "io.polaris.tmc.srm"),
			@Table(schema = "BASESV", name = "SRM_ARCH_NET_ZONE_REL", javaPackage = "io.polaris.tmc.srm"),
		},
		property = {
			@Property(key = "author", value = "Qt"),
			@Property(key = "rpcCodePrefix", value = "Tmc"),
			@Property(key = "mapperDir", value = "mapper/srm/base"),
			@Property(key = "modelPackage", value = "model.nature"),
			@Property(key = "rpcPackage", value = "rpc.nature"),
//			@Property(key = "mapperClassSuffix", value = "Mapper"),
			@Property(key = "entityExtInterface", value = "io.polaris.sdk.tmc.common.entity.TranslatedCreatable, io.polaris.sdk.tmc.common.entity.TranslatedUpdatable"),
		},
		excludeTemplatePaths = {
			VM_PATH_FRONTEND_ROUTES,
			VM_PATH_FRONTEND_LIST_PAGE,
			VM_PATH_FRONTEND_ADD_PAGE,
			VM_PATH_FRONTEND_EDIT_PAGE,
//		},
//		templates = {
//			@Template(path = VM_PATH_SERVICE_EXPORTABLE, filename = FILENAME_SERVICE, dirname = DIRNAME_SERVICE),
		}
	)
	public static class SrmConfig {
	}

	@Code(
		outDir = "${sys['project.dir']}",
		jdbcDriver = "oracle.jdbc.OracleDriver",
		jdbcUrl = "jdbc:oracle:thin:@localhost:1521/cmisdb",
		jdbcUsername = "basesv", jdbcPassword = "basesv",
		tables = {
			@Table(schema = "BASESV", name = "BRM_BCH", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_BIT_POS", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_INSTU", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT_BCH_ROLE", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT_HOME", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT_MENU", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT_MENU_DATA", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT_MENU_FUNC", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT_MENU_RPC", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT_MENU_RPC_GROUP", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT_ROLE", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT_ROLE_MENU", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT_ROLE_MENU_DATA", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT_ROLE_MENU_FUNC", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT_ROLE_MUTEX", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT_USER_AUTHC", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT_USER_HOME", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT_USER_INFO", javaPackage = "io.polaris.tmc.brm",
				columns = {
					@Column(name = "LOGIN_FAIL_COUNT", javaType = "java.lang.Integer")
				}),
			@Table(schema = "BASESV", name = "BRM_TENANT_USER_ROLE", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT_USER_SHORTCUT", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_USER", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_USER_SESSION", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_USER_TENANT_LIMIT", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_LOG_USER_AUTHC", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_LOG_USER_OPER", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TEMPLATE_MENU", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TEMPLATE_MENU_FUNC", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT_MANAGER", javaPackage = "io.polaris.tmc.brm"),
			@Table(schema = "BASESV", name = "BRM_TENANT_ROLE_MANAGER", javaPackage = "io.polaris.tmc.brm"),
		},
		property = {
			@Property(key = "author", value = "Qt"),
			@Property(key = "rpcCodePrefix", value = "Tmc"),
			@Property(key = "mapperDir", value = "mapper/brm/base"),
			@Property(key = "modelPackage", value = "model.nature"),
			@Property(key = "rpcPackage", value = "rpc.nature"),
//			@Property(key = "mapperClassSuffix", value = "Mapper"),
			@Property(key = "entityExtInterface", value = "io.polaris.sdk.tmc.common.entity.TranslatedCreatable, io.polaris.sdk.tmc.common.entity.TranslatedUpdatable"),
		},
		excludeTemplatePaths = {
			VM_PATH_RPC,
			VM_PATH_RPC_EXPORTABLE,
			VM_PATH_RPC_DML_INPUT,
			VM_PATH_RPC_GET_INPUT,
			VM_PATH_RPC_LIST_INPUT,
			VM_PATH_RPC_GET_OUTPUT,
			VM_PATH_RPC_LIST_OUTPUT,

			VM_PATH_FRONTEND_ROUTES,
			VM_PATH_FRONTEND_LIST_PAGE,
			VM_PATH_FRONTEND_ADD_PAGE,
			VM_PATH_FRONTEND_EDIT_PAGE,
//		},
//		templates = {
//			@Template(path = VM_PATH_SERVICE_EXPORTABLE, filename = FILENAME_SERVICE, dirname = DIRNAME_SERVICE),
		}
	)
	public static class BrmConfig {
	}
}
