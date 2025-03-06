package io.polaris.core.converter;

import io.polaris.core.lang.JavaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class EnumConverterTest {


	private EnumConverter<TestEnum> converter;

	@BeforeEach
	public void setUp() {
		converter = new EnumConverter<>(TestEnum.class);
	}

	@Test
	public void testConvertWithNumber() {
		TestEnum result = converter.doConvert(0, JavaType.of(TestEnum.class));
		assertEquals(TestEnum.VALUE1, result);
	}

	@Test
	public void testConvertWithInvalidNumber() {
		TestEnum result = converter.doConvert(3, JavaType.of(TestEnum.class));
		assertNull(result);
	}

	@Test
	public void testConvertWithString() {
		TestEnum result = converter.doConvert("VALUE2", JavaType.of(TestEnum.class));
		assertEquals(TestEnum.VALUE2, result);
	}

	@Test
	public void testConvertWithInvalidString() {
		TestEnum result = converter.doConvert("INVALID", JavaType.of(TestEnum.class));
		assertNull(result);
	}

	@Test
	public void testConvertWithParseMethod() throws Exception {
		TestEnum result = converter.doConvert("value1", JavaType.of(TestEnum.class));
		assertEquals(TestEnum.VALUE1, result);
	}


	private enum TestEnum {
		VALUE1,
		VALUE2,
		VALUE3,

		;

		private static TestEnum parseOf(String value) {
			for (TestEnum testEnum : TestEnum.values()) {
				if (testEnum.name().equalsIgnoreCase(value)) {
					return testEnum;
				}
			}
			return null;
		}
	}
}
