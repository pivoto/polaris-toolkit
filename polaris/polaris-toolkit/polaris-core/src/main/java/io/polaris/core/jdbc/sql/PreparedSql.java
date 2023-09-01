package io.polaris.core.jdbc.sql;

import io.polaris.core.consts.SymbolConsts;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

/**
 * @author Qt
 * @since 1.8,  Aug 12, 2023
 */
@EqualsAndHashCode
@Getter
@Setter
public class PreparedSql {
	public static final PreparedSql EMPTY = new PreparedSql(SymbolConsts.EMPTY, Collections.emptyList());
	private String text;
	private List<Object> bindings;

	public PreparedSql() {
	}

	public PreparedSql(String text, List<Object> bindings) {
		this.text = text;
		this.bindings = bindings;
	}

	@Override
	public String toString() {
		return text + "\nbindings: " + bindings;
	}
}
