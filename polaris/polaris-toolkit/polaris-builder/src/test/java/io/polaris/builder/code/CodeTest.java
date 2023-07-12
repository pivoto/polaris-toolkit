package io.polaris.builder.code;

import io.polaris.builder.code.annotation.Code;
import io.polaris.builder.code.annotation.Table;
import io.polaris.builder.code.annotation.Template;
import io.polaris.builder.code.config.CodeEnv;
import io.polaris.builder.code.config.ConfigParser;
import io.polaris.builder.code.config.TypeMapping;
import io.polaris.core.collection.Iterables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
		System.out.println("targetDir: " + targetDir);
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
		Codes.generate(Config.class);
	}


	@Code(
		outDir = "D:/xcode/jcfc/base/basic/tmc-rm",
		jdbcDriver = "oracle.jdbc.OracleDriver",
		jdbcUrl = "jdbc:oracle:thin:@localhost:1521/cmisdb",
		jdbcUsername = "basesv", jdbcPassword = "basesv",
		tables = {
			@Table(schema = "BASESV", name = "ARM_BCH", javaPackage = "io.polaris.tmc.rm.arm")
		},
		templates = {
//			@Template(path = Template.VM_PATH_ENTITY_FLUENT_MYBATIS, filename = Template.FILENAME_ENTITY, dirname = Template.DIRNAME_ENTITY),
//			@Template(path = Template.VM_PATH_EXAMPLE, filename = Template.FILENAME_EXAMPLE, dirname = Template.DIRNAME_EXAMPLE),
		}
	)
	public static class Config {
	}

}
