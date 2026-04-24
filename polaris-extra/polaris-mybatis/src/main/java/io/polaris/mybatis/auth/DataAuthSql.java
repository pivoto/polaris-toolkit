package io.polaris.mybatis.auth;

/**
 * @author Qt
 * @since Apr 24, 2026
 */
public class DataAuthSql {

	private boolean changed;
	private String rawSql;
	private String resolvedSql;

	public boolean isChanged() {
		return changed;
	}

	public String getRawSql() {
		return rawSql;
	}

	public String getResolvedSql() {
		return resolvedSql;
	}

	void setChanged(boolean changed) {
		this.changed = changed;
	}

	void setRawSql(String rawSql) {
		this.rawSql = rawSql;
	}

	void setResolvedSql(String resolvedSql) {
		this.resolvedSql = resolvedSql;
	}
}
