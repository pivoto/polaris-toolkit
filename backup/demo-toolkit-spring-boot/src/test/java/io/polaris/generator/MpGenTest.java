package io.polaris.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.Collections;

/**
 * @author Qt
 * @since 1.8
 */
public class MpGenTest {
	public static final String URL = "jdbc:mysql://localhost:3306/dev_plat?useSSL=false&useUnicode=true&characterEncoding=UTF8&serverTimezone=Asia/Shanghai";
	public static final String USER = "dev";
	public static final String PASSWORD = "dev";
	private File baseDir;

	/*

	<dependency>
		<groupId>com.baomidou</groupId>
		<artifactId>mybatis-plus-generator</artifactId>
		<version>3.5.2</version>
	</dependency>
	 */

	@BeforeEach
	void beforeEach() {
		final java.net.URL location = getClass().getProtectionDomain().getCodeSource().getLocation();
		this.baseDir = new File(location.getFile().replaceFirst("/target/test-classes/", ""));
	}

	@Test
	void test02() {
		System.out.println(baseDir.getAbsoluteFile());
	}

	@Test
	void test01() {
		String dir = baseDir.getAbsoluteFile() + "/target/generated-sources/";
		FastAutoGenerator.create(URL, USER, PASSWORD)
			.globalConfig(builder ->
					builder.author("Qt") // 设置作者
						.dateType(DateType.SQL_PACK)
						.commentDate(() -> "1.8")
//					.enableSwagger() // 开启 swagger 模式
						.fileOverride() // 覆盖已生成文件
						.disableOpenDir()
						.outputDir(dir) // 指定输出目录
			)
			.packageConfig(builder ->
				builder.parent("com.minzhang.plat.storage") // 设置父包名
					.moduleName("dev") // 设置父包模块名
					.pathInfo(Collections.singletonMap(OutputFile.xml, dir + "mapper/")) // 设置mapperXml生成路径
			)
			.strategyConfig(builder ->
				builder.addInclude("dev_project", "dev_config", "dev_user", "dev_role", "dev_user_role", "dev_user_project", "dev_api_file") // 设置需要生成的表名
					.addTablePrefix("t_", "c_") // 设置过滤表前缀
			)
//			.templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
			.execute();
	}

}
