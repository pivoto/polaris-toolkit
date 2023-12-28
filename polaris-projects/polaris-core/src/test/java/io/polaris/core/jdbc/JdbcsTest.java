package io.polaris.core.jdbc;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcsTest {

	@Test
	void test01() {
		Object arr = Array.newInstance(int.class,10);
		System.out.println(arr.getClass().getComponentType());
		System.out.println(Arrays.toString((Object[])arr)); // error
	}
}
