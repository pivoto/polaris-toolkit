package io.polaris.core.asm.transform;

import io.polaris.core.asm.generator.ClassGenerator;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

/**
 * @author Qt
 * @since May 10, 2024
 */
public class ClassReaderGenerator implements ClassGenerator {
	private final ClassReader r;
	private final Attribute[] attrs;
	private final int flags;

	public ClassReaderGenerator(ClassReader r, int flags) {
		this(r, null, flags);
	}

	public ClassReaderGenerator(ClassReader r, Attribute[] attrs, int flags) {
		this.r = r;
		this.attrs = (attrs != null) ? attrs : new Attribute[0];
		this.flags = flags;
	}

	@Override
	public void generateClass(ClassVisitor v) {
		r.accept(v, attrs, flags);
	}
}
