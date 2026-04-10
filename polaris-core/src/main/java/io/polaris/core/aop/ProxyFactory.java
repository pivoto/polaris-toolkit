package io.polaris.core.aop;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import io.polaris.core.asm.internal.Signature;

/**
 * @author Qt
 * @since May 14, 2024
 */
public interface ProxyFactory {

	ProxyFactory classloader(ClassLoader classloader);

	ProxyFactory superclass(Class<?> superclass);

	ProxyFactory interfaces(Class<?>[] interfaces);

	ProxyFactory addAdvice(Advice... advices);

	ProxyFactory addAdvice(Predicate<Signature> predicate, Advice... advices);

	ProxyFactory addAdvice(BiPredicate<Signature, Method> predicate, Advice... advices);

	default ProxyFactory addBeforeAdvice(BeforeAdvice... advices) {
		return addAdvice(advices);
	}

	default ProxyFactory addBeforeAdvice(Predicate<Signature> predicate, BeforeAdvice... advices) {
		return addAdvice(predicate, advices);
	}

	default ProxyFactory addBeforeAdvice(BiPredicate<Signature, Method> predicate, BeforeAdvice... advices) {
		return addAdvice(predicate, advices);
	}

	default ProxyFactory addFixedAdvice(FixedAdvice... advices) {
		return addAdvice(advices);
	}

	default ProxyFactory addFixedAdvice(Predicate<Signature> predicate, FixedAdvice... advices) {
		return addAdvice(predicate, advices);
	}

	default ProxyFactory addFixedAdvice(BiPredicate<Signature, Method> predicate, FixedAdvice... advices) {
		return addAdvice(predicate, advices);
	}

	default ProxyFactory addAroundAdvice(AroundAdvice... advices) {
		return addAdvice(advices);
	}

	default ProxyFactory addAroundAdvice(Predicate<Signature> predicate, AroundAdvice... advices) {
		return addAdvice(predicate, advices);
	}

	default ProxyFactory addAroundAdvice(BiPredicate<Signature, Method> predicate, AroundAdvice... advices) {
		return addAdvice(predicate, advices);
	}

	default ProxyFactory addAfterAdvice(AfterAdvice... advices) {
		return addAdvice(advices);
	}

	default ProxyFactory addAfterAdvice(Predicate<Signature> predicate, AfterAdvice... advices) {
		return addAdvice(predicate, advices);
	}

	default ProxyFactory addAfterAdvice(BiPredicate<Signature, Method> predicate, AfterAdvice... advices) {
		return addAdvice(predicate, advices);
	}

	default ProxyFactory addThrowingAdvice(ThrowingAdvice... advices) {
		return addAdvice(advices);
	}

	default ProxyFactory addThrowingAdvice(Predicate<Signature> predicate, ThrowingAdvice... advices) {
		return addAdvice(predicate, advices);
	}

	default ProxyFactory addThrowingAdvice(BiPredicate<Signature, Method> predicate, ThrowingAdvice... advices) {
		return addAdvice(predicate, advices);
	}

	default ProxyFactory addFinallyAdvice(FinallyAdvice... advices) {
		return addAdvice(advices);
	}

	default ProxyFactory addFinallyAdvice(Predicate<Signature> predicate, FinallyAdvice... advices) {
		return addAdvice(predicate, advices);
	}

	default ProxyFactory addFinallyAdvice(BiPredicate<Signature, Method> predicate, FinallyAdvice... advices) {
		return addAdvice(predicate, advices);
	}


	ProxyFactory override(Function<Signature, Function<Object[], Object>> overrideGenerator);

	ProxyFactory override(Signature method, Function<Object[], Object> overrideFunction);

	ProxyFactory override(Map<Signature, Function<Object[], Object>> overrideMapper);

	ProxyFactory defaults(Function<Signature, Function<Object[], Object>> defaultGenerator);

	ProxyFactory defaults(Signature method, Function<Object[], Object> defaultFunction);

	ProxyFactory defaults(Map<Signature, Function<Object[], Object>> defaultMapper);

	<T> T get();

}
