package io.polaris.core.javapoet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor8;

import static io.polaris.core.javapoet.Util.checkArgument;
import static io.polaris.core.javapoet.Util.checkNotNull;

/** A fully-qualified class name for top-level and member classes. */
public final class ClassName extends TypeName implements Comparable<ClassName> {
	public static final ClassName OBJECT = ClassName.get(Object.class);

	/** 表示默认Java包的名称。 */
	private static final String NO_PACKAGE = "";

	/** 此类的包名，如果在默认包中则为""。 */
	final String packageName;

	/** 封装类，如果此类未封装在其他类中则为null。 */
	final ClassName enclosingClassName;

	/** 此类的简单名称，例如java.util.Map.Entry的"Entry"。 */
	final String simpleName;

	private List<String> simpleNames;

	/** 完整的类名，例如"java.util.Map.Entry"。 */
	final String canonicalName;

	private ClassName(String packageName, ClassName enclosingClassName, String simpleName) {
		this(packageName, enclosingClassName, simpleName, Collections.emptyList());
	}

	private ClassName(String packageName, ClassName enclosingClassName, String simpleName,
		List<AnnotationSpec> annotations) {
		super(annotations);
		this.packageName = Objects.requireNonNull(packageName, "packageName == null");
		this.enclosingClassName = enclosingClassName;
		this.simpleName = simpleName;
		this.canonicalName = enclosingClassName != null
			? (enclosingClassName.canonicalName + '.' + simpleName)
			: (packageName.isEmpty() ? simpleName : packageName + '.' + simpleName);
	}

	@Override
	public ClassName annotated(List<AnnotationSpec> annotations) {
		return new ClassName(packageName, enclosingClassName, simpleName,
			concatAnnotations(annotations));
	}

	@Override
	public ClassName withoutAnnotations() {
		if (!isAnnotated()) return this;
		ClassName resultEnclosingClassName = enclosingClassName != null
			? enclosingClassName.withoutAnnotations()
			: null;
		return new ClassName(packageName, resultEnclosingClassName, simpleName);
	}

	@Override
	public boolean isAnnotated() {
		return super.isAnnotated() || (enclosingClassName != null && enclosingClassName.isAnnotated());
	}

	/**
	 * 返回包名，例如{@code Map.Entry}的{@code "java.util"}。对于默认包返回空字符串。
	 */
	public String packageName() {
		return packageName;
	}

	/**
	 * 返回封装类，例如{@code Map.Entry}的{@link Map}。如果此类未嵌套在其他类中则返回null。
	 */
	public ClassName enclosingClassName() {
		return enclosingClassName;
	}

	/**
	 * 返回此嵌套组中的顶级类。等效于链接调用{@link #enclosingClassName()}直到结果的封装类为null。
	 */
	public ClassName topLevelClassName() {
		return enclosingClassName != null ? enclosingClassName.topLevelClassName() : this;
	}

	/** 返回类的二进制名称。 */
	public String reflectionName() {
		return enclosingClassName != null
			? (enclosingClassName.reflectionName() + '$' + simpleName)
			: (packageName.isEmpty() ? simpleName : packageName + '.' + simpleName);
	}

	public List<String> simpleNames() {
		if (simpleNames != null) {
			return simpleNames;
		}

		if (enclosingClassName == null) {
			simpleNames = Collections.singletonList(simpleName);
		} else {
			List<String> mutableNames = new ArrayList<>();
			mutableNames.addAll(enclosingClassName().simpleNames());
			mutableNames.add(simpleName);
			simpleNames = Collections.unmodifiableList(mutableNames);
		}
		return simpleNames;
	}

	/**
	 * 返回与此类共享相同封装包或类的类。如果此类由另一个类封装，则等效于{@code enclosingClassName().nestedClass(name)}。
	 * 否则等效于{@code get(packageName(), name)}。
	 */
	public ClassName peerClass(String name) {
		return new ClassName(packageName, enclosingClassName, name);
	}

	/**
	 * 返回指定{@code name}作为此类内部嵌套的新{@link ClassName}实例。
	 */
	public ClassName nestedClass(String name) {
		return new ClassName(packageName, this, name);
	}

	/** 返回此类的简单名称，例如{@link java.util.Map.Entry}的{@code "Entry"}。 */
	public String simpleName() {
		return simpleName;
	}

	/**
	 * 返回此类的完整类名。
	 * 例如{@link java.util.Map.Entry}的{@code "java.util.Map.Entry"}。
	 */
	public String canonicalName() {
		return canonicalName;
	}

	public static ClassName get(Class<?> clazz) {
		checkNotNull(clazz, "clazz == null");
		checkArgument(!clazz.isPrimitive(), "primitive types cannot be represented as a ClassName");
		checkArgument(!void.class.equals(clazz), "'void' type cannot be represented as a ClassName");
		checkArgument(!clazz.isArray(), "array types cannot be represented as a ClassName");

		String anonymousSuffix = "";
		while (clazz.isAnonymousClass()) {
			int lastDollar = clazz.getName().lastIndexOf('$');
			anonymousSuffix = clazz.getName().substring(lastDollar) + anonymousSuffix;
			clazz = clazz.getEnclosingClass();
		}
		String name = clazz.getSimpleName() + anonymousSuffix;

		if (clazz.getEnclosingClass() == null) {
			// Avoid unreliable Class.getPackage(). https://github.com/square/javapoet/issues/295
			int lastDot = clazz.getName().lastIndexOf('.');
			String packageName = (lastDot != -1) ? clazz.getName().substring(0, lastDot) : NO_PACKAGE;
			return new ClassName(packageName, null, name);
		}

		return ClassName.get(clazz.getEnclosingClass()).nestedClass(name);
	}

	/**
	 * 根据给定的完全限定类名字符串返回新的{@link ClassName}实例。此方法假定输入是ASCII并遵循典型的Java样式
	 * （小写包名，大驼峰类名），否则可能会产生不正确的结果或抛出{@link IllegalArgumentException}。
	 * 因此，应优先使用{@link #get(Class)}和{@link #get(Class)}，因为它们可以在没有此类限制的情况下正确创建{@link ClassName}实例。
	 */
	public static ClassName bestGuess(String classNameString) {
		// Add the package name, like "java.util.concurrent", or "" for no package.
		int p = 0;
		while (p < classNameString.length() && Character.isLowerCase(classNameString.codePointAt(p))) {
			p = classNameString.indexOf('.', p) + 1;
			checkArgument(p != 0, "couldn't make a guess for %s", classNameString);
		}
		String packageName = p == 0 ? NO_PACKAGE : classNameString.substring(0, p - 1);

		// Add class names like "Map" and "Entry".
		ClassName className = null;
		for (String simpleName : classNameString.substring(p).split("\\.", -1)) {
			checkArgument(!simpleName.isEmpty() && Character.isUpperCase(simpleName.codePointAt(0)),
				"couldn't make a guess for %s", classNameString);
			className = new ClassName(packageName, className, simpleName);
		}

		return className;
	}

	/**
	 * 根据给定的部分返回类名。例如，使用包名{@code "java.util"}和简单名称{@code "Map"}、{@code "Entry"}调用此方法会产生{@link java.util.Map.Entry}。
	 */
	public static ClassName get(String packageName, String simpleName, String... simpleNames) {
		ClassName className = new ClassName(packageName, null, simpleName);
		for (String name : simpleNames) {
			className = className.nestedClass(name);
		}
		return className;
	}

	/** 返回{@code element}的类名。 */
	public static ClassName get(TypeElement element) {
		checkNotNull(element, "element == null");
		String simpleName = element.getSimpleName().toString();

		return element.getEnclosingElement().accept(new SimpleElementVisitor8<ClassName, Void>() {
			@Override
			public ClassName visitPackage(PackageElement packageElement, Void p) {
				return new ClassName(packageElement.getQualifiedName().toString(), null, simpleName);
			}

			@Override
			public ClassName visitType(TypeElement enclosingClass, Void p) {
				return ClassName.get(enclosingClass).nestedClass(simpleName);
			}

			@Override
			public ClassName visitUnknown(Element unknown, Void p) {
				return get("", simpleName);
			}

			@Override
			public ClassName defaultAction(Element enclosingElement, Void p) {
				throw new IllegalArgumentException("Unexpected type nesting: " + element);
			}
		}, null);
	}

	@Override
	public int compareTo(ClassName o) {
		return canonicalName.compareTo(o.canonicalName);
	}

	@Override
	CodeWriter emit(CodeWriter out) throws IOException {
		boolean charsEmitted = false;
		for (ClassName className : enclosingClasses()) {
			String simpleName;
			if (charsEmitted) {
				// We've already emitted an enclosing class. Emit as we go.
				out.emit(".");
				simpleName = className.simpleName;

			} else if (className.isAnnotated() || className == this) {
				// We encountered the first enclosing class that must be emitted.
				String qualifiedName = out.lookupName(className);
				int dot = qualifiedName.lastIndexOf('.');
				if (dot != -1) {
					out.emitAndIndent(qualifiedName.substring(0, dot + 1));
					simpleName = qualifiedName.substring(dot + 1);
					charsEmitted = true;
				} else {
					simpleName = qualifiedName;
				}

			} else {
				// Don't emit this enclosing type. Keep going so we can be more precise.
				continue;
			}

			if (className.isAnnotated()) {
				if (charsEmitted) out.emit(" ");
				className.emitAnnotations(out);
			}

			out.emit(simpleName);
			charsEmitted = true;
		}

		return out;
	}

	/** 返回此中的所有封装类，最外层优先。 */
	private List<ClassName> enclosingClasses() {
		List<ClassName> result = new ArrayList<>();
		for (ClassName c = this; c != null; c = c.enclosingClassName) {
			result.add(c);
		}
		Collections.reverse(result);
		return result;
	}
}
