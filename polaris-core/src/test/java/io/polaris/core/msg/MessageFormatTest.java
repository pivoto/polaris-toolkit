package io.polaris.core.msg;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import io.polaris.core.err.Exceptions;
import io.polaris.core.io.Consoles;
import io.polaris.core.map.Maps;
import org.junit.jupiter.api.Test;

class MessageFormatTest {

	@Test
	void test01() {
		Object[] args = new Object[]{
			111, 222, 333,
			"a", "b", "c",
			Maps.newFluentMap(new LinkedHashMap<>())
				.put("a", "abc")
				.put("b", 12345)
				.put("c", new Date())
				.put("d", 0)
				.put("e", 1)
				.put("abc", 123)
				.put("type1", "number")
//				.put("", 9999)
				.get(),
		};

		String[] patterns = new String[]{
			"{a{x:-b}c}",
			"{a{x:-b}c,number,00.00}",
			"{{3}{x0:-{x1:-b}{x2:-c}},{type1},00.00}",
			"{a}|{b,number,00.0000}|{c,date,yyyy-MM-dd HH:mm:ss.SSS}|{d,choice,0#xxx|1#yyy|2#zzz}|{e,choice,2#mmm|3#nnn}",
			"{x:-test-default}",
			"{0,number,0.00}|{1}|{2}",
			"{-2}|{-3}|{-4}",
			"{0}|{1}|{2}",
			"{0}|{1}|{2}|{3}",
			"{0}|{1}|{2}|{3}|{4}",
			"{0}|{1}|{2}|{3}|{4}|{5}",
			"{0}|{1}|{2}|{3}|{4}|{5}|{6}",
			"{0}|{1}|{2}|{3}|{4}|{5}|{6}|{,number,0.00}",

		};
		for (String pattern : patterns) {
			try {
				Consoles.log("{} => {}\n\tpattern: {}\n", pattern
					, MessageFormat.format(pattern, args)
					, MessageFormat.newInstance(pattern)
				);
				Consoles.log("{} => {}\n\tpattern: {}\n", pattern
					, MessageFormat.formatWithEmpty(pattern, "8888", args)
					, MessageFormat.newInstance(pattern)
				);
			} catch (Exception e) {
				Consoles.printStackTrace(e);
			}
		}
	}
}
