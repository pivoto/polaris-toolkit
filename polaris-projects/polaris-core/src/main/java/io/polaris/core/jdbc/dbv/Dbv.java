package io.polaris.core.jdbc.dbv;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.polaris.core.jdbc.Jdbcs;
import io.polaris.core.jdbc.base.ResultRowSimpleMapper;
import io.polaris.core.jdbc.dbv.model.Catalog;
import io.polaris.core.jdbc.dbv.model.Column;
import io.polaris.core.jdbc.dbv.model.IndexInfo;
import io.polaris.core.jdbc.dbv.model.PrimaryKey;
import io.polaris.core.jdbc.dbv.model.Schema;
import io.polaris.core.jdbc.dbv.model.Table;
import io.polaris.core.jdbc.dbv.model.TableType;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8
 */
public class Dbv {
	private static final ILogger log = ILoggers.of(Dbv.class);

	public static <T> List<T> read(ResultSet rs, List<T> list, final Class<? extends T> clazz)
		throws InstantiationException, IllegalAccessException {
		try {
			DbvColumnFieldsMeta meta = DbvResultSetFetcher.getColumnFieldsMetadata(rs, clazz);
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
			if (Strings.isEmpty(catalog)){
				catalog = metaData.getConnection().getCatalog();
			}
			if (Strings.isEmpty(schema)){
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
				new ArrayList<Column>(50), Column.class);
			return list;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<Column>();
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
				if (index.getIndexName() == null || index.getIndexName().equals("")) {
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
			return new ArrayList<TableType>();
		}
	}

}
