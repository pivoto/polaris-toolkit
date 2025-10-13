package io.polaris.core.jdbc.annotation.processing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;

import io.polaris.core.annotation.processing.AnnotationProcessorUtils;
import io.polaris.core.annotation.processing.AptAnnotationAttributes;
import io.polaris.core.annotation.processing.AptAnnotations;
import io.polaris.core.javapoet.ClassName;
import io.polaris.core.javapoet.TypeName;
import io.polaris.core.jdbc.annotation.Column;
import io.polaris.core.jdbc.annotation.ColumnProperties;
import io.polaris.core.jdbc.annotation.ColumnProperty;
import io.polaris.core.jdbc.annotation.Expression;
import io.polaris.core.jdbc.annotation.Id;
import io.polaris.core.jdbc.annotation.Table;
import lombok.Data;

/**
 * @author Qt
 * @since Aug 20, 2023
 */
@Data
public class JdbcBeanInfo {
	private final ProcessingEnvironment env;
	private final TypeElement element;
	private String tableName;
	private String tableAlias;
	private String tableCatalog;
	private String tableSchema;
	private String metaSuffix;
	private TypeName beanTypeName;
	private ClassName beanClassName;
	private ClassName metaClassName;
	private List<FieldInfo> fields = new ArrayList<>();
	private List<ExpressionInfo> expressions = new ArrayList<>();
	private boolean sqlGenerated;
	private String sqlSuffix;
	private ClassName sqlClassName;

	public JdbcBeanInfo(ProcessingEnvironment env, TypeElement element, Table table) {
		this.env = env;
		this.element = element;
		init(table);
	}

	private void init(Table table) {
		if (table == null) {
			table = element.getAnnotation(Table.class);
		}
		if (table == null) {
			return;
		}

		this.tableName = table.value();
		this.tableAlias = table.alias();
		this.tableCatalog = table.catalog();
		this.tableSchema = table.schema();
		this.metaSuffix = table.metaSuffix();
		this.sqlGenerated = table.sqlGenerated();
		this.sqlSuffix = table.sqlSuffix();

		this.beanTypeName = TypeName.get(element.asType());
		this.beanClassName = ClassName.get(element);
		String simpleName =
			beanClassName.packageName().isEmpty() ? beanClassName.reflectionName()
				: beanClassName.reflectionName().substring(beanClassName.packageName().length() + 1);
		this.metaClassName = ClassName.get(beanClassName.packageName(), simpleName + metaSuffix);
		this.sqlClassName = ClassName.get(beanClassName.packageName(), simpleName + sqlSuffix);

		visitFieldElement(element);
	}

	private void visitFieldElement(TypeElement element) {
		Set<String> retrieved = new HashSet<>();
		while (!Object.class.getName().equals(element.toString())) {
			TypeName declaredTypeName = TypeName.get(element.asType());
			ClassName declaredClassName = ClassName.get(element);
			for (Element item : element.getEnclosedElements()) {
				if (item instanceof VariableElement) {
					VariableElement variableElement = (VariableElement) item;
					if (variableElement.getKind() == ElementKind.FIELD
						&& !variableElement.getModifiers().contains(Modifier.STATIC)) {
						String fieldName = variableElement.getSimpleName().toString();
						if (retrieved.contains(fieldName)) {
							continue;
						}
						retrieved.add(fieldName);
						if (variableElement.getModifiers().contains(Modifier.TRANSIENT)) {
							continue;
						}
						Map<String, String> columnProperties = getColumnProperties(variableElement);
						//Column column = variableElement.getAnnotation(Column.class);
						//Column column = getAnnotation(variableElement, Column.class);
						Column column = getColumnAnnotation(variableElement);
						if (column != null) {
							if (column.ignored()) {
								continue;
							}
						}
						//Expression expression = variableElement.getAnnotation(Expression.class);
						//Expression expression = getAnnotation(variableElement, Expression.class);
						Expression expression = getExpressionAnnotation(variableElement);
						if (expression != null) {
							if (expression.value() != null && !expression.value().trim().isEmpty()) {
								ExpressionInfo expressionInfo = new ExpressionInfo();
								expressionInfo.properties = columnProperties;
								expressionInfo.declaredTypeName = declaredTypeName;
								expressionInfo.declaredClassName = declaredClassName;
								expressionInfo.fieldTypeName = TypeName.get(variableElement.asType());
								expressionInfo.fieldRawTypeName = AnnotationProcessorUtils.rawType(expressionInfo.fieldTypeName);
								expressionInfo.readExpression(fieldName, expression);
								expressions.add(expressionInfo);
								continue;
							}
						}

						FieldInfo fieldInfo = new FieldInfo();
						fieldInfo.properties = columnProperties;
						fieldInfo.declaredTypeName = declaredTypeName;
						fieldInfo.declaredClassName = declaredClassName;
						fieldInfo.fieldTypeName = TypeName.get(variableElement.asType());
						fieldInfo.fieldRawTypeName = AnnotationProcessorUtils.rawType(fieldInfo.fieldTypeName);
						//Id id = variableElement.getAnnotation(Id.class);
						//Id id = getAnnotation(variableElement, Id.class);
						Id id = getIdAnnotation(variableElement);
						fieldInfo.readColumn(fieldName, column, id);
						fields.add(fieldInfo);
					}
				}
			}
			// super class
			TypeMirror typeMirror = element.getSuperclass();
			if (typeMirror instanceof NoType) {
				break;
			}
			Element typeElement = ((DeclaredType) typeMirror).asElement();
			if (!(typeElement instanceof TypeElement)) {
				break;
			}
			element = (TypeElement) typeElement;
		}
	}

	private <T extends Annotation> T getAnnotation(Element element, Class<T> annotationType) {
		return AnnotationProcessorUtils.getAnnotation(env.getElementUtils(), element, annotationType);
	}

	private ColumnAnnotationAttributes getColumnAnnotation(Element element) {
		TypeElement annotationType = env.getElementUtils().getTypeElement(Column.class.getCanonicalName());
		AptAnnotationAttributes annotationAttributes = AptAnnotations.getMergedAnnotation(env, element, annotationType);
		if (annotationAttributes == null) {
			return null;
		}
		return new ColumnAnnotationAttributes(annotationAttributes);
	}

	private IdAnnotationAttributes getIdAnnotation(Element element) {
		TypeElement annotationType = env.getElementUtils().getTypeElement(Id.class.getCanonicalName());
		AptAnnotationAttributes annotationAttributes = AptAnnotations.getMergedAnnotation(env, element, annotationType);
		if (annotationAttributes == null) {
			return null;
		}
		return new IdAnnotationAttributes(annotationAttributes);
	}

	private ExpressionAnnotationAttributes getExpressionAnnotation(Element element) {
		TypeElement annotationType = env.getElementUtils().getTypeElement(Expression.class.getCanonicalName());
		AptAnnotationAttributes annotationAttributes = AptAnnotations.getMergedAnnotation(env, element, annotationType);
		if (annotationAttributes == null) {
			return null;
		}
		return new ExpressionAnnotationAttributes(annotationAttributes);
	}

	private Map<String, String> getColumnProperties(Element element) {
		TypeElement annotationType = env.getElementUtils().getTypeElement(ColumnProperty.class.getCanonicalName());
		Set<AptAnnotationAttributes> annotationAttributes = AptAnnotations.getMergedRepeatableAnnotation(env, element, annotationType);
		Map<String, String> map = new LinkedHashMap<>();
		for (AptAnnotationAttributes annotationAttribute : annotationAttributes) {
			ColumnPropertyAnnotationAttributes columnProperty = new ColumnPropertyAnnotationAttributes(annotationAttribute);
			String key = columnProperty.key();
			String value = columnProperty.value();
			if (key != null && !key.trim().isEmpty() && value != null && !value.trim().isEmpty()) {
				map.putIfAbsent(key.trim(), value.trim());
			}
		}
		return map;
	}

	@Data
	public static class ExpressionInfo {
		private TypeName declaredTypeName;
		private ClassName declaredClassName;
		private String fieldName;
		private TypeName fieldTypeName;
		private TypeName fieldRawTypeName;
		private int jdbcTypeValue;
		private String jdbcTypeName;
		private String expression;
		private String tableAliasPlaceholder;
		private boolean selectable = true;
		private int sortDirection;
		private int sortPosition;
		private Map<String, String> properties;

		public void readExpression(String fieldName, Expression expression) {
			this.fieldName = fieldName;
			this.expression = expression.value().trim();
			this.jdbcTypeName = expression.jdbcType().trim().toUpperCase();
			this.tableAliasPlaceholder = expression.tableAliasPlaceholder().trim().toUpperCase();
			this.selectable = expression.selectable();
			this.sortDirection = expression.sortDirection();
			this.sortPosition = expression.sortPosition();

			if (!this.jdbcTypeName.isEmpty()) {
				try {
					Field declaredField = Types.class.getDeclaredField(this.jdbcTypeName);
					this.jdbcTypeValue = declaredField.getInt(null);
				} catch (Exception ignore) {
				}
			}
		}
	}

	@Data
	public static class FieldInfo {
		private TypeName declaredTypeName;
		private ClassName declaredClassName;
		private String fieldName;
		private TypeName fieldTypeName;
		private TypeName fieldRawTypeName;
		private boolean id = false;
		private boolean autoIncrement = false;
		private String seqName;
		private String idSql;
		private int jdbcTypeValue;
		private String jdbcTypeName;
		private String columnName;
		private boolean nullable = false;
		private boolean insertable = true;
		private boolean updatable = true;
		private String updateDefault;
		private String insertDefault;
		private String updateDefaultSql;
		private String insertDefaultSql;
		private boolean version = false;
		private boolean logicDeleted = false;
		private boolean createTime = false;
		private boolean updateTime = false;
		private int sortDirection;
		private int sortPosition;
		private Map<String, String> properties;

		public void readColumn(String fieldName, Column column, Id id) {
			this.fieldName = fieldName;
			if (column != null) {
				this.columnName = column.value().trim();
				this.jdbcTypeName = column.jdbcType().trim().toUpperCase();
				this.nullable = column.nullable();
				this.insertable = column.insertable();
				this.updatable = column.updatable();
				this.updateDefault = column.updateDefault();
				this.insertDefault = column.insertDefault();
				this.updateDefaultSql = column.updateDefaultSql();
				this.insertDefaultSql = column.insertDefaultSql();
				this.version = column.version();
				this.logicDeleted = column.logicDeleted();
				this.createTime = column.createTime();
				this.updateTime = column.updateTime();
				this.sortDirection = column.sortDirection();
				this.sortPosition = column.sortPosition();
			}
			if (this.jdbcTypeName != null && !this.jdbcTypeName.isEmpty()) {
				try {
					Field declaredField = Types.class.getDeclaredField(this.jdbcTypeName);
					this.jdbcTypeValue = declaredField.getInt(null);
				} catch (Exception ignore) {
				}
			}
			if (id != null) {
				this.id = true;
				this.autoIncrement = id.auto();
				this.seqName = id.seqName();
				this.idSql = id.sql();
			}
			if (this.columnName == null || this.columnName.isEmpty()) {
				this.columnName = AnnotationProcessorUtils.camelToUnderlineUpperCase(this.fieldName);
			}
		}
	}
}
