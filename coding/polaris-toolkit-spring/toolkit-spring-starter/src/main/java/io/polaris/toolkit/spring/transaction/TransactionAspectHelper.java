package io.polaris.toolkit.spring.transaction;

import io.polaris.toolkit.spring.annotation.EnableDynamicTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.concurrent.Callable;

/**
 * @author Qt
 * @version Dec 30, 2021
 * @since 1.8
 */
@Slf4j
public class TransactionAspectHelper {

	private static final ThreadLocal<Boolean> RUNNING = ThreadLocal.withInitial(() -> false);

	public static void start() {
		RUNNING.set(true);
	}

	public static void stop() {
		RUNNING.remove();
	}

	public static boolean isStarted() {
		return Boolean.TRUE.equals(RUNNING.get());
	}

	public interface Invokable {
		Object invoke() throws Throwable;
	}

	public static Object proceed(Invokable interceptorCall, Invokable directiveCall) throws Throwable {
		if (TransactionAspectHelper.isStarted()) {
			return directiveCall.invoke();
		} else {
			TransactionAspectHelper.start();
			try {
				return interceptorCall.invoke();
			} finally {
				TransactionAspectHelper.stop();
			}
		}
	}

	public static boolean registerAspectJIfNecessary(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(
				EnableDynamicTransaction.class.getName(), false));
		if (annotationAttributes != null) {
			if (annotationAttributes.getBoolean("enableAspectJAutoProxy")) {
				AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry);
				if (annotationAttributes.getBoolean("proxyTargetClass")) {
					AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
				}
				if (annotationAttributes.getBoolean("exposeProxy")) {
					AopConfigUtils.forceAutoProxyCreatorToExposeProxy(registry);
				}
				return true;
			}
		}
		return false;
	}
}
