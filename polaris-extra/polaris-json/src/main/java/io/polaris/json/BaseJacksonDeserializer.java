package io.polaris.json;

import java.io.IOException;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * @author Qt
 * @since Feb 04, 2024
 */
public abstract class BaseJacksonDeserializer<T> extends JsonDeserializer<T> {

	protected final JavaType javaType;
	protected final Class<?> rawClass;

	public BaseJacksonDeserializer(Annotated annotated) {
		if (annotated == null) {
			this.javaType = null;
			this.rawClass = null;
		} else {
			JavaType javaType = annotated.getType();
			Class<?> rawClass = annotated.getRawType();
			if (rawClass == void.class) {
				if (annotated instanceof AnnotatedWithParams) {
					int count = ((AnnotatedWithParams) annotated).getParameterCount();
					if (count == 1) {
						javaType = ((AnnotatedWithParams) annotated).getParameterType(0);
						rawClass = ((AnnotatedWithParams) annotated).getRawParameterType(0);
					}
				}
			}
			this.javaType = javaType;
			this.rawClass = rawClass;
		}
	}

	@Override
	public Class<?> handledType() {
		if (javaType != null) {
			return javaType.getRawClass();
		}
		return rawClass;
	}

	@SuppressWarnings("unchecked")
	protected T createBean(Supplier<T> supplier) {
		try {
			Class<?> rawClass = handledType();
			if (rawClass != null) {
				return (T) rawClass.newInstance();
			}
			return supplier.get();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	protected T createBean() {
		try {
			Class<?> rawClass = handledType();
			if (rawClass != null) {
				return (T) rawClass.newInstance();
			}
			return null;
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	protected TreeNode readFirstOfArray(JsonParser p) throws IOException {
		TreeNode tree = p.getCodec().readTree(p);
		while (tree != null && tree.isArray()) {
			ArrayNode arr = (ArrayNode) tree;
			if (!arr.isEmpty()) {
				tree = arr.get(0);
			} else {
				tree = null;
			}
		}
		return tree;
	}

	protected ArrayNode readAsArray(JsonParser p) throws IOException {
		TreeNode tree = p.getCodec().readTree(p);
		if (tree == null) {
			return null;
		}
		if (tree.isArray()) {
			return (ArrayNode) tree;
		}
		ArrayNode arrayNode = (ArrayNode) p.getCodec().createArrayNode();
		if (tree instanceof JsonNode) {
			arrayNode.add((JsonNode) tree);
		}
		return arrayNode;
	}

}

