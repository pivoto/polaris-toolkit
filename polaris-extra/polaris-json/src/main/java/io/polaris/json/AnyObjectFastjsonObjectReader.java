package io.polaris.json;

import com.alibaba.fastjson2.JSONReader;

import java.lang.reflect.Type;

/**
 * @author Qt
 * @since 1.8
 */
public class AnyObjectFastjsonObjectReader extends BaseFastjsonObjectReader<Object> {

	@Override
	public Object readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
		if (jsonReader.isObject()) {
			return jsonReader.read(fieldType);
		} else if (jsonReader.isArray()) {
			String[] strings = jsonReader.readStringArray();
			for (String string : strings) {
				return Fastjsons.toJavaObject(string, fieldType);
			}
			return null;
		} else {
			return Fastjsons.toJavaObject(jsonReader.read(String.class), fieldType);
		}
	}
}
