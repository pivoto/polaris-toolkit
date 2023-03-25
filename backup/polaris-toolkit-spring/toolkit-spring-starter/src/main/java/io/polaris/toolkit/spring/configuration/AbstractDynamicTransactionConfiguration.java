package io.polaris.toolkit.spring.configuration;

import io.polaris.toolkit.spring.constants.ToolkitConstants;
import io.polaris.toolkit.spring.transaction.DynamicTransactionProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.ClassFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.AbstractTransactionManagementConfiguration;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Qt
 * @version Dec 31, 2021
 * @see ProxyTransactionManagementConfiguration#transactionAdvisor(org.springframework.transaction.interceptor.TransactionAttributeSource, org.springframework.transaction.interceptor.TransactionInterceptor)
 * @since 1.8
 */
@Configuration
@EnableConfigurationProperties(DynamicTransactionProperties.class)
public abstract class AbstractDynamicTransactionConfiguration extends AbstractTransactionManagementConfiguration {
	protected DynamicTransactionProperties properties;
	protected PlatformTransactionManager transactionManager;


	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	public AbstractDynamicTransactionConfiguration(DynamicTransactionProperties properties, PlatformTransactionManager transactionManager) {
		this.properties = properties;
		this.transactionManager = transactionManager;
	}

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {
	}

	protected TransactionInterceptor buildInterceptor(TransactionAttributeSource source, TransactionManager transactionManager) {
		return new TransactionInterceptor(transactionManager, source);
	}

	protected NameMatchTransactionAttributeSource buildNameMatchTxAttrSource() {
		NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();

		// REQUIRES_NEW
		{
			String transactionalMethods = properties.getNewTransactionalMethods();
			if (StringUtils.hasText(transactionalMethods)) {
				RuleBasedTransactionAttribute attr = new RuleBasedTransactionAttribute();
				attr.setPropagationBehavior(TransactionAttribute.PROPAGATION_REQUIRES_NEW);
				attr.setIsolationLevel(TransactionAttribute.ISOLATION_READ_COMMITTED);
				addRollbackRules(attr, properties.getRollbackExceptions(), properties.getNoRollbackExceptions());
				String[] methods = StringUtils.delimitedListToStringArray(transactionalMethods, ToolkitConstants.STANDARD_DELIMITER);
				for (String method : methods) {
					source.addTransactionalMethod(method, attr);
				}
			}
		}

		// readOnly
		{
			String transactionalMethods = properties.getReadonlyTransactionalMethods();
			if (StringUtils.hasText(transactionalMethods)) {
				RuleBasedTransactionAttribute attr = new RuleBasedTransactionAttribute();
				attr.setPropagationBehavior(TransactionAttribute.PROPAGATION_REQUIRED);
				attr.setReadOnly(true);
				addRollbackRules(attr, properties.getRollbackExceptions(), properties.getNoRollbackExceptions());
				String[] methods = StringUtils.delimitedListToStringArray(transactionalMethods, ToolkitConstants.STANDARD_DELIMITER);
				for (String method : methods) {
					source.addTransactionalMethod(method, attr);
				}
			}
		}

		// REQUIRED
		{
			RuleBasedTransactionAttribute attr = new RuleBasedTransactionAttribute();
			attr.setPropagationBehavior(TransactionAttribute.PROPAGATION_REQUIRED);
			attr.setIsolationLevel(TransactionAttribute.ISOLATION_READ_COMMITTED);
			addRollbackRules(attr, properties.getRollbackExceptions(), properties.getNoRollbackExceptions());
			String transactionalMethods = properties.getStdTransactionalMethods();
			if (StringUtils.hasText(transactionalMethods)) {
				String[] methods = StringUtils.delimitedListToStringArray(transactionalMethods, ToolkitConstants.STANDARD_DELIMITER);
				for (String method : methods) {
					source.addTransactionalMethod(method, attr);
				}
			} else {
				source.addTransactionalMethod("*", attr);
			}
		}


		// custom rules
		{
			List<DynamicTransactionProperties.TransactionalMethodsRule> rules = properties.getRules();
			if (!CollectionUtils.isEmpty(rules)) {
				for (DynamicTransactionProperties.TransactionalMethodsRule rule : rules) {
					RuleBasedTransactionAttribute attr = new RuleBasedTransactionAttribute();
					attr.setPropagationBehavior(rule.getPropagation().getNumber());
					attr.setIsolationLevel(rule.getIsolation().getNumber());
					if (rule.getTimeout() > 0) {
						attr.setTimeout(rule.getTimeout());
					}
					if (rule.isReadOnly()) {
						attr.setReadOnly(rule.isReadOnly());
					}
					String noRollbackExceptions = rule.getNoRollbackExceptions();
					String rollbackExceptions = rule.getRollbackExceptions();
					if (!StringUtils.hasText(noRollbackExceptions)) {
						noRollbackExceptions = properties.getNoRollbackExceptions();
					}
					if (!StringUtils.hasText(rollbackExceptions)) {
						rollbackExceptions = properties.getRollbackExceptions();
					}
					addRollbackRules(attr, rollbackExceptions, noRollbackExceptions);

					String ruleMethods = rule.getMethods();
					if (StringUtils.hasText(ruleMethods)) {
						String[] methods = StringUtils.delimitedListToStringArray(ruleMethods, ToolkitConstants.STANDARD_DELIMITER);
						for (String method : methods) {
							source.addTransactionalMethod(method, attr);
						}
					}
				}
			}
		}
		return source;
	}

	private void addRollbackRules(RuleBasedTransactionAttribute attr, String rollbackExceptions, String noRollbackExceptions) {
		if (StringUtils.hasText(noRollbackExceptions)) {
			String[] exceptions = StringUtils.delimitedListToStringArray(noRollbackExceptions, ToolkitConstants.STANDARD_DELIMITER);
			for (String exception : exceptions) {
				attr.getRollbackRules().add(new NoRollbackRuleAttribute(exception));
			}
		}
		if (StringUtils.hasText(rollbackExceptions)) {
			String[] exceptions = StringUtils.delimitedListToStringArray(rollbackExceptions, ToolkitConstants.STANDARD_DELIMITER);
			for (String exception : exceptions) {
				attr.getRollbackRules().add(new RollbackRuleAttribute(exception));
			}
		}
	}

	protected AnnotationTransactionAttributeSource buildAnnotationTxAttrSource() {
		return new AnnotationTransactionAttributeSource(false);
	}

	protected AnnotationTransactionAttributeSource buildMethodAnnotationTxAttrSource() {
		return new AnnotationTransactionAttributeSource(false) {
			@Override
			protected TransactionAttribute findTransactionAttribute(Class<?> clazz) {
				return null;
			}
		};
	}

	protected AnnotationTransactionAttributeSource buildClassAnnotationTxAttrSource() {
		return new AnnotationTransactionAttributeSource(false) {
			@Override
			protected TransactionAttribute findTransactionAttribute(Method method) {
				return null;
			}
		};
	}

	@RequiredArgsConstructor
	static class ClassAnnotationFilter implements ClassFilter {

		private final Class<? extends Annotation> annotationType;
		private final boolean matchesMethod;


		@Override
		public boolean matches(Class<?> targetClass) {
			if (AnnotatedElementUtils.hasAnnotation(targetClass, this.annotationType)) {
				return true;
			}

			if (matchesMethod) {
				Set<Class<?>> classes = new LinkedHashSet<>();
				if (!Proxy.isProxyClass(targetClass)) {
					classes.add(ClassUtils.getUserClass(targetClass));
				}
				classes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetClass));

				for (Class<?> clazz : classes) {
					Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
					for (Method method : methods) {
						if (AnnotatedElementUtils.hasAnnotation(method, this.annotationType)) {
							return true;
						}
					}
				}
			}
			return false;
		}

	}
}
