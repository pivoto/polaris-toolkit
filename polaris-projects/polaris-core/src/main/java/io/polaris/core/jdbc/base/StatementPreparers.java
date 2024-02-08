package io.polaris.core.jdbc.base;

import java.sql.Types;

/**
 * @author Qt
 * @since 1.8,  Feb 06, 2024
 */
public class StatementPreparers {

	public static StatementPreparer allOf(StatementPreparer... preparers) {
		return st -> {
			for (StatementPreparer preparer : preparers) {
				preparer.set(st);
			}
		};
	}

	public static StatementPreparer of(Iterable<?> parameters) {
		return st -> {
			int i = 1;
			for (Object o : parameters) {
				if (o == null) {
					st.setNull(i, Types.VARCHAR);
				} else {
					st.setObject(i, o);
				}
				i++;
			}
		};
	}

	public static StatementPreparer of(Object[] parameters) {
		return st -> {
			int i = 1;
			for (Object o : parameters) {
				if (o == null) {
					st.setNull(i, Types.VARCHAR);
				} else {
					st.setObject(i, o);
				}
				i++;
			}
		};
	}
}
