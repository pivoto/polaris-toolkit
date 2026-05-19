package io.polaris.core.jdbc.dbv;

import java.lang.reflect.Field;

import io.polaris.core.asm.reflect.BeanAccess;
import lombok.Data;

/**
 * @author Qt
 * @since May 06, 2024
 */
@Data
public class DbvColumnFieldMeta {
	private Field field;
	private DbvColumnGetter getter;
	private String column;
	private BeanAccess<?> beanAccess;


	public void set(Object obj, Object value) throws IllegalAccessException {
		if (field == null) {
			return;
		}
		if (beanAccess != null) {
			if (beanAccess.containsSetterOrField(field.getName()) &&
				(field.getType().equals(beanAccess.propertyType(field.getName()))
					|| field.getGenericType().equals(beanAccess.propertyGenericType(field.getName()))
				)) {
				beanAccess.setPropertyOrField(obj, field.getName(), value);
				return;
			}
		}
		field.setAccessible(true);
		field.set(obj, value);
	}

}
