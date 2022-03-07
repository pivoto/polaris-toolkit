package io.polaris.toolkit.spring.autoconfigure;

import io.polaris.toolkit.spring.annotation.EnableDynamicTransaction;
import io.polaris.toolkit.spring.condition.ConditionalOnEnableDynamicTransaction;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Qt
 * @version Dec 29, 2021
 * @since 1.8
 */
@ConditionalOnClass(PlatformTransactionManager.class)
@ConditionalOnEnableDynamicTransaction
@Configuration()
@AutoConfigureBefore(TransactionAutoConfiguration.class)
@AutoConfigureAfter({JtaAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class, Neo4jDataAutoConfiguration.class})
public class DynamicTransactionAutoConfiguration {

	@Configuration
	@EnableDynamicTransaction
	public static class ImportConfiguration {
	}

}
