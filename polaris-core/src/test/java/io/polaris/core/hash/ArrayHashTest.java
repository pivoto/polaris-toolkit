package io.polaris.core.hash;

import io.polaris.core.TestConsole;

import static io.polaris.core.hash.ArrayHash.hash;
import static org.junit.jupiter.api.Assertions.*;

class ArrayHashTest {

	public static void main(String[] args) {
		TestConsole.println(hash("Ca".toCharArray()));
		TestConsole.println(hash("DB".toCharArray()));
		TestConsole.println(hash("Ca"));
		TestConsole.println(hash("DB"));
		TestConsole.println("Ca".hashCode());
		TestConsole.println("DB".hashCode());
	}

}
