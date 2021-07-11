package io.awesome.builder.reader;

import io.awesome.builder.bean.db.Table;

/**
 * @author Qt
 */
public interface TablesReader {

	Table read(String catalogName, String schemaName, String tableName);

	void close();

}
