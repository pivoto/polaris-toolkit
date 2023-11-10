package io.polaris.core.jdbc.sql.statement.expression;

import io.polaris.core.regex.Patterns;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class PatternExpressionTest {

	@Test
	void test01() {
		Pattern pattern = Patterns.getPattern("^ref(\\d*)$");
		String[] args = {"ref123","ref","ref11x","ref2"};
		for (String s : args) {
			Matcher m = pattern.matcher(s);
			if (m.matches()) {
				System.out.printf("%s: %s, %s \n", s,m.group(), m.group(1));
			}
		}
	}
}
