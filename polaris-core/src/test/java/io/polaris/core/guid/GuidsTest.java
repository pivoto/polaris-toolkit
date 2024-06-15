package io.polaris.core.guid;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GuidsTest {

	@Test
	void detectStackTraceClassName() {
		TestConsole.printx(Guids.detectStackTraceClassName());

	}

}
