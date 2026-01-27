package io.polaris.core.classpath.analyzer;


import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.annotation.concurrent.NotThreadSafe;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.string.Strings;
import lombok.Getter;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author Qt
 * @since Aug 11, 2025
 */
@NotThreadSafe
public class EntryClassVisitor extends ClassVisitor {
	private static final Logger log = Loggers.of(EntryClassVisitor.class);
	@Getter
	private final EntryContext ctx;
	@Getter
	private final Class<?> entryClass;
	@Getter
	private final String className;
	@Getter
	private final String methodName;
	@Getter
	private final String methodDesc;
	@Getter
	private final List<MethodInfo> methodInfoList = new ArrayList<>();
	@Getter
	private ClassInfo classInfo;


	public EntryClassVisitor(EntryContext ctx, Class<?> entryClass, String className, String methodName, String methodDesc) {
		this(Opcodes.ASM9, ctx, entryClass, className, methodName, methodDesc);
	}

	public EntryClassVisitor(int api, EntryContext ctx, Class<?> entryClass, String className, String methodName, String methodDesc) {
		super(api);
		this.ctx = ctx;
		this.entryClass = entryClass;
		this.className = className;
		this.methodName = methodName;
		this.methodDesc = methodDesc;
	}

	@Override
	public void visit(int version, int access, String name,
		String signature, String superName, String[] interfaces) {
		if (Strings.isBlank(className)
			|| Strings.isBlank(methodName)
			|| Strings.isBlank(methodDesc)) {
			return;
		}

		this.classInfo = ctx.classMap().computeIfAbsent(name, k -> {
			ClassInfo classInfo = new ClassInfo();
			classInfo.setVersion(version);
			classInfo.setAccess(access);
			classInfo.setName(name);
			classInfo.setSignature(signature);
			classInfo.setSuperName(superName);
			classInfo.setInterfaces(interfaces);
			return classInfo;
		});

		log.debug("visit classInfo:{}", classInfo);
	}


	@Override
	public void visitSource(String source, String debug) {
		log.debug("visitSource source:{}, debug:{}", source, debug);
	}

	@Override
	public void visitOuterClass(String owner, String name, String desc) {
		log.debug("visitOuterClass owner:{}, name:{}, desc:{}", owner, name, desc);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		log.debug("visitAnnotation desc:{}, visible:{}", desc, visible);
		AnnotationInfo annotationInfo = new AnnotationInfo(desc, visible);
		classInfo.addAnnotation(annotationInfo);
		BiConsumer<String, AnnotationInfo.Value> consumer = (name, value) -> {
			annotationInfo.getValues().put(name, value);
		};
		return new EntryAnnotationVisitor(super.api, consumer);
	}

	@Override
	public void visitAttribute(Attribute attr) {
		log.debug("visitAttribute attr:{}", attr);
	}

	@Override
	public void visitInnerClass(String name, String outerName,
		String innerName, int access) {
		log.debug("visitInnerClass name:{}, outerName:{}, innerName:{}, access:{}", name, outerName, innerName, access);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc,
		String signature, Object value) {
		log.debug("visitField access:{}, name:{}, desc:{}, signature:{}, value:{}", access, name, desc, signature, value);
		return null;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
		log.debug("visitMethod access:{}, name:{}, descriptor:{}, signature:{}, exceptions:{}", access, name, descriptor, signature, exceptions);
		/* // 不在这里限制包路径 ，是否解析类由调用方决定
		if (!ctx.filterPackage(this.classInfo.getName())) {
			return null;
		} */
		String methodDesc = this.methodDesc;
		String methodName = this.methodName;

		if ("*".equals(methodName) ||
			(
				name.equals(methodName)
					&& (descriptor.equals(methodDesc) || "*".equals(methodDesc))
			)
		) {
			MethodKey methodKey = new MethodKey(this.classInfo.getName(), name, descriptor);
			MethodInfo methodInfo = ctx.methodMap().get(methodKey);
			if (methodInfo != null) {
				methodInfoList.add(methodInfo);
				return null;
			}
			methodInfo = new MethodInfo();
			methodInfo.setClassInfo(classInfo);
			methodInfo.setKey(methodKey);
			methodInfo.setAccess(access);
			methodInfo.setName(name);
			methodInfo.setDescriptor(descriptor);
			methodInfo.setSignature(signature);
			methodInfo.setExceptions(exceptions);
			ctx.methodMap().put(methodKey, methodInfo);

			methodInfoList.add(methodInfo);
			return new EntryMethodVisitor(super.api, this, methodInfo);
		}
		return null;
	}

	@Override
	public void visitEnd() {
		log.debug("visitEnd");
	}

}
