package io.polaris.core.asm.reflect;

import io.polaris.core.asm.internal.AsmUtils;
import io.polaris.core.collection.Iterables;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

/**
 * ASM 字段访问器
 * <p>通过 ASM 字节码生成技术，为 Java 字段提供高性能的直接访问能力</p>
 * <p>支持通过索引快速读取和设置字段值，包括所有基本类型和对象类型</p>
 * <p>参考：<a href="https://github.com/EsotericSoftware/reflectasm">https://github.com/EsotericSoftware/reflectasm</a></p>
 *
 * @author Qt
 * @see <a href="https://github.com/EsotericSoftware/reflectasm">https://github.com/EsotericSoftware/reflectasm</a>
 * @since Aug 04, 2023
 */
@SuppressWarnings("rawtypes")
public abstract class FieldAccess {
	/**
	 * 访问类缓存池
	 */
	private static final AccessClassPool<Class, FieldAccess> pool = new AccessClassPool<>();
	/**
	 * 字段名称数组
	 */
	private String[] fieldNames;
	/**
	 * 字段类型数组
	 */
	private Class[] fieldTypes;
	/**
	 * 字段对象数组
	 */
	private Field[] fields;
	/**
	 * 字段泛型类型数组
	 */
	private java.lang.reflect.Type[] fieldGenericTypes;

	/**
	 * 获取指定名称字段的索引
	 *
	 * @param fieldName 字段名称
	 * @return 字段索引
	 * @throws IllegalArgumentException 当字段不存在时抛出
	 */
	public int getIndex(String fieldName) {
		for (int i = 0, n = fieldNames.length; i < n; i++) {
			if (fieldNames[i].equals(fieldName)) {
				return i;
			}
		}
		throw new IllegalArgumentException("Unable to find non-private field: " + fieldName);
	}

	/**
	 * 获取指定字段对象的索引
	 *
	 * @param field 字段对象
	 * @return 字段索引
	 * @throws IllegalArgumentException 当字段不存在时抛出
	 */
	public int getIndex(Field field) {
		for (int i = 0, n = fields.length; i < n; i++) {
			if (fields[i].equals(field)) {
				return i;
			}
		}
		throw new IllegalArgumentException("Unable to find non-private field: " + field);
	}

	/**
	 * 设置字段值（对象类型）
	 *
	 * @param instance 目标实例
	 * @param fieldName 字段名称
	 * @param value 新值
	 */
	public void set(Object instance, String fieldName, Object value) {
		set(instance, getIndex(fieldName), value);
	}

	/**
	 * 获取字段值（对象类型）
	 *
	 * @param instance 目标实例
	 * @param fieldName 字段名称
	 * @return 字段值
	 */
	public Object get(Object instance, String fieldName) {
		return get(instance, getIndex(fieldName));
	}

	/**
	 * 获取所有字段名称列表
	 *
	 * @return 字段名称列表
	 */
	public List<String> getFieldNames() {
		return Iterables.asList(fieldNames) ;
	}

	/**
	 * 获取所有字段类型列表
	 *
	 * @return 字段类型列表
	 */
	public List<Class> getFieldTypes() {
		return Iterables.asList(fieldTypes);
	}

	/**
	 * 获取所有字段泛型类型列表
	 *
	 * @return 字段泛型类型列表
	 */
	public List<java.lang.reflect.Type> getFieldGenericTypes() {
		return Iterables.asList(fieldGenericTypes);
	}

	/**
	 * 获取字段数量
	 *
	 * @return 字段数量
	 */
	public int getFieldCount() {
		return fieldTypes.length;
	}

	/**
	 * 获取所有字段对象列表
	 *
	 * @return 字段对象列表
	 */
	public List<Field> getFields() {
		return Iterables.asList(fields);
	}

	/**
	 * 设置字段值（索引方式，对象类型）
	 * <p>子类必须实现此方法以提供具体的字段设置逻辑</p>
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @param value 新值
	 */
	abstract public void set(Object instance, int fieldIndex, Object value);

	/**
	 * 设置 boolean 类型字段值
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @param value 新值
	 */
	abstract public void setBoolean(Object instance, int fieldIndex, boolean value);

	/**
	 * 设置 byte 类型字段值
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @param value 新值
	 */
	abstract public void setByte(Object instance, int fieldIndex, byte value);

	/**
	 * 设置 short 类型字段值
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @param value 新值
	 */
	abstract public void setShort(Object instance, int fieldIndex, short value);

	/**
	 * 设置 int 类型字段值
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @param value 新值
	 */
	abstract public void setInt(Object instance, int fieldIndex, int value);

	/**
	 * 设置 long 类型字段值
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @param value 新值
	 */
	abstract public void setLong(Object instance, int fieldIndex, long value);

	/**
	 * 设置 double 类型字段值
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @param value 新值
	 */
	abstract public void setDouble(Object instance, int fieldIndex, double value);

	/**
	 * 设置 float 类型字段值
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @param value 新值
	 */
	abstract public void setFloat(Object instance, int fieldIndex, float value);

	/**
	 * 设置 char 类型字段值
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @param value 新值
	 */
	abstract public void setChar(Object instance, int fieldIndex, char value);

	/**
	 * 获取字段值（索引方式，对象类型）
	 * <p>子类必须实现此方法以提供具体的字段获取逻辑</p>
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @return 字段值
	 */
	abstract public Object get(Object instance, int fieldIndex);

	/**
	 * 获取 String 类型字段值
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @return 字段值
	 */
	abstract public String getString(Object instance, int fieldIndex);

	/**
	 * 获取 char 类型字段值
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @return 字段值
	 */
	abstract public char getChar(Object instance, int fieldIndex);

	/**
	 * 获取 boolean 类型字段值
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @return 字段值
	 */
	abstract public boolean getBoolean(Object instance, int fieldIndex);

	/**
	 * 获取 byte 类型字段值
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @return 字段值
	 */
	abstract public byte getByte(Object instance, int fieldIndex);

	/**
	 * 获取 short 类型字段值
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @return 字段值
	 */
	abstract public short getShort(Object instance, int fieldIndex);

	/**
	 * 获取 int 类型字段值
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @return 字段值
	 */
	abstract public int getInt(Object instance, int fieldIndex);

	/**
	 * 获取 long 类型字段值
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @return 字段值
	 */
	abstract public long getLong(Object instance, int fieldIndex);

	/**
	 * 获取 double 类型字段值
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @return 字段值
	 */
	abstract public double getDouble(Object instance, int fieldIndex);

	/**
	 * 获取 float 类型字段值
	 *
	 * @param instance 目标实例
	 * @param fieldIndex 字段索引
	 * @return 字段值
	 */
	abstract public float getFloat(Object instance, int fieldIndex);

	/**
	 * 获取或创建字段访问器实例
	 *
	 * @param type 要访问的类型
	 * @return 字段访问器实例
	 */
	public static FieldAccess get(Class type){
		return pool.computeIfAbsent(type, FieldAccess::create);
	}

	/** @param type Must not be the Object class, an interface, a primitive type, or void. */
	public static FieldAccess create(Class type) {
		if (type.getSuperclass() == null) {
			throw new IllegalArgumentException("类型不能为Object、interface、原始类型或void");
		}

		ArrayList<Field> fields = new ArrayList<>();
		Class nextClass = type;
		while (nextClass != null && nextClass != Object.class) {
			Field[] declaredFields = nextClass.getDeclaredFields();
			for (int i = 0, n = declaredFields.length; i < n; i++) {
				Field field = declaredFields[i];
				int modifiers = field.getModifiers();
				if (Modifier.isStatic(modifiers)) {
					continue;
				}
				if (Modifier.isPrivate(modifiers)) {
					continue;
				}
				fields.add(field);
			}
			nextClass = nextClass.getSuperclass();
		}

		String[] fieldNames = new String[fields.size()];
		Class[] fieldTypes = new Class[fields.size()];
		java.lang.reflect.Type[] fieldGenericTypes = new Class[fields.size()];
		for (int i = 0, n = fieldNames.length; i < n; i++) {
			fieldNames[i] = fields.get(i).getName();
			fieldTypes[i] = fields.get(i).getType();
			fieldGenericTypes[i] = fields.get(i).getGenericType();
		}

		String accessClassName = AccessClassLoader.buildAccessClassName(type, FieldAccess.class);

		Class accessClass;
		AccessClassLoader loader = AccessClassLoader.get(type);
		synchronized (loader) {
			accessClass = loader.loadAccessClass(accessClassName);
			if (accessClass == null) {
				String accessClassNameInternal = accessClassName.replace('.', '/');
				String classNameInternal = type.getName().replace('.', '/');

				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
				cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal, null,
					FieldAccess.class.getName().replace('.', '/'),
					null);
				insertConstructor(cw);
				insertGetObject(cw, classNameInternal, fields);
				insertSetObject(cw, classNameInternal, fields);
				insertGetPrimitive(cw, classNameInternal, fields, Type.BOOLEAN_TYPE);
				insertSetPrimitive(cw, classNameInternal, fields, Type.BOOLEAN_TYPE);
				insertGetPrimitive(cw, classNameInternal, fields, Type.BYTE_TYPE);
				insertSetPrimitive(cw, classNameInternal, fields, Type.BYTE_TYPE);
				insertGetPrimitive(cw, classNameInternal, fields, Type.SHORT_TYPE);
				insertSetPrimitive(cw, classNameInternal, fields, Type.SHORT_TYPE);
				insertGetPrimitive(cw, classNameInternal, fields, Type.INT_TYPE);
				insertSetPrimitive(cw, classNameInternal, fields, Type.INT_TYPE);
				insertGetPrimitive(cw, classNameInternal, fields, Type.LONG_TYPE);
				insertSetPrimitive(cw, classNameInternal, fields, Type.LONG_TYPE);
				insertGetPrimitive(cw, classNameInternal, fields, Type.DOUBLE_TYPE);
				insertSetPrimitive(cw, classNameInternal, fields, Type.DOUBLE_TYPE);
				insertGetPrimitive(cw, classNameInternal, fields, Type.FLOAT_TYPE);
				insertSetPrimitive(cw, classNameInternal, fields, Type.FLOAT_TYPE);
				insertGetPrimitive(cw, classNameInternal, fields, Type.CHAR_TYPE);
				insertSetPrimitive(cw, classNameInternal, fields, Type.CHAR_TYPE);
				insertGetString(cw, classNameInternal, fields);
				cw.visitEnd();
				accessClass = loader.defineAccessClass(accessClassName, cw.toByteArray());
			}
		}
		try {
			FieldAccess access = (FieldAccess) accessClass.newInstance();
			access.fieldNames = fieldNames;
			access.fieldTypes = fieldTypes;
			access.fieldGenericTypes = fieldGenericTypes;
			access.fields = fields.toArray(new Field[fields.size()]);
			return access;
		} catch (Throwable t) {
			throw new IllegalStateException("创建访问类失败: " + accessClassName, t);
		}
	}

	static private void insertConstructor(ClassWriter cw) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, FieldAccess.class.getName().replace('.', '/'), "<init>", "()V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	static private void insertSetObject(ClassWriter cw, String classNameInternal, ArrayList<Field> fields) {
		int maxStack = 6;
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "set", "(Ljava/lang/Object;ILjava/lang/Object;)V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ILOAD, 2);

		if (!fields.isEmpty()) {
			maxStack--;
			Label[] labels = new Label[fields.size()];
			for (int i = 0, n = labels.length; i < n; i++) {
				labels[i] = new Label();
			}
			Label defaultLabel = new Label();
			mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

			for (int i = 0, n = labels.length; i < n; i++) {
				Field field = fields.get(i);
				Type fieldType = Type.getType(field.getType());

				mv.visitLabel(labels[i]);
				mv.visitFrame(F_SAME, 0, null, 0, null);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitTypeInsn(CHECKCAST, classNameInternal);
				mv.visitVarInsn(ALOAD, 3);

				AsmUtils.autoUnBoxing(mv, fieldType);

				mv.visitFieldInsn(PUTFIELD, field.getDeclaringClass().getName().replace('.', '/'), field.getName(),
					fieldType.getDescriptor());
				mv.visitInsn(RETURN);
			}

			mv.visitLabel(defaultLabel);
			mv.visitFrame(F_SAME, 0, null, 0, null);
		}
		mv = insertThrowExceptionForFieldNotFound(mv);
		mv.visitMaxs(maxStack, 4);
		mv.visitEnd();
	}

	static private void insertGetObject(ClassWriter cw, String classNameInternal, ArrayList<Field> fields) {
		int maxStack = 6;
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "get", "(Ljava/lang/Object;I)Ljava/lang/Object;", null, null);
		mv.visitCode();
		mv.visitVarInsn(ILOAD, 2);

		if (!fields.isEmpty()) {
			maxStack--;
			Label[] labels = new Label[fields.size()];
			for (int i = 0, n = labels.length; i < n; i++) {
				labels[i] = new Label();
			}
			Label defaultLabel = new Label();
			mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

			for (int i = 0, n = labels.length; i < n; i++) {
				Field field = fields.get(i);

				mv.visitLabel(labels[i]);
				mv.visitFrame(F_SAME, 0, null, 0, null);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitTypeInsn(CHECKCAST, classNameInternal);
				mv.visitFieldInsn(GETFIELD, field.getDeclaringClass().getName().replace('.', '/'), field.getName(),
					Type.getDescriptor(field.getType()));

				Type fieldType = Type.getType(field.getType());
				AsmUtils.autoBoxing(mv, fieldType);

				mv.visitInsn(ARETURN);
			}

			mv.visitLabel(defaultLabel);
			mv.visitFrame(F_SAME, 0, null, 0, null);
		}
		insertThrowExceptionForFieldNotFound(mv);
		mv.visitMaxs(maxStack, 3);
		mv.visitEnd();
	}

	static private void insertGetString(ClassWriter cw, String classNameInternal, ArrayList<Field> fields) {
		int maxStack = 6;
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "getString", "(Ljava/lang/Object;I)Ljava/lang/String;", null, null);
		mv.visitCode();
		mv.visitVarInsn(ILOAD, 2);

		if (!fields.isEmpty()) {
			maxStack--;
			Label[] labels = new Label[fields.size()];
			Label labelForInvalidTypes = new Label();
			boolean hasAnyBadTypeLabel = false;
			for (int i = 0, n = labels.length; i < n; i++) {
				if (fields.get(i).getType().equals(String.class)) {
					labels[i] = new Label();
				} else {
					labels[i] = labelForInvalidTypes;
					hasAnyBadTypeLabel = true;
				}
			}
			Label defaultLabel = new Label();
			mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

			for (int i = 0, n = labels.length; i < n; i++) {
				if (!labels[i].equals(labelForInvalidTypes)) {
					Field field = fields.get(i);
					mv.visitLabel(labels[i]);
					mv.visitFrame(F_SAME, 0, null, 0, null);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitTypeInsn(CHECKCAST, classNameInternal);
					mv.visitFieldInsn(GETFIELD, field.getDeclaringClass().getName().replace('.', '/'), field.getName(),
						"Ljava/lang/String;");
					mv.visitInsn(ARETURN);
				}
			}
			// Rest of fields: different type
			if (hasAnyBadTypeLabel) {
				mv.visitLabel(labelForInvalidTypes);
				mv.visitFrame(F_SAME, 0, null, 0, null);
				insertThrowExceptionForFieldType(mv, "String");
			}
			// Default: field not found
			mv.visitLabel(defaultLabel);
			mv.visitFrame(F_SAME, 0, null, 0, null);
		}
		insertThrowExceptionForFieldNotFound(mv);
		mv.visitMaxs(maxStack, 3);
		mv.visitEnd();
	}

	static private void insertSetPrimitive(ClassWriter cw, String classNameInternal, ArrayList<Field> fields,
										   Type primitiveType) {
		int maxStack = 6;
		int maxLocals = 4; // See correction below for LLOAD and DLOAD
		final String setterMethodName;
		final String typeNameInternal = primitiveType.getDescriptor();
		final int loadValueInstruction;
		switch (primitiveType.getSort()) {
			case Type.BOOLEAN:
				setterMethodName = "setBoolean";
				loadValueInstruction = ILOAD;
				break;
			case Type.BYTE:
				setterMethodName = "setByte";
				loadValueInstruction = ILOAD;
				break;
			case Type.CHAR:
				setterMethodName = "setChar";
				loadValueInstruction = ILOAD;
				break;
			case Type.SHORT:
				setterMethodName = "setShort";
				loadValueInstruction = ILOAD;
				break;
			case Type.INT:
				setterMethodName = "setInt";
				loadValueInstruction = ILOAD;
				break;
			case Type.FLOAT:
				setterMethodName = "setFloat";
				loadValueInstruction = FLOAD;
				break;
			case Type.LONG:
				setterMethodName = "setLong";
				loadValueInstruction = LLOAD;
				maxLocals++; // (LLOAD and DLOAD actually load two slots)
				break;
			case Type.DOUBLE:
				setterMethodName = "setDouble";
				loadValueInstruction = DLOAD;
				maxLocals++; // (LLOAD and DLOAD actually load two slots)
				break;
			default:
				setterMethodName = "set";
				loadValueInstruction = ALOAD;
				break;
		}
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, setterMethodName, "(Ljava/lang/Object;I" + typeNameInternal + ")V", null,
			null);
		mv.visitCode();
		mv.visitVarInsn(ILOAD, 2);

		if (!fields.isEmpty()) {
			maxStack--;
			Label[] labels = new Label[fields.size()];
			Label labelForInvalidTypes = new Label();
			boolean hasAnyBadTypeLabel = false;
			for (int i = 0, n = labels.length; i < n; i++) {
				if (Type.getType(fields.get(i).getType()).equals(primitiveType)) {
					labels[i] = new Label();
				} else {
					labels[i] = labelForInvalidTypes;
					hasAnyBadTypeLabel = true;
				}
			}
			Label defaultLabel = new Label();
			mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

			for (int i = 0, n = labels.length; i < n; i++) {
				if (!labels[i].equals(labelForInvalidTypes)) {
					Field field = fields.get(i);
					mv.visitLabel(labels[i]);
					mv.visitFrame(F_SAME, 0, null, 0, null);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitTypeInsn(CHECKCAST, classNameInternal);
					mv.visitVarInsn(loadValueInstruction, 3);
					mv.visitFieldInsn(PUTFIELD, field.getDeclaringClass().getName().replace('.', '/'), field.getName(),
						typeNameInternal);
					mv.visitInsn(RETURN);
				}
			}
			// Rest of fields: different type
			if (hasAnyBadTypeLabel) {
				mv.visitLabel(labelForInvalidTypes);
				mv.visitFrame(F_SAME, 0, null, 0, null);
				insertThrowExceptionForFieldType(mv, primitiveType.getClassName());
			}
			// Default: field not found
			mv.visitLabel(defaultLabel);
			mv.visitFrame(F_SAME, 0, null, 0, null);
		}
		mv = insertThrowExceptionForFieldNotFound(mv);
		mv.visitMaxs(maxStack, maxLocals);
		mv.visitEnd();
	}

	static private void insertGetPrimitive(ClassWriter cw, String classNameInternal, ArrayList<Field> fields,
										   Type primitiveType) {
		int maxStack = 6;
		final String getterMethodName;
		final String typeNameInternal = primitiveType.getDescriptor();
		final int returnValueInstruction;
		switch (primitiveType.getSort()) {
			case Type.BOOLEAN:
				getterMethodName = "getBoolean";
				returnValueInstruction = IRETURN;
				break;
			case Type.BYTE:
				getterMethodName = "getByte";
				returnValueInstruction = IRETURN;
				break;
			case Type.CHAR:
				getterMethodName = "getChar";
				returnValueInstruction = IRETURN;
				break;
			case Type.SHORT:
				getterMethodName = "getShort";
				returnValueInstruction = IRETURN;
				break;
			case Type.INT:
				getterMethodName = "getInt";
				returnValueInstruction = IRETURN;
				break;
			case Type.FLOAT:
				getterMethodName = "getFloat";
				returnValueInstruction = FRETURN;
				break;
			case Type.LONG:
				getterMethodName = "getLong";
				returnValueInstruction = LRETURN;
				break;
			case Type.DOUBLE:
				getterMethodName = "getDouble";
				returnValueInstruction = DRETURN;
				break;
			default:
				getterMethodName = "get";
				returnValueInstruction = ARETURN;
				break;
		}
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, getterMethodName, "(Ljava/lang/Object;I)" + typeNameInternal, null, null);
		mv.visitCode();
		mv.visitVarInsn(ILOAD, 2);

		if (!fields.isEmpty()) {
			maxStack--;
			Label[] labels = new Label[fields.size()];
			Label labelForInvalidTypes = new Label();
			boolean hasAnyBadTypeLabel = false;
			for (int i = 0, n = labels.length; i < n; i++) {
				if (Type.getType(fields.get(i).getType()).equals(primitiveType)) {
					labels[i] = new Label();
				} else {
					labels[i] = labelForInvalidTypes;
					hasAnyBadTypeLabel = true;
				}
			}
			Label defaultLabel = new Label();
			mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

			for (int i = 0, n = labels.length; i < n; i++) {
				Field field = fields.get(i);
				if (!labels[i].equals(labelForInvalidTypes)) {
					mv.visitLabel(labels[i]);
					mv.visitFrame(F_SAME, 0, null, 0, null);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitTypeInsn(CHECKCAST, classNameInternal);
					mv.visitFieldInsn(GETFIELD, field.getDeclaringClass().getName().replace('.', '/'), field.getName(),
						typeNameInternal);
					mv.visitInsn(returnValueInstruction);
				}
			}
			// Rest of fields: different type
			if (hasAnyBadTypeLabel) {
				mv.visitLabel(labelForInvalidTypes);
				mv.visitFrame(F_SAME, 0, null, 0, null);
				insertThrowExceptionForFieldType(mv, primitiveType.getClassName());
			}
			// Default: field not found
			mv.visitLabel(defaultLabel);
			mv.visitFrame(F_SAME, 0, null, 0, null);
		}
		mv = insertThrowExceptionForFieldNotFound(mv);
		mv.visitMaxs(maxStack, 3);
		mv.visitEnd();
	}

	static private MethodVisitor insertThrowExceptionForFieldNotFound(MethodVisitor mv) {
		mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
		mv.visitInsn(DUP);
		mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
		mv.visitInsn(DUP);
		mv.visitLdcInsn("Field not found: ");
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
		mv.visitVarInsn(ILOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
		mv.visitInsn(ATHROW);
		return mv;
	}

	static private MethodVisitor insertThrowExceptionForFieldType(MethodVisitor mv, String fieldType) {
		mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
		mv.visitInsn(DUP);
		mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
		mv.visitInsn(DUP);
		mv.visitLdcInsn("Field not declared as " + fieldType + ": ");
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
		mv.visitVarInsn(ILOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
		mv.visitInsn(ATHROW);
		return mv;
	}

}
