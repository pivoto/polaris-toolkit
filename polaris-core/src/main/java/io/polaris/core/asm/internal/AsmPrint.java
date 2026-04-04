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
 * ASM 打印工具类，用于打印字节码的可读形式
 * <p>
 * 支持打印为 ASM 代码或文本格式，便于调试和分析
 *
 * @author Qt
 * @since Aug 03, 2023
 */
public class AsmPrint {

	/**
	 * 打印字节码的可读形式
	 *
	 * @param classBytes 字节码数组
	 * @param asmCode true 表示打印为 ASM 代码格式，false 表示打印为文本格式
	 */
	public static void print(byte[] classBytes, boolean asmCode) {
		// 设置解析选项：跳过帧信息和调试信息
		int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
		// 根据参数选择使用 ASMifier 或 Textifier 打印机
		Printer printer = asmCode ? new ASMifier() : new Textifier();
		PrintWriter printWriter = new PrintWriter(System.out, true);
		// 创建跟踪类访问器，用于打印类结构
		TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
		// 读取并解析字节码
		new ClassReader(classBytes).accept(traceClassVisitor, parsingOptions);
	}

	/**
	 * 打印字节码数组（默认 ASM 代码格式）
	 *
	 * @param className 字节码数组（参数名有误，实际应为 classBytes）
	 */
	public static void print(byte[] className) {
		print(className, true);
	}

	/**
	 * 根据类名打印字节码
	 *
	 * @param className 类的全限定名
	 * @param asmCode true 表示打印为 ASM 代码，false 表示打印为文本格式
	 * @throws IOException 读取类文件时发生 IO 异常
	 */
	public static void print(String className, boolean asmCode) throws IOException {
		// 设置解析选项：跳过帧信息和调试信息
		int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
		// 根据参数选择使用 ASMifier 或 Textifier 打印机
		Printer printer = asmCode ? new ASMifier() : new Textifier();
		PrintWriter printWriter = new PrintWriter(System.out, true);
		// 创建跟踪类访问器，用于打印类结构
		TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
		// 通过类名读取类字节码并打印
		new ClassReader(className).accept(traceClassVisitor, parsingOptions);
	}

	/**
	 * 根据类名打印字节码（默认 ASM 代码格式）
	 *
	 * @param className 类名
	 * @throws IOException 读取类文件时发生 IO 异常
	 */
	public static void print(String className) throws IOException {
		print(className, true);
	}

	/**
	 * 打印 Class 对象的字节码
	 *
	 * @param clazz Class 对象
	 * @param asmCode true 表示打印为 ASM 代码，false 表示打印为文本格式
	 * @throws IOException 读取类文件时发生 IO 异常
	 */
	public static void print(Class<?> clazz, boolean asmCode) throws IOException {
		// 设置解析选项：跳过帧信息和调试信息
		int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
		// 根据参数选择使用 ASMifier 或 Textifier 打印机
		Printer printer = asmCode ? new ASMifier() : new Textifier();
		PrintWriter printWriter = new PrintWriter(System.out, true);
		// 创建跟踪类访问器，用于打印类结构
		TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
		// 从类加载器获取类字节码流
		InputStream in = clazz.getClassLoader().getResourceAsStream(clazz.getName().replace('.', '/') + ".class");
		// 将输入流转换为字节数组
		byte[] bytes = IO.toBytes(in);
		// 读取并解析字节码
		new ClassReader(bytes).accept(traceClassVisitor, parsingOptions);
	}

	/**
	 * 打印 Class 对象的字节码（默认 ASM 代码格式）
	 *
	 * @param clazz Class 对象
	 * @throws IOException 读取类文件时发生 IO 异常
	 */
	public static void print(Class<?> clazz) throws IOException {
		print(clazz, true);
	}
}
