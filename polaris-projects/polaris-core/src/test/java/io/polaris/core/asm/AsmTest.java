package io.polaris.core.asm;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import io.polaris.core.compiler.MemoryClassLoader;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.lang.TypeRefs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.testng.asserts.Assertion;

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

		Assertions.assertEquals(className, c.getName());
		Object o = c.newInstance();
		Assertions.assertInstanceOf(TypeRef.class, o);
		Assertions.assertInstanceOf(ParameterizedType.class, ((TypeRef) o).getType());
		Assertions.assertEquals(Map.class, ((ParameterizedType)((TypeRef) o).getType()).getRawType());
		Assertions.assertEquals(TypeRefs.getType("java.util.Map<java.lang.String, java.lang.String>"), ((TypeRef) o).getType());
	}


}
