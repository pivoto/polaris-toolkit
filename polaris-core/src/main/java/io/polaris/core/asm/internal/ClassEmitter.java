package io.polaris.core.asm.internal;

import java.util.HashMap;
import java.util.Map;

import io.polaris.core.asm.transform.ClassTransformer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since May 10, 2024
 */
public class ClassEmitter extends ClassTransformer {
	private ClassInfo classInfo;
	private Map fieldInfo;

	private static int hookCounter;
	private MethodVisitor rawStaticInit;
	private CodeEmitter staticInit;
	private CodeEmitter staticHook;
	private Signature staticHookSig;

	public ClassEmitter(ClassVisitor cv) {
		setTarget(cv);
	}

	public ClassEmitter() {
		super(AsmConsts.ASM_API);
	}

	public void setTarget(ClassVisitor cv) {
		this.cv = cv;
		fieldInfo = new HashMap();

		// just to be safe
		staticInit = staticHook = null;
		staticHookSig = null;
	}

	synchronized private static int getNextHook() {
		return ++hookCounter;
	}

	public ClassInfo getClassInfo() {
		return classInfo;
	}

	public void begin_class(int version, final int access, String className, final Type superType, final Type[] interfaces, String source) {
		final Type classType = Type.getType("L" + className.replace('.', '/') + ";");
		classInfo = new ClassInfo() {
			public Type getType() {
				return classType;
			}
			public Type getSuperType() {
				return (superType != null) ? superType : AsmConsts.TYPE_OBJECT;
			}
			public Type[] getInterfaces() {
				return interfaces;
			}
			public int getModifiers() {
				return access;
			}
		};
		cv.visit(version,
			access,
			classInfo.getType().getInternalName(),
			null,
			classInfo.getSuperType().getInternalName(),
			AsmTypes.toInternalNames(interfaces));
		if (source != null)
			cv.visitSource(source, null);
		init();
	}

	public CodeEmitter getStaticHook() {
		if (AsmTypes.isInterface(getAccess())) {
			throw new IllegalStateException("static hook is invalid for this class");
		}
		if (staticHook == null) {
			staticHookSig = new Signature("$ASM$STATIC_HOOK" + getNextHook(), "()V");
			staticHook = begin_method(AsmConsts.ACC_STATIC,
				staticHookSig,
				null);
			if (staticInit != null) {
				staticInit.invoke_static_this(staticHookSig);
			}
		}
		return staticHook;
	}

	protected void init() {
	}

	public int getAccess() {
		return classInfo.getModifiers();
	}

	public Type getClassType() {
		return classInfo.getType();
	}

	public Type getSuperType() {
		return classInfo.getSuperType();
	}

	public void end_class() {
		if (staticHook != null && staticInit == null) {
			// force creation of static init
			begin_static();
		}
		if (staticInit != null) {
			staticHook.return_value();
			staticHook.end_method();
			rawStaticInit.visitInsn(AsmConsts.RETURN);
			rawStaticInit.visitMaxs(0, 0);
			staticInit = staticHook = null;
			staticHookSig = null;
		}
		cv.visitEnd();
	}

	public CodeEmitter begin_method(int access, Signature sig, Type[] exceptions) {
		if (classInfo == null)
			throw new IllegalStateException("classInfo is null! " + this);
		MethodVisitor v = cv.visitMethod(access,
			sig.getName(),
			sig.getDescriptor(),
			null,
			AsmTypes.toInternalNames(exceptions));
		if (sig.equals(AsmConsts.SIG_STATIC) && !AsmTypes.isInterface(getAccess())) {
			rawStaticInit = v;
			MethodVisitor wrapped = new MethodVisitor(AsmConsts.ASM_API, v) {
				public void visitMaxs(int maxStack, int maxLocals) {
					// ignore
				}
				public void visitInsn(int insn) {
					if (insn != AsmConsts.RETURN) {
						super.visitInsn(insn);
					}
				}
			};
			staticInit = new CodeEmitter(this, wrapped, access, sig, exceptions);
			if (staticHook == null) {
				// force static hook creation
				getStaticHook();
			} else {
				staticInit.invoke_static_this(staticHookSig);
			}
			return staticInit;
		} else if (sig.equals(staticHookSig)) {
			return new CodeEmitter(this, v, access, sig, exceptions) {
				public boolean isStaticHook() {
					return true;
				}
			};
		} else {
			return new CodeEmitter(this, v, access, sig, exceptions);
		}
	}

	public CodeEmitter begin_static() {
		return begin_method(AsmConsts.ACC_STATIC, AsmConsts.SIG_STATIC, null);
	}

	public void declare_field(int access, String name, Type type, Object value) {
		FieldInfo existing = (FieldInfo)fieldInfo.get(name);
		FieldInfo info = new FieldInfo(access, name, type, value);
		if (existing != null) {
			if (!info.equals(existing)) {
				throw new IllegalArgumentException("Field \"" + name + "\" has been declared differently");
			}
		} else {
			fieldInfo.put(name, info);
			cv.visitField(access, name, type.getDescriptor(), null, value);
		}
	}

	boolean isFieldDeclared(String name) {
		return fieldInfo.get(name) != null;
	}

	FieldInfo getFieldInfo(String name) {
		FieldInfo field = (FieldInfo)fieldInfo.get(name);
		if (field == null) {
			throw new IllegalArgumentException("Field " + name + " is not declared in " + getClassType().getClassName());
		}
		return field;
	}

	static class FieldInfo {
		int access;
		String name;
		Type type;
		Object value;

		public FieldInfo(int access, String name, Type type, Object value) {
			this.access = access;
			this.name = name;
			this.type = type;
			this.value = value;
		}

		public boolean equals(Object o) {
			if (o == null)
				return false;
			if (!(o instanceof FieldInfo))
				return false;
			FieldInfo other = (FieldInfo)o;
			if (access != other.access ||
				!name.equals(other.name) ||
				!type.equals(other.type)) {
				return false;
			}
			if ((value == null) ^ (other.value == null))
				return false;
			if (value != null && !value.equals(other.value))
				return false;
			return true;
		}

		public int hashCode() {
			return access ^ name.hashCode() ^ type.hashCode() ^ ((value == null) ? 0 : value.hashCode());
		}
	}

	public void visit(int version,
		int access,
		String name,
		String signature,
		String superName,
		String[] interfaces) {
		begin_class(version,
			access,
			name.replace('/', '.'),
			AsmTypes.fromInternalName(superName),
			AsmTypes.fromInternalNames(interfaces),
			null); // TODO
	}

	public void visitEnd() {
		end_class();
	}

	public FieldVisitor visitField(int access,
		String name,
		String desc,
		String signature,
		Object value) {
		declare_field(access, name, Type.getType(desc), value);
		return null; // TODO
	}

	public MethodVisitor visitMethod(int access,
		String name,
		String desc,
		String signature,
		String[] exceptions) {
		return begin_method(access,
			new Signature(name, desc),
			AsmTypes.fromInternalNames(exceptions));
	}
}
