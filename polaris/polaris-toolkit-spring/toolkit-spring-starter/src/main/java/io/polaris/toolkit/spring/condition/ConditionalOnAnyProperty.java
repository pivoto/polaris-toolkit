package io.polaris.toolkit.spring.condition;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Qt
 * @version Dec 31, 2021
 * @since 1.8
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(ConditionalOnAnyProperty.OnCondition.class)
public @interface ConditionalOnAnyProperty {
	String prefix() default "";

	String[] name() default {};

	String[] values() default {};

	boolean matchIfMissing() default false;

	/**
	 * @author Qt
	 * @version Dec 31, 2021
	 * @since 1.8
	 */
	class OnCondition extends SpringBootCondition {

		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
			AnnotationAttributes attributes = AnnotationAttributes.fromMap(
					metadata.getAnnotationAttributes(ConditionalOnAnyProperty.class.getName()));
			if (attributes == null) {
				return ConditionOutcome.noMatch(
						ConditionMessage.forCondition(ConditionalOnAnyProperty.class)
								.because("no metadata"));
			}
			Environment env = context.getEnvironment();
			Spec spec = new Spec(attributes);
			return spec.matches(env);
		}

		static class Spec {
			private final boolean matchIfMissing;
			private final String prefix;
			private final String[] name;
			private final Set<Object> values;

			public Spec(AnnotationAttributes attributes) {
				this.matchIfMissing = attributes.getBoolean("matchIfMissing");
				this.prefix = attributes.getString("prefix");
				this.name = attributes.getStringArray("name");
				this.values = new HashSet<>();
				Collections.addAll(values, attributes.getStringArray("values"));
			}

			public ConditionOutcome matches(Environment env) {
				if (name.length == 0) {
					if (!StringUtils.hasText(prefix)) {
						return ConditionOutcome.noMatch(
								ConditionMessage.forCondition(ConditionalOnAnyProperty.class)
										.because("no specified names"));
					}
					String key = this.prefix;
					String val = env.getProperty(key);
					if (val == null) {
						return missing();
					}
					if (values.contains(val)) {
						return matched(key, val);
					}
					return noMatched();
				}
				for (String s : name) {
					String key = prefix + "." + s;
					String val = env.getProperty(key);
					if (val == null) {
						return missing();
					}
					if (values.contains(val)) {
						return matched(key, val);
					}
				}
				return noMatched();
			}

			private ConditionOutcome missing() {
				ConditionMessage message = ConditionMessage.forCondition(ConditionalOnAnyProperty.class)
						.because("missing property : " + this.toString());
				return matchIfMissing ? ConditionOutcome.match(message) : ConditionOutcome.noMatch(message);
			}

			private ConditionOutcome matched(String key, String val) {
				ConditionMessage message = ConditionMessage.forCondition(ConditionalOnAnyProperty.class)
						.because("property (" + key + " = " + val + ") match " + this.toString() + "");
				return ConditionOutcome.match(message);
			}

			private ConditionOutcome noMatched() {
				ConditionMessage message = ConditionMessage.forCondition(ConditionalOnAnyProperty.class)
						.because("not match " + this.toString() + "");
				return ConditionOutcome.noMatch(message);
			}

			@Override
			public String toString() {
				return "AnyPropertyCondition{" +
						"matchIfMissing=" + matchIfMissing +
						", prefix='" + prefix + '\'' +
						", name=" + Arrays.toString(name) +
						", anyValue=" + values +
						'}';
			}
		}

	}
}
