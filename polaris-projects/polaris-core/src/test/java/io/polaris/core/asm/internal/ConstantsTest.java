package io.polaris.core.asm.internal;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {


	@Test
	void test01() {

		TestConsole.printx(Constants.SIG_STATIC);
		TestConsole.printx(Constants.TYPE_OBJECT_ARRAY);
		TestConsole.printx(Constants.TYPE_CLASS_ARRAY);
		TestConsole.printx(Constants.TYPE_STRING_ARRAY);
		TestConsole.printx(Constants.TYPE_OBJECT);
		TestConsole.printx(Constants.TYPE_CLASS);
		TestConsole.printx(Constants.TYPE_CLASS_LOADER);
		TestConsole.printx(Constants.TYPE_CHARACTER);
		TestConsole.printx(Constants.TYPE_BOOLEAN);
		TestConsole.printx(Constants.TYPE_STRING_BUILDER);
		TestConsole.printx(Constants.TYPE_STRING_BUFFER);
		TestConsole.printx(Constants.TYPE_RUNTIME_EXCEPTION);
		TestConsole.printx(Constants.TYPE_ILLEGAL_ARGUMENT_EXCEPTION);
		TestConsole.printx(Constants.TYPE_SIGNATURE);


	}


}
