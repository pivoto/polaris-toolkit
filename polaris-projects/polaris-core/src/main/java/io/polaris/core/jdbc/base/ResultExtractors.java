package io.polaris.core.jdbc.base;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Feb 06, 2024
 */
public class ResultExtractors {

	public static final ResultMapExtractor<Map<String, Object>> MAP_EXTRACTOR = new ResultMapExtractor<>();
	public static final ResultMapListExtractor<Map<String, Object>> MAP_LIST_EXTRACTOR = new ResultMapListExtractor<>();

	public static ResultExtractor<Map<String, Object>> ofMap() {
		return MAP_EXTRACTOR;
	}

	public static ResultExtractor<List<Map<String, Object>>> ofMapList() {
		return MAP_LIST_EXTRACTOR;
	}

	public static <T extends Map<String, Object>> ResultExtractor<T> ofMap(Class<T> type) {
		return new ResultMapExtractor<>(type);
	}

	public static <T extends Map<String, Object>> ResultExtractor<List<T>> ofMapList(Class<T> type) {
		return new ResultMapListExtractor<>(type);
	}

	public static <T> ResultExtractor<T> ofBean(Class<T> type) {
		return new ResultBeanExtractor<>(type);
	}

	public static <T> ResultExtractor<T> ofBean(Type type) {
		return new ResultBeanExtractor<>(type);
	}

	public static <T> ResultExtractor<T> ofBean(Class<T> type, boolean caseInsensitive, boolean caseCamel) {
		return new ResultBeanExtractor<>(type, caseInsensitive, caseCamel);
	}

	public static <T> ResultExtractor<T> ofBean(Type type, boolean caseInsensitive, boolean caseCamel) {
		return new ResultBeanExtractor<>(type, caseInsensitive, caseCamel);
	}

	public static <T> ResultExtractor<List<T>> ofBeanList(Class<T> type) {
		return new ResultBeanListExtractor<>(type);
	}

	public static <T> ResultExtractor<List<T>> ofBeanList(Type type) {
		return new ResultBeanListExtractor<>(type);
	}

	public static <T> ResultExtractor<List<T>> ofBeanList(Class<T> type, boolean caseInsensitive, boolean caseCamel) {
		return new ResultBeanListExtractor<>(type, caseInsensitive, caseCamel);
	}

	public static <T> ResultExtractor<List<T>> ofBeanList(Type type, boolean caseInsensitive, boolean caseCamel) {
		return new ResultBeanListExtractor<>(type, caseInsensitive, caseCamel);
	}

	public static <T> ResultExtractor<T> ofMapping(BeanMapping<T> mapping) {
		return new ResultBeanMappingExtractor<>(mapping);
	}

	public static <T> ResultExtractor<List<T>> ofMappingList(BeanMapping<T> mapping) {
		return new ResultBeanMappingListExtractor<>(mapping);
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
}
