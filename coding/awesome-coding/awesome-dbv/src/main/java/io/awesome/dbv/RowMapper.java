package io.awesome.dbv;

import java.sql.ResultSet;

/**
 * @author Qt
 */
public interface RowMapper<T> {

	/**
	 * row to object
	 * @param rs
	 * @return
	 * @throws DbvException
	 */
	T rowToObject(ResultSet rs) throws DbvException;
}
