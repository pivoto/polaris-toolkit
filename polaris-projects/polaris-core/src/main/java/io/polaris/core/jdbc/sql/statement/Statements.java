//package io.polaris.core.jdbc.sql.statement;
//
//import io.polaris.core.jdbc.sql.EntityStatements;
//import io.polaris.core.jdbc.sql.query.Criteria;
//import io.polaris.core.jdbc.sql.query.OrderBy;
//
//import java.util.function.Predicate;
//
///**
// * @author Qt
// * @since 1.8,  Aug 31, 2023
// */
//public class Statements {
//
//	public static final Predicate<String> DEFAULT_PREDICATE_EXCLUDE_COLS = name -> false;
//	public static final Predicate<String> DEFAULT_PREDICATE_INCLUDE_NULLS = name -> false;
//
//	public static SelectStatement<?> buildSelect(Class<?> entityClass, Object entity) {
//		return buildSelect(entityClass, EntityStatements.DEFAULT_TABLE_ALIAS, entity, DEFAULT_PREDICATE_INCLUDE_NULLS, null, null);
//	}
//
//	public static SelectStatement<?> buildSelect(Class<?> entityClass, String tableAlias, Object entity) {
//		return buildSelect(entityClass, tableAlias, entity, DEFAULT_PREDICATE_INCLUDE_NULLS, null, null);
//	}
//
//	public static SelectStatement<?> buildSelect(Class<?> entityClass, Object entity, OrderBy orderBy) {
//		return buildSelect(entityClass, EntityStatements.DEFAULT_TABLE_ALIAS, entity, DEFAULT_PREDICATE_INCLUDE_NULLS, null, orderBy);
//	}
//
//	public static SelectStatement<?> buildSelect(Class<?> entityClass, String tableAlias, Object entity, OrderBy orderBy) {
//		return buildSelect(entityClass, tableAlias, entity, DEFAULT_PREDICATE_INCLUDE_NULLS, null, orderBy);
//	}
//
//	public static SelectStatement<?> buildSelect(Class<?> entityClass, Object entity, Criteria criteria, OrderBy orderBy) {
//		return buildSelect(entityClass, EntityStatements.DEFAULT_TABLE_ALIAS, entity, DEFAULT_PREDICATE_INCLUDE_NULLS, criteria, orderBy);
//	}
//
//	public static SelectStatement<?> buildSelect(Class<?> entityClass, String tableAlias, Object entity, Criteria criteria, OrderBy orderBy) {
//		return buildSelect(entityClass, tableAlias, entity, DEFAULT_PREDICATE_INCLUDE_NULLS, criteria, orderBy);
//	}
//
//	public static SelectStatement<?> buildSelect(Class<?> entityClass, String tableAlias, Object entity, Predicate<String> includeWhereNulls, Criteria criteria, OrderBy orderBy) {
//		SelectStatement<?> st = new SelectStatement<>(entityClass, tableAlias);
//		st.selectAll();
//		st.where(criteria);
//		st.orderBy(orderBy);
//		st.where().byEntity(entity, includeWhereNulls);
//		return st;
//	}
//
//	public static InsertStatement<?> buildInsert(Class<?> entityClass, Object entity) {
//		return buildInsert(entityClass, entity, DEFAULT_PREDICATE_INCLUDE_NULLS);
//	}
//
//	public static InsertStatement<?> buildInsert(Class<?> entityClass, Object entity, Predicate<String> includeEntityNulls) {
//		InsertStatement<?> st = new InsertStatement<>(entityClass);
//		st.withEntity(entity, includeEntityNulls);
//		return st;
//	}
//
//	public static UpdateStatement<?> buildUpdate(Class<?> entityClass, Object entity, Object where) {
//		return buildUpdate(entityClass, EntityStatements.DEFAULT_TABLE_ALIAS, entity, DEFAULT_PREDICATE_INCLUDE_NULLS, where, DEFAULT_PREDICATE_INCLUDE_NULLS);
//	}
//
//	public static UpdateStatement<?> buildUpdate(Class<?> entityClass, String tableAlias, Object entity, Object where) {
//		return buildUpdate(entityClass, tableAlias, entity, DEFAULT_PREDICATE_INCLUDE_NULLS, where, DEFAULT_PREDICATE_INCLUDE_NULLS);
//	}
//
//	public static UpdateStatement<?> buildUpdate(Class<?> entityClass, String tableAlias, Object entity, Predicate<String> includeEntityNulls, Object where) {
//		return buildUpdate(entityClass, tableAlias, entity, includeEntityNulls, where, DEFAULT_PREDICATE_INCLUDE_NULLS);
//	}
//
//	public static UpdateStatement<?> buildUpdate(Class<?> entityClass, String tableAlias, Object entity, Predicate<String> includeEntityNulls, Object where, Predicate<String> includeWhereNulls) {
//		UpdateStatement<?> st = new UpdateStatement<>(entityClass, tableAlias);
//		st.withEntity(entity, includeEntityNulls);
//		if (where instanceof Criteria) {
//			st.where((Criteria) where);
//		} else {
//			st.where().byEntity(where, includeWhereNulls);
//		}
//		return st;
//	}
//
//	public static UpdateStatement<?> buildUpdateById(Class<?> entityClass, Object entity) {
//		return buildUpdateById(entityClass, EntityStatements.DEFAULT_TABLE_ALIAS, entity, DEFAULT_PREDICATE_INCLUDE_NULLS);
//	}
//
//	public static UpdateStatement<?> buildUpdateById(Class<?> entityClass, String tableAlias, Object entity) {
//		return buildUpdateById(entityClass, tableAlias, entity, DEFAULT_PREDICATE_INCLUDE_NULLS);
//	}
//
//	public static UpdateStatement<?> buildUpdateById(Class<?> entityClass, String tableAlias, Object entity, Predicate<String> includeEntityNulls) {
//		UpdateStatement<?> st = new UpdateStatement<>(entityClass, tableAlias);
//		st.withEntity(entity, includeEntityNulls);
//		st.where().byEntityId(entity);
//		return st;
//	}
//
//	public static DeleteStatement<?> buildDelete(Class<?> entityClass, Object entity) {
//		return buildDelete(entityClass, EntityStatements.DEFAULT_TABLE_ALIAS, entity, DEFAULT_PREDICATE_INCLUDE_NULLS);
//	}
//
//	public static DeleteStatement<?> buildDelete(Class<?> entityClass, String tableAlias, Object entity) {
//		return buildDelete(entityClass, tableAlias, entity, DEFAULT_PREDICATE_INCLUDE_NULLS);
//	}
//
//	public static DeleteStatement<?> buildDelete(Class<?> entityClass, String tableAlias, Object entity, Predicate<String> includeWhereNulls) {
//		DeleteStatement<?> st = new DeleteStatement<>(entityClass, tableAlias);
//		if (entity instanceof Criteria) {
//			st.where((Criteria) entity);
//		} else {
//			st.where().byEntity(entity, includeWhereNulls);
//		}
//		return st;
//	}
//
//	public static DeleteStatement<?> buildDeleteById(Class<?> entityClass, Object entity) {
//		return buildDeleteById(entityClass, EntityStatements.DEFAULT_TABLE_ALIAS, entity);
//	}
//
//	public static DeleteStatement<?> buildDeleteById(Class<?> entityClass, String tableAlias, Object entity) {
//		DeleteStatement<?> st = new DeleteStatement<>(entityClass, tableAlias);
//		st.where().byEntityId(entity);
//		return st;
//	}
//
//	public static MergeStatement<?> buildMerge(Class<?> entityClass, Object entity) {
//		return buildMerge(entityClass, entity, DEFAULT_PREDICATE_INCLUDE_NULLS);
//	}
//
//	public static MergeStatement<?> buildMerge(Class<?> entityClass, Object entity, Predicate<String> includeEntityNulls) {
//		MergeStatement<?> st = new MergeStatement<>(entityClass, EntityStatements.DEFAULT_TABLE_ALIAS);
//		st.withEntity(entity, includeEntityNulls);
//		return st;
//	}
//
//}
