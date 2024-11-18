package io.polaris.mybatis.util;

import java.util.List;
import java.util.function.Supplier;

import io.polaris.core.jdbc.sql.query.Pageable;

/**
 * @author Qt
 * @since Sep 06, 2023
 */
public class Pages {
	static boolean hasPageHelper;

	static {
		try {
			// noinspection ResultOfMethodCallIgnored
			com.github.pagehelper.PageHelper.class.getName();
			hasPageHelper = true;
		} catch (Throwable e) {
			// no dependency
			hasPageHelper = false;
		}
	}

	public static boolean hasPageHelper() {
		return hasPageHelper;
	}

	public static <E> List<E> doPageQuery(Pageable pageable, Supplier<List<E>> select) {
		if (pageable == null || !hasPageHelper) {
			return select.get();
		}
		//noinspection resource
		try (com.github.pagehelper.Page<E> data = com.github.pagehelper.PageHelper.startPage(pageable.getPageNum(), pageable.getPageSize(), true).doSelectPage(select::get)) {
			pageable.setTotal((int) data.getTotal());
			return data;
		}
	}

	public static <E> List<E> doPageQueryWithoutCount(Pageable pageable, Supplier<List<E>> select) {
		if (pageable == null || !hasPageHelper) {
			return select.get();
		}
		// noinspection resource
		return com.github.pagehelper.PageHelper.startPage(pageable.getPageNum(), pageable.getPageSize(), false).doSelectPage(select::get);
	}

	public static long doPageCount(Pageable pageable, com.github.pagehelper.ISelect select) {
		if (!hasPageHelper) {
			return -1;
		}
		// noinspection resource
		return com.github.pagehelper.PageHelper.startPage(pageable.getPageNum(), pageable.getPageSize(), false)
			.doCount(select);
	}
}
