package io.polaris.core.jdbc.sql.query;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Qt
 * @since 1.8,  Aug 29, 2023
 */
@EqualsAndHashCode
@ToString
public class Pagination implements Pageable {
	private int pageNum;
	private int pageSize;
	private int total;
	private OrderBy orderBy;


	public Pagination() {
	}

	public Pagination(int pageNum, int pageSize) {
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}

	@Override
	public int getPageNum() {
		return pageNum;
	}

	@Override
	public int getPageSize() {
		return pageSize;
	}

	@Override
	public int getTotal() {
		return total;
	}

	@Override
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	@Override
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public void setTotal(int total) {
		this.total = total;
	}

	@Override
	public void setOrderBy(OrderBy orderBy) {
		this.orderBy = orderBy;
	}

	@Override
	public OrderBy getOrderBy() {
		return orderBy;
	}
}
