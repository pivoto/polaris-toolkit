package io.polaris.core.jdbc.dialect;

import io.polaris.core.jdbc.sql.query.Pageable;

/**
 * @author Qt
 * @since Mar 05, 2025
 */
public abstract class BaseDialect implements Dialect {


	protected int getStart(int pageNum, int pageSize) {
		return pageNum > 0 ? (pageNum - 1) * pageSize : 0;
	}

	protected int getEnd(int pageNum, int pageSize) {
		return pageNum > 0 ? pageNum * pageSize : pageSize;
	}


	protected int getStart(Pageable pageable) {
		return pageable.startResult();
	}

	protected int getEnd(Pageable pageable) {
		return pageable.endResult();
	}

}
