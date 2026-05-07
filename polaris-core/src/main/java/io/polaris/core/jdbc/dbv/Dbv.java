package io.polaris.core.jdbc.dbv;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.polaris.core.jdbc.JdbcDriver;
import io.polaris.core.jdbc.JdbcException;
import io.polaris.core.jdbc.Jdbcs;
import io.polaris.core.jdbc.base.ResultRowSimpleMapper;
import io.polaris.core.jdbc.dbv.model.Catalog;
import io.polaris.core.jdbc.dbv.model.Column;
import io.polaris.core.jdbc.dbv.model.IndexInfo;
import io.polaris.core.jdbc.dbv.model.PrimaryKey;
import io.polaris.core.jdbc.dbv.model.Schema;
import io.polaris.core.jdbc.dbv.model.Table;
import io.polaris.core.jdbc.dbv.model.TableType;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.string.Strings;
import io.polaris.core.tuple.Tuple;
import io.polaris.core.tuple.Tuples;

/**
 * @author Qt
 */
public class Dbv {
	private static final Logger log = Loggers.of(Dbv.class);

	public static <T> List<T> read(ResultSet rs, List<T> list, final Class<? extends T> clazz)
		throws InstantiationException, IllegalAccessException {
		try {
			DbvColumnFieldsMeta meta = DbvColumnFieldsMeta.of(rs, clazz);
			while (rs.next()) {
				final T object = clazz.newInstance();
				DbvResultSetFetcher.fetch(meta, rs, object);
				list.add(object);
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} finally {
			Jdbcs.close(rs);
		}
		return list;
	}


	public static <T> List<T> read(ResultSet rs, List<T> list, ResultRowSimpleMapper<T> mapper) {
		try {
			while (rs.next()) {
				list.add(mapper.map(rs));
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} finally {
			Jdbcs.close(rs);
		}
		return list;
	}

	public static List<Catalog> readCatalog(DatabaseMetaData metaData) throws JdbcException {
		try {
			List<Catalog> list = new ArrayList<Catalog>(20);
			read(metaData.getCatalogs(), list, Catalog.class);
			return list;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<Catalog>();
		}
	}

	public static List<Schema> readSchemas(DatabaseMetaData metaData) {
		try {
			List<Schema> list = new ArrayList<Schema>(30);
			read(metaData.getSchemas(), list, Schema.class);
			return list;
		} catch (Exception e) {
			return new ArrayList<Schema>();
		}
	}

	public static List<Table> readTablesWithoutIndexes(DatabaseMetaData metaData,
		String catalog, String schema,
		String tableName, String type) {
		try {
			List<Table> tables = new ArrayList<Table>(100);
			ResultSet rs = metaData.getTables(catalog, schema, tableName, Strings.isEmpty(type) ? null
				: type.split("[,|;\\s]+"));
			read(rs, tables, Table.class);

			for (final Table table : tables) {

				List<PrimaryKey> primaryKeys = readPrimaryKeys(metaData, table);
				table.setPrimaryKeyList(primaryKeys);

				table.setColumnList(readColumns(metaData, table.getTableCatalog(), table.getTableSchema(),
					table.getTableName(), null));

				table.fit();
			}

			return tables;
		} catch (Exception e) {
			return new ArrayList<Table>();
		}
	}

	public static List<Table> readTables(DatabaseMetaData metaData, String catalog, String schema, String tableName, String type) {
		try {
			List<Table> tables = new ArrayList<>(100);
			if (Strings.isEmpty(catalog)) {
				catalog = metaData.getConnection().getCatalog();
			}
			if (Strings.isEmpty(schema)) {
				schema = metaData.getConnection().getSchema();
			}

			ResultSet rs = metaData.getTables(Strings.trimToNull(catalog),
				Strings.trimToNull(schema),
				Strings.trimToNull(tableName),
				Strings.isEmpty(type) ? null : type.split("[,|;\\s]+"));
			read(rs, tables, Table.class);

			for (final Table table : tables) {

				List<PrimaryKey> primaryKeys = readPrimaryKeys(metaData, table);
				table.setPrimaryKeyList(primaryKeys);

				List<IndexInfo> indexes = readIndexes(metaData, table.getTableCatalog(),
					table.getTableSchema(), table.getTableName());
				table.setIndexInfoList(indexes);

				table.setColumnList(readColumns(metaData, table.getTableCatalog(), table.getTableSchema(),
					table.getTableName(), null));

				table.fit();
			}

			return tables;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<Table>();
		}
	}

	public static List<Column> readColumns(DatabaseMetaData metaData, String catalog, String schema, String tableName, String columnName) {
		try {
			List<Column> list = read(metaData.getColumns(catalog, schema, tableName, columnName),
				new ArrayList<>(50), Column.class);

			fixTableColumns(metaData, catalog, schema, tableName, columnName, list);

			return list;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<Column>();
		}
	}

	private static void fixTableColumns(DatabaseMetaData metaData, String catalog, String schema, String tableName, String columnName, List<Column> columns) {
		try {
			JdbcDriver jdbcDriver = JdbcDriver.parse(metaData.getURL());
			if (jdbcDriver == JdbcDriver.ORACLE) {
				int databaseMajorVersion = metaData.getDatabaseMajorVersion();
				// oracle 12以下的无法获取虚拟列信息，这里对12以下进行修复，并忽略过程中的错误
				if (databaseMajorVersion < 12) {
					Connection conn = metaData.getConnection();
					StringBuilder sb = new StringBuilder();
					sb.append("SELECT T.OWNER AS TABLE_SCHEM,")
						.append("T.TABLE_NAME AS TABLE_NAME,")
						.append("T.COLUMN_NAME AS COLUMN_NAME,")
						.append("T.DATA_DEFAULT AS COLUMN_DEF,")
						.append("T.VIRTUAL_COLUMN AS IS_GENERATEDCOLUMN")
						.append(" FROM ALL_TAB_COLS T");
					boolean first = true;
					List<String> params = new ArrayList<>();
					if (Strings.isNotBlank(schema)) {
						if (first) {
							sb.append(" WHERE");
						}
						first = false;
						sb.append(" T.OWNER = ?");
						params.add(schema);
					}
					if (Strings.isNotBlank(tableName)) {
						if (first) {
							sb.append(" WHERE");
						} else {
							sb.append(" AND");
						}
						first = false;
						sb.append(" T.TABLE_NAME = ?");
						params.add(tableName);
					}
					if (Strings.isNotBlank(columnName)) {
						if (first) {
							sb.append(" WHERE");
						} else {
							sb.append(" AND");
						}
						first = false;
						sb.append(" T.COLUMN_NAME = ?");
						params.add(columnName);
					}
					try (PreparedStatement st = conn.prepareStatement(sb.toString());) {
						for (int i = 0; i < params.size(); i++) {
							st.setString(i + 1, params.get(i));
						}
						ResultSet rs = st.executeQuery();
						List<Column> fixedList = read(rs, new ArrayList<>(columns.size()), Column.class);

						Map<Tuple, Column> columnMap = new HashMap<>();
						for (Column column : columns) {
							columnMap.put(Tuples.of(column.getSchema(), column.getTableName(), column.getColumnName()), column);
						}
						for (Column fixedColumn : fixedList) {
							Column column = columnMap.get(Tuples.of(fixedColumn.getSchema(), fixedColumn.getTableName(), fixedColumn.getColumnName()));
							if (Strings.isBlank(column.getColumnDef())) {
								column.setColumnDef(fixedColumn.getColumnDef());
							}
							if (Strings.isBlank(column.getIsGeneratedcolumn())) {
								column.setIsGeneratedcolumn(fixedColumn.getIsGeneratedcolumn());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static List<IndexInfo> readIndexes(DatabaseMetaData metaData, String catalog, String schema, String tableName) {
		try {
			// 如果为true，则允许结果反映近似值或超出数据值; 如果为false，则要求结果准确
			boolean approximate = true;
			List<IndexInfo> indexes = read(
				metaData.getIndexInfo(catalog, schema, tableName, false, approximate),
				new ArrayList<>(20), IndexInfo.class);
			final Iterator<IndexInfo> iter = indexes.iterator();
			while (iter.hasNext()) {
				final IndexInfo index = iter.next();
				if (index.getIndexName() == null || index.getIndexName().isEmpty()) {
					iter.remove();
				}
			}
			return indexes;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	public static List<PrimaryKey> readPrimaryKeys(DatabaseMetaData metaData, final Table table) {
		try {
			List<PrimaryKey> primaryKeys = new ArrayList<>();
			ResultSet rs = metaData.getPrimaryKeys(table.getTableCatalog(), table.getTableSchema(),
				table.getTableName());
			read(rs, primaryKeys, PrimaryKey.class);
			return primaryKeys;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<PrimaryKey>();
		}
	}

	public static List<TableType> readTableTypes(DatabaseMetaData metaData) {
		try {
			List<TableType> list = new ArrayList<>(10);
			read(metaData.getTableTypes(), list, TableType.class);
			return list;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<>();
		}
	}

}
