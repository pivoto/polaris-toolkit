package io.polaris.mybatis.util;

import io.polaris.core.function.Visitable;
import io.polaris.core.io.IO;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.lang.Objs;
import io.polaris.core.lang.bean.Beans;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.type.Alias;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8,  Aug 28, 2023
 */
@Alias("mkit")
public class MybatisToolkit {
	public static int CURSOR_MAX_SIZE = 65533;

	public static boolean isNotEmpty(Object val) {
		return Objs.isNotEmpty(val);
	}

	public static boolean isLikeString(Object val) {
		if (val instanceof String) {
			return ((String) val).startsWith("%") || ((String) val).endsWith("%");
		}
		return false;
	}

	/**
	 * 是否有指定的级联属性
	 *
	 * @param _parameter
	 * @param propertyName 级联属性
	 * @return
	 */
	public static boolean hasPathProperty(Object _parameter, String propertyName) {
		try {
			Object val = Beans.getPathProperty(_parameter, propertyName);
			return isNotEmpty(val);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 是否有指定的属性
	 *
	 * @param _parameter
	 * @param propertyName 属性
	 * @return
	 */
	public static boolean hasProperty(Object _parameter, String propertyName) {
		try {
			Object val = Beans.getProperty(_parameter, propertyName);
			return isNotEmpty(val);
		} catch (Exception e) {
			return false;
		}
	}


	public static TableMeta getTableMeta(String entityClassName) {
		return EntityStatements.getTableMeta(entityClassName);
	}


	public static <E> Visitable<E> buildVisitable(Supplier<Cursor<E>> supplier) {
		return buildVisitable(supplier.get());
	}

	public static <E, V> Visitable<V> buildVisitable(Supplier<Cursor<E>> supplier, Function<E, V> converter) {
		return buildVisitable(supplier.get(), converter);
	}

	public static <E> Visitable<E> buildVisitable(Cursor<E> cursor) {
		return (c) -> {
			try {
				Iterator<E> iter = cursor.iterator();
				for (int i = 0; iter.hasNext() && i < CURSOR_MAX_SIZE; i++) {
					E o = iter.next();
					c.accept(o);
				}
			} finally {
				IO.close(cursor);
			}
		};
	}

	public static <E, V> Visitable<V> buildVisitable(Cursor<E> cursor, Function<E, V> converter) {
		return (c) -> {
			try {
				Iterator<E> iter = cursor.iterator();
				for (int i = 0; iter.hasNext() && i < CURSOR_MAX_SIZE; i++) {
					E o = iter.next();
					V v = converter.apply(o);
					c.accept(v);
				}
			} finally {
				IO.close(cursor);
			}
		};
	}

}
