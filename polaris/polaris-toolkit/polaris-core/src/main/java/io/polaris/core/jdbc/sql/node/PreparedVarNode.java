package io.polaris.core.jdbc.sql.node;

import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.jdbc.sql.BoundSql;
import io.polaris.core.jdbc.sql.PreparedSql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Aug 11, 2023
 */
public class PreparedVarNode extends VarNode {
	public PreparedVarNode(String varName) {
		super(varName);
	}

	@Override
	public PreparedSql asPreparedSql() {
		StringBuilder text = new StringBuilder();
		List<Object> list = new ArrayList<>();
		List<Object> parameters = getVarValues();
		if (parameters == null || parameters.isEmpty()) {
			text.append(SymbolConsts.QUESTION_MARK);
			list.add(null);
		} else {
			for (Object parameter : parameters) {
				if (text.length() > 0) {
					text.append(SymbolConsts.COMMA);
				}
				text.append(SymbolConsts.QUESTION_MARK);
				list.add(parameter);
			}
		}
		return new PreparedSql(text.toString(), list);
	}

	@Override
	public BoundSql asBoundSql(VarNameGenerator generator, String openVarToken, String closeVarToken) {
		StringBuilder text = new StringBuilder();
		Map<String, Object> map = new LinkedHashMap<>();
		List<Object> parameters = getVarValues();
		if (parameters == null || parameters.isEmpty()) {
			String key = generator.generate();
			map.put(key, null);
			text.append(openVarToken).append(key).append(closeVarToken);
		} else {
			for (Object parameter : parameters) {
				if (text.length() > 0) {
					text.append(SymbolConsts.COMMA);
				}
				String key = generator.generate();
				text.append(openVarToken).append(key).append(closeVarToken);
				map.put(key, parameter);
			}
		}
		return new BoundSql(text.toString(), map);
	}

}
