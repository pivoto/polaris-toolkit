package io.awesome.builder.reader.impl;

import com.thoughtworks.xstream.XStream;
import io.awesome.builder.reader.TablesReader;
import io.awesome.builder.bean.Tables;
import io.awesome.builder.bean.db.Catalog;
import io.awesome.builder.bean.db.Schema;
import io.awesome.builder.bean.db.Table;
import io.awesome.dbv.toolkit.MapKit;

import java.io.*;
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
		xs.addDefaultImplementation(MapKit.CaseInsensitiveMap.class, Map.class);
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
	public Table read(String catalogName, String schemaName, String tableName) {
		if (tables != null) {
			Catalog catalog = tables.getCatalog(catalogName);
			if (catalog != null) {
				Schema schema = catalog.getSchema(schemaName);
				if (schema != null) {
					Table table = schema.getTable(tableName);
					if (table != null) {
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
