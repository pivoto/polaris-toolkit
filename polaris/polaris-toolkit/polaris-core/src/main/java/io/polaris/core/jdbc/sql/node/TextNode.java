package io.polaris.core.jdbc.sql.node;

import io.polaris.core.jdbc.sql.BoundSql;
import io.polaris.core.jdbc.sql.PreparedSql;
import io.polaris.core.tuple.Tuple2;
import io.polaris.core.tuple.Tuples;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Aug 11, 2023
 */
public class TextNode implements SqlNode {
	/** SQL语句块 */
	private String text;

	public TextNode(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public PreparedSql asPreparedSql() {
		return new PreparedSql(text, Collections.emptyList());
	}

	@Override
	public BoundSql asBoundSql(VarNameGenerator generator, String openVarToken, String closeVarToken) {
		return new BoundSql(text, Collections.emptyMap());
	}

	@Override
	public boolean isTextNode() {
		return true;
	}


}

