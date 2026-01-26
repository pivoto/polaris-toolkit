package io.polaris.mybatis.auth;

import java.util.Collection;

import net.sf.jsqlparser.schema.Table;

/**
 * @author Qt
 * @since Jan 25, 2026
 */
public interface TableWhereGenerator {

	String apply(Collection<Table> tables);

}
