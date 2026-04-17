package io.polaris.mybatis.enhance;

import java.util.Map;
import java.util.function.BiConsumer;

import javax.annotation.Nonnull;

import io.polaris.core.collection.ObjectArrays;
import io.polaris.core.jdbc.sql.BindingKeyModel;
import lombok.Data;

/**
 * @author Qt
 * @since Apr 01, 2026
 */
@Data
public class BindingKeyDefinition {
	/** 实体对象参数 */
	private String[] entity;
	/** where条件参数 */
	private String[] where;
	/** 排序列参数 */
	private String[] orderBy;
	/** insert或update中需要包含的空值列集合或数组，包括NULL与空串 */
	private String[] includeEmptyColumns;
	/** insert或update中需要包含空值处理的标志 */
	private String[] includeAllEmpty;
	/** insert或update中需要包含的列列集合或数组 */
	private String[] includeColumns;
	/** insert或update中需要排除的列列集合或数组 */
	private String[] excludeColumns;
	/** sql 语句型参数 */
	private String[] sql;
	/** where条件中需要包含的空值列列集合或数组，包括NULL与空串 */
	private String[] whereIncludeEmptyColumns;
	/** where条件中是否包含空值处理的标志，包括NULL与空串 */
	private String[] whereIncludeAllEmpty;
	/** where条件中需要包含的列列集合或数组 */
	private String[] whereIncludeColumns;
	/** where条件中需要排除的列列集合或数组 */
	private String[] whereExcludeColumns;
	/** 是否支持逻辑删除处理的标志 */
	private String[] withLogicDeleted;
	/** 是否使用Count语句查询存在性 */
	private String[] existsByCount;

	public boolean hasDefinition() {
		return ObjectArrays.isNotEmpty(entity) || ObjectArrays.isNotEmpty(where) || ObjectArrays.isNotEmpty(orderBy)
			|| ObjectArrays.isNotEmpty(includeEmptyColumns) || ObjectArrays.isNotEmpty(includeAllEmpty)
			|| ObjectArrays.isNotEmpty(includeColumns) || ObjectArrays.isNotEmpty(excludeColumns)
			|| ObjectArrays.isNotEmpty(sql) || ObjectArrays.isNotEmpty(whereIncludeEmptyColumns)
			|| ObjectArrays.isNotEmpty(whereIncludeAllEmpty) || ObjectArrays.isNotEmpty(whereIncludeColumns)
			|| ObjectArrays.isNotEmpty(whereExcludeColumns) || ObjectArrays.isNotEmpty(withLogicDeleted)
			|| ObjectArrays.isNotEmpty(existsByCount);
	}

	/**
	 * 将当前定义对象转换为 BindingKeyModel 对象。
	 * <p>
	 * 该方法会遍历所有定义字段，从参数对象中提取对应的值，并设置到 BindingKeyModel 中。
	 * 每个字段定义了一组候选键名，方法会按顺序检查这些键名是否存在于参数对象中，
	 * 一旦找到存在的键名，就将其值设置到对应的模型属性中。
	 *
	 * @param parameterObject 参数对象，包含所有可能的绑定键值对
	 * @return 转换后的 BindingKeyModel 对象；如果没有任何字段匹配，则返回 null
	 */
	@Nonnull
	public BindingKeyModel toBindingKeyModel(Map<String, Object> parameterObject) {
		// 使用数组引用以便在 lambda 中修改
		BindingKeyModel[] modelRef = new BindingKeyModel[1];
		// 依次解析各个字段的定义
		resolveDefinition(parameterObject, entity, modelRef, BindingKeyModel::entity);
		resolveDefinition(parameterObject, where, modelRef, BindingKeyModel::where);
		resolveDefinition(parameterObject, orderBy, modelRef, BindingKeyModel::orderBy);
		resolveDefinition(parameterObject, includeEmptyColumns, modelRef, BindingKeyModel::includeEmptyColumns);
		resolveDefinition(parameterObject, includeAllEmpty, modelRef, BindingKeyModel::includeAllEmpty);
		resolveDefinition(parameterObject, includeColumns, modelRef, BindingKeyModel::includeColumns);
		resolveDefinition(parameterObject, excludeColumns, modelRef, BindingKeyModel::excludeColumns);
		resolveDefinition(parameterObject, sql, modelRef, BindingKeyModel::sql);
		resolveDefinition(parameterObject, whereIncludeEmptyColumns, modelRef, BindingKeyModel::whereIncludeEmptyColumns);
		resolveDefinition(parameterObject, whereIncludeAllEmpty, modelRef, BindingKeyModel::whereIncludeAllEmpty);
		resolveDefinition(parameterObject, whereIncludeColumns, modelRef, BindingKeyModel::whereIncludeColumns);
		resolveDefinition(parameterObject, whereExcludeColumns, modelRef, BindingKeyModel::whereExcludeColumns);
		resolveDefinition(parameterObject, withLogicDeleted, modelRef, BindingKeyModel::withLogicDeleted);
		resolveDefinition(parameterObject, existsByCount, modelRef, BindingKeyModel::existsByCount);
		return modelRef[0] == null ? BindingKeyModel.DEFAULT : modelRef[0].freeze();
	}

	private static void resolveDefinition(Map<String, Object> parameterObject, String[] defKeys, BindingKeyModel[] modelRef, BiConsumer<BindingKeyModel, String> setter) {
		if (ObjectArrays.isNotEmpty(defKeys)) {
			for (String key : defKeys) {
				if (parameterObject.containsKey(key)) {
					if (modelRef[0] == null) {
						modelRef[0] = new BindingKeyModel();
					}
					setter.accept(modelRef[0], key);
					break;
				}
			}
		}
	}

}
