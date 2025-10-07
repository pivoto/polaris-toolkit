package io.polaris.core.jdbc.annotation.processing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementScanner8;
import javax.tools.Diagnostic;

import io.polaris.core.annotation.processing.AnnotationProcessorUtils;
import io.polaris.core.annotation.processing.AptAnnotationAttributes;
import io.polaris.core.annotation.processing.AptAnnotations;
import io.polaris.core.annotation.processing.BaseProcessor;
import io.polaris.core.javapoet.*;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.EntityMeta;
import io.polaris.core.jdbc.ExpressionMeta;
import io.polaris.core.jdbc.annotation.Table;

/**
 * @author Qt
 * @since Aug 20, 2023
 */
@SuppressWarnings("all")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("io.polaris.core.jdbc.annotation.Table")
public class JdbcAnnotationProcessor extends BaseProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			return true;
		}
		processMerged(roundEnv);
		return true;
	}

	private void processMerged(RoundEnvironment roundEnv) {
		Set<? extends Element> rootElements = roundEnv.getRootElements();
		Map<Element, TableAnnotationAttributes> targets = new LinkedHashMap<>();
		TypeElement tableType = env.getElementUtils().getTypeElement(Table.class.getCanonicalName());
		ElementScanner8<Void, Void> scanner = new ElementScanner8<Void, Void>() {
			@Override
			public Void scan(Element element, Void p) {
				if (element instanceof TypeElement) {
					if (element.getKind() == ElementKind.CLASS) {
						if (!targets.containsKey(element)) {
							AptAnnotationAttributes annotationAttributes = AptAnnotations.getMergedAnnotation(env, element, tableType);
							if (annotationAttributes != null) {
								TableAnnotationAttributes table = new TableAnnotationAttributes(annotationAttributes);
								targets.put(element, table);
							}
						}
					}
				}
				return super.scan(element, p);
			}
		};
		for (Element element : rootElements) {
			scanner.scan(element);
		}

		targets.forEach((key, table) -> {
			TypeElement element = (TypeElement) key;
			JdbcBeanInfo beanInfo = new JdbcBeanInfo(this.env, element, table);
			generateMetaClass(beanInfo);
			generateSqlClass(beanInfo);
		});
	}

	private void processDeeply(RoundEnvironment roundEnv) {
		Set<? extends Element> rootElements = roundEnv.getRootElements();
		Map<Element, Table> targets = new LinkedHashMap<>();
		ElementScanner8<Void, Void> scanner = new ElementScanner8<Void, Void>() {
			@Override
			public Void scan(Element element, Void p) {
				if (element instanceof TypeElement) {
					if (element.getKind() == ElementKind.CLASS) {
						Table table = AnnotationProcessorUtils.getAnnotation(env.getElementUtils(), element, Table.class);
						if (table != null) {
							targets.put(element, table);
						}
					}
				}
				return super.scan(element, p);
			}
		};
		for (Element element : rootElements) {
			scanner.scan(element);
		}

		targets.forEach((key, table) -> {
			TypeElement element = (TypeElement) key;
			JdbcBeanInfo beanInfo = new JdbcBeanInfo(this.env, element, table);
			generateMetaClass(beanInfo);
			generateSqlClass(beanInfo);
		});
	}

	private void processDirectly(RoundEnvironment roundEnv) {
		Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Table.class);
		set.forEach(element -> {
			if (!(element instanceof TypeElement)) {
				return;
			}
			if (element.getKind() != ElementKind.CLASS) {
				return;
			}
			JdbcBeanInfo beanInfo = new JdbcBeanInfo(this.env, (TypeElement) element, null);
			generateMetaClass(beanInfo);
			generateSqlClass(beanInfo);
		});
	}


	private void generateMetaClass(JdbcBeanInfo beanInfo) {
		ClassName className = beanInfo.getMetaClassName();
		ClassName entityMetaClassName = ClassName.get(EntityMeta.class);
		ClassName columnMetaClassName = ClassName.get(ColumnMeta.class);
		ClassName expressionMetaClassName = ClassName.get(ExpressionMeta.class);

		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
			.addModifiers(Modifier.PUBLIC)
			.addSuperinterface(entityMetaClassName);

		// FieldName
		{
			ClassName fieldsClassName = className.nestedClass("FieldName");
			TypeSpec.Builder fieldsClassBuilder = TypeSpec.classBuilder(fieldsClassName)
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC);

			for (JdbcBeanInfo.FieldInfo field : beanInfo.getFields()) {
				fieldsClassBuilder.addField(
					FieldSpec.builder(ClassName.get(String.class), field.getFieldName(), Modifier.PUBLIC,
							Modifier.STATIC, Modifier.FINAL)
						.initializer("$S", field.getFieldName())
						.build()
				);
			}
			for (JdbcBeanInfo.ExpressionInfo field : beanInfo.getExpressions()) {
				fieldsClassBuilder.addField(
					FieldSpec.builder(ClassName.get(String.class), field.getFieldName(), Modifier.PUBLIC,
							Modifier.STATIC, Modifier.FINAL)
						.initializer("$S", field.getFieldName())
						.build()
				);
			}

			classBuilder.addType(fieldsClassBuilder.build());
		}
		// ColumnName
		{
			ClassName columnsClassName = className.nestedClass("ColumnName");
			TypeSpec.Builder columnsClassBuilder = TypeSpec.classBuilder(columnsClassName)
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC);

			for (JdbcBeanInfo.FieldInfo field : beanInfo.getFields()) {
				columnsClassBuilder.addField(
					FieldSpec.builder(ClassName.get(String.class), field.getFieldName(), Modifier.PUBLIC,
							Modifier.STATIC, Modifier.FINAL)
						.initializer("$S", field.getColumnName())
						.build()
				);
			}
			for (JdbcBeanInfo.ExpressionInfo field : beanInfo.getExpressions()) {
				columnsClassBuilder.addField(
					FieldSpec.builder(ClassName.get(String.class), field.getFieldName(), Modifier.PUBLIC,
							Modifier.STATIC, Modifier.FINAL)
						.initializer("$S", field.getExpression())
						.build()
				);
			}

			classBuilder.addType(columnsClassBuilder.build());
		}

		ParameterizedTypeName columnMetaMapTypeName = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), columnMetaClassName);
		ParameterizedTypeName expressionMetaMapTypeName = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), expressionMetaClassName);
		classBuilder.addField(
			FieldSpec.builder(ClassName.get(String.class),
				"SCHEMA", Modifier.FINAL, Modifier.PUBLIC, Modifier.STATIC
			).initializer("$S", beanInfo.getTableSchema()).build()
		);
		classBuilder.addField(
			FieldSpec.builder(ClassName.get(String.class),
				"CATALOG", Modifier.FINAL, Modifier.PUBLIC, Modifier.STATIC
			).initializer("$S", beanInfo.getTableCatalog()).build()
		);
		classBuilder.addField(
			FieldSpec.builder(ClassName.get(String.class),
				"TABLE", Modifier.FINAL, Modifier.PUBLIC, Modifier.STATIC
			).initializer("$S", beanInfo.getTableName()).build()
		);
		classBuilder.addField(
			FieldSpec.builder(ClassName.get(String.class),
				"ALIAS", Modifier.FINAL, Modifier.PUBLIC, Modifier.STATIC
			).initializer("$S", beanInfo.getTableAlias()).build()
		);
		classBuilder.addField(
			FieldSpec.builder(columnMetaMapTypeName,
				"COLUMNS", Modifier.FINAL, Modifier.PUBLIC, Modifier.STATIC
			).build()
		);
		classBuilder.addField(
			FieldSpec.builder(expressionMetaMapTypeName,
				"EXPRESSIONS", Modifier.FINAL, Modifier.PUBLIC, Modifier.STATIC
			).build()
		);
		{
			CodeBlock.Builder codeBlock = CodeBlock.builder()
				.addStatement("$T map = new $T<>()", columnMetaMapTypeName, ClassName.get(LinkedHashMap.class));
			for (JdbcBeanInfo.FieldInfo field : beanInfo.getFields()) {
				StringBuilder format = new StringBuilder();
				format.append("map.put($S,$T.builder()")
					.append(".schema($S)")
					.append(".catalog($S)")
					.append(".tableName($S)")
					.append(".fieldName($S)")
					.append(".fieldType($T.class)")
					.append(".columnName($S)")
					.append(".jdbcType($S)")
					.append(".jdbcTypeValue($L)")
					.append(".updateDefault($S)")
					.append(".insertDefault($S)")
					.append(".nullable($L)")
					.append(".insertable($L)")
					.append(".updatable($L)")
					.append(".version($L)")
					.append(".logicDeleted($L)")
					.append(".createTime($L)")
					.append(".updateTime($L)")
					.append(".primaryKey($L)")
					.append(".autoIncrement($L)")
					.append(".seqName($S)")
					.append(".idSql($S)")
					.append(".updateDefaultSql($S)")
					.append(".insertDefaultSql($S)");
				List<Object> args = new ArrayList<>();
				args.add(field.getFieldName());
				args.add(columnMetaClassName);
				args.add(beanInfo.getTableSchema());
				args.add(beanInfo.getTableCatalog());
				args.add(beanInfo.getTableName());
				args.add(field.getFieldName());
				args.add(field.getFieldRawTypeName());
				args.add(field.getColumnName());
				args.add(field.getJdbcTypeName());
				args.add(field.getJdbcTypeValue());
				args.add(field.getUpdateDefault());
				args.add(field.getInsertDefault());
				args.add(field.isNullable());
				args.add(field.isInsertable());
				args.add(field.isUpdatable());
				args.add(field.isVersion());
				args.add(field.isLogicDeleted());
				args.add(field.isCreateTime());
				args.add(field.isUpdateTime());
				args.add(field.isId());
				args.add(field.isAutoIncrement());
				args.add(field.getSeqName());
				args.add(field.getIdSql());
				args.add(field.getUpdateDefaultSql());
				args.add(field.getInsertDefaultSql());
				if (field.getProperties() != null) {
					for (Map.Entry<String, String> entry : field.getProperties().entrySet()) {
						args.add(entry.getKey());
						args.add(entry.getValue());
						format.append(".properties($S,$S)");
					}
				}
				format.append(".build())");
				codeBlock.addStatement(format.toString(), args.toArray(new Object[0]));
			}
			classBuilder.addStaticBlock(
				codeBlock.addStatement("COLUMNS = $T.unmodifiableMap(map)", ClassName.get(Collections.class))
					.build()
			);
		}
		{
			CodeBlock.Builder codeBlock = CodeBlock.builder()
				.addStatement("$T map = new $T<>()", expressionMetaMapTypeName, ClassName.get(LinkedHashMap.class));
			for (JdbcBeanInfo.ExpressionInfo field : beanInfo.getExpressions()) {
				StringBuilder format = new StringBuilder()
					.append("map.put($S,$T.builder()")
					.append(".schema($S)")
					.append(".catalog($S)")
					.append(".tableName($S)")
					.append(".fieldName($S)")
					.append(".fieldType($T.class)")
					.append(".expression($S)")
					.append(".jdbcType($S)")
					.append(".jdbcTypeValue($L)")
					.append(".tableAliasPlaceholder($S)")
					.append(".selectable($L)");
				List<Object> args = new ArrayList<>();
				args.add(field.getFieldName());
				args.add(expressionMetaClassName);
				args.add(beanInfo.getTableSchema());
				args.add(beanInfo.getTableCatalog());
				args.add(beanInfo.getTableName());
				args.add(field.getFieldName());
				args.add(field.getFieldRawTypeName());
				args.add(field.getExpression());
				args.add(field.getJdbcTypeName());
				args.add(field.getJdbcTypeValue());
				args.add(field.getTableAliasPlaceholder());
				args.add(field.isSelectable());
				if (field.getProperties() != null) {
					for (Map.Entry<String, String> entry : field.getProperties().entrySet()) {
						args.add(entry.getKey());
						args.add(entry.getValue());
						format.append(".properties($S,$S)");
					}
				}
				format.append(".build())");
				codeBlock.addStatement(format.toString(), args.toArray(new Object[0]));
			}
			classBuilder.addStaticBlock(
				codeBlock.addStatement("EXPRESSIONS = $T.unmodifiableMap(map)", ClassName.get(Collections.class))
					.build()
			);
		}


		JavaFile javaFile = JavaFile.builder(className.packageName(), classBuilder.build()).build();
		try {
			javaFile.writeTo(filer);
		} catch (IOException t) {
			messager.printMessage(Diagnostic.Kind.ERROR, t.toString());
			t.printStackTrace();
		}
	}


	private void generateSqlClass(JdbcBeanInfo beanInfo) {
		if (!beanInfo.isSqlGenerated()) {
			return;
		}
		TypeName beanTypeName = beanInfo.getBeanTypeName();
		ClassName beanClassName = beanInfo.getBeanClassName();
		ClassName sqlClassName = beanInfo.getSqlClassName();

		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(sqlClassName)
			.addModifiers(Modifier.PUBLIC);

		ClassName classNameTextNode = ClassName.get("io.polaris.core.jdbc.sql.node", "TextNode");
		ClassName classNameBaseSelect = ClassName.get("io.polaris.core.jdbc.sql.statement", "SelectStatement");
		ClassName classNameBaseInsert = ClassName.get("io.polaris.core.jdbc.sql.statement", "InsertStatement");
		ClassName classNameBaseUpdate = ClassName.get("io.polaris.core.jdbc.sql.statement", "UpdateStatement");
		ClassName classNameBaseDelete = ClassName.get("io.polaris.core.jdbc.sql.statement", "DeleteStatement");
		ClassName classNameSegment = ClassName.get("io.polaris.core.jdbc.sql.statement", "Segment");
		ClassName classNameBaseCol = ClassName.get("io.polaris.core.jdbc.sql.statement.segment", "SelectSegment");
		ClassName classNameBaseTable = ClassName.get("io.polaris.core.jdbc.sql.statement.segment", "TableSegment");
		ClassName classNameBaseJoin = ClassName.get("io.polaris.core.jdbc.sql.statement.segment", "JoinSegment");
		ClassName classNameBaseGroupBy = ClassName.get("io.polaris.core.jdbc.sql.statement.segment", "GroupBySegment");
		ClassName classNameBaseOrderBy = ClassName.get("io.polaris.core.jdbc.sql.statement.segment", "OrderBySegment");
		ClassName classNameBaseAnd = ClassName.get("io.polaris.core.jdbc.sql.statement.segment", "AndSegment");
		ClassName classNameBaseOr = ClassName.get("io.polaris.core.jdbc.sql.statement.segment", "OrSegment");
		ClassName classNameBaseCriterion = ClassName.get("io.polaris.core.jdbc.sql.statement.segment", "CriterionSegment");
		ClassName classNameJoinBuilder = ClassName.get("io.polaris.core.jdbc.sql.statement.segment", "JoinBuilder");
		ClassName classNameColumnSegment = ClassName.get("io.polaris.core.jdbc.sql.statement.segment", "ColumnSegment");

		ClassName classNameSelect = sqlClassName.nestedClass("Select");
		ClassName classNameInsert = sqlClassName.nestedClass("Insert");
		ClassName classNameUpdate = sqlClassName.nestedClass("Update");
		ClassName classNameDelete = sqlClassName.nestedClass("Delete");
		ClassName classNameSelectCol = sqlClassName.nestedClass("SelectCol");
		ClassName classNameJoin = sqlClassName.nestedClass("Join");
		ClassName classNameGroupBy = sqlClassName.nestedClass("GroupBy");
		ClassName classNameOrderBy = sqlClassName.nestedClass("OrderBy");
		ClassName classNameAnd = sqlClassName.nestedClass("And");
		ClassName classNameOr = sqlClassName.nestedClass("Or");

		// region inner class

		// region select
		{
			TypeSpec.Builder nestedBuilder = TypeSpec.classBuilder(classNameSelect)
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.superclass(ParameterizedTypeName.get(
					classNameBaseSelect, classNameSelect
				));
			{
				// 构造
				nestedBuilder.addMethod(MethodSpec.constructorBuilder()
					.addModifiers(Modifier.PUBLIC)
					.addStatement("super($T.class)", beanClassName)
					.build());
				nestedBuilder.addMethod(MethodSpec.constructorBuilder()
					.addModifiers(Modifier.PUBLIC)
					.addParameter(ParameterSpec.builder(ClassName.get(String.class), "alias").build())
					.addStatement("super($T.class, alias)", beanClassName)
					.build());
			}
			{
				nestedBuilder.addMethod(MethodSpec.methodBuilder("buildSelect")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PROTECTED)
					.returns(ParameterizedTypeName.get(classNameSelectCol, classNameSelect))
					.addStatement("return new $T<>(getThis(), getTable())", classNameSelectCol)
					.build());
				nestedBuilder.addMethod(MethodSpec.methodBuilder("buildWhere")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PROTECTED)
					.returns(ParameterizedTypeName.get(classNameAnd, classNameSelect))
					.addStatement("return new $T<>(getThis(), getTable())", classNameAnd)
					.build());
				nestedBuilder.addMethod(MethodSpec.methodBuilder("buildGroupBy")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PROTECTED)
					.returns(ParameterizedTypeName.get(classNameGroupBy, classNameSelect))
					.addStatement("return new $T<>(getThis(), getTable())", classNameGroupBy)
					.build());
				nestedBuilder.addMethod(MethodSpec.methodBuilder("buildOrderBy")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PROTECTED)
					.returns(ParameterizedTypeName.get(classNameOrderBy, classNameSelect))
					.addStatement("return new $T<>(getThis(), getTable())", classNameOrderBy)
					.build());
				nestedBuilder.addMethod(MethodSpec.methodBuilder("select")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameSelectCol, classNameSelect))
					.addStatement("return super.select()")
					.build());
				nestedBuilder.addMethod(MethodSpec.methodBuilder("where")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameAnd, classNameSelect))
					.addStatement("return super.where()")
					.build());
				nestedBuilder.addMethod(MethodSpec.methodBuilder("groupBy")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameGroupBy, classNameSelect))
					.addStatement("return super.groupBy()")
					.build());
				nestedBuilder.addMethod(MethodSpec.methodBuilder("having")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameAnd, classNameSelect))
					.addStatement("return super.having()")
					.build());
				nestedBuilder.addMethod(MethodSpec.methodBuilder("orderBy")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameOrderBy, classNameSelect))
					.addStatement("return super.orderBy()")
					.build());
			}
			{
				for (JdbcBeanInfo.FieldInfo field : beanInfo.getFields()) {
					String fieldName = field.getFieldName();
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(classNameSelect)
						.addStatement("return select($S)", fieldName)
						.build());
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(classNameSelect)
						.addParameter(ParameterSpec.builder(ClassName.get(String.class), "alias").build())
						.addStatement("return select($S, alias)", fieldName)
						.build());
				}
				// 支持表达式字段
				for (JdbcBeanInfo.ExpressionInfo expression : beanInfo.getExpressions()) {
					String fieldName = expression.getFieldName();
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(classNameSelect)
						.addStatement("return select($S)", fieldName)
						.build());
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(classNameSelect)
						.addParameter(ParameterSpec.builder(ClassName.get(String.class), "alias").build())
						.addStatement("return select($S, alias)", fieldName)
						.build());
				}
			}
			classBuilder.addType(nestedBuilder.build());
		}
		// endregion select
		// region insert
		{
			TypeSpec.Builder nestedBuilder = TypeSpec.classBuilder(classNameInsert)
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.superclass(ParameterizedTypeName.get(
					classNameBaseInsert, classNameInsert
				));

			nestedBuilder.addMethod(MethodSpec.constructorBuilder()
				.addModifiers(Modifier.PUBLIC)
				.addStatement("super($T.class)", beanClassName)
				.build());
			nestedBuilder.addMethod(MethodSpec.constructorBuilder()
				.addModifiers(Modifier.PUBLIC)
				.addParameter(ParameterSpec.builder(ClassName.get(String.class), "alias").build())
				.addStatement("super($T.class, alias)", beanClassName)
				.build());

			{
				for (JdbcBeanInfo.FieldInfo field : beanInfo.getFields()) {
					String fieldName = field.getFieldName();

					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(ParameterizedTypeName.get(classNameColumnSegment,
							classNameInsert, WildcardTypeName.subtypeOf(TypeName.OBJECT)))
						.addStatement("return column($S)", fieldName)
						.build());

					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(classNameInsert)
						.addParameter(ParameterSpec.builder(TypeName.OBJECT, "value").build())
						.addStatement("return column($S, value)", fieldName)
						.build());

					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(classNameInsert)
						.addParameter(ParameterSpec.builder(TypeName.OBJECT, "value").build())
						.addParameter(ParameterSpec.builder(
							ParameterizedTypeName.get(
								ClassName.get(BiPredicate.class), ClassName.get(String.class), TypeName.OBJECT
							)
							, "predicate").build())
						.addStatement("return column($S, value, predicate)", fieldName)
						.build());
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(classNameInsert)
						.addParameter(ParameterSpec.builder(TypeName.OBJECT, "value").build())
						.addParameter(ParameterSpec.builder(
							ParameterizedTypeName.get(
								ClassName.get(Supplier.class), ClassName.get(Boolean.class)
							)
							, "predicate").build())
						.addStatement("return column($S, value, predicate)", fieldName)
						.build());
				}
			}
			classBuilder.addType(nestedBuilder.build());
		}
		// endregion insert
		// region update
		{
			TypeSpec.Builder nestedBuilder = TypeSpec.classBuilder(classNameUpdate)
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.superclass(ParameterizedTypeName.get(
					classNameBaseUpdate, classNameUpdate
				));
			;
			{
				nestedBuilder.addMethod(MethodSpec.constructorBuilder()
					.addModifiers(Modifier.PUBLIC)
					.addStatement("super($T.class)", beanClassName)
					.build());
				nestedBuilder.addMethod(MethodSpec.constructorBuilder()
					.addModifiers(Modifier.PUBLIC)
					.addParameter(ParameterSpec.builder(ClassName.get(String.class), "alias").build())
					.addStatement("super($T.class, alias)", beanClassName)
					.build());
			}
			{
				nestedBuilder.addMethod(MethodSpec.methodBuilder("buildWhere")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PROTECTED)
					.returns(ParameterizedTypeName.get(classNameAnd, classNameUpdate))
					.addStatement("return new $T<>(getThis(), getTable())", classNameAnd)
					.build());
				nestedBuilder.addMethod(MethodSpec.methodBuilder("where")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameAnd, classNameUpdate))
					.addStatement("return super.where()")
					.build());
			}
			{
				for (JdbcBeanInfo.FieldInfo field : beanInfo.getFields()) {
					String fieldName = field.getFieldName();
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(ParameterizedTypeName.get(classNameColumnSegment,
							classNameUpdate, WildcardTypeName.subtypeOf(TypeName.OBJECT)))
						.addStatement("return column($S)", fieldName)
						.build());

					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(classNameUpdate)
						.addParameter(ParameterSpec.builder(TypeName.OBJECT, "value").build())
						.addStatement("return column($S, value)", fieldName)
						.build());
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(classNameUpdate)
						.addParameter(ParameterSpec.builder(TypeName.OBJECT, "value").build())
						.addParameter(ParameterSpec.builder(
							ParameterizedTypeName.get(
								ClassName.get(BiPredicate.class), ClassName.get(String.class), TypeName.OBJECT
							)
							, "predicate").build())
						.addStatement("return column($S, value, predicate)", fieldName)
						.build());
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(classNameUpdate)
						.addParameter(ParameterSpec.builder(TypeName.OBJECT, "value").build())
						.addParameter(ParameterSpec.builder(
							ParameterizedTypeName.get(
								ClassName.get(Supplier.class), ClassName.get(Boolean.class)
							)
							, "predicate").build())
						.addStatement("return column($S, value, predicate)", fieldName)
						.build());
				}
			}

			classBuilder.addType(nestedBuilder.build());
		}
		// endregion update
		// region delete
		{
			TypeSpec.Builder nestedBuilder = TypeSpec.classBuilder(classNameDelete)
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.superclass(ParameterizedTypeName.get(
					classNameBaseDelete, classNameDelete
				));
			{
				// 构造
				nestedBuilder.addMethod(MethodSpec.constructorBuilder()
					.addModifiers(Modifier.PUBLIC)
					.addStatement("super($T.class)", beanClassName)
					.build());
				nestedBuilder.addMethod(MethodSpec.constructorBuilder()
					.addModifiers(Modifier.PUBLIC)
					.addParameter(ParameterSpec.builder(ClassName.get(String.class), "alias").build())
					.addStatement("super($T.class, alias)", beanClassName)
					.build());
			}
			{
				nestedBuilder.addMethod(MethodSpec.methodBuilder("buildWhere")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PROTECTED)
					.returns(ParameterizedTypeName.get(classNameAnd, classNameDelete))
					.addStatement("return new $T<>(getThis(), getTable())", classNameAnd)
					.build());
				nestedBuilder.addMethod(MethodSpec.methodBuilder("where")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameAnd, classNameDelete))
					.addStatement("return super.where()")
					.build());

			}

			classBuilder.addType(nestedBuilder.build());
		}
		// endregion delete

		// region selectCol
		{
			TypeSpec.Builder nestedBuilder = TypeSpec.classBuilder(classNameSelectCol)
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.addTypeVariable(TypeVariableName.get("O", ParameterizedTypeName.get(classNameSegment, TypeVariableName.get("O"))))
				.superclass(ParameterizedTypeName.get(
					classNameBaseCol, TypeVariableName.get("O"), ParameterizedTypeName.get(classNameSelectCol, TypeVariableName.get("O"))
				));
			{
				nestedBuilder.addMethod(MethodSpec.constructorBuilder()
					.addModifiers(Modifier.PUBLIC)
					.addParameter(ParameterSpec.builder(TypeVariableName.get("O"), "owner").build())
					.addParameter(ParameterSpec.builder(
						ParameterizedTypeName.get(classNameBaseTable,
							WildcardTypeName.subtypeOf(
								ParameterizedTypeName.get(classNameBaseTable, WildcardTypeName.subtypeOf(TypeName.OBJECT))
							)
						), "table").build())
					.addStatement("super(owner, table)")
					.build());
			}

			for (JdbcBeanInfo.FieldInfo field : beanInfo.getFields()) {
				String fieldName = field.getFieldName();
				nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameSelectCol, TypeVariableName.get("O")))
					.addStatement("return column($S)", fieldName)
					.build());
			}
			// 支持表达式字段
			for (JdbcBeanInfo.ExpressionInfo expression : beanInfo.getExpressions()) {
				String fieldName = expression.getFieldName();
				nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameSelectCol, TypeVariableName.get("O")))
					.addStatement("return column($S)", fieldName)
					.build());
			}
			classBuilder.addType(nestedBuilder.build());
		}
		// endregion selectCol

		// region join
		{
			TypeSpec.Builder nestedBuilder = TypeSpec.classBuilder(classNameJoin)
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.addTypeVariable(TypeVariableName.get("O",
					ParameterizedTypeName.get(classNameBaseSelect, TypeVariableName.get("O"))))
				.superclass(ParameterizedTypeName.get(
					classNameBaseJoin, TypeVariableName.get("O"),
					ParameterizedTypeName.get(classNameJoin, TypeVariableName.get("O"))
				));
			// construct
			{
				nestedBuilder.addMethod(MethodSpec.constructorBuilder()
					.addModifiers(Modifier.PUBLIC)
					.addParameter(ParameterSpec.builder(TypeVariableName.get("O"), "owner").build())
					.addParameter(ParameterSpec.builder(classNameTextNode, "conj").build())
					.addParameter(ParameterSpec.builder(
						ParameterizedTypeName.get(ClassName.get(Class.class),
							WildcardTypeName.subtypeOf(TypeName.OBJECT)
						), "entityClass").build())
					.addParameter(ParameterSpec.builder(ClassName.get(String.class), "alias").build())
					.addStatement("super(owner, conj, entityClass, alias)")
					.build());
				nestedBuilder.addMethod(MethodSpec.constructorBuilder()
					.addModifiers(Modifier.PUBLIC)
					.addParameter(ParameterSpec.builder(TypeVariableName.get("O"), "owner").build())
					.addParameter(ParameterSpec.builder(classNameTextNode, "conj").build())
					.addParameter(ParameterSpec.builder(
						ParameterizedTypeName.get(classNameBaseSelect,
							WildcardTypeName.subtypeOf(TypeName.OBJECT)
						), "select").build())
					.addParameter(ParameterSpec.builder(ClassName.get(String.class), "alias").build())
					.addStatement("super(owner, conj, select, alias)")
					.build());
			}
			// builder
			{
				nestedBuilder.addMethod(MethodSpec.methodBuilder("builder")
					.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
					.addTypeVariable(
						TypeVariableName.get("O", ParameterizedTypeName.get(classNameBaseSelect, TypeVariableName.get("O")))
					)
					.returns(
						ParameterizedTypeName.get(
							classNameJoinBuilder,
							TypeVariableName.get("O"),
							ParameterizedTypeName.get(classNameJoin, TypeVariableName.get("O"))
						)
					)
					.addStatement("return (statement, conj, alias) -> new $T<>(statement, conj, $T.class, alias)", classNameJoin, beanClassName)
					.build());
			}
			// override
			{
				nestedBuilder.addMethod(MethodSpec.methodBuilder("buildSelect")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PROTECTED)
					.returns(ParameterizedTypeName.get(classNameSelectCol,
						ParameterizedTypeName.get(classNameJoin, TypeVariableName.get("O"))
					))
					.addStatement("return new $T<>(getThis(), getTable())", classNameSelectCol)
					.build());
				nestedBuilder.addMethod(MethodSpec.methodBuilder("buildWhere")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PROTECTED)
					.returns(ParameterizedTypeName.get(classNameAnd,
						ParameterizedTypeName.get(classNameJoin, TypeVariableName.get("O"))
					))
					.addStatement("return new $T<>(getThis(), getTable())", classNameAnd)
					.build());
				nestedBuilder.addMethod(MethodSpec.methodBuilder("buildGroupBy")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PROTECTED)
					.returns(ParameterizedTypeName.get(classNameGroupBy, ParameterizedTypeName.get(classNameJoin, TypeVariableName.get("O"))
					))
					.addStatement("return new $T<>(getThis(), getTable())", classNameGroupBy)
					.build());
				nestedBuilder.addMethod(MethodSpec.methodBuilder("buildOrderBy")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PROTECTED)
					.returns(ParameterizedTypeName.get(classNameOrderBy, ParameterizedTypeName.get(classNameJoin, TypeVariableName.get("O"))
					))
					.addStatement("return new $T<>(getThis(), getTable())", classNameOrderBy)
					.build());

				nestedBuilder.addMethod(MethodSpec.methodBuilder("on")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameAnd,
						ParameterizedTypeName.get(classNameJoin, TypeVariableName.get("O"))
					))
					.addStatement("return super.on()")
					.build());
				nestedBuilder.addMethod(MethodSpec.methodBuilder("where")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameAnd,
						ParameterizedTypeName.get(classNameJoin, TypeVariableName.get("O"))
					))
					.addStatement("return super.where()")
					.build());
				nestedBuilder.addMethod(MethodSpec.methodBuilder("select")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameSelectCol,
						ParameterizedTypeName.get(classNameJoin, TypeVariableName.get("O"))
					))
					.addStatement("return super.select()")
					.build());

				nestedBuilder.addMethod(MethodSpec.methodBuilder("groupBy")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameGroupBy, ParameterizedTypeName.get(classNameJoin, TypeVariableName.get("O"))
					))
					.addStatement("return super.groupBy()")
					.build());
				nestedBuilder.addMethod(MethodSpec.methodBuilder("having")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameAnd, ParameterizedTypeName.get(classNameJoin, TypeVariableName.get("O"))
					))
					.addStatement("return super.having()")
					.build());
				nestedBuilder.addMethod(MethodSpec.methodBuilder("orderBy")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameOrderBy, ParameterizedTypeName.get(classNameJoin, TypeVariableName.get("O"))
					))
					.addStatement("return super.orderBy()")
					.build());
			}
			// fields
			{
				for (JdbcBeanInfo.FieldInfo field : beanInfo.getFields()) {
					String fieldName = field.getFieldName();
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(ParameterizedTypeName.get(classNameJoin, TypeVariableName.get("O")))
						.addStatement("return select($S)", fieldName)
						.build());
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(ParameterizedTypeName.get(classNameJoin, TypeVariableName.get("O")))
						.addParameter(ParameterSpec.builder(ClassName.get(String.class), "alias").build())
						.addStatement("return select($S, alias)", fieldName)
						.build());
				}
				// 支持表达式字段
				for (JdbcBeanInfo.ExpressionInfo expression : beanInfo.getExpressions()) {
					String fieldName = expression.getFieldName();
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(ParameterizedTypeName.get(classNameJoin, TypeVariableName.get("O")))
						.addStatement("return select($S)", fieldName)
						.build());
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(ParameterizedTypeName.get(classNameJoin, TypeVariableName.get("O")))
						.addParameter(ParameterSpec.builder(ClassName.get(String.class), "alias").build())
						.addStatement("return select($S, alias)", fieldName)
						.build());
				}
			}
			classBuilder.addType(nestedBuilder.build());
		}
		// endregion join
		// region group by
		{
			TypeSpec.Builder nestedBuilder = TypeSpec.classBuilder(classNameGroupBy)
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.addTypeVariable(TypeVariableName.get("O",
					ParameterizedTypeName.get(classNameSegment, TypeVariableName.get("O"))))
				.superclass(ParameterizedTypeName.get(
					classNameBaseGroupBy, TypeVariableName.get("O"),
					ParameterizedTypeName.get(classNameGroupBy, TypeVariableName.get("O"))
				));
			// construct
			{
				nestedBuilder.addMethod(MethodSpec.constructorBuilder()
					.addModifiers(Modifier.PUBLIC)
					.addParameter(ParameterSpec.builder(TypeVariableName.get("O"), "owner").build())
					.addParameter(ParameterSpec.builder(
						ParameterizedTypeName.get(classNameBaseTable,
							WildcardTypeName.subtypeOf(TypeName.OBJECT)
						), "table").build())
					.addStatement("super(owner, table)")
					.build());
			}
			// fields
			{
				for (JdbcBeanInfo.FieldInfo field : beanInfo.getFields()) {
					String fieldName = field.getFieldName();
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(ParameterizedTypeName.get(classNameGroupBy, TypeVariableName.get("O")))
						.addStatement("return column($S)", fieldName)
						.build());
				}
				// 支持表达式字段
				for (JdbcBeanInfo.ExpressionInfo expression : beanInfo.getExpressions()) {
					String fieldName = expression.getFieldName();
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(ParameterizedTypeName.get(classNameGroupBy, TypeVariableName.get("O")))
						.addStatement("return column($S)", fieldName)
						.build());
				}
			}
			classBuilder.addType(nestedBuilder.build());
		}
		// endregion group by
		// region order by
		{
			TypeSpec.Builder nestedBuilder = TypeSpec.classBuilder(classNameOrderBy)
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.addTypeVariable(TypeVariableName.get("O",
					ParameterizedTypeName.get(classNameSegment, TypeVariableName.get("O"))))
				.superclass(ParameterizedTypeName.get(
					classNameBaseOrderBy, TypeVariableName.get("O"),
					ParameterizedTypeName.get(classNameOrderBy, TypeVariableName.get("O"))
				));
			// construct
			{
				nestedBuilder.addMethod(MethodSpec.constructorBuilder()
					.addModifiers(Modifier.PUBLIC)
					.addParameter(ParameterSpec.builder(TypeVariableName.get("O"), "owner").build())
					.addParameter(ParameterSpec.builder(
						ParameterizedTypeName.get(classNameBaseTable,
							WildcardTypeName.subtypeOf(TypeName.OBJECT)
						), "table").build())
					.addStatement("super(owner, table)")
					.build());
			}
			// fields
			{
				for (JdbcBeanInfo.FieldInfo field : beanInfo.getFields()) {
					String fieldName = field.getFieldName();
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(ParameterizedTypeName.get(classNameOrderBy, TypeVariableName.get("O")))
						.addStatement("return column($S)", fieldName)
						.build());
				}
				// 支持表达式字段
				for (JdbcBeanInfo.ExpressionInfo expression : beanInfo.getExpressions()) {
					String fieldName = expression.getFieldName();
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(ParameterizedTypeName.get(classNameOrderBy, TypeVariableName.get("O")))
						.addStatement("return column($S)", fieldName)
						.build());
				}
			}
			classBuilder.addType(nestedBuilder.build());
		}
		// endregion order by
		// region and
		{
			ParameterizedTypeName andWithO = ParameterizedTypeName.get(classNameAnd, TypeVariableName.get("O"));
			TypeSpec.Builder nestedBuilder = TypeSpec.classBuilder(classNameAnd)
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.addTypeVariable(TypeVariableName.get("O",
					ParameterizedTypeName.get(classNameSegment, TypeVariableName.get("O"))))
				.superclass(ParameterizedTypeName.get(
					classNameBaseAnd, TypeVariableName.get("O"),
					andWithO
				));
			// construct
			{
				nestedBuilder.addMethod(MethodSpec.constructorBuilder()
					.addModifiers(Modifier.PUBLIC)
					.addTypeVariable(TypeVariableName.get("T",
						ParameterizedTypeName.get(classNameBaseTable, WildcardTypeName.subtypeOf(TypeName.OBJECT))
					))
					.addParameter(ParameterSpec.builder(TypeVariableName.get("O"), "owner").build())
					.addParameter(ParameterSpec.builder(
						TypeVariableName.get("T"), "table").build())
					.addStatement("super(owner, table)")
					.build());
			}
			// override
			{
				nestedBuilder.addMethod(MethodSpec.methodBuilder("and")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameAnd, andWithO
					))
					.addStatement("$T x = new $T<>(getThis(), getTable())",
						ParameterizedTypeName.get(classNameAnd, andWithO),
						classNameAnd
					)
					.addStatement("addCriterion(new $T<>(getThis(), x))", classNameBaseCriterion)
					.addStatement("return x")
					.build());

				nestedBuilder.addMethod(MethodSpec.methodBuilder("or")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameOr, andWithO))
					.addStatement("$T x = new $T<>(getThis(), getTable())",
						ParameterizedTypeName.get(classNameOr, andWithO),
						classNameOr
					)
					.addStatement("addCriterion(new $T<>(getThis(), x))", classNameBaseCriterion)
					.addStatement("return x")
					.build());
			}
			// fields
			{
				for (JdbcBeanInfo.FieldInfo field : beanInfo.getFields()) {
					String fieldName = field.getFieldName();
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(ParameterizedTypeName.get(classNameBaseCriterion,
							andWithO, WildcardTypeName.subtypeOf(TypeName.OBJECT)))
						.addStatement("return column($S)", fieldName)
						.build());
				}
				// 支持表达式字段
				for (JdbcBeanInfo.ExpressionInfo expression : beanInfo.getExpressions()) {
					String fieldName = expression.getFieldName();
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(ParameterizedTypeName.get(classNameBaseCriterion,
							andWithO, WildcardTypeName.subtypeOf(TypeName.OBJECT)))
						.addStatement("return column($S)", fieldName)
						.build());
				}
			}
			classBuilder.addType(nestedBuilder.build());
		}
		// endregion and
		// region or
		{
			ParameterizedTypeName orWithO = ParameterizedTypeName.get(classNameOr, TypeVariableName.get("O"));
			TypeSpec.Builder nestedBuilder = TypeSpec.classBuilder(classNameOr)
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.addTypeVariable(TypeVariableName.get("O",
					ParameterizedTypeName.get(classNameSegment, TypeVariableName.get("O"))))
				.superclass(ParameterizedTypeName.get(classNameBaseOr, TypeVariableName.get("O"), orWithO
				));
			// construct
			{
				nestedBuilder.addMethod(MethodSpec.constructorBuilder()
					.addModifiers(Modifier.PUBLIC)
					.addTypeVariable(TypeVariableName.get("T",
						ParameterizedTypeName.get(classNameBaseTable, WildcardTypeName.subtypeOf(TypeName.OBJECT))
					))
					.addParameter(ParameterSpec.builder(TypeVariableName.get("O"), "owner").build())
					.addParameter(ParameterSpec.builder(
						TypeVariableName.get("T"), "table").build())
					.addStatement("super(owner, table)")
					.build());
			}
			// override
			{
				nestedBuilder.addMethod(MethodSpec.methodBuilder("and")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameAnd
						, orWithO
					))
					.addStatement("$T x = new $T<>(getThis(), getTable())",
						ParameterizedTypeName.get(classNameAnd, orWithO),
						classNameAnd
					)
					.addStatement("addCriterion(new $T<>(getThis(), x))", classNameBaseCriterion)
					.addStatement("return x")
					.build());

				nestedBuilder.addMethod(MethodSpec.methodBuilder("or")
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC)
					.returns(ParameterizedTypeName.get(classNameOr, orWithO))
					.addStatement("$T x = new $T<>(getThis(), getTable())",
						ParameterizedTypeName.get(classNameOr, orWithO),
						classNameOr
					)
					.addStatement("addCriterion(new $T<>(getThis(), x))", classNameBaseCriterion)
					.addStatement("return x")
					.build());
			}
			// fields
			{
				for (JdbcBeanInfo.FieldInfo field : beanInfo.getFields()) {
					String fieldName = field.getFieldName();
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(ParameterizedTypeName.get(classNameBaseCriterion,
							orWithO, WildcardTypeName.subtypeOf(TypeName.OBJECT)))
						.addStatement("return column($S)", fieldName)
						.build());
				}
				// 支持表达式字段
				for (JdbcBeanInfo.ExpressionInfo expression : beanInfo.getExpressions()) {
					String fieldName = expression.getFieldName();
					nestedBuilder.addMethod(MethodSpec.methodBuilder(fieldName)
						.addModifiers(Modifier.PUBLIC)
						.returns(ParameterizedTypeName.get(classNameBaseCriterion,
							orWithO, WildcardTypeName.subtypeOf(TypeName.OBJECT)))
						.addStatement("return column($S)", fieldName)
						.build());
				}
			}
			classBuilder.addType(nestedBuilder.build());
		}
		// endregion or

		// endregion inner class

		// region static method
		{
			classBuilder.addMethod(MethodSpec.methodBuilder("join")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.addTypeVariable(
					TypeVariableName.get("O", ParameterizedTypeName.get(classNameBaseSelect, TypeVariableName.get("O")))
				)
				.returns(
					ParameterizedTypeName.get(
						classNameJoinBuilder,
						TypeVariableName.get("O"),
						ParameterizedTypeName.get(classNameJoin, TypeVariableName.get("O"))
					)
				)
				.addStatement("return $T.builder()", classNameJoin)
				.build());
			classBuilder.addMethod(MethodSpec.methodBuilder("select")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(classNameSelect)
				.addStatement("return new $T()", classNameSelect)
				.build());
			classBuilder.addMethod(MethodSpec.methodBuilder("select")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(classNameSelect)
				.addParameter(ClassName.get(String.class), "alias")
				.addStatement("return new $T(alias)", classNameSelect)
				.build());

			classBuilder.addMethod(MethodSpec.methodBuilder("insert")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(classNameInsert)
				.addStatement("return new $T()", classNameInsert)
				.build());
			classBuilder.addMethod(MethodSpec.methodBuilder("insert")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(classNameInsert)
				.addParameter(ClassName.get(String.class), "alias")
				.addStatement("return new $T(alias)", classNameInsert)
				.build());

			classBuilder.addMethod(MethodSpec.methodBuilder("update")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(classNameUpdate)
				.addStatement("return new $T()", classNameUpdate)
				.build());
			classBuilder.addMethod(MethodSpec.methodBuilder("update")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(classNameUpdate)
				.addParameter(ClassName.get(String.class), "alias")
				.addStatement("return new $T(alias)", classNameUpdate)
				.build());

			classBuilder.addMethod(MethodSpec.methodBuilder("delete")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(classNameDelete)
				.addStatement("return new $T()", classNameDelete)
				.build());
			classBuilder.addMethod(MethodSpec.methodBuilder("delete")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(classNameDelete)
				.addParameter(ClassName.get(String.class), "alias")
				.addStatement("return new $T(alias)", classNameDelete)
				.build());

		}
		// endregion static method

		JavaFile javaFile = JavaFile.builder(sqlClassName.packageName(), classBuilder.build()).build();
		try {
			javaFile.writeTo(filer);
		} catch (IOException t) {
			messager.printMessage(Diagnostic.Kind.ERROR, t.toString());
			t.printStackTrace();
		}
	}
}
