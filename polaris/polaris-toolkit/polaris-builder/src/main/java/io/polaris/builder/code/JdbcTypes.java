package io.polaris.builder.code;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class JdbcTypes {

	private static final Map<Integer, String> typeNames = new HashMap<>();
	private static final Map<String, Integer> typeValues = new HashMap<>();
	private static final Map<Integer, Class> javaTypes = new HashMap<>();

	static {
		try {
			Field[] fields = Types.class.getDeclaredFields();
			for (Field field : fields) {
				if (!Modifier.isStatic(field.getModifiers()) || field.getType() != int.class) {
					continue;
				}
				String name = field.getName();
				Integer val = (Integer) field.get(null);
				typeNames.put(val, name);
				typeValues.put(name, val);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		buildJavaTypes();
	}

	private static void buildJavaTypes() {
		javaTypes.put(Types.BIT, Boolean.class);
		javaTypes.put(Types.TINYINT, Byte.class);
		javaTypes.put(Types.SMALLINT, Short.class);
		javaTypes.put(Types.INTEGER, Integer.class);
		javaTypes.put(Types.BIGINT, Long.class);
		javaTypes.put(Types.FLOAT, Double.class);
		javaTypes.put(Types.REAL, Float.class); // NUMBER
		javaTypes.put(Types.DOUBLE, Double.class);
		javaTypes.put(Types.NUMERIC, BigDecimal.class);
		javaTypes.put(Types.DECIMAL, BigDecimal.class);
		javaTypes.put(Types.CHAR, String.class);
		javaTypes.put(Types.VARCHAR, String.class);
		javaTypes.put(Types.LONGVARCHAR, String.class);
		javaTypes.put(Types.DATE, java.util.Date.class);
		javaTypes.put(Types.TIME, java.sql.Time.class);
		javaTypes.put(Types.TIMESTAMP, java.sql.Timestamp.class);
		javaTypes.put(Types.BINARY, byte[].class);
		javaTypes.put(Types.VARBINARY, byte[].class);
		javaTypes.put(Types.LONGVARBINARY, byte[].class);
		javaTypes.put(Types.BLOB, byte[].class);
		javaTypes.put(Types.CLOB, String.class);
		javaTypes.put(Types.NCHAR, String.class);
		javaTypes.put(Types.NVARCHAR, String.class);
		javaTypes.put(Types.LONGNVARCHAR, String.class);
		javaTypes.put(Types.NCLOB, String.class);

		// experimental
		javaTypes.put(Types.STRUCT, java.sql.Struct.class);
		javaTypes.put(Types.REF, java.sql.Ref.class);
		javaTypes.put(Types.ARRAY, java.sql.Array.class);
		javaTypes.put(Types.ROWID, java.sql.RowId.class);
		javaTypes.put(Types.REF_CURSOR, java.sql.ResultSet.class);
	}

	public static Class getJavaType(int type) {
		return javaTypes.get(type);
	}

	public static Class getJavaType(String jdbcTypeName) {
		return javaTypes.get(getTypeValue(jdbcTypeName));
	}

	public static int getTypeValue(String jdbcTypeName) {
		return typeValues.getOrDefault(jdbcTypeName, Types.VARCHAR);
	}

	public static String getTypeName(int jdbcType) {
		return typeNames.getOrDefault(jdbcType, "VARCHAR");
	}
}
