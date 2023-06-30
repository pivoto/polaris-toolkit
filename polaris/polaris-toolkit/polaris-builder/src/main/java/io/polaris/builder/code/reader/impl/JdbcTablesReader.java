package io.polaris.builder.code.reader.impl;

import io.polaris.builder.code.JdbcTypes;
import io.polaris.builder.code.dto.ColumnDto;
import io.polaris.builder.code.dto.TableDto;
import io.polaris.builder.code.reader.TablesReader;
import io.polaris.builder.dbv.cfg.Configurations;
import io.polaris.builder.dbv.cfg.DatabaseCfg;
import io.polaris.core.jdbc.Jdbcs;
import io.polaris.core.jdbc.dbv.Dbv;
import io.polaris.core.jdbc.dbv.model.Column;
import io.polaris.core.jdbc.dbv.model.Table;
import io.polaris.core.string.Strings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * @author Qt
 * @version Jul 11, 2021
 * @since 1.8
 */
public class JdbcTablesReader implements TablesReader {
	private Connection conn;
	private DatabaseCfg cfg;

	public JdbcTablesReader(File jdbcCfgFile) throws IOException {
		cfg = Configurations.getDatabaseCfg(new FileInputStream(jdbcCfgFile));
	}

	public JdbcTablesReader(InputStream jdbcCfgFileInput) throws IOException {
		cfg = Configurations.getDatabaseCfg(jdbcCfgFileInput);
	}

	@Override
	public TableDto read(String catalogName, String schemaName, String tableName) {
		try {
			if (conn == null || conn.isClosed()) {
				conn =Jdbcs.getConnection(cfg.getJdbcDriver(),cfg.getJdbcUrl(),cfg.getJdbcInfoProperties());
			}
			DatabaseMetaData metaData = conn.getMetaData();
			// 读取数据库中的表的元数据
			List<Table> tables = Dbv.readTables(metaData, catalogName, schemaName,
				tableName, null/*"TABLE"*/);
			if (tables != null && tables.size() > 0) {
				Table tab = tables.get(0);
				// 构造代码生成工具所需表结构明细的元数据对象
				TableDto table = new TableDto();
				table.setName(tab.getTableName());
				table.setComment(Strings.coalesce(tab.getRemarks(), tab.getTableName()));

				Set<String> pkColumns = tab.getPkColumns();
				List<Column> columnList = tab.getColumnList();
				for (Column col : columnList) {
					ColumnDto column = new ColumnDto();
					column.setName(col.getColumnName());
					column.setType(col.getDataType());
					column.setComment(Strings.coalesce(col.getRemarks(), col.getColumnName()));
					column.setDefaultValue(col.getColumnDef());
					column.setNullable(!col.isNotNull());
					column.setPrimary(col.isPrimaryKey());
					column.setColumnSize(col.getColumnSize());
					column.setDecimalDigits(col.getDecimalDigits());
					column.setAutoincrement(col.isAutoincrement());
					column.setGenerated(col.isGenerated());
					column.setJdbcType(JdbcTypes.getTypeName(col.getDataType()));
					column.setJavaType(JdbcTypes.getJavaType(col.getDataType(), col.getColumnSize(),col.getDecimalDigits()).getName());
					table.getColumns().add(column);
					if (pkColumns.contains(col.getColumnName())) {
						column.setPrimary(true);
						table.getPkColumns().add(column);
					} else {
						table.getNormalColumns().add(column);
					}
				}
				return table;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void close() {
		Jdbcs.close(conn);
	}
}
