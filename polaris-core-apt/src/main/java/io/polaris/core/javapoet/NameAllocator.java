package io.polaris.core.javapoet;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.lang.model.SourceVersion;

import static io.polaris.core.javapoet.Util.checkNotNull;

/**
 * 分配Java标识符名称以避免冲突、关键字和无效字符。使用方法是，
 * 首先创建一个实例并分配所有需要的名称。通常这是用户提供的名称和常量的混合：<pre>   {@code
 *
 *   NameAllocator nameAllocator = new NameAllocator();
 *   for (MyProperty property : properties) {
 *     nameAllocator.newName(property.name(), property);
 *   }
 *   nameAllocator.newName("sb", "string builder");
 * }</pre>
 * <p>
 * 为每次分配传递一个唯一的标记对象。标记作用域名称，并且可以在稍后通过传递{@code tag}到
 * {@link #get(Object)}来查询已分配的名称。通常标记是要命名的对象。在上面的例子中，
 * 我们使用{@code property}作为用户提供的属性名称，使用{@code "string builder"}作为我们的常量字符串构建器。
 *
 * <p>一旦我们分配了名称，就可以在生成代码时使用它们：<pre>   {@code
 *
 *   MethodSpec.Builder builder = MethodSpec.methodBuilder("toString")
 *       .addAnnotation(Override.class)
 *       .addModifiers(Modifier.PUBLIC)
 *       .returns(String.class);
 *
 *   builder.addStatement("$1T $2N = new $1T()",
 *       StringBuilder.class, nameAllocator.get("string builder"));
 *   for (MyProperty property : properties) {
 *     builder.addStatement("$N.append($N)",
 *         nameAllocator.get("string builder"), nameAllocator.get(property));
 *   }
 *   builder.addStatement("return $N", nameAllocator.get("string builder"));
 *   return builder.build();
 * }</pre>
 * <p>
 * 如果遇到冲突，上述代码会生成唯一名称。给定用户提供的属性名称{@code ab}和{@code sb}，
 * 这将生成以下内容：<pre>   {@code
 *
 *   &#64;Override
 *   public String toString() {
 *     StringBuilder sb_ = new StringBuilder();
 *     sb_.append(ab);
 *     sb_.append(sb);
 *     return sb_.toString();
 *   }
 * }</pre>
 * <p>
 * 下划线被附加到{@code sb}以避免与用户提供的{@code sb}属性冲突。
 * 对于以数字开头的名称也会添加前缀下划线，并用于替换不安全的字符如空格或破折号。
 *
 * <p>在处理多个独立的内部作用域时，使用外部作用域的NameAllocator的{@link #clone()}来进一步细化特定内部作用域的名称分配。
 */
public final class NameAllocator implements Cloneable {
	private final Set<String> allocatedNames;
	private final Map<Object, String> tagToName;

	public NameAllocator() {
		this(new LinkedHashSet<>(), new LinkedHashMap<>());
	}

	private NameAllocator(LinkedHashSet<String> allocatedNames,
		LinkedHashMap<Object, String> tagToName) {
		this.allocatedNames = allocatedNames;
		this.tagToName = tagToName;
	}

	/**
	 * 返回一个新的名称，使用{@code suggestion}，该名称不是Java标识符或与其他名称冲突。
	 */
	public String newName(String suggestion) {
		return newName(suggestion, UUID.randomUUID().toString());
	}

	/**
	 * 返回一个新的名称，使用{@code suggestion}，该名称不会是Java标识符或与其他名称冲突。
	 * 通过传递{@code tag}到{@link #get(Object)}可以多次查询返回的值。
	 */
	public String newName(String suggestion, Object tag) {
		checkNotNull(suggestion, "suggestion");
		checkNotNull(tag, "tag");

		suggestion = toJavaIdentifier(suggestion);

		while (SourceVersion.isKeyword(suggestion) || !allocatedNames.add(suggestion)) {
			suggestion = suggestion + "_";
		}

		String replaced = tagToName.put(tag, suggestion);
		if (replaced != null) {
			tagToName.put(tag, replaced); // Put things back as they were!
			throw new IllegalArgumentException("tag " + tag + " cannot be used for both '" + replaced
				+ "' and '" + suggestion + "'");
		}

		return suggestion;
	}

	public static String toJavaIdentifier(String suggestion) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < suggestion.length(); ) {
			int codePoint = suggestion.codePointAt(i);
			if (i == 0
				&& !Character.isJavaIdentifierStart(codePoint)
				&& Character.isJavaIdentifierPart(codePoint)) {
				result.append("_");
			}

			int validCodePoint = Character.isJavaIdentifierPart(codePoint) ? codePoint : '_';
			result.appendCodePoint(validCodePoint);
			i += Character.charCount(codePoint);
		}
		return result.toString();
	}

	/** 检索使用{@link #newName(String, Object)}创建的名称。 */
	public String get(Object tag) {
		String result = tagToName.get(tag);
		if (result == null) {
			throw new IllegalArgumentException("unknown tag: " + tag);
		}
		return result;
	}

	/**
	 * 创建此NameAllocator的深拷贝。对于创建多个独立的细化非常有用，
	 * 这些细化将用于各自定义多个独立作用域的内部代码块。
	 *
	 * @return 此NameAllocator的深拷贝。
	 */
	@Override
	public NameAllocator clone() {
		return new NameAllocator(
			new LinkedHashSet<>(this.allocatedNames),
			new LinkedHashMap<>(this.tagToName));
	}

}
