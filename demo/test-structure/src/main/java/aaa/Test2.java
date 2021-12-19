package aaa;

import bbb.Gtest1;
import ccc.Test1;

/**
 * @author Qt
 * @version Jun 17, 2021
 */
public class Test2 {
	public static void main(String[] args) {
		System.out.println();
		Test1.main(args);
		Test1 a = new Test1();
		System.out.println(a);
		Gtest1.main(args);
	}
}
