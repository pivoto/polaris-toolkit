package io.polaris.builder.reader;

import io.polaris.builder.bean.db.Table;

/**
 * @author Qt
 */
public interface TablesReader {

	Table read(String catalogName, String schemaName, String tableName);

	void close();

}
