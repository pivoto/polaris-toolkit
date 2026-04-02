package io.polaris.mybatis.enhance;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.string.Strings;
import io.polaris.mybatis.annotation.MapperParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.reflection.ParamNameUtil;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * @author Qt
 * @see ParamNameResolver
 * @since Mar 30, 2026
 */
@Slf4j
public class EnhancedParamNameResolver {

	public static BindingKeyDefinition resolve(Method method) {
		final BindingKeyDefinition bindingKeyDefinition = new BindingKeyDefinition();

		boolean hasParamAnnotation = false;
		final Class<?>[] paramTypes = method.getParameterTypes();
		final Annotation[][] paramAnnotations = method.getParameterAnnotations();
		final SortedMap<Integer, String[]> possibleNames = new TreeMap<>();
		int paramCount = paramAnnotations.length;

		for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
			if (isSpecialParameter(paramTypes[paramIndex])) {
				// skip special parameters
				continue;
			}
			String[] names = null;
			for (Annotation annotation : paramAnnotations[paramIndex]) {
				if (annotation instanceof Param) {
					hasParamAnnotation = true;
					String specName = ((Param) annotation).value();
					// add generic param names (param1, param2, ...)
					String genericParamName = ParamNameResolver.GENERIC_NAME_PREFIX + (1 + possibleNames.size());
					if (genericParamName.equals(specName)) {
						names = new String[]{specName};
					} else {
						names = new String[]{specName, genericParamName};
					}
					break;
				}
			}
			// @Param was not specified.
			if (names == null) {
				// 声明的参数名，或形如 arg0, arg1, ...
				String actualParamName = getActualParamName(method, paramIndex);
				// use the parameter index as the name ("0", "1", ...)
				String indexParamName = String.valueOf(possibleNames.size());
				// add generic param names (param1, param2, ...)
				String genericParamName = ParamNameResolver.GENERIC_NAME_PREFIX + (1 + possibleNames.size());
				if (indexParamName.equals(actualParamName)) {
					names = new String[]{actualParamName, genericParamName};
				} else if (genericParamName.equals(actualParamName)) {
					names = new String[]{actualParamName, indexParamName};
				} else {
					names = new String[]{actualParamName, indexParamName, genericParamName};
				}
			}
			possibleNames.put(paramIndex, names);
		}

		resolveMethodAnnotation(bindingKeyDefinition, method);


		final int realCount = possibleNames.size();
		if (realCount > 0) {
			if (realCount == 1 && !hasParamAnnotation) {
				Integer paramIndex = possibleNames.firstKey();
				Class<?> paramType = paramTypes[paramIndex];
				if (paramType == Object.class) {
					log.error("Mapper方法未明确声明入参的绑定键名，请使用@Param注解：`{}`", method);
				}
				String actualParamName = possibleNames.get(paramIndex)[0];
				for (Annotation annotation : paramAnnotations[paramIndex]) {
					resolveParamAnnotation(bindingKeyDefinition, new String[]{actualParamName}, annotation);
				}
			} else {
				for (Integer paramIndex : possibleNames.keySet()) {
					for (Annotation annotation : paramAnnotations[paramIndex]) {
						String[] names = possibleNames.get(paramIndex);
						resolveParamAnnotation(bindingKeyDefinition, names, annotation);
					}
				}
			}
		}

		return bindingKeyDefinition;
	}

	private static void resolveMethodAnnotation(BindingKeyDefinition bindingKeyDefinition, Method method) {
		MapperParam mapperParam = method.getAnnotation(MapperParam.class);
		if (mapperParam != null) {
			String entity = mapperParam.entity();
			if (Strings.isNotBlank(entity) && !BindingKeys.ENTITY.equals(entity)) {
				bindingKeyDefinition.setEntity(new String[]{entity});
			}
			String where = mapperParam.where();
			if (Strings.isNotBlank(where) && !BindingKeys.WHERE.equals(where)) {
				bindingKeyDefinition.setWhere(new String[]{where});
			}
			String orderBy = mapperParam.orderBy();
			if (Strings.isNotBlank(orderBy) && !BindingKeys.ORDER_BY.equals(orderBy)) {
				bindingKeyDefinition.setOrderBy(new String[]{orderBy});
			}
			String includeEmptyColumns = mapperParam.includeEmptyColumns();
			if (Strings.isNotBlank(includeEmptyColumns) && !BindingKeys.INCLUDE_EMPTY_COLUMNS.equals(includeEmptyColumns)) {
				bindingKeyDefinition.setIncludeEmptyColumns(new String[]{includeEmptyColumns});
			}
			String includeAllEmpty = mapperParam.includeAllEmpty();
			if (Strings.isNotBlank(includeAllEmpty) && !BindingKeys.INCLUDE_ALL_EMPTY.equals(includeAllEmpty)) {
				bindingKeyDefinition.setIncludeAllEmpty(new String[]{includeAllEmpty});
			}
			String includeColumns = mapperParam.includeColumns();
			if (Strings.isNotBlank(includeColumns) && !BindingKeys.INCLUDE_COLUMNS.equals(includeColumns)) {
				bindingKeyDefinition.setIncludeColumns(new String[]{includeColumns});
			}
			String excludeColumns = mapperParam.excludeColumns();
			if (Strings.isNotBlank(excludeColumns) && !BindingKeys.EXCLUDE_COLUMNS.equals(excludeColumns)) {
				bindingKeyDefinition.setExcludeColumns(new String[]{excludeColumns});
			}
			String sql = mapperParam.sql();
			if (Strings.isNotBlank(sql) && !BindingKeys.SQL.equals(sql)) {
				bindingKeyDefinition.setSql(new String[]{sql});
			}
			String whereIncludeEmptyColumns = mapperParam.whereIncludeEmptyColumns();
			if (Strings.isNotBlank(whereIncludeEmptyColumns) && !BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS.equals(whereIncludeEmptyColumns)) {
				bindingKeyDefinition.setWhereIncludeEmptyColumns(new String[]{whereIncludeEmptyColumns});
			}
			String whereIncludeAllEmpty = mapperParam.whereIncludeAllEmpty();
			if (Strings.isNotBlank(whereIncludeAllEmpty) && !BindingKeys.WHERE_INCLUDE_ALL_EMPTY.equals(whereIncludeAllEmpty)) {
				bindingKeyDefinition.setWhereIncludeAllEmpty(new String[]{whereIncludeAllEmpty});
			}
			String whereIncludeColumns = mapperParam.whereIncludeColumns();
			if (Strings.isNotBlank(whereIncludeColumns) && !BindingKeys.WHERE_INCLUDE_COLUMNS.equals(whereIncludeColumns)) {
				bindingKeyDefinition.setWhereIncludeColumns(new String[]{whereIncludeColumns});
			}
			String whereExcludeColumns = mapperParam.whereExcludeColumns();
			if (Strings.isNotBlank(whereExcludeColumns) && !BindingKeys.WHERE_EXCLUDE_COLUMNS.equals(whereExcludeColumns)) {
				bindingKeyDefinition.setWhereExcludeColumns(new String[]{whereExcludeColumns});
			}
			String withLogicDeleted = mapperParam.withLogicDeleted();
			if (Strings.isNotBlank(withLogicDeleted) && !BindingKeys.WITH_LOGIC_DELETED.equals(withLogicDeleted)) {
				bindingKeyDefinition.setWithLogicDeleted(new String[]{withLogicDeleted});
			}
		}
	}

	private static void resolveParamAnnotation(BindingKeyDefinition bindingKeyDefinition, String[] paramKeys, Annotation annotation) {
		if (annotation instanceof MapperParam.Entity) {
			mergeBindingKeyDefinition(paramKeys, bindingKeyDefinition.getEntity(), bindingKeyDefinition::setEntity, BindingKeys.ENTITY);
			return;
		}
		if (annotation instanceof MapperParam.Where) {
			mergeBindingKeyDefinition(paramKeys, bindingKeyDefinition.getWhere(), bindingKeyDefinition::setWhere, BindingKeys.WHERE);
			return;
		}
		if (annotation instanceof MapperParam.OrderBy) {
			mergeBindingKeyDefinition(paramKeys, bindingKeyDefinition.getOrderBy(), bindingKeyDefinition::setOrderBy, BindingKeys.ORDER_BY);
			return;
		}
		if (annotation instanceof MapperParam.IncludeEmptyColumns) {
			mergeBindingKeyDefinition(paramKeys, bindingKeyDefinition.getIncludeEmptyColumns(), bindingKeyDefinition::setIncludeEmptyColumns, BindingKeys.INCLUDE_EMPTY_COLUMNS);
			return;
		}
		if (annotation instanceof MapperParam.IncludeAllEmpty) {
			mergeBindingKeyDefinition(paramKeys, bindingKeyDefinition.getIncludeAllEmpty(), bindingKeyDefinition::setIncludeAllEmpty, BindingKeys.INCLUDE_ALL_EMPTY);
			return;
		}
		if (annotation instanceof MapperParam.IncludeColumns) {
			mergeBindingKeyDefinition(paramKeys, bindingKeyDefinition.getIncludeColumns(), bindingKeyDefinition::setIncludeColumns, BindingKeys.INCLUDE_COLUMNS);
			return;
		}
		if (annotation instanceof MapperParam.ExcludeColumns) {
			mergeBindingKeyDefinition(paramKeys, bindingKeyDefinition.getExcludeColumns(), bindingKeyDefinition::setExcludeColumns, BindingKeys.EXCLUDE_COLUMNS);
			return;
		}
		if (annotation instanceof MapperParam.Sql) {
			mergeBindingKeyDefinition(paramKeys, bindingKeyDefinition.getSql(), bindingKeyDefinition::setSql, BindingKeys.SQL);
			return;
		}
		if (annotation instanceof MapperParam.WhereIncludeEmptyColumns) {
			mergeBindingKeyDefinition(paramKeys, bindingKeyDefinition.getWhereIncludeEmptyColumns(), bindingKeyDefinition::setWhereIncludeEmptyColumns, BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS);
			return;
		}
		if (annotation instanceof MapperParam.WhereIncludeAllEmpty) {
			mergeBindingKeyDefinition(paramKeys, bindingKeyDefinition.getWhereIncludeAllEmpty(), bindingKeyDefinition::setWhereIncludeAllEmpty, BindingKeys.WHERE_INCLUDE_ALL_EMPTY);
			return;
		}
		if (annotation instanceof MapperParam.WhereIncludeColumns) {
			mergeBindingKeyDefinition(paramKeys, bindingKeyDefinition.getWhereIncludeColumns(), bindingKeyDefinition::setWhereIncludeColumns, BindingKeys.WHERE_INCLUDE_COLUMNS);
			return;
		}
		if (annotation instanceof MapperParam.WhereExcludeColumns) {
			mergeBindingKeyDefinition(paramKeys, bindingKeyDefinition.getWhereExcludeColumns(), bindingKeyDefinition::setWhereExcludeColumns, BindingKeys.WHERE_EXCLUDE_COLUMNS);
			return;
		}
		if (annotation instanceof MapperParam.WithLogicDeleted) {
			mergeBindingKeyDefinition(paramKeys, bindingKeyDefinition.getWithLogicDeleted(), bindingKeyDefinition::setWithLogicDeleted, BindingKeys.WITH_LOGIC_DELETED);
			return;
		}
	}

	private static void mergeBindingKeyDefinition(String[] paramKeys, String[] methodKeys, Consumer<String[]> setter, String defaultKey) {
		Set<String> set = new LinkedHashSet<>(Arrays.asList(paramKeys));
		if (methodKeys != null) {
			set.addAll(Arrays.asList(methodKeys));
		}
		set.remove(defaultKey);
		if (set.isEmpty()) {
			setter.accept(null);
			return;
		}
		setter.accept(set.toArray(new String[0]));
	}

	private static String getActualParamName(Method method, int paramIndex) {
		return ParamNameUtil.getParamNames(method).get(paramIndex);
	}

	private static boolean isSpecialParameter(Class<?> clazz) {
		return RowBounds.class.isAssignableFrom(clazz) || ResultHandler.class.isAssignableFrom(clazz);
	}
}
