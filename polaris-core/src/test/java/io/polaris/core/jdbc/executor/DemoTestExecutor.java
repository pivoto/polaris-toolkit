package io.polaris.core.jdbc.executor;

import java.util.List;

import io.polaris.core.jdbc.annotation.Key;
import io.polaris.core.jdbc.annotation.SqlQuery;
import io.polaris.core.jdbc.annotation.EntityDelete;
import io.polaris.core.jdbc.annotation.EntityInsert;
import io.polaris.core.jdbc.annotation.EntitySelect;
import io.polaris.core.jdbc.annotation.SqlEntity;
import io.polaris.core.jdbc.annotation.SqlRaw;
import io.polaris.core.jdbc.annotation.SqlRawSimple;
import io.polaris.core.jdbc.annotation.segment.SqlRawItem;
import io.polaris.core.jdbc.annotation.segment.SqlRawItem1;
import io.polaris.core.jdbc.entity.DemoTest01Entity;
import io.polaris.core.jdbc.entity.DemoTest02Entity;
import io.polaris.core.jdbc.sql.consts.BindingKeys;

/**
 * @author Qt
 * @since  Feb 08, 2024
 */
public interface DemoTestExecutor {


	@EntityInsert(table = DemoTest01Entity.class)
	int insertDemoTest01(@Key(BindingKeys.ENTITY) DemoTest01Entity param);

	@EntitySelect(table = DemoTest01Entity.class, byId = false)
	List<DemoTest01Entity> getDemoTest01List(@Key(BindingKeys.ENTITY) DemoTest01Entity param);

	@EntityDelete(table = DemoTest01Entity.class, byId = false)
	int deleteDemoTest01(@Key(BindingKeys.ENTITY) DemoTest01Entity param);


	@EntityInsert(table = DemoTest02Entity.class)
	int insertDemoTest02(@Key(BindingKeys.ENTITY) DemoTest02Entity param);

	@EntitySelect(table = DemoTest02Entity.class, byId = false)
	List<DemoTest02Entity> getDemoTest02List(@Key(BindingKeys.ENTITY) DemoTest02Entity param);

	@EntityDelete(table = DemoTest02Entity.class, byId = false)
	int deleteDemoTest02(@Key(BindingKeys.ENTITY) DemoTest02Entity param);


	@SqlQuery
	@SqlEntity(table = {DemoTest01Entity.class}, alias = {"x"})
	@SqlRaw({
		@SqlRawItem(
			forEachKey = "ids", itemKey = "id", separator = " union all "
			, value = "select &{x.*} from &{x} where &{x.id} = #{id}"
		)
	})
	List<DemoTest01Entity> getDemoTest01ListByIds1(@Key("ids") Long[] ids);

	@SqlQuery
	@SqlEntity(table = {DemoTest01Entity.class}, alias = {"x"})
	@SqlRaw({
		@SqlRawItem("select &{x.*} from &{x} where 1=1"),
		@SqlRawItem(forEachKey = "ids", itemKey = "id", separator = ",", open = " and &{x.id} in (", close = ") ",
			value = "#{id}"
		),
		@SqlRawItem(""),
	})
	List<DemoTest01Entity> getDemoTest01ListByIds2(@Key("ids") Long[] ids);

	@SqlQuery
	@SqlEntity(table = {DemoTest01Entity.class}, alias = {"x"})
	@SqlRaw({
		@SqlRawItem("select &{x.*} from &{x} where 1=1"),
		@SqlRawItem(forEachKey = "ids", itemKey = "id", separator = " or ", open = " and ( ", close = " ) ",
			subset = {
				@SqlRawItem1("&{x.id}"),
				@SqlRawItem1("="),
				@SqlRawItem1("#{id}"),
			}
		)
	})
	List<DemoTest01Entity> getDemoTest01ListByIds3(@Key("ids") Long[] ids);


	@SqlQuery
	@SqlEntity(table = {DemoTest01Entity.class}, alias = {"x"})
	@SqlRawSimple("select &{x.*} from &{x} where &{x.id} = #{id}")
//		@EntitySelect(table = DemoTest01Entity.class, byId = true)
	DemoTest01Entity getDemoTest01ById(@Key(DemoTest01Entity.Fields.id) Long id);


}
