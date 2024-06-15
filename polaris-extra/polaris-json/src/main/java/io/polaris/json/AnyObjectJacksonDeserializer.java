package io.polaris.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.node.ValueNode;

/**
 * @author Qt
 * @since  Feb 04, 2024
 */
public class AnyObjectJacksonDeserializer extends BaseJacksonDeserializer<Object> {

	public AnyObjectJacksonDeserializer(Annotated annotated) {
		super(annotated);
	}

	@Override
	public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		JsonNode treeNode = ctxt.readTree(p);
		if (treeNode.isObject()) {
			return ctxt.readTreeAsValue(treeNode, this.javaType);
		} else if (treeNode.isArray()) {
			int size = treeNode.size();
			if (size > 0) {
				JsonNode node = treeNode.get(0);
				if (node.isObject()) {
					return ctxt.readTreeAsValue(node, this.javaType);
				} else if (node.isValueNode()) {
					String text = ((ValueNode) node).asText();
					return Jacksons.toJavaObject(text, this.javaType);
				}
			}
		} else {
			String text = treeNode.asText();
			return Jacksons.toJavaObject(text, this.javaType);
		}
		return null;
	}
}
