package io.polaris.toolkit.spring.configuration;

import io.polaris.toolkit.spring.constants.ToolkitConstants;
import io.polaris.toolkit.spring.support.InheritedAnnotationClassFilter;
import io.polaris.toolkit.spring.transaction.DynamicTransactionProperties;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.aspectj.TypePatternClassFilter;
import org.springframework.aop.support.ClassFilters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.CompositeTransactionAttributeSource;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 * @version Dec 30, 2021
 * @see ProxyTransactionManagementConfiguration#transactionAdvisor(org.springframework.transaction.interceptor.TransactionAttributeSource, org.springframework.transaction.interceptor.TransactionInterceptor)
 * @since 1.8
 */
@Configuration
@EnableConfigurationProperties(DynamicTransactionProperties.class)
public class DynamicProxyTransactionConfiguration extends AbstractDynamicTransactionConfiguration {

	protected TransactionInterceptor nameMatchTransactionInterceptor;
	protected TransactionInterceptor annotationTransactionInterceptor;

	protected NameMatchTransactionAttributeSource nameMatchTransactionAttributeSource;
	protected AnnotationTransactionAttributeSource annotationTransactionAttributeSource;

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	public DynamicProxyTransactionConfiguration(DynamicTransactionProperties properties, PlatformTransactionManager transactionManager) {
		super(properties, transactionManager);
		this.nameMatchTransactionAttributeSource = buildNameMatchTxAttrSource();
		this.annotationTransactionAttributeSource = buildAnnotationTxAttrSource();
		this.nameMatchTransactionInterceptor = buildInterceptor(nameMatchTransactionAttributeSource, this.transactionManager);
		this.annotationTransactionInterceptor = buildInterceptor(annotationTransactionAttributeSource, this.transactionManager);
	}


	@Bean
	public BeanFactoryTransactionAttributeSourceAdvisor txAdvisor() {
		BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
		List<ClassFilter> filters = new ArrayList<>();
		if (StringUtils.hasText(properties.getClassPattern())) {
			filters.add(new TypePatternClassFilter(properties.getClassPattern()));
		}
		if (enableAspectFilter()) {
			addClassFilters(filters);
		}
		if (filters.isEmpty()) {
			filters.add(clazz -> false);
		}

		CompositeTransactionAttributeSource transactionAttributeSource =
				new CompositeTransactionAttributeSource(
						this.annotationTransactionAttributeSource,
						this.nameMatchTransactionAttributeSource
				);
		advisor.setTransactionAttributeSource(transactionAttributeSource);
		advisor.setAdvice(buildInterceptor(transactionAttributeSource, this.transactionManager));
		advisor.setClassFilter(ClassFilters.union(filters.toArray(new ClassFilter[0])));
		advisor.setOrder(ToolkitConstants.ORDER_TRANSACTION_ASPECT);

		return advisor;
	}

	protected boolean enableAspectFilter() {
		return true;
	}

	protected void addClassFilters(List<ClassFilter> filters) {
		if (properties.isEnableTransactionalAspect()) {

			filters.add(InheritedAnnotationClassFilter.withClassOrMethodAnnotation(Transactional.class));
			ClassLoader classLoader = getClass().getClassLoader();
			if (ClassUtils.isPresent("javax.transaction.Transactional", classLoader)) {
				Class<? extends Annotation> annotationType = (Class<? extends Annotation>)
						ClassUtils.resolveClassName("javax.transaction.Transactional", classLoader);
				filters.add(InheritedAnnotationClassFilter.withClassOrMethodAnnotation(annotationType));
			}
		}
		if (properties.isEnableServiceAspect()) {
			filters.add(InheritedAnnotationClassFilter.withClassOrMethodAnnotation(Service.class));
		}
		if (properties.isEnableRepositoryAspect()) {
			filters.add(InheritedAnnotationClassFilter.withClassOrMethodAnnotation(Repository.class));
		}
	}

}
