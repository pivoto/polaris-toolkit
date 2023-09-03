package io.polaris.core.asm;

import io.polaris.core.compiler.MemoryClassLoader;
import io.polaris.core.lang.TypeRef;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Qt
 * @since 1.8,  Aug 04, 2023
 */
public class AsmTest {

	@Test
	void test01() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String className = "javax.lang.AsmTestObject";
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		classWriter.visit(V1_8, ACC_PUBLIC + ACC_SUPER, className.replace('.','/'),
			"L" + TypeRef.class.getName().replace('.','/') + "<Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;>;", TypeRef.class.getName().replace('.','/'),
			null);
		{
			MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, TypeRef.class.getName().replace('.','/'), "<init>", "()V", false);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}
		classWriter.visitEnd();
		byte[] byteArray = classWriter.toByteArray();
		MemoryClassLoader loader = MemoryClassLoader.getInstance();
		loader.add(className, byteArray);
		Class<?> c = loader.loadClass(className);
		System.out.println(c);
		TypeRef o = (TypeRef) c.newInstance();
		System.out.println(o);
		System.out.println(o.getType());
	}


}
