package io.polaris.toolkit.spring.configuration;

import io.polaris.toolkit.spring.transaction.DynamicTransactionProperties;
import io.polaris.toolkit.spring.transaction.JtaTransactionalAspect;
import io.polaris.toolkit.spring.transaction.SpringRepositoryAspect;
import io.polaris.toolkit.spring.transaction.SpringServiceAspect;
import io.polaris.toolkit.spring.transaction.SpringTransactionalAspect;
import org.springframework.aop.ClassFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.List;

/**
 * @author Qt
 * @version Dec 30, 2021
 * @see ProxyTransactionManagementConfiguration#transactionAdvisor(TransactionAttributeSource, TransactionInterceptor)
 * @since 1.8
 */
@Configuration
@EnableConfigurationProperties(DynamicTransactionProperties.class)
public class DynamicAspectTransactionConfiguration {

	@Configuration
	@EnableConfigurationProperties(DynamicTransactionProperties.class)
	public static class TransactionalAspectConfig extends AbstractDynamicTransactionConfiguration {
		protected TransactionInterceptor annotationTransactionInterceptor;
		protected AnnotationTransactionAttributeSource annotationTransactionAttributeSource;

		@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
		public TransactionalAspectConfig(DynamicTransactionProperties properties, PlatformTransactionManager transactionManager) {
			super(properties, transactionManager);
			this.annotationTransactionAttributeSource = buildAnnotationTxAttrSource();
			this.annotationTransactionInterceptor = buildInterceptor(annotationTransactionAttributeSource, this.transactionManager);
		}

		@Bean
		@ConditionalOnClass(name = "javax.transaction.Transactional")
		public JtaTransactionalAspect jtaTransactionalAspect() {
			return new JtaTransactionalAspect(annotationTransactionInterceptor);
		}

		@Bean
		public SpringTransactionalAspect springTransactionalAspect() {
			return new SpringTransactionalAspect(annotationTransactionInterceptor);
		}
	}


	@Configuration
	@EnableConfigurationProperties(DynamicTransactionProperties.class)
	public static class SpringServiceAspectConfig extends AbstractDynamicTransactionConfiguration {
		protected TransactionInterceptor nameMatchTransactionInterceptor;
		protected NameMatchTransactionAttributeSource nameMatchTransactionAttributeSource;

		@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
		public SpringServiceAspectConfig(DynamicTransactionProperties properties, PlatformTransactionManager transactionManager) {
			super(properties, transactionManager);
			this.nameMatchTransactionAttributeSource = buildNameMatchTxAttrSource();
			this.nameMatchTransactionInterceptor = buildInterceptor(nameMatchTransactionAttributeSource, this.transactionManager);
		}

		@Bean
		public SpringServiceAspect springServiceAspect() {
			return new SpringServiceAspect(nameMatchTransactionInterceptor);
		}
	}

	@Configuration
	@EnableConfigurationProperties(DynamicTransactionProperties.class)
	public static class SpringRepositoryAspectConfig extends AbstractDynamicTransactionConfiguration {
		protected TransactionInterceptor nameMatchTransactionInterceptor;
		protected NameMatchTransactionAttributeSource nameMatchTransactionAttributeSource;

		@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
		public SpringRepositoryAspectConfig(DynamicTransactionProperties properties, PlatformTransactionManager transactionManager) {
			super(properties, transactionManager);
			this.nameMatchTransactionAttributeSource = buildNameMatchTxAttrSource();
			this.nameMatchTransactionInterceptor = buildInterceptor(nameMatchTransactionAttributeSource, this.transactionManager);
		}

		@Bean
		public SpringRepositoryAspect springRepositoryAspect() {
			return new SpringRepositoryAspect(nameMatchTransactionInterceptor);
		}

	}


	@Configuration
	@EnableConfigurationProperties(DynamicTransactionProperties.class)
	public static class ProxyTransactionConfig extends DynamicProxyTransactionConfiguration {

		@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
		public ProxyTransactionConfig(DynamicTransactionProperties properties, PlatformTransactionManager transactionManager) {
			super(properties, transactionManager);
		}

		@Override
		protected boolean enableAspectFilter() {
			return false;
		}
	}


}
