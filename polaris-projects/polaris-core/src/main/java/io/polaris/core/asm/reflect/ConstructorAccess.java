package io.polaris.core.asm.reflect;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Qt
 * @see <a href="https://github.com/EsotericSoftware/reflectasm">https://github.com/EsotericSoftware/reflectasm</a>
 * @since 1.8,  Aug 04, 2023
 */
public abstract class ConstructorAccess<T> {

	private static final AccessPool<Class, ConstructorAccess> pool = new AccessPool<>();
	/** 是否非静态成员内部类 */
	private boolean isNonStaticMemberClass;
	/**
	 * 是否非静态成员内部类
	 */
	public boolean isNonStaticMemberClass() {
		return isNonStaticMemberClass;
	}

	/**
	 * 通过默认构造器实例化顶级类或静态内部类
	 */
	public abstract T newInstance();

	/**
	 * 通过默认构造器实例化非静态成员内部类
	 */
	public abstract T newInstance(Object enclosingInstance);

	@SuppressWarnings("unchecked")
	public static <T> ConstructorAccess<T> get(Class<T> type) {
		return pool.computeIfAbsent(type, ConstructorAccess::create);
	}
	public static <T> ConstructorAccess<T> create(Class<T> type) {
		Class enclosingType = type.getEnclosingClass();

		boolean isNonStaticMemberClass = enclosingType != null && type.isMemberClass() && !Modifier.isStatic(type.getModifiers());

		String accessClassName = AccessClassLoader.buildAccessClassName(type, ConstructorAccess.class);

		Class accessClass;
		AccessClassLoader loader = AccessClassLoader.get(type);
		synchronized (loader) {
			accessClass = loader.loadAccessClass(accessClassName);
			if (accessClass == null) {
				String accessClassNameInternal = accessClassName.replace('.', '/');
				String classNameInternal = type.getName().replace('.', '/');
				String enclosingClassNameInternal;
				Constructor<T> constructor = null;
				int modifiers = 0;
				if (!isNonStaticMemberClass) {
					enclosingClassNameInternal = null;
					try {
						constructor = type.getDeclaredConstructor((Class[]) null);
						modifiers = constructor.getModifiers();
					} catch (Exception ex) {
						throw new RuntimeException("缺少默认无参构造器方法: " + type.getName(), ex);
					}
					if (Modifier.isPrivate(modifiers)) {
						throw new RuntimeException("默认无参构造器方法不能声明为private: " + type.getName());
					}
				} else {
					enclosingClassNameInternal = enclosingType.getName().replace('.', '/');
					try {
						// 内部类的默认构造器
						constructor = type.getDeclaredConstructor(enclosingType);
						modifiers = constructor.getModifiers();
					} catch (Exception ex) {
						throw new RuntimeException("非静态内部类缺少默认构造器方法: " + type.getName(), ex);
					}
					if (Modifier.isPrivate(modifiers)) {
						throw new RuntimeException("非静态内部类的默认构造器方法不能声明为private: " + type.getName());
					}
				}
				String superclassNameInternal = Modifier.isPublic(modifiers)
					? PublicConstructorAccess.class.getName().replace('.', '/')
					: ConstructorAccess.class.getName().replace('.', '/');

				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
				cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal, null, superclassNameInternal, null);

				insertConstructor(cw, superclassNameInternal);
				insertNewInstance(cw, classNameInternal);
				insertNewInstanceInner(cw, classNameInternal, enclosingClassNameInternal);

				cw.visitEnd();
				accessClass = loader.defineAccessClass(accessClassName, cw.toByteArray());
			}
		}
		ConstructorAccess<T> access;
		try {
			access = (ConstructorAccess<T>) accessClass.newInstance();
		} catch (Throwable t) {
			throw new RuntimeException("实例化构造器访问类失败: " + accessClassName, t);
		}
		if (!(access instanceof PublicConstructorAccess) && !AccessClassLoader.areInSameRuntimeClassLoader(type, accessClass)) {
			// 判断构造器访问权限，非公有构造方法必须由相同类加载器下的类调用，否则会抛出IllegalAccessError
			throw new RuntimeException((!isNonStaticMemberClass
				? "默认无参构造器未声明为public，且无法定义同类加载器下的访问类: "
				: "非静态成员内部类的构造器未声明为public，且无法定义同类加载器下的访问类: ")
				+ type.getName());
		}
		access.isNonStaticMemberClass = isNonStaticMemberClass;
		return access;
	}

	static private void insertConstructor(ClassWriter cw, String superclassNameInternal) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, superclassNameInternal, "<init>", "()V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	static void insertNewInstance(ClassWriter cw, String classNameInternal) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "()Ljava/lang/Object;", null, null);
		mv.visitCode();
		mv.visitTypeInsn(NEW, classNameInternal);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, classNameInternal, "<init>", "()V");
		mv.visitInsn(ARETURN);
		mv.visitMaxs(2, 1);
		mv.visitEnd();
	}

	static void insertNewInstanceInner(ClassWriter cw, String classNameInternal, String enclosingClassNameInternal) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
		mv.visitCode();
		if (enclosingClassNameInternal != null) {
			mv.visitTypeInsn(NEW, classNameInternal);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, enclosingClassNameInternal);
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
			mv.visitInsn(POP);
			mv.visitMethodInsn(INVOKESPECIAL, classNameInternal, "<init>", "(L" + enclosingClassNameInternal + ";)V");
			mv.visitInsn(ARETURN);
			mv.visitMaxs(4, 2);
		} else {
			mv.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
			mv.visitInsn(DUP);
			mv.visitLdcInsn("非内部类不支持此构造方法.");
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>", "(Ljava/lang/String;)V");
			mv.visitInsn(ATHROW);
			mv.visitMaxs(3, 2);
		}
		mv.visitEnd();
	}

}
