package io.polaris.core.jdbc.sql.node;

import io.polaris.core.consts.StdConsts;
import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.jdbc.sql.BoundSql;
import io.polaris.core.jdbc.sql.PreparedSql;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Qt
 * @since 1.8,  Aug 11, 2023
 */
public class ReplacedVarNode extends VarNode {

	public ReplacedVarNode(String varName) {
		super(varName);
	}

	public String getReplacement() {
		StringBuilder text = new StringBuilder();
		List<Object> parameters = getVarValues();
		if (parameters == null || parameters.isEmpty()) {
			text.append(StdConsts.NULL);
		} else {
			for (Object parameter : parameters) {
				if (text.length() > 0) {
					text.append(SymbolConsts.COMMA);
				}
				text.append(Objects.toString(parameter));
			}
		}
		return text.toString();
	}

	@Override
	public PreparedSql asPreparedSql() {
		return new PreparedSql(getReplacement(), Collections.emptyList());
	}

	@Override
	public BoundSql asBoundSql(VarNameGenerator generator, String openVarToken, String closeVarToken) {
		return new BoundSql(getReplacement(), Collections.emptyMap());
	}

}
