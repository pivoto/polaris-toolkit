package io.polaris.builder.code;


import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
@Slf4j
public class JdbcTypes {

	private static final Map<Integer, String> typeNames = new HashMap<>();
	private static final Map<String, Integer> typeValues = new HashMap<>();
	private static final Map<Integer, Class> javaTypes = new HashMap<>();
	private static final ThreadLocal<Deque<Map<Integer, Class>>> local = new ThreadLocal<>();

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

	public static void removeCustomMappings() {
		Deque<Map<Integer, Class>> queue = local.get();
		if (queue == null) {
			return;
		}
		queue.pollFirst();
		if (queue.isEmpty()) {
			local.remove();
		}
	}

	public static Map<Integer, Class> createCustomMappings() {
		Deque<Map<Integer, Class>> queue = local.get();
		if (queue == null) {
			local.set(queue = new LinkedList<>());
		}
		Map<Integer, Class> map;
		queue.offerFirst(map = new HashMap<>());
		return map;
	}

	public static boolean addCustomMapping(String jdbcType, String javaType) {
		Class c;
		try {
			c = Class.forName(javaType);
		} catch (ClassNotFoundException e) {
			if (!javaType.contains(".")) {
				try {
					c = Class.forName("java.lang." + javaType);
				} catch (ClassNotFoundException ex) {
					log.warn("", e);
					return false;
				}
			} else {
				log.warn("", e);
				return false;
			}
		}
		Integer type = getTypeValue(jdbcType);
		if (type == null) {
			return false;
		}
		return addCustomMapping(type, c);
	}

	public static boolean addCustomMapping(int jdbcType, Class javaType) {
		Deque<Map<Integer, Class>> queue = local.get();
		if (queue == null || queue.isEmpty()) {
			return false;
		}
		queue.peekFirst().put(jdbcType, javaType);
		return true;
	}

	public static Class getCustomJavaType(int type) {
		Deque<Map<Integer, Class>> queue = local.get();
		if (queue != null) {
			for (Iterator<Map<Integer, Class>> iter = queue.iterator(); iter.hasNext(); ) {
				Map<Integer, Class> next = iter.next();
				Class c = next.get(type);
				if (c != null) {
					return c;
				}
			}
		}
		return null;
	}

	public static Class getJavaType(int type) {
		return javaTypes.get(type);
	}
	public static Class getJavaType(int type, int columnSize, int decimalDigits) {
		Class c = javaTypes.get(type);
		if (c == BigDecimal.class) {
			if (decimalDigits == 0) {
				c = Long.class;
			}
		}
		return c;
	}

	public static Integer getTypeValue(String jdbcTypeName) {
		return typeValues.get(jdbcTypeName);
	}

	public static Integer getTypeValue(String jdbcTypeName, int defaultVal) {
		return typeValues.getOrDefault(jdbcTypeName, defaultVal);
	}

	public static String getTypeName(int jdbcType) {
		return typeNames.get(jdbcType);
	}

	public static String getTypeName(int jdbcType, String defaultVal) {
		return typeNames.getOrDefault(jdbcType, defaultVal);
	}
}
