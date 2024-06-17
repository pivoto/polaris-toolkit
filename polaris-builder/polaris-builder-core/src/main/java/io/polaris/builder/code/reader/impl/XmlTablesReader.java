package io.polaris.builder.code.reader.impl;

import io.polaris.builder.code.dto.*;
import io.polaris.builder.code.reader.TablesReader;
import io.polaris.core.map.CaseInsensitiveMap;
import com.thoughtworks.xstream.XStream;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Qt
 */
public class XmlTablesReader implements TablesReader {

	Tables tables;

	public XmlTablesReader(File xmlDataFile) throws IOException {
		if (!xmlDataFile.exists()) {
			if (xmlDataFile.getParentFile() != null && !xmlDataFile.getParentFile().exists()) {
				xmlDataFile.getParentFile().mkdirs();
			}
			try (PrintWriter writer = new PrintWriter(xmlDataFile);) {
				writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				writer.println("<tables></tables>");
				writer.flush();
			}
		}
		this.tables = unmarshal(xmlDataFile);
	}

	protected XStream buildXStream() {
		XStream xs = new XStream();
		xs.ignoreUnknownElements();
		xs.autodetectAnnotations(true);
		xs.processAnnotations(Tables.class);
		xs.addDefaultImplementation(CaseInsensitiveMap.class, Map.class);
		return xs;
	}

	protected Tables unmarshal(File xmlDataFile) {
		Tables tables = new Tables();
		try (InputStream in = new FileInputStream(xmlDataFile)) {
			buildXStream().fromXML(in, tables);
		} catch (Exception e) {
		}
		return tables;
	}

	protected void marshal(Tables tables, File xmlDataFile) {
		try (Writer writer = new PrintWriter(xmlDataFile);) {
			buildXStream().toXML(tables, writer);
		} catch (Exception e) {
		}
	}

	@Override
	public TableDto read(String catalogName, String schemaName, String tableName) {
		if (tables != null) {
			CatalogDto catalog = tables.getCatalog(catalogName);
			if (catalog != null) {
				SchemaDto schema = catalog.getSchema(schemaName);
				if (schema != null) {
					TableDto table = schema.getTable(tableName);
					if (table != null) {
						table.setPkColumns(new ArrayList<>());
						table.setNormalColumns(new ArrayList<>());
						for (ColumnDto column : table.getColumns()) {
							column.prepare4Type();
							if (column.isPrimary()) {
								table.getPkColumns().add(column);
							} else {
								table.getNormalColumns().add(column);
							}
						}
						return table;
					}
				}
			}
		}
		return null;
	}

	@Override
	public void close() {
	}

}
