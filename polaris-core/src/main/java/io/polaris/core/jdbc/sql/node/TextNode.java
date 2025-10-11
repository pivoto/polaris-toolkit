package io.polaris.core.jdbc.sql.node;

import java.util.Collections;
import java.util.function.Predicate;

import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.jdbc.sql.BoundSql;
import io.polaris.core.jdbc.sql.PreparedSql;

/**
 * @author Qt
 * @since  Aug 11, 2023
 */
@AnnotationProcessing
public class TextNode implements SqlNode, Cloneable {
	/** SQL语句块 */
	private String text;

	public TextNode(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return asPreparedSql().getText();
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public PreparedSql asPreparedSql() {
		if (text == null || text.isEmpty()) {
			return PreparedSql.EMPTY;
		}
		return new PreparedSql(text, Collections.emptyList());
	}

	@Override
	public BoundSql asBoundSql(Predicate<String> varPropFilter, VarNameGenerator generator, String openVarToken, String closeVarToken) {
		if (text == null || text.isEmpty()) {
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

