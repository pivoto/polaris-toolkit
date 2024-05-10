package io.polaris.core.asm.reflect;

import java.util.HashMap;
import java.util.Map;

import io.polaris.core.TestConsole;
import io.polaris.core.asm.reflect.copy.CopyBean00;
import io.polaris.core.asm.reflect.copy.sub.SubCopyBean00;
import io.polaris.core.collection.Iterables;
import io.polaris.core.consts.SystemKeys;
import io.polaris.core.converter.Converters;
import io.polaris.core.lang.copier.CopyOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since  Apr 14, 2024
 */
public class BeanCopierTest {
	static {
		System.setProperty(SystemKeys.JAVA_CLASS_BYTES_TMPDIR, "/data/classes");
	}

	private CopyBean00 source = new CopyBean00();

	{
		source.errorIntVal0 = -9999;
		source.fieldNoMapping0 = "fieldNoMapping0";
		source.setMethodNoMapping0("methodNoMapping0");
		source.setMethodToFieldStr0("methodToFieldStr0");
		source.setMethodToFieldInt0(111);
		source.fieldToMethodStrVal0 = "fieldToMethodStrVal0";
		source.fieldToMethodIntVal0 = 222;

		source.fieldCapitalizeVal0 = "fieldCapitalizeVal0";
		source.field_underline_to_camel_val0 = "field_underline_to_camel_val0";
		source.fieldCamelToUnderlineVal0 = "fieldCamelToUnderlineVal0";
		source.fieldIgnoreCaseVal0 = "fieldIgnoreCaseVal0";
		source.field_IGNORE_CASE_underline_to_camel_val0 = "field_IGNORE_CASE_underline_to_camel_val0";
		source.fieldIgnoreCaseCamelToUnderlineVal0 = "fieldIgnoreCaseCamelToUnderlineVal0";


		source.setStrVal0("strVal0");
		source.setIntVal0(333);
		source.publicStrVal0 = "str";
		source.publicIntVal0 = 444;
	}


	@Test
	void test_copyBeanToMap() {
		BeanCopier<CopyBean00> copier = BeanCopier.get(CopyBean00.class);
		TestConsole.printx(copier);
		TestConsole.printx("source: {}", source);
		Map<String, Object> target = new HashMap<>();
		copier.copyBeanToMap(source, target);
		TestConsole.printx("target: {}", target);
		Assertions.assertEquals(source.getIntVal0(), target.get("intVal0"));
		Assertions.assertEquals(source.getStrVal0(), target.get("strVal0"));
		Assertions.assertEquals(source.publicStrVal0, target.get("publicStrVal0"));
		Assertions.assertEquals(source.publicIntVal0, target.get("publicIntVal0"));
		Assertions.assertEquals(source.getMethodToFieldStr0(), target.get("methodToFieldStr0"));
		Assertions.assertEquals(source.getMethodToFieldInt0(), target.get("methodToFieldInt0"));
		Assertions.assertEquals(source.fieldToMethodStrVal0, target.get("fieldToMethodStrVal0"));
		Assertions.assertEquals(source.fieldToMethodIntVal0, target.get("fieldToMethodIntVal0"));
	}
	@Test
	void test_copyBeanToMapByOptions() {
		BeanCopier<CopyBean00> copier = BeanCopier.get(CopyBean00.class);
		TestConsole.printx(copier);
		TestConsole.printx("source: {}", source);
		Map<String, Object> target = new HashMap<>();
		copier.copyBeanToMap(source, target,
			CopyOptions.create().ignoreCase(true)
				.ignoreCapitalize(true)
				.enableCamelToUnderlineCase(true)
				.enableUnderlineToCamelCase(true)
				.ignoreKeys(Iterables.asSet("fieldToMethodStrVal0", "fieldToMethodIntVal0"))
				.keyMapping(key -> {
					if ("strVal0".equals(key)) {
						return "fieldToMethodStrVal0";
					}
					if ("intVal0".equals(key)) {
						return "fieldToMethodIntVal0";
					}
					return key;
				}));
		TestConsole.printx("target: {}", target);
		Assertions.assertNotEquals(source.getIntVal0(), target.get("intVal0"));
		Assertions.assertNotEquals(source.getStrVal0(), target.get("strVal0"));
		Assertions.assertEquals(source.publicStrVal0, target.get("publicStrVal0"));
		Assertions.assertEquals(source.publicIntVal0, target.get("publicIntVal0"));
		Assertions.assertEquals(source.getMethodToFieldStr0(), target.get("methodToFieldStr0"));
		Assertions.assertEquals(source.getMethodToFieldInt0(), target.get("methodToFieldInt0"));
		Assertions.assertEquals(source.getStrVal0(), target.get("fieldToMethodStrVal0"));
		Assertions.assertEquals(source.getIntVal0(), target.get("fieldToMethodIntVal0"));
	}

	@Test
	void test_copyMapToBean() {
		BeanCopier<SubCopyBean00> copier = BeanCopier.get(SubCopyBean00.class);
		TestConsole.printx(copier);
		Map<String, Object> source = new HashMap<>();
		source.put("intVal0",111);
		source.put("strVal0","strVal0");
		source.put("publicIntVal0",222);
		source.put("methodToFieldInt0",333);
		source.put("fieldToMethodIntVal0",444);
		TestConsole.printx("source: {}", source);
		SubCopyBean00 target = new SubCopyBean00();
		copier.copyMapToBean(source, target, Converters::convertQuietly);
		TestConsole.printx("target: {}", target);
		Assertions.assertEquals( source.get("intVal0"),target.getIntVal0());
		Assertions.assertEquals( source.get("strVal0"),target.getStrVal0());
		Assertions.assertEquals( source.get("publicStrVal0"),target.publicStrVal0);
		Assertions.assertEquals( source.get("publicIntVal0"),target.publicIntVal0);
		Assertions.assertEquals( source.get("methodToFieldStr0"),target.methodToFieldStr0);
		Assertions.assertEquals( source.get("methodToFieldInt0"),target.methodToFieldInt0);
		Assertions.assertEquals( source.get("fieldToMethodStrVal0"),target.getFieldToMethodStrVal0());
		Assertions.assertEquals( source.get("fieldToMethodIntVal0"),target.getFieldToMethodIntVal0());
	}
	@Test
	void test_copyMapToBeanByOptions() {
		BeanCopier<SubCopyBean00> copier = BeanCopier.get(SubCopyBean00.class);
		TestConsole.printx(copier);
		Map<String, Object> source = new HashMap<>();
		source.put("intVal0",111);
		source.put("strVal0","strVal0");
		source.put("publicIntVal0","222");
		source.put("methodToFieldInt0",333);
		source.put("fieldToMethodIntVal0",444);
		source.put("fieldCapitalizeVal0","fieldCapitalizeVal0");
		source.put("field_underline_to_camel_val0","field_underline_to_camel_val0");
		source.put("fieldCamelToUnderlineVal0","fieldCamelToUnderlineVal0");
		source.put("fieldIgnoreCaseVal0","fieldIgnoreCaseVal0");
		source.put("field_IGNORE_CASE_underline_to_camel_val0","field_IGNORE_CASE_underline_to_camel_val0");
		source.put("fieldIgnoreCaseCamelToUnderlineVal0","fieldIgnoreCaseCamelToUnderlineVal0");
		TestConsole.printx("source: {}", source);
		SubCopyBean00 target = new SubCopyBean00();
		copier.copyMapToBean(source, target,
			CopyOptions.create().ignoreCase(true)
				.ignoreCapitalize(true)
				.enableCamelToUnderlineCase(true)
				.enableUnderlineToCamelCase(true)
				.ignoreKeys(Iterables.asSet("fieldToMethodStrVal0", "fieldToMethodIntVal0"))
				.keyMapping(key -> {
					if ("strVal0".equals(key)) {
						return "fieldToMethodStrVal0";
					}
					if ("intVal0".equals(key)) {
						return "fieldToMethodIntVal0";
					}
					return key;
				}));
		TestConsole.printx("target: {}", target);
		Assertions.assertEquals( source.get("strVal0"),target.getFieldToMethodStrVal0());
		Assertions.assertEquals( source.get("intVal0"),target.getFieldToMethodIntVal0());
		Assertions.assertNotEquals( source.get("strVal0"),target.getIntVal0());
		Assertions.assertNotEquals( source.get("intVal0"),target.getStrVal0());
		Assertions.assertEquals( source.get("publicIntVal0"),String.valueOf(target.publicIntVal0));
		Assertions.assertEquals( source.get("fieldCapitalizeVal0"),target.FieldCapitalizeVal0);
		Assertions.assertEquals( source.get("field_underline_to_camel_val0"),target.fieldUnderlineToCamelVal0);
		Assertions.assertEquals( source.get("fieldCamelToUnderlineVal0"),target.field_camel_to_underline_val0);
		Assertions.assertEquals( source.get("fieldIgnoreCaseVal0"),target.fieldignorecaseval0);
		Assertions.assertEquals( source.get("field_IGNORE_CASE_underline_to_camel_val0"),target.fieldIgnoreCaseUnderlineToCamelVal0);
		Assertions.assertEquals( source.get("fieldIgnoreCaseCamelToUnderlineVal0"),target.field_IGNORE_CASE_camel_to_underline_val0);
	}
	@Test
	void test_copyBeanToBean() {
		BeanCopier<CopyBean00> copier = BeanCopier.get(CopyBean00.class);
		TestConsole.printx(copier);
		TestConsole.printx("source: {}", source);
		SubCopyBean00 target = new SubCopyBean00();
		copier.copyBeanToBean(source, target);
		TestConsole.printx("target: {}", target);
		Assertions.assertEquals(source.getIntVal0(), target.getIntVal0());
		Assertions.assertEquals(source.getStrVal0(), target.getStrVal0());
		Assertions.assertEquals(source.publicStrVal0, target.publicStrVal0);
		Assertions.assertEquals(source.publicIntVal0, target.publicIntVal0);
		Assertions.assertEquals(source.getMethodToFieldStr0(), target.methodToFieldStr0);
		Assertions.assertEquals(source.getMethodToFieldInt0(), target.methodToFieldInt0);
		Assertions.assertEquals(source.fieldToMethodStrVal0, target.getFieldToMethodStrVal0());
		Assertions.assertEquals(source.fieldToMethodIntVal0, target.getFieldToMethodIntVal0());
	}

	@Test
	void test_copyBeanToBeanByOptions() {
		BeanCopier<CopyBean00> copier = BeanCopier.get(CopyBean00.class);
		TestConsole.printx(copier);
		TestConsole.printx("source: {}", source);
		SubCopyBean00 target = new SubCopyBean00();
		copier.copyBeanToBean(source, SubCopyBean00.class, target,
			CopyOptions.create().ignoreCase(true)
				.ignoreCapitalize(true)
				.enableCamelToUnderlineCase(true)
				.enableUnderlineToCamelCase(true)
		);
		TestConsole.printx("target: {}", target);
		Assertions.assertEquals(source.fieldCapitalizeVal0, target.FieldCapitalizeVal0);
		Assertions.assertEquals(source.field_underline_to_camel_val0, target.fieldUnderlineToCamelVal0);
		Assertions.assertEquals(source.fieldCamelToUnderlineVal0, target.field_camel_to_underline_val0);
		Assertions.assertEquals(source.fieldIgnoreCaseVal0, target.fieldignorecaseval0);
		Assertions.assertEquals(source.field_IGNORE_CASE_underline_to_camel_val0, target.fieldIgnoreCaseUnderlineToCamelVal0);
		Assertions.assertEquals(source.fieldIgnoreCaseCamelToUnderlineVal0, target.field_IGNORE_CASE_camel_to_underline_val0);
	}

	@Test
	void test_copyBeanToBeanByOptions_ignoreKeys() {
		BeanCopier<CopyBean00> copier = BeanCopier.get(CopyBean00.class);
		TestConsole.printx(copier);
		TestConsole.printx("source: {}", source);
		SubCopyBean00 target = new SubCopyBean00();
		copier.copyBeanToBean(source, SubCopyBean00.class, target,
			CopyOptions.create().ignoreCase(true)
				.ignoreCapitalize(true)
				.enableCamelToUnderlineCase(true)
				.enableUnderlineToCamelCase(true)
				.ignoreKeys(Iterables.asSet("strVal0", "intVal0"))
		);
		TestConsole.printx("target: {}", target);
		Assertions.assertNotEquals(source.getIntVal0(), target.getIntVal0());
		Assertions.assertNotEquals(source.getStrVal0(), target.getStrVal0());
	}

	@Test
	void test_copyBeanToBeanByOptions_keyMapping() {
		BeanCopier<CopyBean00> copier = BeanCopier.get(CopyBean00.class);
		TestConsole.printx(copier);
		TestConsole.printx("source: {}", source);
		SubCopyBean00 target = new SubCopyBean00();
		copier.copyBeanToBean(source, SubCopyBean00.class, target,
			CopyOptions.create().ignoreCase(true)
				.ignoreCapitalize(true)
				.enableCamelToUnderlineCase(true)
				.enableUnderlineToCamelCase(true)
				.ignoreKeys(Iterables.asSet("fieldToMethodStrVal0", "fieldToMethodIntVal0"))
				.keyMapping(key -> {
					if ("strVal0".equals(key)) {
						return "fieldToMethodStrVal0";
					}
					if ("intVal0".equals(key)) {
						return "fieldToMethodIntVal0";
					}
					return key;
				})
		);
		TestConsole.printx("target: {}", target);
		Assertions.assertEquals(source.getIntVal0(), target.getFieldToMethodIntVal0());
		Assertions.assertEquals(source.getStrVal0(), target.getFieldToMethodStrVal0());
		Assertions.assertNotEquals(source.getIntVal0(), target.getIntVal0());
		Assertions.assertNotEquals(source.getStrVal0(), target.getStrVal0());
	}

	@Test
	void test_AccessBean00_copyBeanToBean() {
		BeanCopier<AccessBean00> copier = BeanCopier.get(AccessBean00.class);
		TestConsole.printx(copier);
		{
			AccessBean00 source = new AccessBean00();
			AccessBean00 target = new AccessBean00();
			target.publicStrVal0 = "test";

			source.publicLongVal0 = 321;
			source.setIntVal0(123);
			source.publicStrVal0 = "test123";

			copier.copyBeanToBean(source, target);

			TestConsole.printx("target.getIntVal0: {}", target.getIntVal0());
			TestConsole.printx("target.publicLongVal0: {}", target.publicLongVal0);
			TestConsole.printx("target.publicStrVal0: {}", target.publicStrVal0);
			TestConsole.printx("target.publicStaticStrVal0: {}", target.publicStaticStrVal0);

			Assertions.assertEquals(source.publicLongVal0, target.publicLongVal0);
			Assertions.assertEquals(source.getIntVal0(), target.getIntVal0());
			Assertions.assertEquals(source.publicStrVal0, target.publicStrVal0);

		}

	}
}
