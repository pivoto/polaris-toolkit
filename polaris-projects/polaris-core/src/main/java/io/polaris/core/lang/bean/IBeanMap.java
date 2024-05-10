package io.polaris.core.lang.bean;

import java.util.Map;

/**
 * @author Qt
 * @since  Aug 07, 2023
 */
public interface IBeanMap<T> extends Map<String, Object> {

	<V> V copyToBean(V bean);

	Map<String, Object> copyToMap(Map<String, Object> map);

	Map<String, Object> copyToMap();

	T getBean();

}
