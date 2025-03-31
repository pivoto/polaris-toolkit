package io.polaris.core.lang;

import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.lang.model.element.Modifier;

import io.polaris.core.asm.internal.AsmTypes;
import io.polaris.core.compiler.MemoryClassLoader;
import io.polaris.core.compiler.MemoryCompiler;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import io.polaris.core.javapoet.ArrayTypeName;
import io.polaris.core.javapoet.ClassName;
import io.polaris.core.javapoet.JavaFile;
import io.polaris.core.javapoet.ParameterizedTypeName;
import io.polaris.core.javapoet.TypeName;
import io.polaris.core.javapoet.TypeSpec;
import io.polaris.core.javapoet.WildcardTypeName;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;

/**
 * @author Qt
 * @since 1.8
 */
public class TypeRefs {
	private static final ILogger log = ILoggers.of(TypeRefs.class);
	private static final AtomicLong seq = new AtomicLong(0);
	private static final Map<String, Type> refs = new ConcurrentHashMap<>();

	public static Type getType(String typeStr) throws ClassNotFoundException {
		TypeName typeName = parse(typeStr);
		return getType(typeName);
	}

	public static TypeName parse(String typeStr) throws ClassNotFoundException {
		int i = typeStr.indexOf("<");
		if (i < 0) {
			return toSimpleTypeName(typeStr);
		}

		class Parameterized {
			ClassName raw;
			List<TypeName> args = new ArrayList<>();

			public Parameterized(ClassName raw) {
				this.raw = raw;
			}
		}
		class SubWildcard {
		}
		class SuperWildcard {
		}

		Deque<Object> stack = new ArrayDeque<>();
		StringBuilder sb = new StringBuilder();
		char[] cs = typeStr.trim().toCharArray();
		for (int j = 0; j < cs.length; j++) {
			char c = cs[j];
			if (Character.isWhitespace(c)) {
				continue;
			}
			if (c == '?') {
				int k = j + 1;
				for (; k < cs.length; k++) {
					if (Character.isWhitespace(cs[k])) {
						continue;
					}
					break;
				}
				if (cs.length >= k + 7) {
					if (Arrays.equals("extends".toCharArray(), Arrays.copyOfRange(cs, k, k + 7))) {
						stack.offerLast(new SubWildcard());
						j = k + 6;
					}
				} else if (cs.length >= k + 5) {
					if (Arrays.equals("super".toCharArray(), Arrays.copyOfRange(cs, k, k + 5))) {
						stack.offerLast(new SuperWildcard());
						j = k + 4;
					}
				} else {
					Object last = stack.peekLast();
					if (last instanceof Parameterized) {
						((Parameterized) last).args.add(WildcardTypeName.subtypeOf(TypeName.OBJECT));
					} else {
						throw new IllegalArgumentException("格式有误：" + typeStr);
					}
				}
			} else if (c == '<') {
				stack.offerLast(new Parameterized(ClassName.get(getClassForName(sb.toString()))));
				sb.setLength(0);
			} else if (c == ',') {
				Object last = stack.peekLast();
				TypeName componentTypeName = null;
				if (sb.length() > 0) {
					componentTypeName = (toSimpleTypeName(sb.toString()));
					sb.setLength(0);
				}
				if (last instanceof SubWildcard) {
					componentTypeName = WildcardTypeName.subtypeOf(componentTypeName == null ? TypeName.OBJECT : componentTypeName);
					stack.pollLast();
					last = stack.peekLast();
				} else if (last instanceof SuperWildcard) {
					componentTypeName = WildcardTypeName.supertypeOf(componentTypeName == null ? TypeName.OBJECT : componentTypeName);
					stack.pollLast();
					last = stack.peekLast();
				}

				if (last instanceof Parameterized) {
					Parameterized parameterized = (Parameterized) last;
					if (componentTypeName != null) {
						parameterized.args.add(componentTypeName);
					}
				} else {
					throw new IllegalArgumentException("格式有误：" + typeStr);
				}
			} else if (c == '>') {
				Object last = stack.pollLast();
				TypeName componentTypeName = null;
				if (sb.length() > 0) {
					componentTypeName = (toSimpleTypeName(sb.toString()));
					sb.setLength(0);
				}
				if (last instanceof SubWildcard) {
					componentTypeName = WildcardTypeName.subtypeOf(componentTypeName == null ? TypeName.OBJECT : componentTypeName);
					last = stack.pollLast();
				} else if (last instanceof SuperWildcard) {
					componentTypeName = WildcardTypeName.supertypeOf(componentTypeName == null ? TypeName.OBJECT : componentTypeName);
					last = stack.pollLast();
				}

				if (last instanceof Parameterized) {
					Parameterized parameterized = (Parameterized) last;
					if (componentTypeName != null) {
						parameterized.args.add(componentTypeName);
					}
					TypeName typeName = ParameterizedTypeName.get(
						parameterized.raw, parameterized.args.toArray(new TypeName[0]));
					if (stack.isEmpty()) {
						if (j < cs.length - 1) {
							throw new IllegalArgumentException("格式有误：" + typeStr);
						}
						return typeName;
					}
					Object last1 = stack.peekLast();
					if (last1 instanceof SubWildcard) {
						typeName = WildcardTypeName.subtypeOf(typeName);
						stack.pollLast();
					} else if (last1 instanceof SuperWildcard) {
						typeName = WildcardTypeName.supertypeOf(typeName);
						stack.pollLast();
					}
					Object last2 = stack.peekLast();
					if (last2 instanceof Parameterized) {
						((Parameterized) last2).args.add(typeName);
					} else {
						throw new IllegalArgumentException("格式有误：" + typeStr);
					}
				} else {
					throw new IllegalArgumentException("格式有误：" + typeStr);
				}
			} else {
				sb.append(c);
			}
		}
		throw new IllegalArgumentException("格式有误：" + typeStr);
	}

	private static TypeName toSimpleTypeName(String typeStr) throws ClassNotFoundException {
		char[] cs = typeStr.toCharArray();
		boolean hasSquareBracket = false;
		int bracketCount = 0;

		int i = cs.length - 1;
		for (; i >= 0; i--) {
			char c = cs[i];
			if (Character.isWhitespace(c)) {
				continue;
			}
			if (c == ']') {
				hasSquareBracket = true;
			} else if (c == '[') {
				if (!hasSquareBracket) {
					throw new ClassNotFoundException(typeStr);
				}
				hasSquareBracket = false;
				bracketCount++;
			} else {
				break;
			}
		}
		if (hasSquareBracket) {
			throw new ClassNotFoundException(typeStr);
		}
		TypeName typeName;
		if (bracketCount == 0) {
			typeName = ClassName.get(getClassForName(typeStr));
		} else {
			String componentTypeStr = new String(cs, 0, i + 1);
			typeName = ClassName.get(getClassForName(componentTypeStr));
			for (int j = 0; j < bracketCount; j++) {
				typeName = ArrayTypeName.of(typeName);
			}
		}
		return typeName;
	}

	private static Class<?> getClassForName(String typeStr) throws ClassNotFoundException {
		try {
			return Class.forName(typeStr);
		} catch (ClassNotFoundException e) {
			if (!typeStr.contains(".")) {
				return Class.forName("java.lang." + typeStr);
			}
			throw e;
		}
	}

	public static Type getType(TypeName typeName) {
		return refs.computeIfAbsent(typeName.toString(), s -> createType(typeName));
	}

	public static Type getType(Class<?> rawType, Type... typeArguments) {
		ParameterizedTypeName typeName = ParameterizedTypeName.get(rawType, typeArguments);
		return getType(typeName);
	}

	static Type createType(TypeName typeName) {
		String packageName = TypeRef.class.getPackage().getName();
		String simpleName = TypeRef.class.getSimpleName() + "$$" + seq.incrementAndGet();
		TypeRef<?> ref = null;
		try {
			ref = createTypeRefByAsm(typeName, packageName, simpleName);
		} catch (Exception e) {
			log.error("", e);
			try {
				ref = createTypeRefByJdk(typeName, packageName, simpleName);
			} catch (Exception ex) {
				e.addSuppressed(ex);
				throw e;
			}
		}
		return ref.getType();
	}

	static TypeRef<?> createTypeRefByJdk(TypeName typeName, String packageName, String simpleName) {
		try {
			JavaFile javaFile = JavaFile.builder(packageName,
				TypeSpec.classBuilder(simpleName)
					.addModifiers(Modifier.PUBLIC)
					.superclass(ParameterizedTypeName.get(ClassName.get(TypeRef.class), typeName))
					.build()).build();
			StringWriter sw = new StringWriter();
			javaFile.writeTo(sw);
			MemoryCompiler memoryCompiler = MemoryCompiler.getInstance();
			Class<?> c = memoryCompiler.compile(packageName + "." + simpleName, sw.toString());
			TypeRef<?> ref = (TypeRef<?>) c.newInstance();
			return ref;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	static TypeRef<?> createTypeRefByAsm(TypeName typeName, String packageName, String simpleName) {
		try {
			String typeRefClassName = TypeRef.class.getName().replace('.', '/');
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, packageName.replace('.', '/') + "/" + simpleName,
				"L" + typeRefClassName + "<" + AsmTypes.toTypeSignature(typeName) + ">;", typeRefClassName,
				null);
			{
				MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKESPECIAL, typeRefClassName, "<init>", "()V", false);
				mv.visitInsn(RETURN);
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
			cw.visitEnd();
			byte[] byteArray = cw.toByteArray();
			MemoryClassLoader loader = MemoryClassLoader.getInstance();
			loader.add(packageName + "." + simpleName, byteArray);
			Class<?> c = loader.loadClass(packageName + "." + simpleName);
			return (TypeRef<?>) c.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

}
