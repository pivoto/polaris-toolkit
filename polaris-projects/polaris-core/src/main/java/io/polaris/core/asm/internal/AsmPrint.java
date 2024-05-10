package io.polaris.core.asm.internal;

import io.polaris.core.io.IO;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * @author Qt
 * @since  Aug 03, 2023
 */
public class AsmPrint {

	public static void print(byte[] classBytes, boolean asmCode) {
		int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
		Printer printer = asmCode ? new ASMifier() : new Textifier();
		PrintWriter printWriter = new PrintWriter(System.out, true);
		TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
		new ClassReader(classBytes).accept(traceClassVisitor, parsingOptions);
	}

	public static void print(byte[] className) {
		print(className, true);
	}

	public static void print(String className, boolean asmCode) throws IOException {
		int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
		Printer printer = asmCode ? new ASMifier() : new Textifier();
		PrintWriter printWriter = new PrintWriter(System.out, true);
		TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
		new ClassReader(className).accept(traceClassVisitor, parsingOptions);
	}

	public static void print(String className) throws IOException {
		print(className, true);
	}

	public static void print(Class<?> clazz, boolean asmCode) throws IOException {
		int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
		Printer printer = asmCode ? new ASMifier() : new Textifier();
		PrintWriter printWriter = new PrintWriter(System.out, true);
		TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
		InputStream in = clazz.getClassLoader().getResourceAsStream(clazz.getName().replace('.', '/') + ".class");
		byte[] bytes = IO.toBytes(in);
		new ClassReader(bytes).accept(traceClassVisitor, parsingOptions);
	}

	public static void print(Class<?> clazz) throws IOException {
		print(clazz, true);
	}
}
