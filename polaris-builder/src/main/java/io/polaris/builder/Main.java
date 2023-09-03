package io.polaris.builder;

import io.polaris.builder.code.Codes;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.IOException;

/**
 * @author Qt
 * @version Jun 12, 2019
 * @since 1.8
 */
public class Main {
	public static void main(String[] args) throws IOException {
		String userDir = System.getProperty("user.dir");
		System.out.println("当前运行目录: " + userDir);
		Arguments arguments = new Arguments();
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
				case "--database":
					i++;
					arguments.setJdbcCfg(args[i]);
					break;
				case "--metadata":
					i++;
					arguments.setXmlData(args[i]);
					break;
				case "--code":
					i++;
					arguments.setCodegen(args[i]);
					break;
				default:
					throw new IllegalArgumentException(args[i]);
			}
		}
		Codes.generate(arguments.getCodegen(), arguments.getJdbcCfg(), arguments.getXmlData());
		System.out.println("生成程序运行完毕, 请查看结果. 当前目录为: " + userDir);
	}

	@Data
	@Accessors(chain = true)
	static class Arguments {
		String jdbcCfg;
		String xmlData;
		String codegen;
	}
}
