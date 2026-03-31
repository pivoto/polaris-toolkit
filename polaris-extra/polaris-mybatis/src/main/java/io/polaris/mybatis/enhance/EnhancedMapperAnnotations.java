package io.polaris.mybatis.enhance;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.string.Strings;
import io.polaris.mybatis.annotation.AutoMapper;
import lombok.Getter;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.mapping.SqlCommandType;

/**
 * @author Qt
 * @since Mar 30, 2026
 */
public class EnhancedMapperAnnotations {

	@SafeVarargs
	public static Optional<AnnotationWrapper<?>> getAnnotationWrapper(String targetDatabaseId, Method method, boolean errorIfNoMatch, Class<? extends Annotation>... targetTypes) {
		return getAnnotationWrapper(targetDatabaseId, method, errorIfNoMatch, Arrays.asList(targetTypes));
	}

	public static Optional<EnhancedMapperAnnotations.AnnotationWrapper<?>> getAnnotationWrapper(String targetDatabaseId, Method method, boolean errorIfNoMatch, Collection<Class<? extends Annotation>> targetTypes) {
		Map<String, EnhancedMapperAnnotations.AnnotationWrapper<?>> statementAnnotations = targetTypes.stream()
			.flatMap(x -> Arrays.stream(method.getAnnotationsByType(x))).map(EnhancedMapperAnnotations.AnnotationWrapper::new)
			.collect(Collectors.toMap(EnhancedMapperAnnotations.AnnotationWrapper::getDatabaseId, x -> x, (existing, duplicate) -> {
				throw new BuilderException(String.format("Detected conflicting annotations '%s' and '%s' on '%s'.",
					existing.getAnnotation(), duplicate.getAnnotation(),
					method.getDeclaringClass().getName() + "." + method.getName()));
			}));
		EnhancedMapperAnnotations.AnnotationWrapper<?> annotationWrapper = null;
		if (targetDatabaseId != null) {
			annotationWrapper = statementAnnotations.get(targetDatabaseId);
		}
		if (annotationWrapper == null) {
			annotationWrapper = statementAnnotations.get("");
		}
		if (errorIfNoMatch && annotationWrapper == null && !statementAnnotations.isEmpty()) {
			// Annotations exist, but there is no matching one for the specified databaseId
			throw new BuilderException(
				String.format(
					"Could not find a statement annotation that correspond a current database or default statement on method '%s.%s'. Current database id is [%s].",
					method.getDeclaringClass().getName(), method.getName(), targetDatabaseId));
		}
		return Optional.ofNullable(annotationWrapper);
	}

	@Nullable
	public <A extends Annotation> EnhancedMapperAnnotations.AnnotationWrapper<A> getAnnotationWrapper(String targetDatabaseId, Method method, Class<A> annotationType, Function<A, String> databaseIdGetter, Function<A, SqlCommandType> sqlCommandTypeGetter) {
		return getAnnotationWrapper(targetDatabaseId, method, false, annotationType, databaseIdGetter, sqlCommandTypeGetter);
	}

	@Nullable
	public static <A extends Annotation> EnhancedMapperAnnotations.AnnotationWrapper<A> getAnnotationWrapper(String targetDatabaseId, Method method, boolean errorIfNoMatch, Class<A> annotationType, Function<A, String> databaseIdGetter, Function<A, SqlCommandType> sqlCommandTypeGetter) {
		A annotation = getAnnotation(targetDatabaseId, method, errorIfNoMatch, annotationType, databaseIdGetter);
		if (annotation == null) {
			return null;
		}
		return new EnhancedMapperAnnotations.AnnotationWrapper<>(annotation, databaseIdGetter, sqlCommandTypeGetter);
	}

	@Nullable
	public static AutoMapper getAutoMapper(String targetDatabaseId, Method method) {
		return getAnnotation(targetDatabaseId, method, false, AutoMapper.class, AutoMapper::databaseId);
	}

	@Nullable
	public static <A extends Annotation> A getAnnotation(String targetDatabaseId, Method method, Class<A> annotationType, Function<A, String> databaseIdGetter) {
		return getAnnotation(targetDatabaseId, method, false, annotationType, databaseIdGetter);
	}

	@Nullable
	public static <A extends Annotation> A getAnnotation(String targetDatabaseId, Method method, boolean errorIfNoMatch, Class<A> annotationType, Function<A, String> databaseIdGetter) {
		A[] annotations = method.getAnnotationsByType(annotationType);
		Map<String, A> map = new HashMap<>();
		for (A annotation : annotations) {
			String databaseId = databaseIdGetter.apply(annotation);
			databaseId = Strings.trimToEmpty(databaseId);

			A existing = map.get(databaseId);
			if (map.containsKey(databaseId)) {
				throw new BuilderException(String.format("Detected conflicting annotations '%s' and '%s' on '%s'.",
					existing, annotation,
					method.getDeclaringClass().getName() + "." + method.getName()));
			}
			map.put(databaseId, annotation);
		}
		A annotation = null;
		if (targetDatabaseId != null) {
			annotation = map.get(targetDatabaseId);
		}
		if (annotation == null) {
			annotation = map.get("");
		}
		if (annotation == null) {
			if (errorIfNoMatch && !map.isEmpty()) {
				// Annotations exist, but there is no matching one for the specified databaseId
				throw new BuilderException(
					String.format(
						"Could not find a statement annotation that correspond a current database or default statement on method '%s.%s'. Current database id is [%s].",
						method.getDeclaringClass().getName(), method.getName(), targetDatabaseId));
			}
			return null;
		}
		return annotation;
	}


	@Getter
	public static class AnnotationWrapper<A extends Annotation> {
		@Nonnull
		private final A annotation;
		@Nullable
		private final String databaseId;
		@Nullable
		private final SqlCommandType sqlCommandType;

		public AnnotationWrapper(@Nonnull A annotation, @Nullable String databaseId, @Nullable SqlCommandType sqlCommandType) {
			this.annotation = annotation;
			this.databaseId = databaseId;
			this.sqlCommandType = sqlCommandType;
		}

		AnnotationWrapper(A annotation, Function<A, String> databaseIdGetter, Function<A, SqlCommandType> sqlCommandTypeGetter) {
			this(annotation,
				databaseIdGetter != null ? databaseIdGetter.apply(annotation) : null,
				sqlCommandTypeGetter != null ? sqlCommandTypeGetter.apply(annotation) : null);
		}

		AnnotationWrapper(A annotation) {
			this(annotation, defaultDatabaseIdGetter(annotation), defaultSqlCommandTypeGetter(annotation));
		}

		static String defaultDatabaseIdGetter(Annotation annotation) {
			if (annotation instanceof AutoMapper) {
				return ((AutoMapper) annotation).databaseId();
			}
			if (annotation instanceof Select) {
				return ((Select) annotation).databaseId();
			}
			if (annotation instanceof Update) {
				return ((Update) annotation).databaseId();
			}
			if (annotation instanceof Insert) {
				return ((Insert) annotation).databaseId();
			}
			if (annotation instanceof Delete) {
				return ((Delete) annotation).databaseId();
			}
			if (annotation instanceof SelectProvider) {
				return ((SelectProvider) annotation).databaseId();
			}
			if (annotation instanceof UpdateProvider) {
				return ((UpdateProvider) annotation).databaseId();
			}
			if (annotation instanceof InsertProvider) {
				return ((InsertProvider) annotation).databaseId();
			}
			if (annotation instanceof DeleteProvider) {
				return ((DeleteProvider) annotation).databaseId();
			}
			if (annotation instanceof Options) {
				return ((Options) annotation).databaseId();
			}
			if (annotation instanceof SelectKey) {
				return ((SelectKey) annotation).databaseId();
			}
			return "";
		}

		static SqlCommandType defaultSqlCommandTypeGetter(Annotation annotation) {
			if (annotation instanceof AutoMapper) {
				return ((AutoMapper) annotation).type().sqlCommandType();
			}
			if (annotation instanceof Select) {
				return SqlCommandType.SELECT;
			}
			if (annotation instanceof Update) {
				return SqlCommandType.UPDATE;
			}
			if (annotation instanceof Insert) {
				return SqlCommandType.INSERT;
			}
			if (annotation instanceof Delete) {
				return SqlCommandType.DELETE;
			}
			if (annotation instanceof SelectProvider) {
				return SqlCommandType.SELECT;
			}
			if (annotation instanceof UpdateProvider) {
				return SqlCommandType.UPDATE;
			}
			if (annotation instanceof InsertProvider) {
				return SqlCommandType.INSERT;
			}
			if (annotation instanceof DeleteProvider) {
				return SqlCommandType.DELETE;
			}
			return SqlCommandType.UNKNOWN;
		}

	}
}
