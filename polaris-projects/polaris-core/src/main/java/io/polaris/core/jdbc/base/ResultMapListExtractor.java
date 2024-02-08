package io.polaris.core.jdbc.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultMapListExtractor<T extends Map<String, Object>> extends ResultMapCollectionExtractor<List<T>, T> {

	public ResultMapListExtractor(Class<T> mapType) {
		super(ArrayList::new, mapType);
	}

	public ResultMapListExtractor() {
		super(ArrayList::new);
	}

}
