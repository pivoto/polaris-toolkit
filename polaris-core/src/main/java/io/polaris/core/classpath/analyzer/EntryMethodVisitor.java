package io.polaris.core.classpath.analyzer;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.annotation.concurrent.NotThreadSafe;

import io.polaris.core.lang.Objs;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.string.Strings;
import lombok.Getter;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;
import org.objectweb.asm.commons.Method;


/**
 * @author Qt
 * @since Aug 11, 2025
 */
@NotThreadSafe
public class EntryMethodVisitor extends MethodVisitor {
	private static final Logger log = Loggers.of(EntryMethodVisitor.class);
	@Getter
	private final EntryClassVisitor classVisitor;
	@Getter
	private final MethodInfo methodInfo;
	@Getter
	private final Method method;
	@Getter
	private final MethodCodeAnalyzer analyzer;


	public EntryMethodVisitor(EntryClassVisitor classVisitor, MethodInfo methodInfo) {
		this(Opcodes.ASM9, classVisitor, methodInfo);
	}

	public EntryMethodVisitor(int api, EntryClassVisitor classVisitor, MethodInfo methodInfo) {
		super(api);
		this.classVisitor = classVisitor;
		this.methodInfo = methodInfo;
		this.method = new Method(methodInfo.getName(), methodInfo.getDescriptor());
		this.analyzer = new MethodCodeAnalyzer(api, classVisitor.getClassName(), methodInfo.getAccess(), methodInfo.getName(), methodInfo.getDescriptor(), methodInfo.getExceptions());
		initListener();
	}

	private void initListener() {
		this.analyzer.onMethodCall((analyzer, methodCall) -> {
			EntryContext ctx = classVisitor.getCtx();
			Set<String> entryKeys = ctx.doAnalyzeMethodCall(this, analyzer, methodCall);
			if (entryKeys != null && !entryKeys.isEmpty()) {
				this.methodInfo.addEntryKeys(entryKeys);
			} else {
				// 未抓取到特定类信息，继续向下扫描
				ClassInfo classInfo = classVisitor.getClassInfo();
				String owner = methodCall.getClassName();
				String name = methodCall.getMethodName();
				String descriptor = methodCall.getMethodDesc();

				// 对本类、父类、父接口或匹配包名的类的方法，继续扫描
				boolean matched =
					Strings.equalsAny(owner, classInfo.getName())
						|| Strings.equalsAny(owner, classInfo.getSuperName())
						|| Strings.equalsAny(owner, classInfo.getInterfaces())
						// 向下钻取，需要判断包名是否满足条件
						|| ctx.filterPackage(owner);
				if (matched) {
					try {
						List<MethodInfo> methodInfoList = ctx.doAnalyze(this.classVisitor.getEntryClass(), owner, name, descriptor);
						if (methodInfoList != null) {
							for (MethodInfo methodInfo : methodInfoList) {
								this.methodInfo.addTraces(methodInfo);
							}
						}
					} catch (IOException e) {
						log.error("", e);
					}
				}
			}

		});
	}

	@Override
	public void visitParameter(String name, int access) {
		log.debug("visitParameter name:{} access:{}", name, access);
		this.methodInfo.addParameterName(name);
		super.visitParameter(name, access);
	}


	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		log.debug("visitAnnotationDefault");
		return super.visitAnnotationDefault();
	}

	@Override
	public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
		log.debug("visitAnnotation descriptor:{}, visible:{}", descriptor, visible);

		AnnotationInfo methodAnnotationInfo = new AnnotationInfo(descriptor, visible);
		this.methodInfo.addMethodAnnotation(methodAnnotationInfo);

		BiConsumer<String, AnnotationInfo.Value> consumer = (name, value) -> {
			methodAnnotationInfo.getValues().put(name, value);
		};
		// 解析方法注解
		return new EntryAnnotationVisitor(super.api, consumer);
	}

	@Override
	public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
		log.debug("visitTypeAnnotation typeRef:{}, typePath:{}, descriptor:{}, visible:{}", typeRef, typePath, descriptor, visible);

		TypeReference typeReference = new TypeReference(typeRef);
		if (typeReference.getSort() == TypeReference.METHOD_RETURN) {
			AnnotationInfo annotationInfo = new AnnotationInfo(descriptor, visible);
			methodInfo.addReturnAnnotation(annotationInfo);
			BiConsumer<String, AnnotationInfo.Value> consumer = (name, value) -> {
				annotationInfo.getValues().put(name, value);
			};
			// 解析返回值注解
			return new EntryAnnotationVisitor(super.api, consumer);
		} else if (typeReference.getSort() == TypeReference.METHOD_FORMAL_PARAMETER) {
			int formalParameterIndex = typeReference.getFormalParameterIndex();
			AnnotationInfo annotationInfo = new AnnotationInfo(descriptor, visible);
			this.methodInfo.addFormalParameterAnnotation(formalParameterIndex, annotationInfo);
			BiConsumer<String, AnnotationInfo.Value> consumer = (name, value) -> {
				annotationInfo.getValues().put(name, value);
			};
			// 解析参数注解
			return new EntryAnnotationVisitor(super.api, consumer);
		}
		// 忽略
		return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
	}

	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter,
		String descriptor, boolean visible) {
		log.debug("visitParameterAnnotation parameter:{}, descriptor:{}, visible:{}", parameter, descriptor, visible);
		AnnotationInfo annotationInfo = new AnnotationInfo(descriptor, visible);
		this.methodInfo.addParameterAnnotation(parameter, annotationInfo);
		BiConsumer<String, AnnotationInfo.Value> consumer = (name, value) -> {
			annotationInfo.getValues().put(name, value);
		};
		// 解析参数注解
		return new EntryAnnotationVisitor(super.api, consumer);
	}

	@Override
	public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
		log.debug("visitAnnotableParameterCount parameterCount:{}, visible:{}", parameterCount, visible);
		super.visitAnnotableParameterCount(parameterCount, visible);
	}

	@Override
	public void visitAttribute(Attribute attr) {
		log.debug("visitAttribute attr:{}", attr);
		super.visitAttribute(attr);
	}

	@Override
	public void visitCode() {
		log.debug("visitCode");
		super.visitCode();
	}


	@Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
		if (log.isDebugEnabled()) {
			log.debug("visitFrame type:{}, nLocal:{}, local:{}, nStack:{}, stack:{}", type, nLocal, Arrays.toString(local), nStack, Arrays.toString(stack));
		}
		this.analyzer.visitFrame(type, nLocal, local, nStack, stack);
		super.visitFrame(type, nLocal, local, nStack, stack);
	}

	@Override
	public void visitInsn(int opcode) {
		log.debug("visitInsn opcode:{}", EntryAnalyzer.asOpcodeString(opcode));
		this.analyzer.visitInsn(opcode);
		super.visitInsn(opcode);
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		log.debug("visitIntInsn opcode:{}, operand:{}", EntryAnalyzer.asOpcodeString(opcode), operand);
		this.analyzer.visitIntInsn(opcode, operand);
		super.visitIntInsn(opcode, operand);
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		log.debug("visitVarInsn opcode:{}, var:{}", EntryAnalyzer.asOpcodeString(opcode), var);
		this.analyzer.visitVarInsn(opcode, var);
		super.visitVarInsn(opcode, var);
	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
		log.debug("visitTypeInsn opcode:{}, type:{}", EntryAnalyzer.asOpcodeString(opcode), type);
		this.analyzer.visitTypeInsn(opcode, type);

		// 忽略构造器调用，只从实例方法切入
		/* EntryContext ctx = classVisitor.getCtx();
		if (ctx.filterPackage(type)) {
		} */
		super.visitTypeInsn(opcode, type);
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
		log.debug("visitFieldInsn opcode:{}, owner:{}, name:{}, descriptor:{}", EntryAnalyzer.asOpcodeString(opcode), owner, name, descriptor);
		this.analyzer.visitFieldInsn(opcode, owner, name, descriptor);
		super.visitFieldInsn(opcode, owner, name, descriptor);
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name,
		String descriptor, boolean isInterface) {
		log.debug("visitMethodInsn opcode:{}, owner:{}, name:{}, descriptor:{}, isInterface:{}", EntryAnalyzer.asOpcodeString(opcode), owner, name, descriptor, isInterface);
		this.analyzer.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
		super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
	}

	@Override
	public void visitInvokeDynamicInsn(String name, String descriptor, Handle bsm, Object... bsmArgs) {
		if (log.isDebugEnabled()) {
			log.debug("visitInvokeDynamicInsn name:{}, descriptor:{}, bsm:(owner:{},name:{},descriptor:{},tag:{},itf:{}), bsmArgs:{}", name, descriptor,
				bsm.getOwner(),
				bsm.getName(),
				bsm.getDesc(),
				bsm.getTag(),
				bsm.isInterface(),
				Objs.toString(bsmArgs));
		}
		this.analyzer.visitInvokeDynamicInsn(name, descriptor, bsm, bsmArgs);

		if (Strings.equals("java/lang/invoke/LambdaMetafactory", bsm.getOwner())
			&& Strings.equals("metafactory", bsm.getName())
			&& Strings.equals("(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", bsm.getDesc())
		) {
			// lambda
			Handle h = null;
			for (Object o : bsmArgs) {
				if (o instanceof Handle) {
					h = (Handle) o;
					break;
				}
			}
			if (h != null) {
				EntryContext ctx = classVisitor.getCtx();
				String methodOwner = h.getOwner();
				// 向下钻取，需要判断包名是否满足条件
				if (ctx.filterPackage(methodOwner)) {
					String methodName = h.getName();
					String methodDesc = h.getDesc();
					try {
						List<MethodInfo> methodInfoList = ctx.doAnalyze(this.classVisitor.getEntryClass(), methodOwner, methodName, methodDesc);
						if (methodInfoList != null) {
							// 当前方法信息中 添加引用的lambda方法信息
							for (MethodInfo methodInfo : methodInfoList) {
								this.methodInfo.addTraces(methodInfo);
							}
						}
					} catch (IOException e) {
						log.error("", e);
					}
				}
			}
		}
		super.visitInvokeDynamicInsn(name, descriptor, bsm, bsmArgs);
	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		log.debug("visitJumpInsn opcode:{}, label:{}", EntryAnalyzer.asOpcodeString(opcode), label);
		this.analyzer.visitJumpInsn(opcode, label);
		super.visitJumpInsn(opcode, label);

	}

	@Override
	public void visitLabel(Label label) {
		log.debug("visitLabel label:{}", label);
		this.analyzer.visitLabel(label);
		super.visitLabel(label);
	}

	@Override
	public void visitLdcInsn(Object value) {
		log.debug("visitLdcInsn value:{}", value);
		this.analyzer.visitLdcInsn(value);
		super.visitLdcInsn(value);
	}


	@Override
	public void visitIincInsn(int varIndex, int increment) {
		log.debug("visitIincInsn varIndex:{}, increment:{}", varIndex, increment);
		this.analyzer.visitIincInsn(varIndex, increment);
		super.visitIincInsn(varIndex, increment);
	}

	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
		log.debug("visitTableSwitchInsn min:{}, max:{}, dflt:{}, labels:{}", min, max, dflt, labels);
		this.analyzer.visitTableSwitchInsn(min, max, dflt, labels);
		super.visitTableSwitchInsn(min, max, dflt, labels);
	}

	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		log.debug("visitLookupSwitchInsn dflt:{}, keys:{}, labels:{}", dflt, keys, labels);
		this.analyzer.visitLookupSwitchInsn(dflt, keys, labels);
		super.visitLookupSwitchInsn(dflt, keys, labels);
	}

	@Override
	public void visitMultiANewArrayInsn(String descriptor, int dims) {
		log.debug("visitMultiANewArrayInsn descriptor:{}, dims:{}", descriptor, dims);
		this.analyzer.visitMultiANewArrayInsn(descriptor, dims);
		super.visitMultiANewArrayInsn(descriptor, dims);
	}


	@Override
	public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
		log.debug("visitInsnAnnotation typeRef:{}, typePath:{}, descriptor:{}, visible:{}", typeRef, typePath, descriptor, visible);
		return super.visitInsnAnnotation(typeRef, typePath, descriptor, visible);
	}

	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler,
		String type) {
		log.debug("visitTryCatchBlock start:{}, end:{}, handler:{}, type:{}", start, end, handler, type);
		this.analyzer.visitTryCatchBlock(start, end, handler, type);
		super.visitTryCatchBlock(start, end, handler, type);
	}

	@Override
	public AnnotationVisitor visitTryCatchAnnotation(int typeRef,
		TypePath typePath, String descriptor, boolean visible) {
		log.debug("visitTryCatchAnnotation typeRef:{}, typePath:{}, descriptor:{}, visible:{}", typeRef, typePath, descriptor, visible);
		// 忽略异常的注解
		return super.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible);
	}

	@Override
	public void visitLocalVariable(String name, String descriptor, String signature,
		Label start, Label end, int index) {
		log.debug("visitLocalVariable name:{}, descriptor:{}, signature:{}, start:{}, end:{}, index:{}", name, descriptor, signature, start, end, index);
		this.analyzer.visitLocalVariable(name, descriptor, signature, start, end, index);
		super.visitLocalVariable(name, descriptor, signature, start, end, index);
	}

	@Override
	public AnnotationVisitor visitLocalVariableAnnotation(int typeRef,
		TypePath typePath, Label[] start, Label[] end, int[] index,
		String descriptor, boolean visible) {
		log.debug("visitLocalVariableAnnotation typeRef:{}, typePath:{}, start:{}, end:{}, index:{}, descriptor:{}, visible:{}", typeRef, typePath, start, end, index, descriptor, visible);
		// 忽略局部变量的注解
		return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index,
			descriptor, visible);
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		log.debug("visitLineNumber line:{}, start:{}", line, start);
		this.analyzer.visitLineNumber(line, start);
		super.visitLineNumber(line, start);
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		log.debug("visitMaxs maxStack:{}, maxLocals:{}", maxStack, maxLocals);
		this.analyzer.visitMaxs(maxStack, maxLocals);
		// 忽略Max信息
		super.visitMaxs(maxStack, maxLocals);
	}

	@Override
	public void visitEnd() {
		if (log.isDebugEnabled()) {
			log.debug("visitEnd");
			log.debug("methodInfo: {}", methodInfo.dump(analyzer));
		}
		methodInfo.calcAndSetSha1(analyzer);
		super.visitEnd();
	}
}
