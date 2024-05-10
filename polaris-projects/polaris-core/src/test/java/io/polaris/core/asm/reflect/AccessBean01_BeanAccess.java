package io.polaris.core.asm.reflect;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Qt
 * @since  Apr 10, 2024
 */
public abstract class AccessBean01_BeanAccess extends BeanLambdaAccess<AccessBean01> {
	@Override
	protected Map<String, BiConsumer<Object, Object>> buildFieldSetters() {
		HashMap<String, BiConsumer<Object, Object>> map = new HashMap<>();
		BiConsumer<Object, Object> consumer = (o, v) -> ((AccessBean01) o).publicStrVal0 = ((String) v);
		map.put(AccessBean01.Fields.strVal0, consumer);
		return map;
	}


	@Override
	protected Map<String, Function<Object, Object>> buildFieldGetters() {
		Map<String, Function<Object, Object>> map = new HashMap<>();
		Function<Object, Object> func = o -> ((AccessBean01) o).publicStrVal0;
		map.put(AccessBean01.Fields.strVal0, func);
		return map;
	}
}
