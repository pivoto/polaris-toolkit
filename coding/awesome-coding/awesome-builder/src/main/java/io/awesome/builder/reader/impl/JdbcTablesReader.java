package io.awesome.builder.reader.impl;

import io.awesome.builder.reader.TablesReader;
import io.awesome.builder.bean.db.Column;
import io.awesome.builder.bean.db.Table;
import io.awesome.dbv.Dbv;
import io.awesome.dbv.cfg.Configurations;
import io.awesome.dbv.cfg.DatabaseCfg;
import io.awesome.dbv.toolkit.DBKit;
import io.awesome.dbv.toolkit.StringKit;

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
	public Table read(String catalogName, String schemaName, String tableName) {
		try {
			if (conn == null || conn.isClosed()) {
				conn = Dbv.getConnection(cfg);
			}
			DatabaseMetaData metaData = conn.getMetaData();
			// 读取数据库中的表的元数据
			List<io.awesome.dbv.model.Table> tables = Dbv.readTables(metaData, catalogName, schemaName,
					tableName, null/*"TABLE"*/);
			if (tables != null && tables.size() > 0) {
				io.awesome.dbv.model.Table tab = tables.get(0);
				// 构造代码生成工具所需表结构明细的元数据对象
				Table table = new Table();
				table.setName(tab.getTableName());
				table.setComment(StringKit.coalesce(tab.getRemarks(), tab.getTableName()));

				Set<String> pkColumns = tab.getPkColumns();
				List<io.awesome.dbv.model.Column> columnList = tab.getColumnList();
				for (io.awesome.dbv.model.Column col : columnList) {
					Column column = new Column();
					column.setName(col.getColumnName());
					column.setType(col.getDataType());
					column.setComment(StringKit.coalesce(col.getRemarks(), col.getColumnName()));
					column.setDefaultValue(col.getColumnDef());
					column.setNullable(!col.isNotNull());
					column.setPrimary(col.isPrimaryKey());
					column.setColumnSize(col.getColumnSize());
					column.setDecimalDigits(col.getDecimalDigits());
					column.setAutoincrement(col.isAutoincrement());
					column.setGenerated(col.isGenerated());
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
		DBKit.close(conn);
	}
}
