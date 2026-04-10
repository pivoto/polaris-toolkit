package io.polaris.core.asm.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.polaris.core.err.BytecodeOperationException;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

/**
 * ASM 字节码发射器工具类
 * <p>
 * 提供常用的字节码生成辅助方法，包括：
 * <ul>
 *   <li>工厂方法和构造方法生成</li>
 *   <li>数组和字符串的批量处理</li>
 *   <li>字符串 switch 语句的实现（支持 Trie 和 Hash 两种方式）</li>
 *   <li>Class 对象的加载优化</li>
 *   <li>对象常量的压栈操作</li>
 *   <li>哈希码计算方法生成</li>
 *   <li>方法和构造方法的动态分发</li>
 *   <li>异常包装和处理</li>
 *   <li>属性访问器（getter/setter）生成</li>
 * </ul>
 * </p>
 *
 * @author Qt
 * @since May 09, 2024
 */
public class Emitters {
	/** 空参数构造方法签名：<init>()V */
	private static final Signature CSTRUCT_NULL =
		AsmTypes.parseConstructor("");
	/** Throwable 构造方法签名：<init>(Ljava/lang/Throwable;)V */
	private static final Signature CSTRUCT_THROWABLE =
		AsmTypes.parseConstructor("Throwable");

	/** Class.getName() 方法签名: getName()Ljava/lang/String; */
	private static final Signature GET_NAME =
		AsmTypes.parseSignature("String getName()");
	/** Object.hashCode() 方法签名 hashCode()I */
	private static final Signature HASH_CODE =
		AsmTypes.parseSignature("int hashCode()");
	/** Object.equals(Object) 方法签名 equals(Ljava/lang/Object;)Z */
	private static final Signature EQUALS =
		AsmTypes.parseSignature("boolean equals(Object)");
	/** String.length() 方法签名 length()I */
	private static final Signature STRING_LENGTH =
		AsmTypes.parseSignature("int length()");
	/** String.charAt(int) 方法签名 charAt(I)C */
	private static final Signature STRING_CHAR_AT =
		AsmTypes.parseSignature("char charAt(int)");
	/** Class.forName(String) 方法签名 */
	private static final Signature FOR_NAME =
		AsmTypes.parseSignature("Class forName(String)");
	/** Double.doubleToLongBits(double) 方法签名 */
	private static final Signature DOUBLE_TO_LONG_BITS =
		AsmTypes.parseSignature("long doubleToLongBits(double)");
	/** Float.floatToIntBits(float) 方法签名 */
	private static final Signature FLOAT_TO_INT_BITS =
		AsmTypes.parseSignature("int floatToIntBits(float)");
	/** Object.toString() 方法签名 */
	private static final Signature TO_STRING =
		AsmTypes.parseSignature("String toString()");
	/** StringBuffer.append(String) 方法签名 */
	private static final Signature APPEND_STRING =
		AsmTypes.parseSignature("StringBuffer append(String)");
	/** StringBuffer.append(int) 方法签名 */
	private static final Signature APPEND_INT =
		AsmTypes.parseSignature("StringBuffer append(int)");
	/** StringBuffer.append(double) 方法签名 */
	private static final Signature APPEND_DOUBLE =
		AsmTypes.parseSignature("StringBuffer append(double)");
	/** StringBuffer.append(float) 方法签名 */
	private static final Signature APPEND_FLOAT =
		AsmTypes.parseSignature("StringBuffer append(float)");
	/** StringBuffer.append(char) 方法签名 */
	private static final Signature APPEND_CHAR =
		AsmTypes.parseSignature("StringBuffer append(char)");
	/** StringBuffer.append(long) 方法签名 */
	private static final Signature APPEND_LONG =
		AsmTypes.parseSignature("StringBuffer append(long)");
	/** StringBuffer.append(boolean) 方法签名 */
	private static final Signature APPEND_BOOLEAN =
		AsmTypes.parseSignature("StringBuffer append(boolean)");
	/** StringBuffer.length() 方法签名 */
	private static final Signature LENGTH =
		AsmTypes.parseSignature("int length()");
	/** StringBuffer.setLength(int) 方法签名 */
	private static final Signature SET_LENGTH =
		AsmTypes.parseSignature("void setLength(int)");
	/** Class.getDeclaredMethod(String, Class[]) 方法签名 */
	private static final Signature GET_DECLARED_METHOD =
		AsmTypes.parseSignature("java.lang.reflect.Method getDeclaredMethod(String, Class[])");


	/** 默认的数组分隔符：使用前缀 "{"、分隔符 ", "、后缀 "}" */
	public static final ArrayDelimiters DEFAULT_DELIMITERS = new ArrayDelimiters("{", ", ", "}");

	/** 私有构造函数，防止实例化 */
	private Emitters() {
	}

	/**
	 * 生成工厂方法
	 * <p>
	 * 生成的代码等价于：
	 * <pre>{@code
	 * public static ReturnType factoryMethod(args) {
	 *     return new ClassName(args);
	 * }
	 * }</pre>
	 * </p>
	 *
	 * @param ce 类发射器
	 * @param sig 工厂方法的签名（包含返回类型和参数类型）
	 */
	public static void factory_method(ClassEmitter ce, Signature sig) {
		CodeEmitter e = ce.begin_method(AsmConsts.ACC_PUBLIC | AsmConsts.ACC_STATIC, sig, null);
		// 创建当前类的新实例
		e.new_instance_this();
		// 复制引用，一份用于构造函数调用，一份用于返回
		e.dup();
		// 加载方法参数
		e.load_args();
		// 调用构造函数
		e.invoke_constructor_this(AsmTypes.parseConstructor(sig.getArgumentTypes()));
		// 返回新创建的实例
		e.return_value();
		e.end_method();
	}

	/**
	 * 生成空的公共构造方法
	 * <p>
	 * 生成的代码等价于：
	 * <pre>{@code
	 * public ClassName() {
	 *     super();
	 * }
	 * }</pre>
	 * </p>
	 *
	 * @param ce 类发射器
	 */
	public static void null_constructor(ClassEmitter ce) {
		CodeEmitter e = ce.begin_method(AsmConsts.ACC_PUBLIC, CSTRUCT_NULL, null);
		// 加载 this 引用
		e.load_this();
		// 调用父类的无参构造函数
		e.super_invoke_constructor();
		// 返回
		e.return_value();
		e.end_method();
	}

	/**
	 * 处理栈顶的数组对象
	 * <p>
	 * 假设栈顶是指定类型的数组。对于数组中的每个元素，
	 * 将元素压入栈并触发回调函数。
	 * 生成的代码结构：
	 * <pre>{@code
	 * for (int i = 0; i < array.length; i++) {
	 *     ElementType element = array[i];
	 *     // callback.processElement(elementType)
	 * }
	 * }</pre>
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param type 数组类型（type.isArray() 必须为 true）
	 * @param callback 每个元素触发的回调函数
	 */
	public static void process_array(CodeEmitter e, Type type, ProcessArrayCallback callback) {
		// 获取数组的组件类型
		Type componentType = AsmTypes.getComponentType(type);
		// 创建局部变量存储数组引用和循环计数器
		Local array = e.make_local();
		Local loopvar = e.make_local(Type.INT_TYPE);
		// 创建循环体和条件检查的标签
		Label loopbody = e.make_label();
		Label checkloop = e.make_label();
		// 保存数组到局部变量，初始化循环计数器为 0
		e.store_local(array);
		e.push(0);
		e.store_local(loopvar);
		// 跳转到条件检查处
		e.goTo(checkloop);

		// 标记循环体开始
		e.mark(loopbody);
		// 加载数组和索引，获取数组元素
		e.load_local(array);
		e.load_local(loopvar);
		e.array_load(componentType);
		// 触发回调处理元素
		callback.processElement(componentType);
		// 循环计数器自增
		e.iinc(loopvar, 1);

		// 标记条件检查处
		e.mark(checkloop);
		// 比较循环计数器和数组长度，如果小于则继续循环
		e.load_local(loopvar);
		e.load_local(array);
		e.arraylength();
		e.if_icmp_lt(loopbody);
	}

	/**
	 * 并行处理栈顶的两个数组对象
	 * <p>
	 * 假设栈顶两个元素是指定类型的数组，且两个数组长度相同。
	 * 对于每对元素，将它们压入栈并触发回调函数。
	 * 生成的代码结构：
	 * <pre>{@code
	 * for (int i = 0; i < array1.length; i++) {
	 *     ElementType elem1 = array1[i];
	 *     ElementType elem2 = array2[i];
	 *     // callback.processElement(elementType) - 处理两个元素
	 * }
	 * }</pre>
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param type 数组类型（type.isArray() 必须为 true）
	 * @param callback 每对元素触发的回调函数
	 */
	public static void process_arrays(CodeEmitter e, Type type, ProcessArrayCallback callback) {
		// 获取数组的组件类型
		Type componentType = AsmTypes.getComponentType(type);
		// 创建局部变量存储两个数组引用和循环计数器
		Local array1 = e.make_local();
		Local array2 = e.make_local();
		Local loopvar = e.make_local(Type.INT_TYPE);
		// 创建循环体和条件检查的标签
		Label loopbody = e.make_label();
		Label checkloop = e.make_label();
		// 保存两个数组到局部变量，初始化循环计数器为 0
		e.store_local(array1);
		e.store_local(array2);
		e.push(0);
		e.store_local(loopvar);
		// 跳转到条件检查处
		e.goTo(checkloop);

		// 标记循环体开始
		e.mark(loopbody);
		// 从第一个数组加载元素
		e.load_local(array1);
		e.load_local(loopvar);
		e.array_load(componentType);
		// 从第二个数组加载元素
		e.load_local(array2);
		e.load_local(loopvar);
		e.array_load(componentType);
		// 触发回调处理两个元素
		callback.processElement(componentType);
		// 循环计数器自增
		e.iinc(loopvar, 1);

		// 标记条件检查处
		e.mark(checkloop);
		// 比较循环计数器和数组长度，如果小于则继续循环
		e.load_local(loopvar);
		e.load_local(array1);
		e.arraylength();
		e.if_icmp_lt(loopbody);
	}

	/**
	 * 生成字符串 switch 语句
	 * <p>
	 * 根据指定的 switch 风格生成不同的实现：
	 * <ul>
	 *   <li>SWITCH_STYLE_TRIE - 使用前缀树（Trie）结构，适合字符串较短且前缀区分度高的场景</li>
	 *   <li>SWITCH_STYLE_HASH - 使用哈希码 + equals 比较，通用场景</li>
	 *   <li>SWITCH_STYLE_HASHONLY - 仅使用哈希码，不执行 equals 比较（性能更高但可能有冲突）</li>
	 * </ul>
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param strings 待匹配的字符串数组
	 * @param switchStyle switch 实现风格
	 * @param callback 每个匹配 case 触发的回调函数
	 * @throws IllegalArgumentException 当 switchStyle 无效时抛出
	 */
	public static void string_switch(CodeEmitter e, String[] strings, int switchStyle, ObjectSwitchCallback callback) {
		try {
			// 根据 switch 风格选择不同的实现策略
			switch (switchStyle) {
				case AsmConsts.SWITCH_STYLE_TRIE:
					string_switch_trie(e, strings, callback);
					break;
				case AsmConsts.SWITCH_STYLE_HASH:
					string_switch_hash(e, strings, callback, false);
					break;
				case AsmConsts.SWITCH_STYLE_HASHONLY:
					string_switch_hash(e, strings, callback, true);
					break;
				default:
					throw new IllegalArgumentException("unknown switch style " + switchStyle);
			}
		} catch (RuntimeException ex) {
			throw ex;
		} catch (Error ex) {
			throw ex;
		} catch (Exception ex) {
			throw new BytecodeOperationException(ex);
		}
	}

	/**
	 * 使用前缀树（Trie）结构实现字符串 switch
	 * <p>
	 * 算法思路：
	 * 1. 按字符串长度分组
	 * 2. 对每个长度组，逐个字符进行比较
	 * 3. 形成树状结构，每个节点根据当前字符值进行分支
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param strings 待匹配的字符串数组
	 * @param callback 每个匹配 case 触发的回调函数
	 * @throws Exception 处理异常
	 */
	private static void string_switch_trie(final CodeEmitter e,
		String[] strings,
		final ObjectSwitchCallback callback) throws Exception {
		// 创建默认分支和结束标签
		final Label def = e.make_label();
		final Label end = e.make_label();
		// 按字符串长度分组，相同长度的字符串放在同一个桶中
		final Map buckets = AsmCollections.bucket(Arrays.asList(strings), new Transformer() {
			public Object transform(Object value) {
				return ((String) value).length();
			}
		});
		// 复制字符串引用，调用 length() 方法获取长度
		e.dup();
		e.invoke_virtual(AsmConsts.TYPE_STRING, STRING_LENGTH);
		// 根据长度进行 switch 分发
		e.process_switch(getSwitchKeys(buckets), new ProcessSwitchCallback() {
			public void processCase(int key, Label ignore_end) throws Exception {
				// 获取相同长度的字符串列表
				List bucket = (List) buckets.get(Integer.valueOf(key));
				// 递归处理每个字符的比较
				stringSwitchHelper(e, bucket, callback, def, end, 0);
			}

			public void processDefault() {
				// 长度不匹配，跳转到默认分支
				e.goTo(def);
			}
		});
		// 标记默认分支，弹出字符串引用，执行默认逻辑
		e.mark(def);
		e.pop();
		callback.processDefault();
		// 标记结束位置
		e.mark(end);
	}

	/**
	 * 递归辅助方法：逐字符比较字符串
	 * <p>
	 * 对于给定位置的字符，按字符值分组并进行 switch 分发。
	 * 如果到达字符串末尾，则执行匹配的回调；否则继续递归比较下一个字符。
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param strings 待比较的字符串列表
	 * @param callback 匹配时的回调函数
	 * @param def 默认分支标签
	 * @param end 结束标签
	 * @param index 当前比较的字符索引
	 * @throws Exception 处理异常
	 */
	private static void stringSwitchHelper(final CodeEmitter e,
		List strings,
		final ObjectSwitchCallback callback,
		final Label def,
		final Label end,
		final int index) throws Exception {
		// 获取字符串长度（所有字符串长度相同）
		final int len = ((String) strings.get(0)).length();
		// 按当前位置的字符值分组
		final Map buckets = AsmCollections.bucket(strings, new Transformer() {
			public Object transform(Object value) {
				return (int) ((String) value).charAt(index);
			}
		});
		// 复制字符串引用，调用 charAt(index) 获取当前字符
		e.dup();
		e.push(index);
		e.invoke_virtual(AsmConsts.TYPE_STRING, STRING_CHAR_AT);
		// 根据字符值进行 switch 分发
		e.process_switch(getSwitchKeys(buckets), new ProcessSwitchCallback() {
			public void processCase(int key, Label ignore_end) throws Exception {
				List bucket = (List) buckets.get(Integer.valueOf(key));
				// 如果已到达字符串末尾，执行匹配的回调
				if (index + 1 == len) {
					e.pop();
					callback.processCase(bucket.get(0), end);
				} else {
					// 否则递归比较下一个字符
					stringSwitchHelper(e, bucket, callback, def, end, index + 1);
				}
			}

			public void processDefault() {
				// 字符不匹配，跳转到默认分支
				e.goTo(def);
			}
		});
	}

	/**
	 * 从分组的 Map 中提取并排序 switch 的键值
	 *
	 * @param buckets 分组的 Map，键为 Integer 类型
	 * @return 排序后的 int 数组
	 */
	static int[] getSwitchKeys(Map buckets) {
		// 提取所有键值
		int[] keys = new int[buckets.size()];
		int index = 0;
		for (Iterator it = buckets.keySet().iterator(); it.hasNext(); ) {
			keys[index++] = ((Integer) it.next()).intValue();
		}
		// 排序以确保 switch 的正确性
		Arrays.sort(keys);
		return keys;
	}

	/**
	 * 使用哈希码实现字符串 switch
	 * <p>
	 * 算法思路：
	 * 1. 计算每个字符串的 hashCode
	 * 2. 根据 hashCode 进行 switch 分发
	 * 3. 对于哈希冲突的情况，使用 equals 方法进一步比较
	 * 4. 如果 skipEquals 为 true 且只有一个候选，则跳过 equals 比较以提升性能
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param strings 待匹配的字符串数组
	 * @param callback 每个匹配 case 触发的回调函数
	 * @param skipEquals 是否跳过 equals 比较
	 * @throws Exception 处理异常
	 */
	private static void string_switch_hash(final CodeEmitter e,
		final String[] strings,
		final ObjectSwitchCallback callback,
		final boolean skipEquals) throws Exception {
		// 按哈希码分组
		final Map buckets = AsmCollections.bucket(Arrays.asList(strings), new Transformer() {
			public Object transform(Object value) {
				return value.hashCode();
			}
		});
		// 创建默认分支和结束标签
		final Label def = e.make_label();
		final Label end = e.make_label();
		// 复制字符串引用，调用 hashCode() 方法
		e.dup();
		e.invoke_virtual(AsmConsts.TYPE_OBJECT, HASH_CODE);
		// 根据哈希码进行 switch 分发
		e.process_switch(getSwitchKeys(buckets), new ProcessSwitchCallback() {
			public void processCase(int key, Label ignore_end) throws Exception {
				// 获取具有相同哈希码的字符串列表
				List bucket = (List) buckets.get(Integer.valueOf(key));
				Label next = null;
				// 如果跳过 equals 且只有一个候选，直接匹配
				if (skipEquals && bucket.size() == 1) {
					if (skipEquals)
						e.pop();
					callback.processCase((String) bucket.get(0), end);
				} else {
					// 遍历所有候选字符串，使用 equals 进行比较
					for (Iterator it = bucket.iterator(); it.hasNext(); ) {
						String string = (String) it.next();
						// 标记下一个比较的位置
						if (next != null) {
							e.mark(next);
						}
						// 如果不是最后一个候选，需要复制字符串引用供后续比较
						if (it.hasNext()) {
							e.dup();
						}
						// 加载待比较的字符串常量
						e.push(string);
						// 调用 equals 方法
						e.invoke_virtual(AsmConsts.TYPE_OBJECT, EQUALS);
						// 如果还有后续候选，不相等时跳转到下一个比较
						if (it.hasNext()) {
							e.if_jump(e.EQ, next = e.make_label());
							e.pop();
						} else {
							// 最后一个候选，不相等时跳转到默认分支
							e.if_jump(e.EQ, def);
						}
						// 执行匹配的回调
						callback.processCase(string, end);
					}
				}
			}

			public void processDefault() {
				// 哈希码不匹配，弹出字符串引用
				e.pop();
			}
		});
		// 标记默认分支，执行默认逻辑
		e.mark(def);
		callback.processDefault();
		// 标记结束位置
		e.mark(end);
	}

	/**
	 * 加载当前类的 Class 对象到栈顶
	 * <p>
	 * 使用优化的方式：在静态初始化块中缓存 Class 对象到静态字段，
	 * 运行时直接读取该字段，避免每次调用 Class.forName。
	 * </p>
	 *
	 * @param e 代码发射器
	 */
	public static void load_class_this(CodeEmitter e) {
		load_class_helper(e, e.getClassEmitter().getClassType());
	}

	/**
	 * 加载指定类型的 Class 对象到栈顶
	 * <p>
	 * 对于基本类型，通过包装类的 TYPE 字段获取；
	 * 对于引用类型，使用优化后的加载方式（缓存到静态字段）。
	 * 注意：不支持 void 类型。
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param type 要加载的类型
	 * @throws IllegalArgumentException 如果尝试加载 void 类型
	 */
	public static void load_class(CodeEmitter e, Type type) {
		// 处理基本类型：通过包装类的 TYPE 字段获取
		if (AsmTypes.isPrimitive(type)) {
			if (type == Type.VOID_TYPE) {
				throw new IllegalArgumentException("cannot load void type");
			}
			e.getstatic(AsmTypes.getBoxedType(type), "TYPE", AsmConsts.TYPE_CLASS);
		} else {
			// 引用类型使用优化后的加载方式
			load_class_helper(e, type);
		}
	}

	/**
	 * 辅助方法：加载引用类型的 Class 对象
	 * <p>
	 * 如果在静态钩子中执行，使用 Class.forName 动态加载；
	 * 否则，将 Class 对象缓存到静态字段中，后续直接读取该字段。
	 * 这样可以避免重复的 Class.forName 调用，提升性能。
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param type 要加载的类型
	 */
	private static void load_class_helper(CodeEmitter e, final Type type) {
		if (e.isStaticHook()) {
			// 在静态钩子中，只能使用 Class.forName 动态加载
			e.push(AsmTypes.emulateClassGetName(type));
			e.invoke_static(AsmConsts.TYPE_CLASS, FOR_NAME);
		} else {
			ClassEmitter ce = e.getClassEmitter();
			String typeName = AsmTypes.emulateClassGetName(type);

			// TODO: can end up with duplicated field names when using chained transformers; incorporate static hook # somehow
			// 生成唯一的字段名，用于缓存 Class 对象
			String fieldName = "$ASM$load_class$" + AsmTypes.escapeType(typeName);
			// 如果字段尚未声明，则创建并初始化
			if (!ce.isFieldDeclared(fieldName)) {
				// 声明私有静态 final 字段
				ce.declare_field(AsmConsts.PRIVATE_FINAL_STATIC, fieldName, AsmConsts.TYPE_CLASS, null);
				// 在静态初始化块中加载 Class 对象并赋值给字段
				CodeEmitter hook = ce.getStaticHook();
				hook.push(typeName);
				hook.invoke_static(AsmConsts.TYPE_CLASS, FOR_NAME);
				hook.putstatic(ce.getClassType(), fieldName, AsmConsts.TYPE_CLASS);
			}
			// 从静态字段中读取 Class 对象
			e.getfield(fieldName);
		}
	}

	/**
	 * 将对象数组压入栈顶
	 * <p>
	 * 生成的代码等价于：
	 * <pre>{@code
	 * new ElementType[] { elem1, elem2, ... }
	 * }</pre>
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param array 对象数组
	 */
	public static void push_array(CodeEmitter e, Object[] array) {
		// 创建指定长度的数组
		e.push(array.length);
		e.newarray(Type.getType(remapComponentType(array.getClass().getComponentType())));
		// 遍历数组元素，逐个赋值
		for (int i = 0; i < array.length; i++) {
			// 复制数组引用
			e.dup();
			// 加载索引
			e.push(i);
			// 加载元素对象
			push_object(e, array[i]);
			// 存储到数组
			e.aastore();
		}
	}

	/**
	 * 重新映射组件类型
	 * <p>
	 * 特殊处理：将 Type.class 映射为 Class.class，
	 * 因为 ASM 中 Type 数组在字节码层面实际是 Class 数组。
	 * </p>
	 *
	 * @param componentType 原始组件类型
	 * @return 重新映射后的类型
	 */
	private static Class remapComponentType(Class componentType) {
		// Type 数组在字节码中实际是 Class 数组
		if (componentType.equals(Type.class))
			return Class.class;
		return componentType;
	}

	/**
	 * 将任意对象压入栈顶
	 * <p>
	 * 支持以下类型的对象：
	 * <ul>
	 *   <li>null - 使用 aconst_null</li>
	 *   <li>数组 - 递归调用 push_array</li>
	 *   <li>String - 直接使用 ldc 指令</li>
	 *   <li>Type - 调用 load_class 加载 Class 对象</li>
	 *   <li>Class - 转换为 Type 后加载</li>
	 *   <li>BigInteger/BigDecimal - 通过构造函数创建</li>
	 * </ul>
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param obj 要压栈的对象
	 * @throws IllegalArgumentException 如果遇到不支持的类型
	 */
	public static void push_object(CodeEmitter e, Object obj) {
		if (obj == null) {
			// null 值直接使用 aconst_null 指令
			e.aconst_null();
		} else {
			Class type = obj.getClass();
			if (type.isArray()) {
				// 数组类型递归处理
				push_array(e, (Object[]) obj);
			} else if (obj instanceof String) {
				// 字符串直接使用 ldc 指令
				e.push((String) obj);
			} else if (obj instanceof Type) {
				// Type 对象加载其对应的 Class
				load_class(e, (Type) obj);
			} else if (obj instanceof Class) {
				// Class 对象转换为 Type 后加载
				load_class(e, Type.getType((Class) obj));
			} else if (obj instanceof BigInteger) {
				// BigInteger 通过字符串构造函数创建
				e.new_instance(AsmConsts.TYPE_BIG_INTEGER);
				e.dup();
				e.push(obj.toString());
				e.invoke_constructor(AsmConsts.TYPE_BIG_INTEGER);
			} else if (obj instanceof BigDecimal) {
				// BigDecimal 通过字符串构造函数创建
				e.new_instance(AsmConsts.TYPE_BIG_DECIMAL);
				e.dup();
				e.push(obj.toString());
				e.invoke_constructor(AsmConsts.TYPE_BIG_DECIMAL);
			} else {
				throw new IllegalArgumentException("unknown type: " + obj.getClass());
			}
		}
	}


	/**
	 * 计算基本类型的哈希码
	 * <p>
	 * 根据不同类型采用不同的哈希算法：
	 * <ul>
	 *   <li>boolean: f ? 0 : 1</li>
	 *   <li>float: Float.floatToIntBits(f)</li>
	 *   <li>double: 先转换为 long，再按 long 处理</li>
	 *   <li>long: (int)(f ^ (f >>> 32))</li>
	 * </ul>
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param type 基本类型
	 */
	private static void hash_primitive(CodeEmitter e, Type type) {
		switch (type.getSort()) {
			case Type.BOOLEAN:
				// boolean: f ? 0 : 1
				e.push(1);
				e.math(e.XOR, Type.INT_TYPE);
				break;
			case Type.FLOAT:
				// float: Float.floatToIntBits(f)
				e.invoke_static(AsmConsts.TYPE_FLOAT, FLOAT_TO_INT_BITS);
				break;
			case Type.DOUBLE:
				// double: Double.doubleToLongBits(f)，然后按 long 处理
				e.invoke_static(AsmConsts.TYPE_DOUBLE, DOUBLE_TO_LONG_BITS);
				// 继续执行 long 的哈希逻辑
			case Type.LONG:
				hash_long(e);
		}
	}

	/**
	 * 计算 long 类型的哈希码
	 * <p>
	 * 算法：(int)(f ^ (f >>> 32))
	 * 将 64 位的 long 值通过异或和无符号右移压缩为 32 位 int。
	 * </p>
	 *
	 * @param e 代码发射器
	 */
	private static void hash_long(CodeEmitter e) {
		// (int)(f ^ (f >>> 32))
		e.dup2();
		// 无符号右移 32 位
		e.push(32);
		e.math(e.USHR, Type.LONG_TYPE);
		// 与原值进行异或
		e.math(e.XOR, Type.LONG_TYPE);
		// 转换为 int 类型
		e.cast_numeric(Type.LONG_TYPE, Type.INT_TYPE);
	}


	/**
	 * 比较栈顶的两个对象引用是否为 null
	 * <p>
	 * 如果两个对象都非 null，不执行任何操作；
	 * 如果一个为 null 或两个都为 null，弹出两个对象并跳转到相应标签。
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param oneNull 只有一个对象为 null 时跳转的标签
	 * @param bothNull 两个对象都为 null 时跳转的标签
	 */
	private static void nullcmp(CodeEmitter e, Label oneNull, Label bothNull) {
		// 复制两个对象引用
		e.dup2();
		// 创建辅助标签
		Label nonNull = e.make_label();
		Label oneNullHelper = e.make_label();
		Label end = e.make_label();
		// 检查第一个对象是否非 null
		e.ifnonnull(nonNull);
		// 第一个为 null，检查第二个
		e.ifnonnull(oneNullHelper);
		// 两个都为 null，弹出并跳转
		e.pop2();
		e.goTo(bothNull);

		// 第一个非 null，标记位置
		e.mark(nonNull);
		// 检查第二个是否为 null
		e.ifnull(oneNullHelper);
		// 两个都非 null，直接结束
		e.goTo(end);

		// 只有一个为 null，标记位置
		e.mark(oneNullHelper);
		// 弹出两个对象并跳转
		e.pop2();
		e.goTo(oneNull);

		// 结束标记
		e.mark(end);
	}


	/**
	 * 缩短 StringBuffer 的长度
	 * <p>
	 * 生成的代码等价于：
	 * <pre>{@code
	 * stringBuffer.setLength(stringBuffer.length() - amt);
	 * }</pre>
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param amt 要减少的长度
	 */
	private static void shrinkStringBuffer(CodeEmitter e, int amt) {
		// 复制 StringBuffer 引用三次：一份用于 length()，一份用于 setLength()，一份保留
		e.dup();
		e.dup();
		// 调用 length() 获取当前长度
		e.invoke_virtual(AsmConsts.TYPE_STRING_BUFFER, LENGTH);
		// 减去指定数量
		e.push(amt);
		e.math(e.SUB, Type.INT_TYPE);
		// 调用 setLength 设置新长度
		e.invoke_virtual(AsmConsts.TYPE_STRING_BUFFER, SET_LENGTH);
	}

	/**
	 * 数组分隔符配置类
	 * <p>
	 * 用于定义数组 toString 时的格式：
	 * <ul>
	 *   <li>before - 前缀，例如 "{"</li>
	 *   <li>inside - 元素之间的分隔符，例如 ", "</li>
	 *   <li>after - 后缀，例如 "}"</li>
	 * </ul>
	 * </p>
	 */
	public static class ArrayDelimiters {
		/** 数组前缀 */
		private String before;
		/** 元素之间的分隔符 */
		private String inside;
		/** 数组后缀 */
		private String after;

		/**
		 * 构造数组分隔符配置
		 *
		 * @param before 前缀
		 * @param inside 元素分隔符
		 * @param after 后缀
		 */
		public ArrayDelimiters(String before, String inside, String after) {
			this.before = before;
			this.inside = inside;
			this.after = after;
		}
	}

	/**
	 * 加载 Method 对象到栈顶
	 * <p>
	 * 生成的代码等价于：
	 * <pre>{@code
	 * Class clazz = ...; // 方法所在的类
	 * Method method = clazz.getDeclaredMethod("methodName", paramTypes);
	 * }</pre>
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param method 方法信息对象
	 */
	public static void load_method(CodeEmitter e, MethodDescriptor method) {
		// 加载方法所在的类
		load_class(e, method.getClassDescriptor().getType());
		// 加载方法名
		e.push(method.getSignature().getName());
		// 加载参数类型数组
		push_object(e, method.getSignature().getArgumentTypes());
		// 调用 getDeclaredMethod 获取 Method 对象
		e.invoke_virtual(AsmConsts.TYPE_CLASS, GET_DECLARED_METHOD);
	}

	/** 参数类型获取接口 */
	private interface ParameterTyper {
		/**
		 * 获取方法的参数类型数组
		 *
		 * @param member 方法信息对象
		 * @return 参数类型数组
		 */
		Type[] getParameterTypes(MethodDescriptor member);
	}

	/**
	 * 生成方法 switch 分发逻辑
	 * <p>
	 * 根据方法名和参数类型动态分发到不同的处理方法。
	 * 适用于重载方法的运行时选择。
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param methods 方法信息列表
	 * @param callback 每个匹配方法触发的回调函数
	 */
	public static void method_switch(CodeEmitter e,
		List methods,
		ObjectSwitchCallback callback) {
		// 使用名称进行分发
		member_switch_helper(e, methods, callback, true);
	}

	/**
	 * 生成构造方法 switch 分发逻辑
	 * <p>
	 * 根据参数类型动态分发到不同的构造方法。
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param constructors 构造方法信息列表
	 * @param callback 每个匹配构造方法触发的回调函数
	 */
	public static void constructor_switch(CodeEmitter e,
		List constructors,
		ObjectSwitchCallback callback) {
		// 不使用名称，直接按参数类型分发
		member_switch_helper(e, constructors, callback, false);
	}

	/**
	 * 成员（方法/构造方法）switch 分发的辅助方法
	 * <p>
	 * 根据 useName 参数决定是否先按方法名分组：
	 * <ul>
	 *   <li>useName=true: 先按方法名 switch，再按参数类型区分重载</li>
	 *   <li>useName=false: 直接按参数类型区分</li>
	 * </ul>
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param members 成员信息列表
	 * @param callback 匹配时的回调函数
	 * @param useName 是否使用方法名进行第一层分发
	 */
	private static void member_switch_helper(final CodeEmitter e,
		List members,
		final ObjectSwitchCallback callback,
		boolean useName) {
		try {
			// 缓存参数类型，避免重复计算
			final Map cache = new HashMap();
			final ParameterTyper cached = new ParameterTyper() {
				public Type[] getParameterTypes(MethodDescriptor member) {
					Type[] types = (Type[]) cache.get(member);
					if (types == null) {
						cache.put(member, types = member.getSignature().getArgumentTypes());
					}
					return types;
				}
			};
			// 创建默认分支和结束标签
			final Label def = e.make_label();
			final Label end = e.make_label();
			if (useName) {
				// 交换栈顶元素顺序，准备调用 getName()
				e.swap();
				// 按方法名分组
				final Map buckets = AsmCollections.bucket(members, new Transformer() {
					public Object transform(Object value) {
						return ((MethodDescriptor) value).getSignature().getName();
					}
				});
				String[] names = (String[]) buckets.keySet().toArray(new String[buckets.size()]);
				// 使用方法名进行字符串 switch
				Emitters.string_switch(e, names, AsmConsts.SWITCH_STYLE_HASH, new ObjectSwitchCallback() {
					public void processCase(Object key, Label dontUseEnd) throws Exception {
						// 对相同名称的方法，按参数大小进一步区分
						member_helper_size(e, (List) buckets.get(key), callback, cached, def, end);
					}

					public void processDefault() throws Exception {
						e.goTo(def);
					}
				});
			} else {
				// 不使用名称，直接按参数大小分组
				member_helper_size(e, members, callback, cached, def, end);
			}
			// 标记默认分支，弹出多余引用，执行默认逻辑
			e.mark(def);
			e.pop();
			callback.processDefault();
			// 标记结束位置
			e.mark(end);
		} catch (RuntimeException ex) {
			throw ex;
		} catch (Error ex) {
			throw ex;
		} catch (Exception ex) {
			throw new BytecodeOperationException(ex);
		}
	}

	/**
	 * 按参数数量分组的辅助方法
	 * <p>
	 * 根据参数数组的长度进行 switch 分发，将相同参数数量的成员分到一组。
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param members 成员信息列表
	 * @param callback 匹配时的回调函数
	 * @param typer 参数类型获取器
	 * @param def 默认分支标签
	 * @param end 结束标签
	 * @throws Exception 处理异常
	 */
	private static void member_helper_size(final CodeEmitter e,
		List members,
		final ObjectSwitchCallback callback,
		final ParameterTyper typer,
		final Label def,
		final Label end) throws Exception {
		// 按参数数量分组
		final Map buckets = AsmCollections.bucket(members, new Transformer() {
			public Object transform(Object value) {
				return typer.getParameterTypes((MethodDescriptor) value).length;
			}
		});
		// 复制参数类型数组，获取其长度
		e.dup();
		e.arraylength();
		// 根据参数数量进行 switch 分发
		e.process_switch(Emitters.getSwitchKeys(buckets), new ProcessSwitchCallback() {
			public void processCase(int key, Label dontUseEnd) throws Exception {
				List bucket = (List) buckets.get(Integer.valueOf(key));
				// 对相同参数数量的成员，按类型进一步区分
				member_helper_type(e, bucket, callback, typer, def, end, new BitSet());
			}

			public void processDefault() throws Exception {
				e.goTo(def);
			}
		});
	}

	/**
	 * 按参数类型区分的递归辅助方法
	 * <p>
	 * 对于每个参数位置，选择区分度最高的位置进行 switch 分发。
	 * 如果无法区分（例如只有返回类型不同），则跳转到默认分支。
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param members 成员信息列表
	 * @param callback 匹配时的回调函数
	 * @param typer 参数类型获取器
	 * @param def 默认分支标签
	 * @param end 结束标签
	 * @param checked 已检查的参数位置位图
	 * @throws Exception 处理异常
	 */
	private static void member_helper_type(final CodeEmitter e,
		List members,
		final ObjectSwitchCallback callback,
		final ParameterTyper typer,
		final Label def,
		final Label end,
		final BitSet checked) throws Exception {
		if (members.size() == 1) {
			// 只有一个候选，直接匹配
			MethodDescriptor member = (MethodDescriptor) members.get(0);
			Type[] types = typer.getParameterTypes(member);
			// need to check classes that have not already been checked via switches
			// 检查尚未通过 switch 验证的参数类型
			for (int i = 0; i < types.length; i++) {
				if (checked == null || !checked.get(i)) {
					// 加载参数类型数组的第 i 个元素
					e.dup();
					e.aaload(i);
					// 调用 getName() 获取类名
					e.invoke_virtual(AsmConsts.TYPE_CLASS, GET_NAME);
					// 与期望的类型名比较
					e.push(AsmTypes.emulateClassGetName(types[i]));
					e.invoke_virtual(AsmConsts.TYPE_OBJECT, EQUALS);
					// 不相等则跳转到默认分支
					e.if_jump(e.EQ, def);
				}
			}
			// 所有类型都匹配，弹出参数数组并执行回调
			e.pop();
			callback.processCase(member, end);
		} else {
			// 多个候选，需要进一步区分
			// choose the index that has the best chance of uniquely identifying member
			// 选择区分度最高的参数位置
			Type[] example = typer.getParameterTypes((MethodDescriptor) members.get(0));
			Map buckets = null;
			int index = -1;
			// 遍历所有参数位置，找到分组最多的位置
			for (int i = 0; i < example.length; i++) {
				final int j = i;
				Map test = AsmCollections.bucket(members, new Transformer() {
					public Object transform(Object value) {
						return AsmTypes.emulateClassGetName(typer.getParameterTypes((MethodDescriptor) value)[j]);
					}
				});
				if (buckets == null || test.size() > buckets.size()) {
					buckets = test;
					index = i;
				}
			}
			if (buckets == null || buckets.size() == 1) {
				// TODO: switch by returnType
				// must have two methods with same name, types, and different return types
				// 无法区分（可能是仅返回类型不同的重载），跳转到默认分支
				e.goTo(def);
			} else {
				// 标记该位置已检查
				checked.set(index);

				// 复制参数数组，加载指定位置的类型
				e.dup();
				e.aaload(index);
				e.invoke_virtual(AsmConsts.TYPE_CLASS, GET_NAME);

				// 按类型名进行字符串 switch
				final Map fbuckets = buckets;
				String[] names = (String[]) buckets.keySet().toArray(new String[buckets.size()]);
				Emitters.string_switch(e, names, AsmConsts.SWITCH_STYLE_HASH, new ObjectSwitchCallback() {
					public void processCase(Object key, Label dontUseEnd) throws Exception {
						// 递归处理同一类型的成员
						member_helper_type(e, (List) fbuckets.get(key), callback, typer, def, end, checked);
					}

					public void processDefault() throws Exception {
						e.goTo(def);
					}
				});
			}
		}
	}

	/**
	 * 包装捕获的异常为指定的异常类型
	 * <p>
	 * 生成的代码等价于：
	 * <pre>{@code
	 * } catch (Throwable t) {
	 *     throw new WrapperException(t);
	 * }
	 * }</pre>
	 * </p>
	 *
	 * @param block 代码块
	 * @param wrapper 包装异常的类型
	 */
	public static void wrap_throwable(Block block, Type wrapper) {
		CodeEmitter e = block.getCodeEmitter();
		// 捕获 Throwable 异常
		e.catch_exception(block, AsmConsts.TYPE_THROWABLE);
		// 创建包装异常实例
		e.new_instance(wrapper);
		// 调整栈顺序：wrapper, throwable -> throwable, wrapper
		e.dup_x1();
		e.swap();
		// 调用包装异常的构造函数
		e.invoke_constructor(wrapper, CSTRUCT_THROWABLE);
		// 抛出包装后的异常
		e.athrow();
	}

	/**
	 * 为类添加多个属性及其访问器方法
	 * <p>
	 * 为每个属性生成：
	 * <ul>
	 *   <li>私有字段：$ASM$prop_属性名</li>
	 *   <li>getter 方法：get属性名()</li>
	 *   <li>setter 方法：set属性名(value)</li>
	 * </ul>
	 * </p>
	 *
	 * @param ce 类发射器
	 * @param names 属性名数组
	 * @param types 属性类型数组
	 */
	public static void add_properties(ClassEmitter ce, String[] names, Type[] types) {
		// 遍历所有属性，逐个添加
		for (int i = 0; i < names.length; i++) {
			// 生成唯一的字段名
			String fieldName = "$ASM$prop_" + names[i];
			// 声明私有字段
			ce.declare_field(AsmConsts.ACC_PRIVATE, fieldName, types[i], null);
			// 添加 getter 和 setter 方法
			Emitters.add_property(ce, names[i], types[i], fieldName);
		}
	}

	/**
	 * 为类添加单个属性及其访问器方法
	 * <p>
	 * 生成 getter 和 setter 方法，方法名遵循 JavaBean 规范：
	 * <ul>
	 *   <li>getter: getPropertyName() 或 isPropertyName()（boolean 类型）</li>
	 *   <li>setter: setPropertyName(value)</li>
	 * </ul>
	 * </p>
	 *
	 * @param ce 类发射器
	 * @param name 属性名
	 * @param type 属性类型
	 * @param fieldName 字段名
	 */
	public static void add_property(ClassEmitter ce, String name, Type type, String fieldName) {
		// 首字母大写，用于生成方法名
		String property = AsmTypes.upperFirst(name);
		CodeEmitter e;
		// 生成 getter 方法
		e = ce.begin_method(AsmConsts.ACC_PUBLIC,
			new Signature("get" + property,
				type,
				AsmConsts.TYPES_EMPTY),
			null);
		// 加载 this 引用
		e.load_this();
		// 读取字段值
		e.getfield(fieldName);
		// 返回值
		e.return_value();
		e.end_method();

		// 生成 setter 方法
		e = ce.begin_method(AsmConsts.ACC_PUBLIC,
			new Signature("set" + property,
				Type.VOID_TYPE,
				new Type[]{type}),
			null);
		// 加载 this 引用
		e.load_this();
		// 加载参数值
		e.load_arg(0);
		// 设置字段值
		e.putfield(fieldName);
		// 返回
		e.return_value();
		e.end_method();
	}

	/**
	 * 包装未声明的受检异常
	 * <p>
	 * 生成的代码结构：
	 * <pre>
	 * } catch (RuntimeException e) {
	 *     throw e;  // 运行时异常直接抛出
	 * } catch (Error e) {
	 *     throw e;  // 错误直接抛出
	 * } catch (DeclaredException e) {
	 *     throw e;  // 已声明的异常直接抛出
	 * } catch (Throwable e) {
	 *     throw new Wrapper(e);  // 其他异常包装后抛出
	 * }
	 * </pre>
	 * </p>
	 *
	 * @param e 代码发射器
	 * @param handler 异常处理块
	 * @param exceptions 已声明的异常类型数组
	 * @param wrapper 包装异常的类型
	 */
	public static void wrap_undeclared_throwable(CodeEmitter e, Block handler, Type[] exceptions, Type wrapper) {
		// 将已声明的异常放入集合
		Set<Type> set = (exceptions == null) ? Collections.emptySet() : new HashSet<>(Arrays.asList(exceptions));

		// 如果已声明 Throwable，无需包装
		if (set.contains(AsmConsts.TYPE_THROWABLE)) {
			return;
		}
		// 捕获已声明的异常，直接抛出
		if (exceptions != null && exceptions.length > 0) {
			for (int i = 0; i < exceptions.length; i++) {
				e.catch_exception(handler, exceptions[i]);
			}
			e.athrow();
		}

		boolean needThrow = false;
		// 捕获 RuntimeException，直接抛出
		if (!set.contains(AsmConsts.TYPE_RUNTIME_EXCEPTION)) {
			e.catch_exception(handler, AsmConsts.TYPE_RUNTIME_EXCEPTION);
			needThrow = true;
		}
		// 捕获 Error，直接抛出
		if (!set.contains(AsmConsts.TYPE_ERROR)) {
			e.catch_exception(handler, AsmConsts.TYPE_ERROR);
			needThrow = true;
		}
		// 如果需要，抛出直接捕获的异常
		if (needThrow) {
			e.athrow();
		}
		// e -> eo -> oeo -> ooe -> o
		// 捕获其他所有 Throwable，包装后抛出
		e.catch_exception(handler, AsmConsts.TYPE_THROWABLE);
		// 创建包装异常实例
		e.new_instance(wrapper);
		// 调整栈顺序
		e.dup_x1();
		e.swap();
		// 调用包装异常构造函数
		e.invoke_constructor(wrapper, CSTRUCT_THROWABLE);
		// 抛出包装后的异常
		e.athrow();
	}

	/**
	 * 开始生成方法的字节码
	 * <p>
	 * 使用方法的原始访问修饰符。
	 * </p>
	 *
	 * @param e 类发射器
	 * @param method 方法信息对象
	 * @return 代码发射器
	 */
	public static CodeEmitter begin_method(ClassEmitter e, MethodDescriptor method) {
		return begin_method(e, method, method.getModifiers());
	}

	/**
	 * 开始生成方法的字节码（指定访问修饰符）
	 * <p>
	 * 允许覆盖方法的原始访问修饰符。
	 * </p>
	 *
	 * @param e 类发射器
	 * @param method 方法信息对象
	 * @param access 访问修饰符
	 * @return 代码发射器
	 */
	public static CodeEmitter begin_method(ClassEmitter e, MethodDescriptor method, int access) {
		// 使用指定的访问修饰符、方法签名和异常类型开始生成方法
		return e.begin_method(access,
			method.getSignature(),
			method.getExceptionTypes());
	}
}
