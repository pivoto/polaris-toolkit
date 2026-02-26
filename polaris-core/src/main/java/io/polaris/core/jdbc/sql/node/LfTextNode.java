package io.polaris.core.jdbc.sql.node;

import io.polaris.core.env.Config;
import io.polaris.core.env.GlobalStdEnvObservable;

/**
 * @author Qt
 * @since Dec 19, 2025
 */
class LfTextNode extends TextNode {
	static final LfTextNode INSTANCE = new LfTextNode();

	private LfTextNode() {
		super(Config.INSTANCE.jdbc().sql().spaceAsLf() ? " " : "\n");
		GlobalStdEnvObservable.addListener(() -> {
			this.text = Config.INSTANCE.jdbc().sql().spaceAsLf() ? " " : "\n";
		});
	}

}
