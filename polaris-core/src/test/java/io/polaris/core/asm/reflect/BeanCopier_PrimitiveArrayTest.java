package io.polaris.core.asm.reflect;

import io.polaris.core.asm.reflect.sample.PrimitiveArrayBeanSource;
import io.polaris.core.asm.reflect.sample.PrimitiveArrayBeanTarget;
import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Apr 04, 2026
 */
@SuppressWarnings("NewClassNamingConvention")
public class BeanCopier_PrimitiveArrayTest {

	@Test
	public void test() {

		PrimitiveArrayBeanSource src = new PrimitiveArrayBeanSource();
		src.setIntArray(new int[]{1, 2, 3});
		src.setLongArray(new long[]{1, 2, 3});
		src.setFloatArray(new float[]{1, 2, 3});
		src.setDoubleArray(new double[]{1, 2, 3});
		src.setBooleanArray(new boolean[]{true, false, true});
		src.setCharArray(new char[]{'a', 'b', 'c'});
		src.setByteArray(new byte[]{1, 2, 3});
		src.setShortArray(new short[]{1, 2, 3});
		src.setObjectArray(new Object[]{new Object(), new Object(), new Object()});


		Consoles.println(src);

		PrimitiveArrayBeanSource dest = new PrimitiveArrayBeanSource();
		BeanCopier.get(PrimitiveArrayBeanSource.class).copyBeanToBean(src, dest);
		Consoles.println(dest);
		PrimitiveArrayBeanTarget dest2 = new PrimitiveArrayBeanTarget();
		BeanCopier.get(PrimitiveArrayBeanSource.class).copyBeanToBean(src, dest2);
		Consoles.println(dest2);

	}
}
