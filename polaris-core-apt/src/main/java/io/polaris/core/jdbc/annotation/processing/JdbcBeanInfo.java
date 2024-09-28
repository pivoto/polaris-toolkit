package io.polaris.core.jdbc.annotation.processing;

import io.polaris.core.annotation.processing.AnnotationProcessorUtils;
import io.polaris.core.jdbc.ExpressionMeta;
import io.polaris.core.jdbc.annotation.Column;
import io.polaris.core.jdbc.annotation.Expression;
import io.polaris.core.jdbc.annotation.Id;
import io.polaris.core.jdbc.annotation.Table;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import lombok.Data;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Qt
 * @since  Aug 20, 2023
 */
@Data
public class JdbcBeanInfo {
	private TypeElement element;
	private String tableName;
	private String tableAlias;
	private String tableCatalog;
	private String tableSchema;
	private String metaSuffix;
	private TypeName beanTypeName;
	private ClassName beanClassName;
	private ClassName metaClassName;
	private List<FieldInfo> fields = new ArrayList<>();
	private List<ExpressionInfo> expressions =  new ArrayList<>();
	private boolean sqlGenerated;
	private String sqlSuffix;
	private ClassName sqlClassName;

	public JdbcBeanInfo(TypeElement element) {
		this.element = element;
		init();
	}

	private void init() {
		Table access = element.getAnnotation(Table.class);
		if (access == null) {
			return;
		}

		this.tableName = access.value();
		this.tableAlias = access.alias();
		this.tableCatalog = access.catalog();
		this.tableSchema = access.schema();
		this.metaSuffix = access.metaSuffix();
		this.sqlGenerated = access.sqlGenerated();
		this.sqlSuffix = access.sqlSuffix();

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
						Column column = variableElement.getAnnotation(Column.class);
						if (column != null) {
							if (column.ignored()) {
								continue;
							}
						}
						Expression expression = variableElement.getAnnotation(Expression.class);
						if (expression != null) {
							if (expression.value() != null && !expression.value().trim().isEmpty()){
								ExpressionInfo expressionInfo = new ExpressionInfo();
								expressionInfo.declaredTypeName = declaredTypeName;
								expressionInfo.declaredClassName = declaredClassName;
								expressionInfo.fieldTypeName = TypeName.get(variableElement.asType());
								expressionInfo.fieldRawTypeName =AnnotationProcessorUtils.rawType(expressionInfo.fieldTypeName);
								expressionInfo.readExpression(fieldName, expression);
								expressions.add(expressionInfo);
								continue;
							}
						}

						FieldInfo fieldInfo = new FieldInfo();
						fieldInfo.declaredTypeName = declaredTypeName;
						fieldInfo.declaredClassName = declaredClassName;
						fieldInfo.fieldTypeName = TypeName.get(variableElement.asType());
						fieldInfo.fieldRawTypeName =AnnotationProcessorUtils.rawType(fieldInfo.fieldTypeName);
						fieldInfo.readColumn(fieldName, column, variableElement.getAnnotation(Id.class));
						fields.add(fieldInfo);
					}
				}
			}
			// super class
			TypeMirror typeMirror = element.getSuperclass();
			if (typeMirror instanceof NoType) {
				break;
			}
			element = (TypeElement) ((DeclaredType) typeMirror).asElement();
		}
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

		public void readExpression(String fieldName, Expression expression) {
			this.fieldName = fieldName;
			this.expression = expression.value().trim();
			this.jdbcTypeName = expression.jdbcType().trim().toUpperCase();
			this.tableAliasPlaceholder = expression.tableAliasPlaceholder().trim().toUpperCase();
			this.selectable = expression.selectable();

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
		private int jdbcTypeValue;
		private String jdbcTypeName;
		private String columnName;
		private boolean nullable = false;
		private boolean insertable = true;
		private boolean updatable = true;
		private String updateDefault;
		private String insertDefault;
		private boolean version = false;
		private boolean logicDeleted = false;
		private boolean createTime = false;
		private boolean updateTime = false;

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
				this.version = column.version();
				this.logicDeleted = column.logicDeleted();
				this.createTime = column.createTime();
				this.updateTime = column.updateTime();
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
			}
			if (this.columnName == null || this.columnName.isEmpty()) {
				this.columnName = AnnotationProcessorUtils.camelToUnderlineUpperCase(this.fieldName);
			}
		}
	}
}
