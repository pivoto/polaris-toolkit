package io.polaris.core.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.polaris.core.asm.proxy.Invocation;
import io.polaris.core.tuple.ValueRef;

/**
 * @author Qt
 * @since May 14, 2024
 */
public class DefaultAdvisor implements Advisor {
	private final BeforeAdvice[] beforeAdvices;
	private final FixedAdvice[] fixedAdvices;
	private final AroundAdvice[] aroundAdvices;
	private final AfterAdvice[] afterAdvices;
	private final ThrowingAdvice[] throwingAdvices;
	private final FinallyAdvice[] finallyAdvices;
	private final Method method;

	public DefaultAdvisor(Method method, Advice... advices) {
		this(method, Arrays.asList(advices));
	}

	public DefaultAdvisor(Method method, Iterable<Advice> advices) {
		this.method = method;
		List<BeforeAdvice> beforeAdvices = new ArrayList<>();
		List<FixedAdvice> fixedAdvices = new ArrayList<>();
		List<AroundAdvice> aroundAdvices = new ArrayList<>();
		List<AfterAdvice> afterAdvices = new ArrayList<>();
		List<ThrowingAdvice> throwingAdvices = new ArrayList<>();
		List<FinallyAdvice> finallyAdvices = new ArrayList<>();
		for (Advice advice : advices) {
			if (advice instanceof BeforeAdvice) {
				beforeAdvices.add((BeforeAdvice) advice);
			} else if (advice instanceof FixedAdvice) {
				fixedAdvices.add((FixedAdvice) advice);
			} else if (advice instanceof AroundAdvice) {
				aroundAdvices.add((AroundAdvice) advice);
			} else if (advice instanceof AfterAdvice) {
				afterAdvices.add((AfterAdvice) advice);
			} else if (advice instanceof ThrowingAdvice) {
				throwingAdvices.add((ThrowingAdvice) advice);
			} else if (advice instanceof FinallyAdvice) {
				finallyAdvices.add((FinallyAdvice) advice);
			}
		}
		this.beforeAdvices = beforeAdvices.isEmpty() ? null : beforeAdvices.toArray(new BeforeAdvice[0]);
		this.fixedAdvices = fixedAdvices.isEmpty() ? null : fixedAdvices.toArray(new FixedAdvice[0]);
		this.aroundAdvices = aroundAdvices.isEmpty() ? null : aroundAdvices.toArray(new AroundAdvice[0]);
		this.afterAdvices = afterAdvices.isEmpty() ? null : afterAdvices.toArray(new AfterAdvice[0]);
		this.throwingAdvices = throwingAdvices.isEmpty() ? null : throwingAdvices.toArray(new ThrowingAdvice[0]);
		this.finallyAdvices = finallyAdvices.isEmpty() ? null : finallyAdvices.toArray(new FinallyAdvice[0]);
	}


	@Override
	public Object advise(Object target, Object[] args, Invocation invocation) throws Throwable {
		Object ret = null;
		Throwable thr = null;
		try {
			before(target, method, args);
			ValueRef<?> ref = fixedValue(target, method, args);
			if (ref != null) {
				ret = ref.get();
			} else {
				ret = around(target, method, args, invocation);
			}
			after(target, method, args, ret);
			return ret;
		} catch (Throwable e) {
			thr = e;
			throwing(target, method, args, thr);
			throw e;
		} finally {
			after(target, method, args, ret, thr);
		}
	}


	void before(Object target, Method method, Object[] args) throws Throwable {
		if (beforeAdvices == null || beforeAdvices.length == 0) {
			return;
		}
		for (int i = 0; i < beforeAdvices.length; i++) {
			BeforeAdvice beforeAdvice = beforeAdvices[i];
			beforeAdvice.before(target, method, args);
		}
	}

	ValueRef<?> fixedValue(Object target, Method method, Object[] args) {
		if (fixedAdvices == null || fixedAdvices.length == 0) {
			return null;
		}
		for (int i = 0; i < fixedAdvices.length; i++) {
			FixedAdvice fixedAdvice = fixedAdvices[i];
			ValueRef<?> ref = fixedAdvice.fixedValue(target, method, args);
			if (ref != null) {
				return ref;
			}
		}
		return null;
	}

	Object around(Object target, Method method, Object[] args, Invocation invocation) throws Throwable {
		if (aroundAdvices == null || aroundAdvices.length == 0) {
			return invocation.invoke(target, args);
		}
		AroundInvocation aroundInvocation = new AroundInvocation(invocation, aroundAdvices, 0, method);
		return aroundInvocation.invoke(target, args);
	}

	void after(Object target, Method method, Object[] args, Object retVal) throws Throwable {
		for (int i = 0; i < afterAdvices.length; i++) {
			AfterAdvice afterAdvice = afterAdvices[i];
			afterAdvice.after(target, method, args, retVal);
		}
	}

	void throwing(Object target, Method method, Object[] args, Throwable e) throws Throwable {
		for (int i = 0; i < throwingAdvices.length; i++) {
			ThrowingAdvice throwingAdvice = throwingAdvices[i];
			throwingAdvice.throwing(target, method, args, e);
		}
	}

	void after(Object target, Method method, Object[] args, Object retVal, Throwable e) throws Throwable {
		for (int i = 0; i < finallyAdvices.length; i++) {
			FinallyAdvice finallyAdvice = finallyAdvices[i];
			finallyAdvice.after(target, method, args, retVal, e);
		}
	}
}
