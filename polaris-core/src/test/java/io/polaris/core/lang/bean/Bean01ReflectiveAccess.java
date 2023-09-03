package io.polaris.core.lang.bean;

import io.polaris.core.asm.reflect.ReflectiveAccess;
import io.polaris.core.err.InvocationException;
import io.polaris.core.tuple.Tuple2;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8,  Aug 05, 2023
 */
public class Bean01ReflectiveAccess extends ReflectiveAccess {
//	@Override
//	protected Class[][] buildConstructorParamTypes() {
//		Class[][] types = new Class[11111][];
//		types[333] = new Class[]{boolean.class};
//		return types;
//	}

	public Map<String,BiConsumer<Object,Object>> buildSetters(){
		Map<String, BiConsumer<Object, Object>> map = new HashMap<>();
		map.put("booleanVal", (o,v)->{
			try {
				((Bean01)o).setBooleanVal((Boolean)v);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return map;
	}

	//	@Override
//	protected Map<String, Tuple2<Class[], BiFunction<Object, Object[], Object>>[]> buildMethods() {
//		Map<String, Tuple2<Class[], BiFunction<Object, Object[], Object>>[]> map = new HashMap<>();
//		Tuple2<Class[], BiFunction<Object, Object[], Object>>[] tuple= new Tuple2[11111];
//
//		Class[] types = new Class[123];
//		BiFunction<Object, Object[], Object> func = (o, args) -> {
//			try {
//				return ((Bean01) o).test02((String) args[1], (Integer) args[999]);
//			} catch (Throwable e) {
//				throw new InvocationException(e);
//			}
//		};
//
//		tuple[123] = new Tuple2<>(types, func);
//		map.put("test01", tuple);
//		return map;
//	}

//	@Override
//	protected Map<String, Tuple2<Function<Object, Object>, BiConsumer<Object, Object>>> buildFields() {
//		Map<String, Tuple2<Function<Object, Object>, BiConsumer<Object, Object>>> map
//			= new HashMap<>();
//		{
//			Function<Object, Object> getter = obj -> ((Bean01) obj).staticId;
//			BiConsumer<Object, Object> setter = (obj, val) -> ((Bean01) obj).staticId = (String) val;
//			Tuple2<Function<Object, Object>, BiConsumer<Object, Object>> tuple = new Tuple2<>(getter, setter);
//			map.put("staticId", tuple);
//		}
//		{
//			Function<Object, Object> getter = obj -> ((Bean01) obj).id;
//			BiConsumer<Object, Object> setter = (obj, val) -> ((Bean01) obj).id = (String) val;
//			Tuple2<Function<Object, Object>, BiConsumer<Object, Object>> tuple = new Tuple2<>(getter, setter);
//			map.put("id", tuple);
//		}
//
//		return map;
//	}
}
