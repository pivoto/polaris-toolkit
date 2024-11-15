package io.polaris.core.guid;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

class GuidsTest {

	@Test
	void detectStackTraceClassName() {
		String msg = Guids.detectStackTraceClassName();
		Consoles.log(msg);

	}

}
