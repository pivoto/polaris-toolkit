package io.polaris.json.migration;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.polaris.core.converter.Converters;
import io.polaris.core.io.IO;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.lang.TypeRefs;
import io.polaris.core.string.Strings;
import io.polaris.json.Jacksons;

/**
 * 用于迁移和替代 com.alibaba.fastjson2.JSON 类
 *
 * @author Qt
 * @since Mar 30, 2025
 */
public interface JSON {

	static Object parse(String text) {
		text = Strings.trim(text);
		if (text == null || text.isEmpty()) {
			return null;
		}
		if (text.charAt(0) == '{') {
			return JSONObject.parse(text);
		} else if (text.charAt(0) == '[') {
			return JSONArray.parse(text);
		} else {
			return Jacksons.toJavaObject(text, Object.class);
		}
	}


	static Object parse(String text, int offset, int length) {
		if (text == null || text.isEmpty() || length == 0) {
			return null;
		}
		return parse(text.substring(offset, length));
	}


	static Object parse(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return parse(new String(bytes));
	}

	static Object parse(byte[] bytes, int offset, int length) {
		if (bytes == null || bytes.length == 0 || length == 0) {
			return null;
		}
		return parse(new String(bytes, offset, length));
	}


	static Object parse(char[] chars) {
		if (chars == null || chars.length == 0) {
			return null;
		}
		return parse(new String(chars));
	}

	static Object parse(char[] chars, int offset, int length) {
		if (chars == null || chars.length == 0 || length == 0) {
			return null;
		}
		return parse(new String(chars, offset, length));
	}


	static Object parse(InputStream in) {
		if (in == null) {
			return null;
		}
		try {
			String text = IO.toString(in);
			return parse(text);
		} catch (IOException e) {
			throw new JSONException(e);
		} finally {
			IO.close(in);
		}
	}

	static JSONObject parseObject(String text) {
		return JSONObject.parseObject(text);
	}

	static JSONObject parseObject(String text, int offset, int length) {
		if (text == null || text.isEmpty() || length == 0) {
			return null;
		}
		return JSONObject.parseObject(text.substring(offset, length));
	}


	static JSONObject parseObject(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return JSONObject.parseObject(new String(bytes));
	}

	static JSONObject parseObject(byte[] bytes, Charset charset) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return JSONObject.parseObject(new String(bytes, charset));
	}

	static JSONObject parseObject(byte[] bytes, int offset, int length) {
		if (bytes == null || bytes.length == 0 || length == 0) {
			return null;
		}
		return JSONObject.parseObject(new String(bytes, offset, length));
	}


	static JSONObject parseObject(byte[] bytes, int offset, int length, Charset charset) {
		if (bytes == null || bytes.length == 0 || length == 0) {
			return null;
		}
		return JSONObject.parseObject(new String(bytes, offset, length, charset));
	}


	static JSONObject parseObject(char[] chars) {
		if (chars == null || chars.length == 0) {
			return null;
		}
		return JSONObject.parseObject(new String(chars));
	}

	static JSONObject parseObject(char[] chars, int offset, int length) {
		if (chars == null || chars.length == 0 || length == 0) {
			return null;
		}
		return JSONObject.parseObject(new String(chars, offset, length));
	}


	static JSONObject parseObject(Reader in) {
		if (in == null) {
			return null;
		}
		try {
			String text = IO.toString(in);
			return JSONObject.parseObject(text);
		} catch (IOException e) {
			throw new JSONException(e);
		} finally {
			IO.close(in);
		}
	}

	static JSONObject parseObject(InputStream in) {
		if (in == null) {
			return null;
		}
		try {
			String text = IO.toString(in);
			return JSONObject.parseObject(text);
		} catch (IOException e) {
			throw new JSONException(e);
		} finally {
			IO.close(in);
		}
	}

	static JSONObject parseObject(URL url) {
		if (url == null) {
			return null;
		}
		try (InputStream is = url.openStream()) {
			return parseObject(is, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new JSONException("JSON#parseObject cannot parse '" + url + "'", e);
		}
	}

	static JSONObject parseObject(InputStream in, Charset charset) {
		if (in == null) {
			return null;
		}
		try {
			String text = IO.toString(in, charset);
			return JSONObject.parseObject(text);
		} catch (IOException e) {
			throw new JSONException(e);
		} finally {
			IO.close(in);
		}
	}


	static <T> T parseObject(String text, Class<T> clazz) {
		if (text == null || text.isEmpty()) {
			return null;
		}
		return Jacksons.toJavaObject(text, clazz);
	}


	static <T> T parseObject(String text, Type type) {
		if (text == null || text.isEmpty()) {
			return null;
		}
		return Jacksons.toJavaObject(text, type);
	}

	static <T> T parseObject(String text, TypeRef<T> type) {
		if (text == null || text.isEmpty()) {
			return null;
		}
		return Jacksons.toJavaObject(text, type);
	}

	static <T> T parseObject(String text, int offset, int length, Class<T> clazz) {
		if (text == null || text.isEmpty() || length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(text.substring(offset, length), clazz);
	}

	static <T> T parseObject(String text, int offset, int length, Type type) {
		if (text == null || text.isEmpty() || length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(text.substring(offset, length), type);
	}

	static <T> T parseObject(char[] chars, Class<T> clazz) {
		if (chars == null || chars.length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(new String(chars), clazz);
	}

	static <T> T parseObject(char[] chars, Type type) {
		if (chars == null || chars.length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(new String(chars), type);
	}

	static <T> T parseObject(char[] chars, int offset, int length, Class<T> clazz) {
		if (chars == null || chars.length == 0 || length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(new String(chars, offset, length), clazz);
	}

	static <T> T parseObject(char[] chars, int offset, int length, Type type) {
		if (chars == null || chars.length == 0 || length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(new String(chars, offset, length), type);
	}


	static <T> T parseObject(byte[] bytes, Class<T> clazz) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(new String(bytes), clazz);
	}

	static <T> T parseObject(byte[] bytes, Type type) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(new String(bytes), type);
	}

	static <T> T parseObject(byte[] bytes, int offset, int length, Class<T> clazz) {
		if (bytes == null || bytes.length == 0 || length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(new String(bytes, offset, length), clazz);
	}

	static <T> T parseObject(byte[] bytes, int offset, int length, Type type) {
		if (bytes == null || bytes.length == 0 || length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(new String(bytes, offset, length), type);
	}


	static <T> T parseObject(Reader input, Type type) {
		if (input == null) {
			return null;
		}
		try {
			return Jacksons.toJavaObject(IO.toString(input), type);
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}

	static <T> T parseObject(InputStream input, Type type) {
		if (input == null) {
			return null;
		}
		try {
			return Jacksons.toJavaObject(IO.toString(input), type);
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}

	static <T> T parseObject(InputStream input, Charset charset, Type type) {
		if (input == null) {
			return null;
		}
		try {
			return Jacksons.toJavaObject(IO.toString(input, charset), type);
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}


	static <T> T parseObject(URL url, Type type) {
		if (url == null) {
			return null;
		}
		try (InputStream is = url.openStream()) {
			return Jacksons.toJavaObject(IO.toString(is), type);
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}


	static JSONArray parseArray(String text) {
		return JSONArray.parseArray(text);
	}

	static JSONArray parseArray(String text, int offset, int length) {
		if (text == null || text.isEmpty() || length == 0) {
			return null;
		}
		return JSONArray.parseArray(text.substring(offset, length));
	}


	static JSONArray parseArray(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return JSONArray.parseArray(new String(bytes));
	}

	static JSONArray parseArray(byte[] bytes, Charset charset) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return JSONArray.parseArray(new String(bytes, charset));
	}

	static JSONArray parseArray(byte[] bytes, int offset, int length) {
		if (bytes == null || bytes.length == 0 || length == 0) {
			return null;
		}
		return JSONArray.parseArray(new String(bytes, offset, length));
	}


	static JSONArray parseArray(byte[] bytes, int offset, int length, Charset charset) {
		if (bytes == null || bytes.length == 0 || length == 0) {
			return null;
		}
		return JSONArray.parseArray(new String(bytes, offset, length, charset));
	}


	static JSONArray parseArray(char[] chars) {
		if (chars == null || chars.length == 0) {
			return null;
		}
		return JSONArray.parseArray(new String(chars));
	}

	static JSONArray parseArray(char[] chars, int offset, int length) {
		if (chars == null || chars.length == 0 || length == 0) {
			return null;
		}
		return JSONArray.parseArray(new String(chars, offset, length));
	}


	static JSONArray parseArray(Reader in) {
		if (in == null) {
			return null;
		}
		try {
			String text = IO.toString(in);
			return JSONArray.parseArray(text);
		} catch (IOException e) {
			throw new JSONException(e);
		} finally {
			IO.close(in);
		}
	}

	static JSONArray parseArray(InputStream in) {
		if (in == null) {
			return null;
		}
		try {
			String text = IO.toString(in);
			return JSONArray.parseArray(text);
		} catch (IOException e) {
			throw new JSONException(e);
		} finally {
			IO.close(in);
		}
	}

	static JSONArray parseArray(URL url) {
		if (url == null) {
			return null;
		}
		try (InputStream is = url.openStream()) {
			return parseArray(is, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new JSONException("JSON#parseArray cannot parse '" + url + "'", e);
		}
	}

	static JSONArray parseArray(InputStream in, Charset charset) {
		if (in == null) {
			return null;
		}
		try {
			String text = IO.toString(in, charset);
			return JSONArray.parseArray(text);
		} catch (IOException e) {
			throw new JSONException(e);
		} finally {
			IO.close(in);
		}
	}


	static <T> List<T> parseArray(String text, Class<T> clazz) {
		if (text == null || text.isEmpty()) {
			return null;
		}
		return Jacksons.toJavaObject(text, TypeRefs.getType(List.class, clazz));
	}


	static <T> List<T> parseArray(String text, Type type) {
		if (text == null || text.isEmpty()) {
			return null;
		}
		return Jacksons.toJavaObject(text, TypeRefs.getType(List.class, type));
	}

	static <T> List<T> parseArray(String text, TypeRef<List<T>> type) {
		if (text == null || text.isEmpty()) {
			return null;
		}
		return Jacksons.toJavaObject(text, type);
	}

	static <T> List<T> parseArray(String text, int offset, int length, Class<T> clazz) {
		if (text == null || text.isEmpty() || length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(text.substring(offset, length), TypeRefs.getType(List.class, clazz));
	}

	static <T> List<T> parseArray(String text, int offset, int length, Type type) {
		if (text == null || text.isEmpty() || length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(text.substring(offset, length), TypeRefs.getType(List.class, type));
	}

	static <T> List<T> parseArray(char[] chars, Class<T> clazz) {
		if (chars == null || chars.length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(new String(chars), TypeRefs.getType(List.class, clazz));
	}

	static <T> List<T> parseArray(char[] chars, Type type) {
		if (chars == null || chars.length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(new String(chars), TypeRefs.getType(List.class, type));
	}

	static <T> List<T> parseArray(char[] chars, int offset, int length, Class<T> clazz) {
		if (chars == null || chars.length == 0 || length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(new String(chars, offset, length), TypeRefs.getType(List.class, clazz));
	}

	static <T> List<T> parseArray(char[] chars, int offset, int length, Type type) {
		if (chars == null || chars.length == 0 || length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(new String(chars, offset, length), TypeRefs.getType(List.class, type));
	}


	static <T> List<T> parseArray(byte[] bytes, Class<T> clazz) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(new String(bytes), TypeRefs.getType(List.class, clazz));
	}

	static <T> List<T> parseArray(byte[] bytes, Type type) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(new String(bytes), TypeRefs.getType(List.class, type));
	}

	static <T> List<T> parseArray(byte[] bytes, int offset, int length, Class<T> clazz) {
		if (bytes == null || bytes.length == 0 || length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(new String(bytes, offset, length), TypeRefs.getType(List.class, clazz));
	}

	static <T> List<T> parseArray(byte[] bytes, int offset, int length, Type type) {
		if (bytes == null || bytes.length == 0 || length == 0) {
			return null;
		}
		return Jacksons.toJavaObject(new String(bytes, offset, length), TypeRefs.getType(List.class, type));
	}


	static <T> List<T> parseArray(Reader input, Type type) {
		if (input == null) {
			return null;
		}
		try {
			return Jacksons.toJavaObject(IO.toString(input), TypeRefs.getType(List.class, type));
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}

	static <T> List<T> parseArray(InputStream input, Type type) {
		if (input == null) {
			return null;
		}
		try {
			return Jacksons.toJavaObject(IO.toString(input), TypeRefs.getType(List.class, type));
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}

	static <T> List<T> parseArray(InputStream input, Charset charset, Type type) {
		if (input == null) {
			return null;
		}
		try {
			return Jacksons.toJavaObject(IO.toString(input, charset), TypeRefs.getType(List.class, type));
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}


	static <T> List<T> parseArray(URL url, Type type) {
		if (url == null) {
			return null;
		}
		try (InputStream is = url.openStream()) {
			return Jacksons.toJavaObject(IO.toString(is), TypeRefs.getType(List.class, type));
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}

	static String toJSONString(Object object) {
		return Jacksons.toJsonString(object);
	}

	static byte[] toJSONBytes(Object object) {
		return Jacksons.toJsonBytes(object);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	static Object toJSON(Object object) {
		if (object == null) {
			return null;
		}
		if (object instanceof JSONObject || object instanceof JSONArray) {
			return object;
		}
		if (object instanceof Map) {
			return JSONObject.of((Map) object);
		}
		if (object instanceof Collection) {
			return JSONArray.of((Collection) object);
		}
		return parse(Jacksons.toJsonString(object));
	}

	static <T> T to(Class<T> clazz, Object object) {
		if (object == null) {
			return null;
		}

		if (object instanceof JSONObject) {
			return ((JSONObject) object).to(clazz);
		}

		return Converters.convert(clazz, object);
	}

	/**
	 * @deprecated please use {@link #to(Class, Object)}
	 */
	static <T> T toJavaObject(Object object, Class<T> clazz) {
		return to(clazz, object);
	}
}
