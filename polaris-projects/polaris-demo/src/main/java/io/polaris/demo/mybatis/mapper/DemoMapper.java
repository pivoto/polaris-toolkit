package io.polaris.demo.mybatis.mapper;

import java.util.List;
import java.util.Map;

import io.polaris.core.jdbc.annotation.Key;
import io.polaris.core.jdbc.annotation.EntityDelete;
import io.polaris.core.jdbc.annotation.EntityInsert;
import io.polaris.core.jdbc.annotation.EntitySelect;
import io.polaris.core.jdbc.annotation.EntityUpdate;
import io.polaris.core.jdbc.annotation.SqlEntity;
import io.polaris.core.jdbc.annotation.SqlRaw;
import io.polaris.core.jdbc.annotation.SqlSelect;
import io.polaris.core.jdbc.annotation.segment.BindingKey;
import io.polaris.core.jdbc.annotation.segment.Condition;
import io.polaris.core.jdbc.annotation.segment.Criteria;
import io.polaris.core.jdbc.annotation.segment.Criteria1;
import io.polaris.core.jdbc.annotation.segment.Criterion;
import io.polaris.core.jdbc.annotation.segment.Join;
import io.polaris.core.jdbc.annotation.segment.JoinColumn;
import io.polaris.core.jdbc.annotation.segment.JoinCriterion;
import io.polaris.core.jdbc.annotation.segment.OrderBy;
import io.polaris.core.jdbc.annotation.segment.SelectColumn;
import io.polaris.core.jdbc.annotation.segment.SqlRawItem;
import io.polaris.core.jdbc.annotation.segment.Where;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.consts.Direction;
import io.polaris.core.jdbc.sql.consts.Relation;
import io.polaris.demo.mybatis.entity.DemoOrgEntity;
import io.polaris.demo.mybatis.entity.DemoOrgEntityMeta;
import io.polaris.demo.mybatis.entity.DemoUserOrgEntity;
import io.polaris.demo.mybatis.entity.DemoUserOrgEntityMeta;
import io.polaris.mybatis.provider.AnyEntityProvider;
import io.polaris.mybatis.provider.ProviderSqlSourceDriver;
import io.polaris.mybatis.scripting.TableRefResolvableDriver;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * @author Qt
 * @since 1.8,  Jan 30, 2024
 */
@Mapper
public interface DemoMapper {

	@InsertProvider(AnyEntityProvider.class)
	@EntityInsert(table = DemoOrgEntity.class, entityKey = "e")
	int insertEntity(@Param("e") DemoOrgEntity param);

	@UpdateProvider(AnyEntityProvider.class)
	@EntityUpdate(table = DemoOrgEntity.class, entityKey = "e")
	int updateEntity(@Param("e") DemoOrgEntity param);

	@DeleteProvider(AnyEntityProvider.class)
	@EntityDelete(table = DemoOrgEntity.class, entityKey = "e")
	int deleteEntity(@Param("e") DemoOrgEntity param);

	@SelectProvider(AnyEntityProvider.class)
	@EntitySelect(table = DemoOrgEntity.class, byId = true)
	DemoOrgEntity selectOrgById(@Param(BindingKeys.ENTITY) DemoOrgEntity param);

	@SelectProvider(AnyEntityProvider.class)
	@EntitySelect(table = DemoOrgEntity.class, byId = false)
	List<DemoOrgEntity> selectOrgList(@Param(BindingKeys.ENTITY) DemoOrgEntity param);

	@SelectProvider(AnyEntityProvider.class)
	@SqlSelect(table = DemoOrgEntity.class,
		where = @Where(
			criteria = {
				@Criteria(value = {
					@Criterion(field = DemoOrgEntityMeta.FieldName.id, eq = @BindingKey("id")),
					@Criterion(field = DemoOrgEntityMeta.FieldName.name, contains = @BindingKey("name")),
				}),
			}
		)
	)
	List<DemoOrgEntity> selectOrgListByAny(@Param("id") Long id, @Param("name") String name);

	@SelectProvider(AnyEntityProvider.class)
	@SqlSelect(table = DemoOrgEntity.class, alias = "o", quotaSelectAlias = true,
		columns = {@SelectColumn(field = "*", aliasWithField = true, aliasPrefix = "org.")},
		join = @Join(table = DemoUserOrgEntity.class, alias = "uo",
			on = @Criteria(
				join = {
					@JoinCriterion(field = DemoUserOrgEntityMeta.FieldName.orgId, eq = @JoinColumn(tableAlias = "o", tableField = DemoOrgEntityMeta.FieldName.id)),
				}
			)
		),
		where = @Where(
			relation = Relation.OR,
			criteria = {
				@Criteria(
					relation = Relation.OR,
					value = {
						@Criterion(field = DemoOrgEntityMeta.FieldName.id, eq = @BindingKey("id")),
					},
					subset = @Criteria1(
						relation = Relation.OR,
						value = {
							@Criterion(field = DemoOrgEntityMeta.FieldName.id, eq = @BindingKey("id1")),
							@Criterion(field = DemoOrgEntityMeta.FieldName.id, eq = @BindingKey("id2")),
						}
					)
				),
				@Criteria(
					value = {
						@Criterion(field = DemoOrgEntityMeta.FieldName.name, contains = @BindingKey("name")),
					}
				),
			}
		),
		orderBy = {@OrderBy(field = DemoOrgEntityMeta.FieldName.id, direction = Direction.DESC,
			condition = {@Condition(bindingKey = "id",
				predicateType = Condition.PredicateType.SCRIPT,
				predicateScriptEngine = "groovy",
				predicateExpression = "id >= 1"
			)})
		}
	)
	List<DemoUserOrgEntity> selectOrgListByAny2(Map<String, Object> param);


	@Lang(ProviderSqlSourceDriver.class)
	@SelectProvider(AnyEntityProvider.class)
	@SqlEntity(table = {DemoOrgEntity.class}, alias = {"x"})
	@SqlRaw({
		@SqlRawItem("select &{x.*} from &{x} where 1=1"),
		@SqlRawItem(forEachKey = "ids", itemKey = "id", separator = ",", open = " and &{x.id} in (", close = ") ",
			value = "#{id}"
		),
		@SqlRawItem(""),
	})
	List<DemoOrgEntity> getOrgListByIds(@Key("ids") Long[] ids);

	List<DemoOrgEntity> getOrgListByIds2(@Key("ids") Long[] ids);

	@Lang(TableRefResolvableDriver.class)
	@Select({
		"<script>",
		"<bind name=\"xEntity\" value=\"'io.polaris.demo.mybatis.entity.DemoOrgEntity'\"/>" +
			"select &amp;{x(${xEntity}).*} from &amp;{x(${xEntity})} where 1=1 " +
			"<if test=\"ids != null and ids.length > 0\">" +
			"and &amp;{x(${xEntity}).id} in " +
			"<foreach collection=\"ids\" item=\"id\" open=\"(\" close=\")\" separator=\",\">" +
			"#{id}" +
			"</foreach>" +
			"</if>",
		"</script>"
	})
	List<DemoOrgEntity> getOrgListByIds3(@Key("ids") Long[] ids);

}
