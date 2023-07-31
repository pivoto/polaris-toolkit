package io.polaris.builder.code.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.polaris.builder.code.annotation.Template.*;

/**
 * @author Qt
 * @since 1.8
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DefaultProperty({
	@Property(key = "author", value = "${sys['user.name']}"),
	@Property(key = "srcDir", value = "src/main/java"),
	@Property(key = "resourceDir", value = "src/main/resources"),
	@Property(key = "basePackage", value = "base"),
	@Property(key = "facadePackage", value = "facade"),
	@Property(key = "entityPackage", value = "entity"),
	@Property(key = "modelPackage", value = "model"),
	@Property(key = "mapperPackage", value = "mapper"),
	@Property(key = "servicePackage", value = "service"),
	@Property(key = "implPackage", value = "impl"),
	@Property(key = "rpcPackage", value = "rpc"),
	@Property(key = "controllerPackage", value = "controller"),
	@Property(key = "entityClassSuffix", value = "Entity"),
	@Property(key = "mapperClassSuffix", value = "Mapper"),
	@Property(key = "serviceClassSuffix", value = "Service"),
	@Property(key = "controllerClassSuffix", value = "Controller"),
	@Property(key = "rpcClassSuffix", value = "Rpc"),
	@Property(key = "rpcDmlInputClassSuffix", value = "DmlInput"),
	@Property(key = "rpcGetInputClassSuffix", value = "GetInput"),
	@Property(key = "rpcGetOutputClassSuffix", value = "GetOutput"),
	@Property(key = "rpcListInputClassSuffix", value = "ListInput"),
	@Property(key = "rpcListOutputClassSuffix", value = "ListOutput"),
	@Property(key = "webDir", value = "src/main/resources/META-INF/resources"),
})
@DefaultTemplate({
	@Template(path = VM_PATH_ENTITY, filename = FILENAME_ENTITY, dirname = DIRNAME_ENTITY),

	@Template(path = VM_PATH_MAPPER, filename = FILENAME_MAPPER, dirname = DIRNAME_MAPPER),
	@Template(path = VM_PATH_MAPPER_XML, filename = FILENAME_MAPPER_XML, dirname = DIRNAME_MAPPER_XML),

	@Template(path = VM_PATH_SERVICE, filename = FILENAME_SERVICE, dirname = DIRNAME_SERVICE),

	@Template(path = VM_PATH_RPC, filename = FILENAME_RPC, dirname = DIRNAME_RPC),
	@Template(path = VM_PATH_RPC_DML_INPUT, filename = FILENAME_RPC_DML_INPUT, dirname = DIRNAME_MODEL),
	@Template(path = VM_PATH_RPC_GET_INPUT, filename = FILENAME_RPC_GET_INPUT, dirname = DIRNAME_MODEL),
	@Template(path = VM_PATH_RPC_LIST_INPUT, filename = FILENAME_RPC_LIST_INPUT, dirname = DIRNAME_MODEL),
	@Template(path = VM_PATH_RPC_GET_OUTPUT, filename = FILENAME_RPC_GET_OUTPUT, dirname = DIRNAME_MODEL),
	@Template(path = VM_PATH_RPC_LIST_OUTPUT, filename = FILENAME_RPC_LIST_OUTPUT, dirname = DIRNAME_MODEL),
})
@DefaultMapping({
	@Mapping(jdbcType = "TIMESTAMP", javaType = "java.util.Date")
})
public @interface Code {

	boolean logWithStd() default true;

	String outDir() default ".";

	Property[] property() default {};

	Mapping[] mapping() default {};

	String tablePrefix() default "_,t_,tbl_";

	String tableSuffix() default "_,_bak,_tmp";

	String columnPrefix() default "_";

	String columnSuffix() default "_";

	Template[] templates() default {};

	String jdbcDriver() default "";

	String jdbcUrl();

	String jdbcUsername();

	String jdbcPassword();


	Table[] tables() default {};
}
