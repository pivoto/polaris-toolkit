package io.polaris.builder.code;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author Qt
 * @since 1.8
 */
public class Codes {

	/**
	 * @param codeXmlPath 代码生成配置文件
	 * @param jdbcXmlPath jdbc数据源配置
	 * @param dataXmlPath xml文件数据源配置
	 * @throws IOException
	 */
	public static void generate(String codeXmlPath, String jdbcXmlPath, String dataXmlPath) throws IOException {
		generator().codeXmlPath(codeXmlPath).jdbcXmlPath(jdbcXmlPath).dataXmlPath(dataXmlPath).generate();
	}

	public static CodeGenerator generator() {
		return new CodeGenerator();
	}

}
