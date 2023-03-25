package io.polaris.toolkit.spring.condition;

import io.polaris.toolkit.spring.constants.ToolkitConstants;
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
@Conditional(ConditionalOnEnableCryptoProperties.OnCondition.class)
public @interface ConditionalOnEnableCryptoProperties {

	class OnCondition extends SpringBootCondition {
		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
			Boolean enabled = context.getEnvironment().getProperty(ToolkitConstants.TOOLKIT_CRYPTO_ENABLED, Boolean.class);
			if (Boolean.TRUE.equals(enabled)) {
				return ConditionOutcome.match(ConditionMessage
						.forCondition(ConditionalOnEnableCryptoProperties.class).because("match"));
			}
			return ConditionOutcome.noMatch(ConditionMessage
					.forCondition(ConditionalOnEnableCryptoProperties.class).because("not match"));
		}
	}
}



