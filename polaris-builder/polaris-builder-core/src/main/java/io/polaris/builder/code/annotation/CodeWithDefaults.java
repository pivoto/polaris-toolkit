package io.polaris.builder.code.annotation;

import org.springframework.core.annotation.AliasFor;

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
	@Property(key = "tunnelPackage", value = "tunnel"),
	@Property(key = "entityPackage", value = "entity"),
	@Property(key = "modelPackage", value = "model"),
	@Property(key = "mapperPackage", value = "mapper"),
	@Property(key = "servicePackage", value = "service"),
	@Property(key = "domainPackage", value = "domain"),
	@Property(key = "implPackage", value = "impl"),
	@Property(key = "rpcPackage", value = "rpc"),
	@Property(key = "controllerPackage", value = "controller"),
	@Property(key = "entityClassSuffix", value = "Entity"),
	@Property(key = "mapperClassSuffix", value = "Mapper"),
	@Property(key = "serviceClassSuffix", value = "Service"),
	@Property(key = "domainClassSuffix", value = "Domain"),
	@Property(key = "controllerClassSuffix", value = "Controller"),
	@Property(key = "rpcClassSuffix", value = "Rpc"),
	@Property(key = "rpcDmlInputClassSuffix", value = "DmlInput"),
	@Property(key = "rpcGetInputClassSuffix", value = "GetInput"),
	@Property(key = "rpcGetOutputClassSuffix", value = "GetOutput"),
	@Property(key = "rpcListInputClassSuffix", value = "ListInput"),
	@Property(key = "rpcListOutputClassSuffix", value = "ListOutput"),
	@Property(key = "entityDtoClassSuffix", value = "Dto"),
	@Property(key = "rpcInlineClientClassSuffix", value = "RpcInlineClient"),
	@Property(key = "rpcClientClassSuffix", value = "RpcClient"),
	@Property(key = "rpcClientDmlInputClassSuffix", value = "DmlClientInput"),
	@Property(key = "rpcClientGetInputClassSuffix", value = "GetClientInput"),
	@Property(key = "rpcClientGetOutputClassSuffix", value = "GetClientOutput"),
	@Property(key = "rpcClientListInputClassSuffix", value = "ListClientInput"),
	@Property(key = "rpcClientListOutputClassSuffix", value = "ListClientOutput"),
	@Property(key = "mapperDir", value = "mapper"),
	@Property(key = "webDir", value = "src/main/resources/META-INF/resources"),
	@Property(key = "frontendDir", value = "src/frontend"),
	@Property(key = "rpcExportable", value = "true"),
	@Property(key = "rpcImportable", value = "true"),
})
@DefaultTemplate({
	@Template(path = VM_PATH_ENTITY, filename = FILENAME_ENTITY, dirname = DIRNAME_ENTITY),

	@Template(path = VM_PATH_MAPPER, filename = FILENAME_MAPPER, dirname = DIRNAME_MAPPER),
//	@Template(path = VM_PATH_MAPPER_XML, filename = FILENAME_MAPPER_XML, dirname = DIRNAME_MAPPER_XML),

	@Template(path = VM_PATH_SERVICE, filename = FILENAME_SERVICE, dirname = DIRNAME_SERVICE),

	@Template(path = VM_PATH_DOMAIN_SERVICE, filename = FILENAME_DOMAIN_SERVICE, dirname = DIRNAME_DOMAIN_SERVICE),

	@Template(path = VM_PATH_RPC, filename = FILENAME_RPC, dirname = DIRNAME_RPC),
	@Template(path = VM_PATH_RPC_DML_INPUT, filename = FILENAME_RPC_DML_INPUT, dirname = DIRNAME_MODEL),
	@Template(path = VM_PATH_RPC_GET_INPUT, filename = FILENAME_RPC_GET_INPUT, dirname = DIRNAME_MODEL),
	@Template(path = VM_PATH_RPC_LIST_INPUT, filename = FILENAME_RPC_LIST_INPUT, dirname = DIRNAME_MODEL),
	@Template(path = VM_PATH_RPC_GET_OUTPUT, filename = FILENAME_RPC_GET_OUTPUT, dirname = DIRNAME_MODEL),
	@Template(path = VM_PATH_RPC_LIST_OUTPUT, filename = FILENAME_RPC_LIST_OUTPUT, dirname = DIRNAME_MODEL),

	@Template(path = VM_PATH_RPC_CLIENT_INLINE, filename = FILENAME_RPC_CLIENT_INLINE, dirname = DIRNAME_RPC_CLIENT),
	@Template(path = VM_PATH_RPC_CLIENT, filename = FILENAME_RPC_CLIENT, dirname = DIRNAME_RPC_CLIENT),
	@Template(path = VM_PATH_RPC_CLIENT_DTO, filename = FILENAME_RPC_CLIENT_DTO, dirname = DIRNAME_MODEL_CLIENT),
	@Template(path = VM_PATH_RPC_CLIENT_DML_INPUT, filename = FILENAME_RPC_CLIENT_DML_INPUT, dirname = DIRNAME_MODEL_CLIENT),
	@Template(path = VM_PATH_RPC_CLIENT_GET_INPUT, filename = FILENAME_RPC_CLIENT_GET_INPUT, dirname = DIRNAME_MODEL_CLIENT),
	@Template(path = VM_PATH_RPC_CLIENT_LIST_INPUT, filename = FILENAME_RPC_CLIENT_LIST_INPUT, dirname = DIRNAME_MODEL_CLIENT),
	@Template(path = VM_PATH_RPC_CLIENT_GET_OUTPUT, filename = FILENAME_RPC_CLIENT_GET_OUTPUT, dirname = DIRNAME_MODEL_CLIENT),
	@Template(path = VM_PATH_RPC_CLIENT_LIST_OUTPUT, filename = FILENAME_RPC_CLIENT_LIST_OUTPUT, dirname = DIRNAME_MODEL_CLIENT),

	@Template(path = VM_PATH_FRONTEND_API, filename = FILENAME_FRONTEND_API, dirname = DIRNAME_FRONTEND),
	@Template(path = VM_PATH_FRONTEND_ROUTES, filename = FILENAME_FRONTEND_ROUTES, dirname = DIRNAME_FRONTEND),
	@Template(path = VM_PATH_FRONTEND_LIST_PAGE, filename = FILENAME_FRONTEND_LIST_PAGE, dirname = DIRNAME_FRONTEND),
	@Template(path = VM_PATH_FRONTEND_ADD_PAGE, filename = FILENAME_FRONTEND_ADD_PAGE, dirname = DIRNAME_FRONTEND),
	@Template(path = VM_PATH_FRONTEND_EDIT_PAGE, filename = FILENAME_FRONTEND_EDIT_PAGE, dirname = DIRNAME_FRONTEND),
})
@DefaultMapping({
	@Mapping(jdbcType = "TIMESTAMP", javaType = "java.util.Date")
})
@DefaultTemplateExcludedPaths({})
@DefaultTemplateAdditional({})
@CodeConfiguration(
	jdbcUrl = "",
	jdbcUsername = "",
	jdbcPassword = ""
)
public @interface CodeWithDefaults {

	/**
	 * 代码生成器的输出日志输出到标准输出流
	 */
	@AliasFor(annotation = CodeConfiguration.class, value = "logWithStd")
	boolean logWithStd() default true;

	/**
	 * 生成文件的输出目录
	 */
	@AliasFor(annotation = CodeConfiguration.class, value = "outDir")
	String outDir() default ".";

	/**
	 * 模板全局属性配置，优先高于默认配置
	 */
	@AliasFor(annotation = CodeConfiguration.class, value = "property")
	Property[] property() default {};

	/**
	 * Jdbc类型与Java类型的自定义映射
	 */
	@AliasFor(annotation = CodeConfiguration.class, value = "mapping")
	Mapping[] mapping() default {};

	/**
	 * 需要截断的表名前缀
	 */
	@AliasFor(annotation = CodeConfiguration.class, value = "tablePrefix")
	String tablePrefix() default "_,t_,tbl_";

	/** 需要截断的表名后缀 */
	@AliasFor(annotation = CodeConfiguration.class, value = "tableSuffix")
	String tableSuffix() default "_,_bak,_tmp";

	/**
	 * 需要截断的列名前缀
	 */
	@AliasFor(annotation = CodeConfiguration.class, value = "columnPrefix")
	String columnPrefix() default "_";

	/**
	 * 需要截断的列名后缀
	 */
	@AliasFor(annotation = CodeConfiguration.class, value = "columnSuffix")
	String columnSuffix() default "_";

	/**
	 * 自定义模板配置，未配置时使用默认
	 */
	@AliasFor(annotation = CodeConfiguration.class, value = "templates")
	Template[] templates() default {};

	/**
	 * 附加的模板配置，在默认模板外追加别的模板，如使用自定义模板，则忽略此配置
	 */
	@AliasFor(annotation = DefaultTemplateAdditional.class, value = "value")
	Template[] additionalTemplates() default {};

	/**
	 * 排除默认模板中指定模板路径的使用，如使用自定义模板，则忽略此配置
	 */
	@AliasFor(annotation = DefaultTemplateExcludedPaths.class, value = "value")
	String[] excludeTemplatePaths() default {
		VM_PATH_FRONTEND_API,
		VM_PATH_FRONTEND_ROUTES,
		VM_PATH_FRONTEND_LIST_PAGE,
		VM_PATH_FRONTEND_ADD_PAGE,
		VM_PATH_FRONTEND_EDIT_PAGE,
	};

	/**
	 * Jdbc连接驱动
	 */
	@AliasFor(annotation = CodeConfiguration.class, value = "jdbcDriver")
	String jdbcDriver() default "";

	/**
	 * Jdbc连接URL
	 */
	@AliasFor(annotation = CodeConfiguration.class, value = "jdbcUrl")
	String jdbcUrl();

	/**
	 * Jdbc连接User
	 */
	@AliasFor(annotation = CodeConfiguration.class, value = "jdbcUsername")
	String jdbcUsername();

	/**
	 * Jdbc连接Password
	 */
	@AliasFor(annotation = CodeConfiguration.class, value = "jdbcPassword")
	String jdbcPassword();


	/**
	 * 需要生成代码的表名及其配置
	 */
	@AliasFor(annotation = CodeConfiguration.class, value = "tables")
	Table[] tables() default {};

	/** 需忽略的列名，支持正则表达式 */
	@AliasFor(annotation = CodeConfiguration.class, value = "ignoredColumns")
	String[] ignoredColumns() default {};
}
