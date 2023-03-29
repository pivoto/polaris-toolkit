package io.polaris.builder.code.reader.impl;

import io.polaris.builder.code.dto.Tables;
import io.polaris.builder.code.dto.CatalogDto;
import io.polaris.builder.code.dto.SchemaDto;
import io.polaris.builder.code.dto.TableDto;

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
	public TableDto read(String catalogName, String schemaName, String tableName) {
		TableDto table = super.read(catalogName, schemaName, tableName);
		if (table == null) {
			table = jdbcTablesReader.read(catalogName, schemaName, tableName);
			if (table != null) {
				// 添加到xml中
				if (super.tables == null) {
					super.tables = new Tables();
				}
				CatalogDto catalog = super.tables.getCatalog(catalogName);
				if (catalog == null) {
					catalog = new CatalogDto();
					catalog.setName(catalogName);
					super.tables.addCatalog(catalog);
				}
				SchemaDto schema = catalog.getSchema(schemaName);
				if (schema == null) {
					schema = new SchemaDto();
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
