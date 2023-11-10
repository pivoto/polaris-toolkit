package io.polaris.mybatis.util;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import io.polaris.core.jdbc.sql.query.Pageable;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8,  Sep 06, 2023
 */
public class Pages {

	public static <E> List<E> doPageQuery(Pageable pageable, Supplier<List<E>> select) {
		if (pageable == null) {
			return select.get();
		}
		com.github.pagehelper.Page<E> data = PageHelper.startPage(pageable.getPageNum(), pageable.getPageSize(), true).doSelectPage(select::get);
		pageable.setTotal((int) data.getTotal());
		return data;
	}

	public static <E> List<E> doPageQueryWithoutCount(Pageable pageable, Supplier<List<E>> select) {
		if (pageable == null) {
			return select.get();
		}
		return PageHelper.startPage(pageable.getPageNum(), pageable.getPageSize(), false).doSelectPage(select::get);
	}

	public static long doPageCount(Pageable papageable, ISelect select) {
		return PageHelper.startPage(papageable.getPageNum(), papageable.getPageSize(), false)
			.doCount(select);
	}
}
