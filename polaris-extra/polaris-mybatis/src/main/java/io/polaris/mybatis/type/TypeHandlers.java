package io.polaris.mybatis.type;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * @author Qt
 * @since Sep 30, 2025
 */
public class TypeHandlers {


	public static void registerAll(Configuration configuration) {
		registerAll(configuration.getTypeHandlerRegistry());
		registerAll(configuration.getTypeAliasRegistry());
	}

	public static void registerAll(TypeHandlerRegistry typeHandlerRegistry) {
		typeHandlerRegistry.register(ArrayTypeHandler.class);
		typeHandlerRegistry.register(BigDecimalArrayTypeHandler.class);
		typeHandlerRegistry.register(BooleanArrayTypeHandler.class);
		typeHandlerRegistry.register(BooleanObjectArrayTypeHandler.class);
		typeHandlerRegistry.register(CharacterArrayTypeHandler.class);
		typeHandlerRegistry.register(CharacterObjectArrayTypeHandler.class);
		typeHandlerRegistry.register(DoubleArrayTypeHandler.class);
		typeHandlerRegistry.register(DoubleObjectArrayTypeHandler.class);
		typeHandlerRegistry.register(FloatArrayTypeHandler.class);
		typeHandlerRegistry.register(FloatObjectArrayTypeHandler.class);
		typeHandlerRegistry.register(IntegerArrayTypeHandler.class);
		typeHandlerRegistry.register(IntegerObjectArrayTypeHandler.class);
		typeHandlerRegistry.register(LongArrayTypeHandler.class);
		typeHandlerRegistry.register(LongObjectArrayTypeHandler.class);
		typeHandlerRegistry.register(ShortArrayTypeHandler.class);
		typeHandlerRegistry.register(ShortObjectArrayTypeHandler.class);
		typeHandlerRegistry.register(StringArrayTypeHandler.class);

		typeHandlerRegistry.register(DynamicDateTypeHandler.class);
		typeHandlerRegistry.register(DynamicTimestampTypeHandler.class);
		typeHandlerRegistry.register(DynamicTimeTypeHandler.class);

		typeHandlerRegistry.setDefaultEnumTypeHandler(DynamicEnumTypeHandler.class);
	}

	public static void registerAll(TypeAliasRegistry typeAliasRegistry) {
		typeAliasRegistry.registerAlias(BlankableEnumOrdinalTypeHandler.class);
		typeAliasRegistry.registerAlias(BlankableEnumTypeHandler.class);
		typeAliasRegistry.registerAlias(DynamicBooleanTypeHandler.class);
		typeAliasRegistry.registerAlias(DynamicDateTypeHandler.class);
		typeAliasRegistry.registerAlias(DynamicEnumOrdinalTypeHandler.class);
		typeAliasRegistry.registerAlias(DynamicEnumTypeHandler.class);
		typeAliasRegistry.registerAlias(DynamicEnumTypeHandler.class);
		typeAliasRegistry.registerAlias(DynamicTimestampTypeHandler.class);
		typeAliasRegistry.registerAlias(DynamicTimeTypeHandler.class);
	}
}
