package io.polaris.demo.mybatis.mapper;

import java.util.List;
import java.util.Map;

import io.polaris.core.jdbc.sql.annotation.EntityDelete;
import io.polaris.core.jdbc.sql.annotation.EntityInsert;
import io.polaris.core.jdbc.sql.annotation.EntitySelect;
import io.polaris.core.jdbc.sql.annotation.EntityUpdate;
import io.polaris.core.jdbc.sql.annotation.SqlSelect;
import io.polaris.core.jdbc.sql.annotation.segment.BindingKey;
import io.polaris.core.jdbc.sql.annotation.segment.Criteria;
import io.polaris.core.jdbc.sql.annotation.segment.Criteria1;
import io.polaris.core.jdbc.sql.annotation.segment.Criterion;
import io.polaris.core.jdbc.sql.annotation.segment.Join;
import io.polaris.core.jdbc.sql.annotation.segment.JoinColumn;
import io.polaris.core.jdbc.sql.annotation.segment.JoinCriterion;
import io.polaris.core.jdbc.sql.annotation.segment.OrderBy;
import io.polaris.core.jdbc.sql.annotation.segment.Where;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.consts.Direction;
import io.polaris.core.jdbc.sql.consts.Relation;
import io.polaris.demo.mybatis.entity.DemoOrgEntity;
import io.polaris.demo.mybatis.entity.DemoOrgEntityMeta;
import io.polaris.demo.mybatis.entity.DemoUserOrgEntity;
import io.polaris.demo.mybatis.entity.DemoUserOrgEntityMeta;
import io.polaris.mybatis.provider.AnyEntityProvider;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
	@SqlSelect(table = DemoOrgEntity.class, alias = "o",
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
		orderBy = {@OrderBy(field = DemoOrgEntityMeta.FieldName.id, direction = Direction.DESC)}
	)
	List<DemoOrgEntity> selectOrgListByAny2(Map<String, Object> param);

}
