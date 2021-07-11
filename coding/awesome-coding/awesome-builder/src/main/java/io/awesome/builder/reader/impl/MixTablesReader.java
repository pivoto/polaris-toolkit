package io.awesome.builder.reader.impl;

import io.awesome.builder.bean.Tables;
import io.awesome.builder.bean.db.Catalog;
import io.awesome.builder.bean.db.Schema;
import io.awesome.builder.bean.db.Table;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Qt
 */
public class MixTablesReader extends XmlTablesReader {

	private JdbcTablesReader jdbcTablesReader;
	private File xmlDataFile;

	public MixTablesReader(File xmlDataFile, File jdbcCfgFile) throws IOException {
		super(xmlDataFile);
		this.xmlDataFile = xmlDataFile;
		this.jdbcTablesReader = new JdbcTablesReader(jdbcCfgFile);
	}

	public MixTablesReader(File xmlDataFile, InputStream jdbcCfgFile) throws IOException {
		super(xmlDataFile);
		this.xmlDataFile = xmlDataFile;
		this.jdbcTablesReader = new JdbcTablesReader(jdbcCfgFile);
	}

	@Override
	public Table read(String catalogName, String schemaName, String tableName) {
		Table table = super.read(catalogName, schemaName, tableName);
		if (table == null) {
			table = jdbcTablesReader.read(catalogName, schemaName, tableName);
			if (table != null) {
				// 添加到xml中
				if (super.tables == null) {
					super.tables = new Tables();
				}
				Catalog catalog = super.tables.getCatalog(catalogName);
				if (catalog == null) {
					catalog = new Catalog();
					catalog.setName(catalogName);
					super.tables.addCatalog(catalog);
				}
				Schema schema = catalog.getSchema(schemaName);
				if (schema == null) {
					schema = new Schema();
					schema.setName(schemaName);
					catalog.addSchema(schema);
				}
				schema.addTable(table);
				return table;
			}
		}
		return table;
	}

	@Override
	public void close() {
		this.jdbcTablesReader.close();
		marshal(super.tables, xmlDataFile);
	}
}
