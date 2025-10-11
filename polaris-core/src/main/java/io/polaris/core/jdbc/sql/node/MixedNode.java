package io.polaris.core.jdbc.sql.node;

import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.jdbc.sql.BoundSql;
import io.polaris.core.jdbc.sql.PreparedSql;
import io.polaris.core.jdbc.sql.VarRef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Qt
 * @since  Aug 11, 2023
 */
public class MixedNode extends VarNode implements Cloneable {

	public MixedNode(String varName) {
		super(varName);
	}

	public String getReplacement() {
		List<Object> parameters = getVarValues();
		if (parameters == null || parameters.isEmpty()) {
			return SqlNodes.NULL.getText();
		} else {
			StringBuilder text = new StringBuilder();
			for (Object parameter : parameters) {
				if (text.length() > 0) {
					text.append(SymbolConsts.COMMA);
				}
				if (parameter instanceof VarRef) {
					parameter = ((VarRef<?>) parameter).getValue();
				}
				// 兼容 VarRef.toString
				text.append(Objects.toString(parameter));
			}
			return text.toString();
		}
	}

	@Override
	public PreparedSql asPreparedSql() {
		return new PreparedSql(getReplacement(), Collections.emptyList());
	}

	@Override
	public BoundSql asBoundSql(Predicate<String> varPropFilter, VarNameGenerator generator, String openVarToken, String closeVarToken) {
		return new BoundSql(getReplacement(), Collections.emptyMap());
	}

	@Override
	public boolean isMixedNode() {
		return true;
	}

	@Override
	public MixedNode copy() {
		return copy(true);
	}

	@Override
	public MixedNode copy(boolean withVarValue) {
		MixedNode clone = new MixedNode(this.varName);
		if (withVarValue && this.varValues != null) {
			clone.varValue = this.varValue;
			clone.varValues = new ArrayList<>(this.varValues);
		}
		return clone;
	}

	@Override
	public MixedNode clone() {
		return copy(true);
	}

}
