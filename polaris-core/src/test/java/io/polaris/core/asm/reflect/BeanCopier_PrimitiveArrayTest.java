package io.polaris.core.asm.reflect;

import io.polaris.core.asm.reflect.sample.PrimitiveArrayBean;
import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Apr 04, 2026
 */
public class BeanCopier_PrimitiveArrayTest {

	@Test
	public void test() {

		PrimitiveArrayBean src = new PrimitiveArrayBean();
		src.setIntArray(new int[]{1, 2, 3});
		src.setLongArray(new long[]{1, 2, 3});
		src.setFloatArray(new float[]{1, 2, 3});
		src.setDoubleArray(new double[]{1, 2, 3});
		src.setBooleanArray(new boolean[]{true, false, true});
		src.setCharArray(new char[]{'a', 'b', 'c'});
		src.setByteArray(new byte[]{1, 2, 3});
		src.setShortArray(new short[]{1, 2, 3});
		src.setObjectArray(new Object[]{new Object(), new Object(), new Object()});

		PrimitiveArrayBean dest = new PrimitiveArrayBean();
		BeanCopier.get(PrimitiveArrayBean.class).copyBeanToBean(src, dest);

		Consoles.println(src);
		Consoles.println(dest);
	}
}
