package io.polaris.core.jdbc.sql.node;

import io.polaris.core.consts.StdKeys;
import io.polaris.core.env.GlobalStdEnv;
import io.polaris.core.env.GlobalStdEnvObservable;

/**
 * @author Qt
 * @since Dec 19, 2025
 */
class LfTextNode extends TextNode {
	static final LfTextNode INSTANCE = new LfTextNode();

	private LfTextNode() {
		super(GlobalStdEnv.getBoolean(StdKeys.JDBC_SQL_SPACE_AS_LF, true) ? " " : "\n");
		GlobalStdEnvObservable.addListener(() -> {
			this.text = GlobalStdEnv.getBoolean(StdKeys.JDBC_SQL_SPACE_AS_LF, true) ? " " : "\n";
		});
	}

}
