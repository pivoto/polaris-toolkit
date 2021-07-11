package io.awesome.dbv;

import io.awesome.dbv.cfg.Configurations;
import io.awesome.dbv.cfg.DatabaseCfg;
import io.awesome.dbv.model.*;
import io.awesome.dbv.toolkit.DBKit;
import io.awesome.dbv.toolkit.StringKit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Qt
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Dbv {

	public static String formatFieldName(String fieldName) {
		return StringKit.formatNameAsJavaStyle(fieldName);
	}

	public static Connection getConnection() throws DbvException {
		try {
			DatabaseCfg cfg = Configurations.getDatabaseCfg();
			return getConnection(cfg);
		} catch (Exception e) {
			throw new DbvException(e);
		}
	}

	public static Connection getConnection(DatabaseCfg cfg) throws SQLException {
		return DBKit.getConnection(cfg.getJdbcDriver(), cfg.getJdbcUrl(), cfg.getJdbcInfoProperties());
	}

	public static Connection getConnection(String cfgPath) throws DbvException {
		try {
			DatabaseCfg cfg = Configurations.getDatabaseCfg(cfgPath);
			return getConnection(cfg);
		} catch (Exception e) {
			throw new DbvException(e);
		}
	}

	public static <T> List<T> read(ResultSet rs, List<T> list, final Class<? extends T> clazz)
		throws InstantiationException, IllegalAccessException {
		try {
			ResultSetFetcher.ColumnFieldsMeta meta = ResultSetFetcher.getColumnFieldsMetadata(rs, clazz);
			while (rs.next()) {
				final T object = clazz.newInstance();
				ResultSetFetcher.fetch(meta, rs, object);
				list.add(object);
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} finally {
			DBKit.close(rs);
		}
		return list;
	}

	public static <T> List<T> read(ResultSet rs, List<T> list, RowMapper<T> mapper) {
		try {
			while (rs.next()) {
				list.add(mapper.rowToObject(rs));
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} finally {
			DBKit.close(rs);
		}
		return list;
	}

	public static List<Catalog> readCatalog(DatabaseMetaData metaData) throws DbvException {
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

	public static List<Table> readTablesWithoutIndexes(DatabaseMetaData metaData, String catalog, String schema,
			String tableName, String type) {
		try {
			List<Table> tables = new ArrayList<Table>(100);
			ResultSet rs = metaData.getTables(catalog, schema, tableName, StringKit.isEmpty(type) ? null
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
			ResultSet rs = metaData.getTables(StringKit.trimToNull(catalog), StringKit.trimToNull(schema), StringKit.trimToNull(tableName), StringKit.isEmpty(type) ? null
				: type.split("[,|;\\s]+"));
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
				new ArrayList<Column>(50), Column.class);
			return list;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<Column>();
		}
	}

	public static List<IndexInfo> readIndexes(DatabaseMetaData metaData, String catalog, String schema, String tableName) {
		try {
//			boolean approximate = false;
			boolean approximate = true;
			List<IndexInfo> indexes = read(
				metaData.getIndexInfo(catalog, schema, tableName, false, approximate),
				new ArrayList<IndexInfo>(20), IndexInfo.class);
			final Iterator<IndexInfo> iter = indexes.iterator();
			while (iter.hasNext()) {
				final IndexInfo index = iter.next();
				if (index.getIndexName() == null || index.getIndexName().equals("")) {
					iter.remove();
				}
			}
			return indexes;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<IndexInfo>();
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
			return new ArrayList<TableType>();
		}
	}


}
