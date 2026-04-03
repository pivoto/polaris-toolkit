package io.polaris.mybatis.enhance.printer;

import java.util.List;

import io.polaris.core.jdbc.sql.PreparedSql;
import org.apache.ibatis.mapping.BoundSql;

/**
 * @author Qt
 * @since Apr 03, 2026
 */
public class SqlRecorders {

	public static  <M> M get(Class<M> mapperInterface) {
		return SqlRecorder.ofDefault().get(mapperInterface);
	}

	public static BoundSql pollFirstBoundSql() {
		return SqlRecorder.ofDefault().pollFirstBoundSql();
	}

	public static BoundSql pollLastBoundSql() {
		return SqlRecorder.ofDefault().pollLastBoundSql();
	}

	public static List<BoundSql> pollAllBoundSql() {
		return SqlRecorder.ofDefault().pollAllBoundSql();
	}

	public static PreparedSql pollFirstPreparedSql() {
		return SqlRecorder.ofDefault().pollFirstPreparedSql();
	}

	public static PreparedSql pollLastPreparedSql() {
		return SqlRecorder.ofDefault().pollLastPreparedSql();
	}

	public static List<PreparedSql> pollALlPreparedSql() {
		return SqlRecorder.ofDefault().pollALlPreparedSql();
	}

	public static String pollFirstSql() {
		return SqlRecorder.ofDefault().pollFirstSql();
	}

	public static String pollLastSql() {
		return SqlRecorder.ofDefault().pollLastSql();
	}

	public static List<String> pollALlSql() {
		return SqlRecorder.ofDefault().pollALlSql();
	}

	public static String pollALlSqlText() {
		return SqlRecorder.ofDefault().pollALlSqlText();
	}

	public static PreparedSql asPreparedSql(BoundSql boundSql) {
		return SqlRecorder.ofDefault().asPreparedSql(boundSql);
	}

	public static String asSqlString(BoundSql boundSql) {
		return SqlRecorder.ofDefault().asSqlString(boundSql);
	}

}
