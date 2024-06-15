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
 * @author Qt
 * @since May 09, 2024
 */
public class Emitters {
	private static final Signature CSTRUCT_NULL =
		AsmTypes.parseConstructor("");
	private static final Signature CSTRUCT_THROWABLE =
		AsmTypes.parseConstructor("Throwable");

	private static final Signature GET_NAME =
		AsmTypes.parseSignature("String getName()");
	private static final Signature HASH_CODE =
		AsmTypes.parseSignature("int hashCode()");
	private static final Signature EQUALS =
		AsmTypes.parseSignature("boolean equals(Object)");
	private static final Signature STRING_LENGTH =
		AsmTypes.parseSignature("int length()");
	private static final Signature STRING_CHAR_AT =
		AsmTypes.parseSignature("char charAt(int)");
	private static final Signature FOR_NAME =
		AsmTypes.parseSignature("Class forName(String)");
	private static final Signature DOUBLE_TO_LONG_BITS =
		AsmTypes.parseSignature("long doubleToLongBits(double)");
	private static final Signature FLOAT_TO_INT_BITS =
		AsmTypes.parseSignature("int floatToIntBits(float)");
	private static final Signature TO_STRING =
		AsmTypes.parseSignature("String toString()");
	private static final Signature APPEND_STRING =
		AsmTypes.parseSignature("StringBuffer append(String)");
	private static final Signature APPEND_INT =
		AsmTypes.parseSignature("StringBuffer append(int)");
	private static final Signature APPEND_DOUBLE =
		AsmTypes.parseSignature("StringBuffer append(double)");
	private static final Signature APPEND_FLOAT =
		AsmTypes.parseSignature("StringBuffer append(float)");
	private static final Signature APPEND_CHAR =
		AsmTypes.parseSignature("StringBuffer append(char)");
	private static final Signature APPEND_LONG =
		AsmTypes.parseSignature("StringBuffer append(long)");
	private static final Signature APPEND_BOOLEAN =
		AsmTypes.parseSignature("StringBuffer append(boolean)");
	private static final Signature LENGTH =
		AsmTypes.parseSignature("int length()");
	private static final Signature SET_LENGTH =
		AsmTypes.parseSignature("void setLength(int)");
	private static final Signature GET_DECLARED_METHOD =
		AsmTypes.parseSignature("java.lang.reflect.Method getDeclaredMethod(String, Class[])");


	public static final ArrayDelimiters DEFAULT_DELIMITERS = new ArrayDelimiters("{", ", ", "}");

	private Emitters() {
	}

	public static void factory_method(ClassEmitter ce, Signature sig) {
		CodeEmitter e = ce.begin_method(AsmConsts.ACC_PUBLIC, sig, null);
		e.new_instance_this();
		e.dup();
		e.load_args();
		e.invoke_constructor_this(AsmTypes.parseConstructor(sig.getArgumentTypes()));
		e.return_value();
		e.end_method();
	}

	public static void null_constructor(ClassEmitter ce) {
		CodeEmitter e = ce.begin_method(AsmConsts.ACC_PUBLIC, CSTRUCT_NULL, null);
		e.load_this();
		e.super_invoke_constructor();
		e.return_value();
		e.end_method();
	}

	/**
	 * Process an array on the stack. Assumes the top item on the stack
	 * is an array of the specified type. For each element in the array,
	 * puts the element on the stack and triggers the callback.
	 * @param type the type of the array (type.isArray() must be true)
	 * @param callback the callback triggered for each element
	 */
	public static void process_array(CodeEmitter e, Type type, ProcessArrayCallback callback) {
		Type componentType = AsmTypes.getComponentType(type);
		Local array = e.make_local();
		Local loopvar = e.make_local(Type.INT_TYPE);
		Label loopbody = e.make_label();
		Label checkloop = e.make_label();
		e.store_local(array);
		e.push(0);
		e.store_local(loopvar);
		e.goTo(checkloop);

		e.mark(loopbody);
		e.load_local(array);
		e.load_local(loopvar);
		e.array_load(componentType);
		callback.processElement(componentType);
		e.iinc(loopvar, 1);

		e.mark(checkloop);
		e.load_local(loopvar);
		e.load_local(array);
		e.arraylength();
		e.if_icmp(e.LT, loopbody);
	}

	/**
	 * Process two arrays on the stack in parallel. Assumes the top two items on the stack
	 * are arrays of the specified class. The arrays must be the same length. For each pair
	 * of elements in the arrays, puts the pair on the stack and triggers the callback.
	 * @param type the type of the arrays (type.isArray() must be true)
	 * @param callback the callback triggered for each pair of elements
	 */
	public static void process_arrays(CodeEmitter e, Type type, ProcessArrayCallback callback) {
		Type componentType = AsmTypes.getComponentType(type);
		Local array1 = e.make_local();
		Local array2 = e.make_local();
		Local loopvar = e.make_local(Type.INT_TYPE);
		Label loopbody = e.make_label();
		Label checkloop = e.make_label();
		e.store_local(array1);
		e.store_local(array2);
		e.push(0);
		e.store_local(loopvar);
		e.goTo(checkloop);

		e.mark(loopbody);
		e.load_local(array1);
		e.load_local(loopvar);
		e.array_load(componentType);
		e.load_local(array2);
		e.load_local(loopvar);
		e.array_load(componentType);
		callback.processElement(componentType);
		e.iinc(loopvar, 1);

		e.mark(checkloop);
		e.load_local(loopvar);
		e.load_local(array1);
		e.arraylength();
		e.if_icmp(e.LT, loopbody);
	}

	public static void string_switch(CodeEmitter e, String[] strings, int switchStyle, ObjectSwitchCallback callback) {
		try {
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

	private static void string_switch_trie(final CodeEmitter e,
		String[] strings,
		final ObjectSwitchCallback callback) throws Exception {
		final Label def = e.make_label();
		final Label end = e.make_label();
		final Map buckets = AsmCollections.bucket(Arrays.asList(strings), new Transformer() {
			public Object transform(Object value) {
				return ((String)value).length();
			}
		});
		e.dup();
		e.invoke_virtual(AsmConsts.TYPE_STRING, STRING_LENGTH);
		e.process_switch(getSwitchKeys(buckets), new ProcessSwitchCallback() {
			public void processCase(int key, Label ignore_end) throws Exception {
				List bucket = (List)buckets.get(Integer.valueOf(key));
				stringSwitchHelper(e, bucket, callback, def, end, 0);
			}
			public void processDefault() {
				e.goTo(def);
			}
		});
		e.mark(def);
		e.pop();
		callback.processDefault();
		e.mark(end);
	}

	private static void stringSwitchHelper(final CodeEmitter e,
		List strings,
		final ObjectSwitchCallback callback,
		final Label def,
		final Label end,
		final int index) throws Exception {
		final int len = ((String)strings.get(0)).length();
		final Map buckets = AsmCollections.bucket(strings, new Transformer() {
			public Object transform(Object value) {
				return (int) ((String)value).charAt(index);
			}
		});
		e.dup();
		e.push(index);
		e.invoke_virtual(AsmConsts.TYPE_STRING, STRING_CHAR_AT);
		e.process_switch(getSwitchKeys(buckets), new ProcessSwitchCallback() {
			public void processCase(int key, Label ignore_end) throws Exception {
				List bucket = (List)buckets.get(Integer.valueOf(key));
				if (index + 1 == len) {
					e.pop();
					callback.processCase(bucket.get(0), end);
				} else {
					stringSwitchHelper(e, bucket, callback, def, end, index + 1);
				}
			}
			public void processDefault() {
				e.goTo(def);
			}
		});
	}

	static int[] getSwitchKeys(Map buckets) {
		int[] keys = new int[buckets.size()];
		int index = 0;
		for (Iterator it = buckets.keySet().iterator(); it.hasNext();) {
			keys[index++] = ((Integer)it.next()).intValue();
		}
		Arrays.sort(keys);
		return keys;
	}

	private static void string_switch_hash(final CodeEmitter e,
		final String[] strings,
		final ObjectSwitchCallback callback,
		final boolean skipEquals) throws Exception {
		final Map buckets = AsmCollections.bucket(Arrays.asList(strings), new Transformer() {
			public Object transform(Object value) {
				return value.hashCode();
			}
		});
		final Label def = e.make_label();
		final Label end = e.make_label();
		e.dup();
		e.invoke_virtual(AsmConsts.TYPE_OBJECT, HASH_CODE);
		e.process_switch(getSwitchKeys(buckets), new ProcessSwitchCallback() {
			public void processCase(int key, Label ignore_end) throws Exception {
				List bucket = (List)buckets.get(Integer.valueOf(key));
				Label next = null;
				if (skipEquals && bucket.size() == 1) {
					if (skipEquals)
						e.pop();
					callback.processCase((String)bucket.get(0), end);
				} else {
					for (Iterator it = bucket.iterator(); it.hasNext();) {
						String string = (String)it.next();
						if (next != null) {
							e.mark(next);
						}
						if (it.hasNext()) {
							e.dup();
						}
						e.push(string);
						e.invoke_virtual(AsmConsts.TYPE_OBJECT, EQUALS);
						if (it.hasNext()) {
							e.if_jump(e.EQ, next = e.make_label());
							e.pop();
						} else {
							e.if_jump(e.EQ, def);
						}
						callback.processCase(string, end);
					}
				}
			}
			public void processDefault() {
				e.pop();
			}
		});
		e.mark(def);
		callback.processDefault();
		e.mark(end);
	}

	public static void load_class_this(CodeEmitter e) {
		load_class_helper(e, e.getClassEmitter().getClassType());
	}

	public static void load_class(CodeEmitter e, Type type) {
		if (AsmTypes.isPrimitive(type)) {
			if (type == Type.VOID_TYPE) {
				throw new IllegalArgumentException("cannot load void type");
			}
			e.getstatic(AsmTypes.getBoxedType(type), "TYPE", AsmConsts.TYPE_CLASS);
		} else {
			load_class_helper(e, type);
		}
	}

	private static void load_class_helper(CodeEmitter e, final Type type) {
		if (e.isStaticHook()) {
			// have to fall back on non-optimized load
			e.push(AsmTypes.emulateClassGetName(type));
			e.invoke_static(AsmConsts.TYPE_CLASS, FOR_NAME);
		} else {
			ClassEmitter ce = e.getClassEmitter();
			String typeName = AsmTypes.emulateClassGetName(type);

			// TODO: can end up with duplicated field names when using chained transformers; incorporate static hook # somehow
			String fieldName = "$ASM$load_class$" + AsmTypes.escapeType(typeName);
			if (!ce.isFieldDeclared(fieldName)) {
				ce.declare_field(AsmConsts.PRIVATE_FINAL_STATIC, fieldName, AsmConsts.TYPE_CLASS, null);
				CodeEmitter hook = ce.getStaticHook();
				hook.push(typeName);
				hook.invoke_static(AsmConsts.TYPE_CLASS, FOR_NAME);
				hook.putstatic(ce.getClassType(), fieldName, AsmConsts.TYPE_CLASS);
			}
			e.getfield(fieldName);
		}
	}

	public static void push_array(CodeEmitter e, Object[] array) {
		e.push(array.length);
		e.newarray(Type.getType(remapComponentType(array.getClass().getComponentType())));
		for (int i = 0; i < array.length; i++) {
			e.dup();
			e.push(i);
			push_object(e, array[i]);
			e.aastore();
		}
	}

	private static Class remapComponentType(Class componentType) {
		if (componentType.equals(Type.class))
			return Class.class;
		return componentType;
	}

	public static void push_object(CodeEmitter e, Object obj) {
		if (obj == null) {
			e.aconst_null();
		} else {
			Class type = obj.getClass();
			if (type.isArray()) {
				push_array(e, (Object[])obj);
			} else if (obj instanceof String) {
				e.push((String)obj);
			} else if (obj instanceof Type) {
				load_class(e, (Type)obj);
			} else if (obj instanceof Class) {
				load_class(e, Type.getType((Class)obj));
			} else if (obj instanceof BigInteger) {
				e.new_instance(AsmConsts.TYPE_BIG_INTEGER);
				e.dup();
				e.push(obj.toString());
				e.invoke_constructor(AsmConsts.TYPE_BIG_INTEGER);
			} else if (obj instanceof BigDecimal) {
				e.new_instance(AsmConsts.TYPE_BIG_DECIMAL);
				e.dup();
				e.push(obj.toString());
				e.invoke_constructor(AsmConsts.TYPE_BIG_DECIMAL);
			} else {
				throw new IllegalArgumentException("unknown type: " + obj.getClass());
			}
		}
	}



	private static void hash_primitive(CodeEmitter e, Type type) {
		switch (type.getSort()) {
			case Type.BOOLEAN:
				// f ? 0 : 1
				e.push(1);
				e.math(e.XOR, Type.INT_TYPE);
				break;
			case Type.FLOAT:
				// Float.floatToIntBits(f)
				e.invoke_static(AsmConsts.TYPE_FLOAT, FLOAT_TO_INT_BITS);
				break;
			case Type.DOUBLE:
				// Double.doubleToLongBits(f), hash_code(Long.TYPE)
				e.invoke_static(AsmConsts.TYPE_DOUBLE, DOUBLE_TO_LONG_BITS);
				// fall through
			case Type.LONG:
				hash_long(e);
		}
	}

	private static void hash_long(CodeEmitter e) {
		// (int)(f ^ (f >>> 32))
		e.dup2();
		e.push(32);
		e.math(e.USHR, Type.LONG_TYPE);
		e.math(e.XOR, Type.LONG_TYPE);
		e.cast_numeric(Type.LONG_TYPE, Type.INT_TYPE);
	}


	/**
	 * If both objects on the top of the stack are non-null, does nothing.
	 * If one is null, or both are null, both are popped off and execution
	 * branches to the respective label.
	 * @param oneNull label to branch to if only one of the objects is null
	 * @param bothNull label to branch to if both of the objects are null
	 */
	private static void nullcmp(CodeEmitter e, Label oneNull, Label bothNull) {
		e.dup2();
		Label nonNull = e.make_label();
		Label oneNullHelper = e.make_label();
		Label end = e.make_label();
		e.ifnonnull(nonNull);
		e.ifnonnull(oneNullHelper);
		e.pop2();
		e.goTo(bothNull);

		e.mark(nonNull);
		e.ifnull(oneNullHelper);
		e.goTo(end);

		e.mark(oneNullHelper);
		e.pop2();
		e.goTo(oneNull);

		e.mark(end);
	}


	private static void shrinkStringBuffer(CodeEmitter e, int amt) {
		e.dup();
		e.dup();
		e.invoke_virtual(AsmConsts.TYPE_STRING_BUFFER, LENGTH);
		e.push(amt);
		e.math(e.SUB, Type.INT_TYPE);
		e.invoke_virtual(AsmConsts.TYPE_STRING_BUFFER, SET_LENGTH);
	}

	public static class ArrayDelimiters {
		private String before;
		private String inside;
		private String after;

		public ArrayDelimiters(String before, String inside, String after) {
			this.before = before;
			this.inside = inside;
			this.after = after;
		}
	}

	public static void load_method(CodeEmitter e, MethodInfo method) {
		load_class(e, method.getClassInfo().getType());
		e.push(method.getSignature().getName());
		push_object(e, method.getSignature().getArgumentTypes());
		e.invoke_virtual(AsmConsts.TYPE_CLASS, GET_DECLARED_METHOD);
	}

	private interface ParameterTyper {
		Type[] getParameterTypes(MethodInfo member);
	}

	public static void method_switch(CodeEmitter e,
		List methods,
		ObjectSwitchCallback callback) {
		member_switch_helper(e, methods, callback, true);
	}

	public static void constructor_switch(CodeEmitter e,
		List constructors,
		ObjectSwitchCallback callback) {
		member_switch_helper(e, constructors, callback, false);
	}

	private static void member_switch_helper(final CodeEmitter e,
		List members,
		final ObjectSwitchCallback callback,
		boolean useName) {
		try {
			final Map cache = new HashMap();
			final ParameterTyper cached = new ParameterTyper() {
				public Type[] getParameterTypes(MethodInfo member) {
					Type[] types = (Type[])cache.get(member);
					if (types == null) {
						cache.put(member, types = member.getSignature().getArgumentTypes());
					}
					return types;
				}
			};
			final Label def = e.make_label();
			final Label end = e.make_label();
			if (useName) {
				e.swap();
				final Map buckets = AsmCollections.bucket(members, new Transformer() {
					public Object transform(Object value) {
						return ((MethodInfo)value).getSignature().getName();
					}
				});
				String[] names = (String[])buckets.keySet().toArray(new String[buckets.size()]);
				Emitters.string_switch(e, names, AsmConsts.SWITCH_STYLE_HASH, new ObjectSwitchCallback() {
					public void processCase(Object key, Label dontUseEnd) throws Exception {
						member_helper_size(e, (List)buckets.get(key), callback, cached, def, end);
					}
					public void processDefault() throws Exception {
						e.goTo(def);
					}
				});
			} else {
				member_helper_size(e, members, callback, cached, def, end);
			}
			e.mark(def);
			e.pop();
			callback.processDefault();
			e.mark(end);
		} catch (RuntimeException ex) {
			throw ex;
		} catch (Error ex) {
			throw ex;
		} catch (Exception ex) {
			throw new BytecodeOperationException(ex);
		}
	}

	private static void member_helper_size(final CodeEmitter e,
		List members,
		final ObjectSwitchCallback callback,
		final ParameterTyper typer,
		final Label def,
		final Label end) throws Exception {
		final Map buckets = AsmCollections.bucket(members, new Transformer() {
			public Object transform(Object value) {
				return typer.getParameterTypes((MethodInfo)value).length;
			}
		});
		e.dup();
		e.arraylength();
		e.process_switch(Emitters.getSwitchKeys(buckets), new ProcessSwitchCallback() {
			public void processCase(int key, Label dontUseEnd) throws Exception {
				List bucket = (List)buckets.get(Integer.valueOf(key));
				member_helper_type(e, bucket, callback, typer, def, end, new BitSet());
			}
			public void processDefault() throws Exception {
				e.goTo(def);
			}
		});
	}

	private static void member_helper_type(final CodeEmitter e,
		List members,
		final ObjectSwitchCallback callback,
		final ParameterTyper typer,
		final Label def,
		final Label end,
		final BitSet checked) throws Exception {
		if (members.size() == 1) {
			MethodInfo member = (MethodInfo)members.get(0);
			Type[] types = typer.getParameterTypes(member);
			// need to check classes that have not already been checked via switches
			for (int i = 0; i < types.length; i++) {
				if (checked == null || !checked.get(i)) {
					e.dup();
					e.aaload(i);
					e.invoke_virtual(AsmConsts.TYPE_CLASS, GET_NAME);
					e.push(AsmTypes.emulateClassGetName(types[i]));
					e.invoke_virtual(AsmConsts.TYPE_OBJECT, EQUALS);
					e.if_jump(e.EQ, def);
				}
			}
			e.pop();
			callback.processCase(member, end);
		} else {
			// choose the index that has the best chance of uniquely identifying member
			Type[] example = typer.getParameterTypes((MethodInfo)members.get(0));
			Map buckets = null;
			int index = -1;
			for (int i = 0; i < example.length; i++) {
				final int j = i;
				Map test = AsmCollections.bucket(members, new Transformer() {
					public Object transform(Object value) {
						return AsmTypes.emulateClassGetName(typer.getParameterTypes((MethodInfo)value)[j]);
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
				e.goTo(def);
			} else {
				checked.set(index);

				e.dup();
				e.aaload(index);
				e.invoke_virtual(AsmConsts.TYPE_CLASS, GET_NAME);

				final Map fbuckets = buckets;
				String[] names = (String[])buckets.keySet().toArray(new String[buckets.size()]);
				Emitters.string_switch(e, names, AsmConsts.SWITCH_STYLE_HASH, new ObjectSwitchCallback() {
					public void processCase(Object key, Label dontUseEnd) throws Exception {
						member_helper_type(e, (List)fbuckets.get(key), callback, typer, def, end, checked);
					}
					public void processDefault() throws Exception {
						e.goTo(def);
					}
				});
			}
		}
	}

	public static void wrap_throwable(Block block, Type wrapper) {
		CodeEmitter e = block.getCodeEmitter();
		e.catch_exception(block, AsmConsts.TYPE_THROWABLE);
		e.new_instance(wrapper);
		e.dup_x1();
		e.swap();
		e.invoke_constructor(wrapper, CSTRUCT_THROWABLE);
		e.athrow();
	}

	public static void add_properties(ClassEmitter ce, String[] names, Type[] types) {
		for (int i = 0; i < names.length; i++) {
			String fieldName = "$ASM$prop_" + names[i];
			ce.declare_field(AsmConsts.ACC_PRIVATE, fieldName, types[i], null);
			Emitters.add_property(ce, names[i], types[i], fieldName);
		}
	}

	public static void add_property(ClassEmitter ce, String name, Type type, String fieldName) {
		String property = AsmTypes.upperFirst(name);
		CodeEmitter e;
		e = ce.begin_method(AsmConsts.ACC_PUBLIC,
			new Signature("get" + property,
				type,
				AsmConsts.TYPES_EMPTY),
			null);
		e.load_this();
		e.getfield(fieldName);
		e.return_value();
		e.end_method();

		e = ce.begin_method(AsmConsts.ACC_PUBLIC,
			new Signature("set" + property,
				Type.VOID_TYPE,
				new Type[]{ type }),
			null);
		e.load_this();
		e.load_arg(0);
		e.putfield(fieldName);
		e.return_value();
		e.end_method();
	}

	/* generates:
		 } catch (RuntimeException e) {
			 throw e;
		 } catch (Error e) {
			 throw e;
		 } catch (<DeclaredException> e) {
			 throw e;
		 } catch (Throwable e) {
			 throw new <Wrapper>(e);
		 }
	*/
	public static void wrap_undeclared_throwable(CodeEmitter e, Block handler, Type[] exceptions, Type wrapper) {
		Set set = (exceptions == null) ? Collections.EMPTY_SET : new HashSet(Arrays.asList(exceptions));

		if (set.contains(AsmConsts.TYPE_THROWABLE))
			return;

		boolean needThrow = exceptions != null;
		if (!set.contains(AsmConsts.TYPE_RUNTIME_EXCEPTION)) {
			e.catch_exception(handler, AsmConsts.TYPE_RUNTIME_EXCEPTION);
			needThrow = true;
		}
		if (!set.contains(AsmConsts.TYPE_ERROR)) {
			e.catch_exception(handler, AsmConsts.TYPE_ERROR);
			needThrow = true;
		}
		if (exceptions != null) {
			for (int i = 0; i < exceptions.length; i++) {
				e.catch_exception(handler, exceptions[i]);
			}
		}
		if (needThrow) {
			e.athrow();
		}
		// e -> eo -> oeo -> ooe -> o
		e.catch_exception(handler, AsmConsts.TYPE_THROWABLE);
		e.new_instance(wrapper);
		e.dup_x1();
		e.swap();
		e.invoke_constructor(wrapper, CSTRUCT_THROWABLE);
		e.athrow();
	}

	public static CodeEmitter begin_method(ClassEmitter e, MethodInfo method) {
		return begin_method(e, method, method.getModifiers());
	}

	public static CodeEmitter begin_method(ClassEmitter e, MethodInfo method, int access) {
		return e.begin_method(access,
			method.getSignature(),
			method.getExceptionTypes());
	}
}
