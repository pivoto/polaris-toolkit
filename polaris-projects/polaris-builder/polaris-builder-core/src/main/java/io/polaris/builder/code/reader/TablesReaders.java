package io.polaris.builder.code.reader;

import io.polaris.builder.code.reader.impl.JdbcTablesReader;
import io.polaris.builder.code.reader.impl.MixTablesReader;
import io.polaris.builder.code.reader.impl.XmlTablesReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Qt
 * @version Jun 04, 2019
 */
public class TablesReaders {

	public static TablesReader newXmlTablesReader(File xmlDataFile) throws IOException {
		return new XmlTablesReader(xmlDataFile);
	}

	public static TablesReader newJdbcTablesReader(File jdbcCfgFile) throws IOException {
		return new JdbcTablesReader(jdbcCfgFile);
	}

	public static TablesReader newJdbcTablesReader(InputStream jdbcCfg) throws IOException {
		return new JdbcTablesReader(jdbcCfg);
	}

	public static TablesReader newTablesReader(File xmlDataFile, File jdbcCfgFile) throws IOException {
		return new MixTablesReader(xmlDataFile, jdbcCfgFile);
	}

	public static TablesReader newTablesReader(File xmlDataFile, InputStream jdbcCfg) throws IOException {
		return new MixTablesReader(xmlDataFile, jdbcCfg);
	}
}
