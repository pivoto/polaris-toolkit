package io.polaris.toolkit.spring.condition;

import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 * @version Dec 31, 2021
 * @since 1.8
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(ConditionalOnAspectJAutoProxy.OnCondition.class)
public @interface ConditionalOnAspectJAutoProxy {
	/**
	 * @author Qt
	 * @version Dec 31, 2021
	 * @since 1.8
	 */
	class OnCondition extends SpringBootCondition {

		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
			ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
			boolean hasBean = beanFactory.containsBeanDefinition(AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME);
			if (hasBean) {
				return ConditionOutcome.match(ConditionMessage
						.forCondition(ConditionalOnAspectJAutoProxy.class).because("match"));
			}
			return ConditionOutcome.noMatch(ConditionMessage
					.forCondition(ConditionalOnAspectJAutoProxy.class).because("not match"));
		}
	}
}


