package io.polaris.rdb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since  Apr 24, 2024
 */
public class OracleDDLTest {

	/**
	 * --输出信息采用缩排或换行格式化
	 * EXEC DBMS_METADATA.set_transform_param(DBMS_METADATA.session_transform, 'PRETTY', TRUE);
	 * --确保每个语句都带分号
	 * EXEC DBMS_METADATA.set_transform_param(DBMS_METADATA.session_transform, 'SQLTERMINATOR', TRUE);
	 * --关闭表索引、外键等关联（后面单独生成）
	 * EXEC DBMS_METADATA.set_transform_param(DBMS_METADATA.session_transform, 'CONSTRAINTS', FALSE);
	 * EXEC DBMS_METADATA.set_transform_param(DBMS_METADATA.session_transform, 'REF_CONSTRAINTS', FALSE);
	 * EXEC DBMS_METADATA.set_transform_param(DBMS_METADATA.session_transform, 'CONSTRAINTS_AS_ALTER', FALSE);
	 * --关闭存储、表空间属性
	 * EXEC DBMS_METADATA.set_transform_param(DBMS_METADATA.session_transform, 'STORAGE', FALSE);
	 * EXEC DBMS_METADATA.set_transform_param(DBMS_METADATA.session_transform, 'TABLESPACE', FALSE);
	 * --关闭创建表的PCTFREE、NOCOMPRESS等属性
	 * EXEC DBMS_METADATA.set_transform_param(DBMS_METADATA.session_transform, 'SEGMENT_ATTRIBUTES', FALSE);
	 *
	 * @throws SQLException
	 */
	@Test
	public void test01() throws SQLException {
		DataSource dataSource = OracleDataSources.buildDataSource();

		try (Connection conn = dataSource.getConnection();) {

			conn.setAutoCommit(false);

			Statement st = conn.createStatement();

			st.executeUpdate("begin " +
				"dbms_metadata.set_transform_param(dbms_metadata.session_transform, 'PRETTY', true);" +
				"DBMS_METADATA.set_transform_param(DBMS_METADATA.session_transform, 'SQLTERMINATOR', TRUE);" +
				"DBMS_METADATA.set_transform_param(DBMS_METADATA.session_transform, 'STORAGE', FALSE);" +
				"dbms_metadata.set_transform_param(dbms_metadata.session_transform, 'SEGMENT_ATTRIBUTES', FALSE);" +
				"DBMS_METADATA.set_transform_param(DBMS_METADATA.session_transform, 'TABLESPACE', true);" +
				" end;");

			ResultSet rs = st.executeQuery("select dbms_metadata.get_ddl('TABLE','JOB_INFO_CFG') from dual");
			rs.next();
			System.out.println(rs.getString(1));

		}
	}
}
