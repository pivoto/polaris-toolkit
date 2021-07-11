package io.awesome.dbv;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * @author Qt
 * @version Oct 16, 2020
 */
public class ClassRuleTestName extends TestWatcher {

	private Description description;

	@Override
	protected void starting(Description description) {
		this.description = description;
	}

	public Description getDescription() {
		return description;
	}

}
