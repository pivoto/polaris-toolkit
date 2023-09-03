package io.polaris.core.jdbc.sql.query;

/**
 * @author Qt
 * @since 1.8,  Aug 29, 2023
 */
public interface Pageable {

	static Pageable newInstance() {
		return new Pagination();
	}

	static Pageable newInstance(int pageNum, int pageSize) {
		return new Pagination(pageNum, pageSize);
	}

	int getPageNum();

	int getPageSize();

	int getTotal();

	void setPageNum(int pageNum);

	void setPageSize(int pageSize);

	void setTotal(int total);

	OrderBy getOrderBy();

	void setOrderBy(OrderBy orderBy);

}
