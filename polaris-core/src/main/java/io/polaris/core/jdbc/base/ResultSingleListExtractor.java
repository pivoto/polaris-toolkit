package io.polaris.core.jdbc.base;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultSingleListExtractor extends ResultSingleCollectionExtractor<List<Object>> {

	public ResultSingleListExtractor() {
		super(ArrayList::new);
	}

	public ResultSingleListExtractor(Type type) {
		super(ArrayList::new, type);
	}

}
