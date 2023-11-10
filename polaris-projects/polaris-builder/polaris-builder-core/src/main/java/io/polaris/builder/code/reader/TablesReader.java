package io.polaris.builder.code.reader;

import io.polaris.builder.code.dto.TableDto;

/**
 * @author Qt
 */
public interface TablesReader {

	TableDto read(String catalogName, String schemaName, String tableName);

	void close();

}
