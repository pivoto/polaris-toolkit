package io.polaris.builder.code;

import io.polaris.builder.code.annotation.*;
import io.polaris.builder.code.config.CodeEnv;
import io.polaris.builder.code.config.ConfigParser;
import io.polaris.builder.code.config.TypeMapping;
import io.polaris.core.collection.Iterables;
import io.polaris.core.env.GlobalStdEnv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.math.BigDecimal;
import java.sql.Types;

import static io.polaris.builder.code.annotation.Template.*;

/**
 * @author Qt
 * @since 1.8
 */
public class CodeTest {
	private String targetDir;

	@BeforeEach
	void beforeEach() {
		String dir = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
		targetDir = dir.replaceFirst("test-classes/$", "");
		System.setProperty("project.dir", targetDir);
		System.out.println("targetDir: " + targetDir);
		GlobalStdEnv.set("project.dir", targetDir);

		GlobalStdEnv.set("jdbc.driver", "oracle.jdbc.OracleDriver");

		GlobalStdEnv.set("jdbc.username", "cmis_config");
		GlobalStdEnv.set("jdbc.password", "cmis_config");
		GlobalStdEnv.set("jdbc.url", "jdbc:oracle:thin:@localhost:1521/cmisdb");

//		GlobalStdEnv.set("jdbc.url","jdbc:oracle:thin:@20.4.16.235:1521:cmisdb");
//		GlobalStdEnv.set("jdbc.username","cmis_config");
//		GlobalStdEnv.set("jdbc.password","cmis_config");

		GlobalStdEnv.set("code.schema", "CMIS_CONFIG");
		GlobalStdEnv.set("code.javaPackage", "io.polaris.jtc.rm.srm");
		GlobalStdEnv.set("code.frontendDir", "srm");
		GlobalStdEnv.set("code.mapperDir", "mapper/srm/base");
		GlobalStdEnv.set("code.modelPackage", "model");
		GlobalStdEnv.set("code.rpcPackage", "rpc");
		GlobalStdEnv.set("code.entityExtInterface", "io.polaris.framework.infra.entity.StandardEntity");
	}


	@Test
	void testXml() throws Exception {
		CodeEnv env = new CodeEnv();
		env.setMappings(Iterables.asSet(
			new TypeMapping(JdbcTypes.getTypeName(Types.DECIMAL), BigDecimal.class.getName())
			, new TypeMapping(JdbcTypes.getTypeName(Types.DECIMAL), BigDecimal.class.getName())
		));
		System.out.println(ConfigParser.buildXStream().toXML(env));
	}


	@Test
	void testGenerate() throws Exception {
		GlobalStdEnv.set("code.modelPackage", "model");
		GlobalStdEnv.set("code.rpcPackage", "rpc");
		Codes.generate(Config.class);
	}

	@Test
	void test01() {
		System.out.println(AnnotatedElementUtils.findMergedAnnotation(Config.class, CodeConfiguration.class));
		System.out.println(AnnotatedElementUtils.findMergedAnnotation(Config.class, DefaultTemplateExcludedPaths.class));
		System.out.println(AnnotatedElementUtils.findMergedAnnotation(Config.class, DefaultTemplateAdditional.class));
	}

	@CodeWithDefaults(
		outDir = "${project.dir}",
		jdbcDriver = "${jdbc.driver}",
		jdbcUrl = "${jdbc.url}",
		jdbcUsername = "${jdbc.username}", jdbcPassword = "${jdbc.password}",
		tables = {
			@Table(schema = "${code.schema}", name = "SRM_BIT_POS", javaPackage = "${code.javaPackage}"),
		},
		property = {
			@Property(key = "author", value = "Qt"),
			@Property(key = "rpcCodePrefix", value = "Jtc"),
			@Property(key = "mapperClassSuffix", value = "Mapper"),
			@Property(key = "mapperDir", value = "${code.mapperDir:mapper}"),
			@Property(key = "modelPackage", value = "${code.modelPackage:model}"),
			@Property(key = "rpcPackage", value = "${code.rpcPackage:rpc}"),
			@Property(key = "frontendDir", value = "${code.frontendDir:src/frontend}"),
			@Property(key = "entityExtInterface", value = "${code.entityExtInterface:io.polaris.framework.infra.entity.StandardEntity}"),
		},
		templates = {
//			@Template(path = Template.VM_PATH_EXAMPLE, filename = Template.FILENAME_EXAMPLE, dirname = Template.DIRNAME_EXAMPLE),
//			@Template(path = VM_PATH_ENTITY, filename = FILENAME_ENTITY, dirname = DIRNAME_ENTITY),
//
//			@Template(path = VM_PATH_MAPPER, filename = FILENAME_MAPPER, dirname = DIRNAME_MAPPER),
//			@Template(path = VM_PATH_MAPPER_XML, filename = FILENAME_MAPPER_XML, dirname = DIRNAME_MAPPER_XML),
//
//			@Template(path = VM_PATH_SERVICE, filename = FILENAME_SERVICE, dirname = DIRNAME_SERVICE),
//
//			@Template(path = VM_PATH_RPC, filename = FILENAME_RPC, dirname = DIRNAME_RPC),
//			@Template(path = VM_PATH_RPC_DML_INPUT, filename = FILENAME_RPC_DML_INPUT, dirname = DIRNAME_MODEL),
//			@Template(path = VM_PATH_RPC_GET_INPUT, filename = FILENAME_RPC_GET_INPUT, dirname = DIRNAME_MODEL),
//			@Template(path = VM_PATH_RPC_LIST_INPUT, filename = FILENAME_RPC_LIST_INPUT, dirname = DIRNAME_MODEL),
//			@Template(path = VM_PATH_RPC_GET_OUTPUT, filename = FILENAME_RPC_GET_OUTPUT, dirname = DIRNAME_MODEL),
//			@Template(path = VM_PATH_RPC_LIST_OUTPUT, filename = FILENAME_RPC_LIST_OUTPUT, dirname = DIRNAME_MODEL),
		},
		excludeTemplatePaths = {
			VM_PATH_FRONTEND_ROUTES,
			VM_PATH_FRONTEND_LIST_PAGE,
			VM_PATH_FRONTEND_ADD_PAGE,
			VM_PATH_FRONTEND_EDIT_PAGE,
		}
	)
	public static class Config {
	}

}
