package io.polaris.core.jdbc.sql.query;

/**
 * 页号从1开始
 *
 * @author Qt
 * @since Aug 29, 2023
 */
public interface Pageable {

	int getPageNum();

	int getPageSize();

	int getTotal();

	void setPageNum(int pageNum);

	void setPageSize(int pageSize);

	void setTotal(int total);

	OrderBy getOrderBy();

	void setOrderBy(OrderBy orderBy);

	/**
	 * 获取起始数据下标，从0开始
	 *
	 * @return
	 */
	default int startResult() {
		return (getPageNum() - 1) * getPageSize();
	}

	/**
	 * 获取结束数据下标，从0开始
	 *
	 * @return
	 */
	default int endResult() {
		return getPageNum() * getPageSize() - 1;
	}

	/**
	 * 获取起始数据行号，从1开始
	 *
	 * @return
	 */
	default int startRow() {
		return (getPageNum() - 1) * getPageSize() + 1;
	}

	/**
	 * 获取结束数据行号，从1开始
	 *
	 * @return
	 */
	default int endRow() {
		return getPageNum() * getPageSize();
	}

	/**
	 * 创建分页对象
	 *
	 * @return
	 */
	static Pageable newInstance() {
		return new Pagination();
	}

	/**
	 * 创建分页对象
	 *
	 * @param pageNum  页号
	 * @param pageSize 页大小
	 * @return
	 */
	static Pageable newInstance(int pageNum, int pageSize) {
		return new Pagination(pageNum, pageSize);
	}

	/**
	 * 获取起始数据下标，从0开始
	 *
	 * @param page 分页对象
	 * @return
	 */
	static int getStartResult(Pageable page) {
		return (page.getPageNum() - 1) * page.getPageSize();
	}

	/**
	 * 获取结束数据下标，从0开始
	 *
	 * @param page 分页对象
	 * @return
	 */
	static int getEndResult(Pageable page) {
		return page.getPageNum() * page.getPageSize() - 1;
	}

	/**
	 * 获取起始数据行号，从1开始
	 *
	 * @param page 分页对象
	 * @return
	 */
	static int getStartRow(Pageable page) {
		return (page.getPageNum() - 1) * page.getPageSize() + 1;
	}

	/**
	 * 获取结束数据行号，从1开始
	 *
	 * @param page 分页对象
	 * @return
	 */
	static int getEndRow(Pageable page) {
		return page.getPageNum() * page.getPageSize();
	}

}
