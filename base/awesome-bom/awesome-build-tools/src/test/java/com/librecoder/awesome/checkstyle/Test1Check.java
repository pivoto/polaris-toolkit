package com.librecoder.awesome.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;

/**
 * @author Qt
 * @since Jun 17, 2021
 */
public class Test1Check extends AbstractCheck {
	@Override
	public int[] getDefaultTokens() {
		return new int[0];
	}

	@Override
	public int[] getAcceptableTokens() {
		return new int[0];
	}

	@Override
	public int[] getRequiredTokens() {
		return new int[0];
	}
}
