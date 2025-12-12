package io.polaris.core.jdbc.base;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import io.polaris.core.jdbc.sql.query.Pageable;
import io.polaris.core.lang.bean.CaseModeOption;

/**
 * @author Qt
 * @since Feb 06, 2024
 */
public class ResultExtractors {

	private static final ResultMapExtractor<Map<String, Object>> MAP_EXTRACTOR = new ResultMapExtractor<>();
	private static final ResultMapListExtractor<Map<String, Object>> MAP_LIST_EXTRACTOR = new ResultMapListExtractor<>();


	// region map

	public static <C extends Collection<Map<String, Object>>> ResultExtractor<C> ofMapCollection(Supplier<C> collectionBuilder) {
		return new ResultMapCollectionExtractor<>(collectionBuilder);
	}

	public static ResultExtractor<List<Map<String, Object>>> ofMapList() {
		return MAP_LIST_EXTRACTOR;
	}

	public static ResultExtractor<Map<String, Object>> ofMap() {
		return MAP_EXTRACTOR;
	}

	public static <C extends Collection<T>, T extends Map<String, Object>> ResultExtractor<C> ofMapCollection(Supplier<C> collectionBuilder, Class<T> type) {
		return new ResultMapCollectionExtractor<>(collectionBuilder, type);
	}

	public static <T extends Map<String, Object>> ResultExtractor<List<T>> ofMapList(Class<T> type) {
		return new ResultMapListExtractor<>(type);
	}

	public static <T extends Map<String, Object>> ResultExtractor<T> ofMap(Class<T> type) {
		return new ResultMapExtractor<>(type);
	}

	// endregion map

	// region bean

	public static <C extends Collection<T>, T> ResultExtractor<C> ofBeanCollection(Supplier<C> collectionBuilder, Class<T> type) {
		return new ResultBeanCollectionExtractor<>(collectionBuilder, type);
	}

	public static <C extends Collection<T>, T> ResultExtractor<C> ofBeanCollection(Supplier<C> collectionBuilder, Type type) {
		return new ResultBeanCollectionExtractor<>(collectionBuilder, type);
	}

	public static <C extends Collection<T>, T> ResultExtractor<C> ofBeanCollection(Supplier<C> collectionBuilder, Class<T> type, CaseModeOption caseMode) {
		return new ResultBeanCollectionExtractor<>(collectionBuilder, type, caseMode);
	}

	public static <C extends Collection<T>, T> ResultExtractor<C> ofBeanCollection(Supplier<C> collectionBuilder, Type type, CaseModeOption caseMode) {
		return new ResultBeanCollectionExtractor<>(collectionBuilder, type, caseMode);
	}

	public static <T> ResultExtractor<List<T>> ofBeanList(Class<T> type) {
		return new ResultBeanListExtractor<>(type);
	}

	public static <T> ResultExtractor<List<T>> ofBeanList(Type type) {
		return new ResultBeanListExtractor<>(type);
	}

	public static <T> ResultExtractor<List<T>> ofBeanList(Class<T> type, CaseModeOption caseMode) {
		return new ResultBeanListExtractor<>(type, caseMode);
	}

	public static <T> ResultExtractor<List<T>> ofBeanList(Type type, CaseModeOption caseMode) {
		return new ResultBeanListExtractor<>(type, caseMode);
	}

	public static <T> ResultExtractor<T> ofBean(Class<T> type) {
		return new ResultBeanExtractor<>(type);
	}

	public static <T> ResultExtractor<T> ofBean(Type type) {
		return new ResultBeanExtractor<>(type);
	}

	public static <T> ResultExtractor<T> ofBean(Class<T> type, CaseModeOption caseMode) {
		return new ResultBeanExtractor<>(type, caseMode);
	}

	public static <T> ResultExtractor<T> ofBean(Type type, CaseModeOption caseMode) {
		return new ResultBeanExtractor<>(type, caseMode);
	}

	// endregion bean

	// region mapping

	public static <C extends Collection<T>, T> ResultExtractor<C> ofMappingCollection(Supplier<C> collectionBuilder, BeanMapping<T> mapping) {
		return new ResultBeanMappingCollectionExtractor<>(collectionBuilder, mapping);
	}

	public static <T> ResultExtractor<List<T>> ofMappingList(BeanMapping<T> mapping) {
		return new ResultBeanMappingListExtractor<>(mapping);
	}

	public static <T> ResultExtractor<T> ofMapping(BeanMapping<T> mapping) {
		return new ResultBeanMappingExtractor<>(mapping);
	}

	// endregion mapping

	// region mapper

	public static <T> ResultExtractor<List<T>> ofMapperList(ResultRowSimpleMapper<T> mapper) {
		return rs -> {
			List<T> list = new ArrayList<>();
			while (rs.next()) {
				list.add(mapper.map(rs));
			}
			return list;
		};
	}

	public static <T> ResultExtractor<T> ofMapper(ResultRowSimpleMapper<T> mapper) {
		return rs -> {
			if (rs.next()) {
				return mapper.map(rs);
			}
			return null;
		};
	}


	// endregion mapper

	// region single

	public static <C extends Collection<T>, T> ResultExtractor<C> ofSingleCollection(Supplier<C> collectionBuilder, Class<T> type) {
		return new ResultSingleCollectionExtractor<>(collectionBuilder, type);
	}

	public static <C extends Collection<T>, T> ResultExtractor<C> ofSingleCollection(Supplier<C> collectionBuilder, Type type) {
		return new ResultSingleCollectionExtractor<>(collectionBuilder, type);
	}

	public static <C extends Collection<T>, T> ResultExtractor<C> ofSingleCollection(Supplier<C> collectionBuilder) {
		return new ResultSingleCollectionExtractor<>(collectionBuilder, Object.class);
	}

	public static <T> ResultExtractor<List<T>> ofSingleList(Class<T> type) {
		return new ResultSingleListExtractor<>(type);
	}

	public static <T> ResultExtractor<List<T>> ofSingleList(Type type) {
		return new ResultSingleListExtractor<>(type);
	}

	public static <T> ResultExtractor<List<T>> ofSingleList() {
		return new ResultSingleListExtractor<>(Object.class);
	}

	public static <T> ResultExtractor<T> ofSingle(Class<T> type) {
		return new ResultSingleExtractor<>(type);
	}

	public static <T> ResultExtractor<T> ofSingle(Type type) {
		return new ResultSingleExtractor<>(type);
	}

	public static <T> ResultExtractor<Object> ofSingle() {
		return new ResultSingleExtractor<>(Object.class);
	}

	// endregion single


	public static <T> T extract(ResultSet rs, ResultRowMapper<T> mapper) throws SQLException {
		if (rs.next()) {
			String[] columns = ResultRowMappers.getColumns(rs);
			return mapper.map(rs, columns);
		}
		return null;
	}

	public static <T> T extract(ResultSet rs, ResultRowMapper<T> mapper, String[] columns) throws SQLException {
		if (rs.next()) {
			return mapper.map(rs, columns);
		}
		return null;
	}


	public static <T> void extract(ResultSet rs, ResultRowMapper<T> mapper, Collection<T> list) throws SQLException {
		String[] columns = ResultRowMappers.getColumns(rs);
		extract(rs, mapper, list, columns);
	}

	public static <T> void extract(ResultSet rs, ResultRowMapper<T> mapper, Collection<T> list, String[] columns) throws SQLException {
		while (rs.next()) {
			T map = mapper.map(rs, columns);
			list.add(map);
		}
	}

	public static <T> void extract(ResultSet rs, ResultRowMapper<T> mapper, Collection<T> list, Pageable pageable) throws SQLException {
		String[] columns = ResultRowMappers.getColumns(rs);
		extract(rs, mapper, list, pageable, columns);
	}

	public static <T> void extract(ResultSet rs, ResultRowMapper<T> mapper, Collection<T> list, Pageable pageable, String[] columns) throws SQLException {
		if (pageable != null) {
			int start = pageable.startResult();
			int size = pageable.getPageSize();
			for (int i = 0; i < start; i++) {
				if (!rs.next()) {
					break;
				}
			}
			for (int i = 0; i < size; i++) {
				if (!rs.next()) {
					break;
				}
				T map = mapper.map(rs, columns);
				list.add(map);
			}
		} else {
			while (rs.next()) {
				T map = mapper.map(rs, columns);
				list.add(map);
			}
		}
	}
}
