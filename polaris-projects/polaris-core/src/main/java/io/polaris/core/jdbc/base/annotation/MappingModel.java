package io.polaris.core.jdbc.base.annotation;

import javax.annotation.Nullable;

import io.polaris.core.jdbc.base.BeanCompositeMapping;
import io.polaris.core.jdbc.base.BeanMapping;
import io.polaris.core.jdbc.base.BeanPropertyMapping;
import io.polaris.core.lang.annotation.AnnotationAttributes;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.Strings;
import lombok.Getter;

/**
 * @author Qt
 * @since 1.8,  Feb 08, 2024
 */
public class MappingModel {
	private static final String PROPERTY = Reflects.getPropertyName(Mapping.Composite::property);
	private static final String ENTITY_TYPE = Reflects.getPropertyName(Mapping.Composite::entityType);
	private static final String COLUMNS = Reflects.getPropertyName(Mapping.Composite::columns);
	private static final String COMPOSITES = Reflects.getPropertyName(Mapping.Composite::composites);
	@Getter
	private boolean caseInsensitive = true;
	@Getter
	private boolean caseCamel = true;
	@Getter
	private BeanMapping<?> beanMapping;

	public static MappingModel of(@Nullable Mapping mapping) {
		MappingModel model = new MappingModel();
		if (mapping != null) {
			Class<?> entityType = mapping.entityType();
			if (entityType != void.class) {
				BeanMapping<?> beanMapping = new BeanMapping<>(entityType);
				model.beanMapping = beanMapping;
				for (Mapping.Column column : mapping.columns()) {
					beanMapping.column(new BeanPropertyMapping(column.property(), column.column()));
				}
				Mapping.Composite[] composites = mapping.composites();
				for (Mapping.Composite composite : composites) {
					AnnotationAttributes attributes = AnnotationAttributes.of(composite);
					fillComposite(beanMapping, attributes);
				}
			}
			model.caseCamel = mapping.caseCamel();
			model.caseInsensitive = mapping.caseInsensitive();
		}
		return model;
	}

	private static void fillComposite(BeanMapping<?> beanMapping, AnnotationAttributes attributes) {
		String property = attributes.getString(PROPERTY);
		Class<?> entityType = attributes.getClass(ENTITY_TYPE);
		if (Strings.isNotBlank(property) && entityType != void.class) {
			BeanMapping<?> subMapping = new BeanMapping<>(entityType);
			beanMapping.composite(new BeanCompositeMapping<>(property, subMapping));
			Mapping.Column[] columns = attributes.getAnnotationArray(COLUMNS, Mapping.Column.class);
			for (Mapping.Column column : columns) {
				subMapping.column(new BeanPropertyMapping(column.property(), column.column()));
			}
			AnnotationAttributes[] composites = attributes.getAnnotationAttributesArray(COMPOSITES);
			for (AnnotationAttributes composite : composites) {
				fillComposite(subMapping, composite);
			}
		}
	}
}
