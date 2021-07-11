package io.awesome.builder;

import io.awesome.builder.reader.TablesReader;
import io.awesome.builder.reader.TablesReaders;
import io.awesome.dbv.toolkit.IOKit;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.*;

/**
 * @author Qt
 * @version Jun 12, 2019
 * @since 1.8
 */
@Slf4j
public class Main {
	public static void main(String[] args) throws IOException {
		String userDir = System.getProperty("user.dir");
		System.out.println("当前运行目录: " + userDir);
		Arguments arguments = new Arguments();
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
				case "--jdbcCfg":
					i++;
					arguments.setJdbcCfg(args[i]);
					break;
				case "--xmlData":
					i++;
					arguments.setXmlData(args[i]);
					break;
				case "--codegen":
					i++;
					arguments.setCodegen(args[i]);
					break;
				default:
					throw new IllegalArgumentException(args[i]);
			}
		}
		generate(arguments);
		System.out.println("生成程序运行完毕, 请查看结果. 当前目录为: " + userDir);
	}

	private static void generate(Arguments arguments) throws IOException {
		InputStream jdbcInput = null;
		try {
			jdbcInput = IOKit.getInputStream(arguments.getJdbcCfg());
		} catch (FileNotFoundException e) {
			log.warn(e.getMessage(), e);
		}
		TablesReader tablesReader = null;
		if (jdbcInput == null) {
			if (StringUtils.isBlank(arguments.getXmlData()) || !new File(arguments.getXmlData()).isFile()) {
				throw new IllegalArgumentException("Jdbc连接配置与Xml数据源文件均不存在");
			} else {
				tablesReader = TablesReaders.newXmlTablesReader(new File(arguments.getXmlData()));
			}
		} else {
			if (StringUtils.isBlank(arguments.getXmlData())){
				tablesReader = TablesReaders.newJdbcTablesReader(jdbcInput);
			}else{
				File xmlData = new File(arguments.getXmlData());
				if (!xmlData.exists()) {
					xmlData.getAbsoluteFile().getParentFile().mkdirs();
					try(PrintWriter writer = new PrintWriter(xmlData);) {
						writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
						writer.println("<tables></tables>");
						writer.flush();
					}
				}
				if(xmlData.isFile()) {
					tablesReader = TablesReaders.newTablesReader(xmlData, jdbcInput);
				}else {
					tablesReader = TablesReaders.newJdbcTablesReader(jdbcInput);
				}
			}
		}
		try {
			CodeGenerator generator = new CodeGenerator(tablesReader, arguments.getCodegen());
			generator.generate();
		} finally {
			tablesReader.close();
		}
		/*File file = new File(arguments.getCodegen());
		if (file.isDirectory()) {
			for (File f : file.listFiles(f -> f.getName().endsWith(".xml"))) {
				CodeGenerator generator = new CodeGenerator(tablesReader, new FileInputStream(f));
				generator.generate();
			}
		} else if (file.isFile()) {
			CodeGenerator generator = new CodeGenerator(tablesReader, new FileInputStream(file));
			generator.generate();

		}*/
	}

	@Data
	@Accessors(chain = true)
	static class Arguments {
		String jdbcCfg;
		String xmlData;
		String codegen;
	}
}
