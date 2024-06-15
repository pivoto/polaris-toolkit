package io.polaris.core.jdbc.base;

import io.polaris.core.lang.bean.MetaObject;

/**
 * @author Qt
 * @since  Feb 06, 2024
 */
public class GeneratedKeyConsumers {

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static GeneratedKeyConsumer of(Object bindings, String[] keyProperties) {
		return rs -> {
			if (rs.next()) {
				if (keyProperties != null) {
					MetaObject metaObject = MetaObject.of((Class) bindings.getClass());
					for (int i = 0; i < keyProperties.length; i++) {
						Object val = rs.getObject(i + 1);
						metaObject.setPathProperty(bindings, keyProperties[i], val);
					}
				}
			}
		};
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static GeneratedKeyConsumer ofIterable(Iterable<Object> bindings, String[] keyProperties) {
		return rs -> {
			if (keyProperties != null) {
				for (Object binding : bindings) {
					MetaObject metaObject = MetaObject.of((Class) binding.getClass());
					if (rs.next()) {
						for (int i = 0; i < keyProperties.length; i++) {
							Object val = rs.getObject(i + 1);
							metaObject.setPathProperty(binding, keyProperties[i], val);
						}
					} else {
						break;
					}
				}
			}
		};
	}

}
