package io.polaris.core.hash;

import io.polaris.core.io.Consoles;

import static io.polaris.core.hash.ArrayHash.hash;

class ArrayHashTest {

	public static void main(String[] args) {
		Object[] args4 = new Object[]{hash("Ca".toCharArray())};
		Consoles.println(args4);
		Object[] args3 = new Object[]{hash("DB".toCharArray())};
		Consoles.println(args3);
		Object[] args2 = new Object[]{hash("Ca")};
		Consoles.println(args2);
		Object[] args1 = new Object[]{hash("DB")};
		Consoles.println(args1);
		Consoles.println("Ca".hashCode());
		Consoles.println("DB".hashCode());
	}

}
