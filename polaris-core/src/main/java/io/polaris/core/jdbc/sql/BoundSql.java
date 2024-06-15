package io.polaris.core.jdbc.sql;

import io.polaris.core.consts.SymbolConsts;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Map;

/**
 * @author Qt
 * @since  Aug 12, 2023
 */
@EqualsAndHashCode
@Getter
@Setter
public class BoundSql {
	public static final BoundSql EMPTY = new BoundSql(SymbolConsts.EMPTY, Collections.emptyMap());
	private String text;
	private Map<String, Object> bindings;

	public BoundSql() {
	}

	public BoundSql(String text, Map<String, Object> bindings) {
		this.text = text;
		this.bindings = bindings;
	}

	@Override
	public String toString() {
		return text + "\n" + bindings;
	}
}
