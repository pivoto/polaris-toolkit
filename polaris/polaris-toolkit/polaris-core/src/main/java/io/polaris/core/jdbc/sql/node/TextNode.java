package io.polaris.core.jdbc.sql.node;

import io.polaris.core.jdbc.sql.BoundSql;
import io.polaris.core.jdbc.sql.PreparedSql;

import java.util.Collections;

/**
 * @author Qt
 * @since 1.8,  Aug 11, 2023
 */
public class TextNode implements SqlNode, Cloneable {
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
		if (text.length() == 0) {
			return PreparedSql.EMPTY;
		}
		return new PreparedSql(text, Collections.emptyList());
	}

	@Override
	public BoundSql asBoundSql(VarNameGenerator generator, String openVarToken, String closeVarToken) {
		if (text.length() == 0) {
			return BoundSql.EMPTY;
		}
		return new BoundSql(text, Collections.emptyMap());
	}

	@Override
	public TextNode copy() {
		return copy(true);
	}

	@Override
	public TextNode copy(boolean withVarValue) {
		return new TextNode(this.text);
	}

	@Override
	public TextNode clone() {
		return copy(true);
	}

	@Override
	public boolean isTextNode() {
		return true;
	}


}

