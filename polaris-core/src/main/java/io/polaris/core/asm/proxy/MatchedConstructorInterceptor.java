//package io.polaris.core.asm.proxy;
//
//import java.lang.reflect.Constructor;
//import java.util.function.Predicate;
//
//import lombok.Getter;
//
///**
// * @author Qt
// * @since May 12, 2024
// */
//@Getter
//public class MatchedConstructorInterceptor {
//
//	private final Predicate<Constructor<?>> predicate;
//	private final ConstructorInterceptor[] interceptors;
//
//	public MatchedConstructorInterceptor(Predicate<Constructor<?>> predicate, ConstructorInterceptor... interceptors) {
//		this.predicate = predicate;
//		this.interceptors = interceptors;
//	}
//
//	public boolean accept(Constructor<?> constructor) {
//		return predicate == null || predicate.test(constructor);
//	}
//}
