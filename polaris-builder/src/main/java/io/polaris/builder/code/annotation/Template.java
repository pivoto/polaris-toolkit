package io.polaris.builder.code.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 * @since 1.8
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Template {

	String VM_PATH_EXAMPLE = "/vm/example.txt.vm";
	String VM_PATH_ENTITY = "/vm/java/base/entity/Entity.java.vm";
	String VM_PATH_ENTITY_FLUENT_MYBATIS = "/vm/java/base/entity/FluentMybatisEntity.java.vm";
	String VM_PATH_MAPPER = "/vm/java/base/mapper/Mapper.java.vm";
	String VM_PATH_SERVICE = "/vm/java/base/service/Service.java.vm";
	String VM_PATH_MAPPER_XML = "/vm/resources/Mapper.xml.vm";
	String VM_PATH_CONTROLLER = "/vm/java/facade/controller/Controller.java.vm";
	String VM_PATH_RPC = "/vm/java/facade/rpc/Rpc.java.vm";
	String VM_PATH_RPC_DML_INPUT = "/vm/java/facade/model/RpcDmlInput.java.vm";
	String VM_PATH_RPC_GET_INPUT = "/vm/java/facade/model/RpcGetInput.java.vm";
	String VM_PATH_RPC_LIST_INPUT = "/vm/java/facade/model/RpcListInput.java.vm";
	String VM_PATH_RPC_GET_OUTPUT = "/vm/java/facade/model/RpcGetOutput.java.vm";
	String VM_PATH_RPC_LIST_OUTPUT = "/vm/java/facade/model/RpcListOutput.java.vm";

	String VM_PATH_FRONTEND_ROUTES = "/vm/frontend/routes.ts.vm";
	String VM_PATH_FRONTEND_LIST_PAGE = "/vm/frontend/ListPage.vue.vm";
	String VM_PATH_FRONTEND_ADD_PAGE = "/vm/frontend/AddPage.vue.vm";
	String VM_PATH_FRONTEND_EDIT_PAGE = "/vm/frontend/EditPage.vue.vm";

	String FILENAME_EXAMPLE = "${table.javaClassName}.example.txt";
	String FILENAME_ENTITY = "${table.javaClassName}${env.entityClassSuffix}.java";
	String FILENAME_MAPPER = "${table.javaClassName}${env.mapperClassSuffix}.java";
	String FILENAME_SERVICE = "${table.javaClassName}${env.serviceClassSuffix}.java";
	String FILENAME_MAPPER_XML = "${table.javaClassName}${env.mapperClassSuffix}.xml";
	String FILENAME_CONTROLLER = "${table.javaClassName}${env.controllerClassSuffix}.java";
	String FILENAME_RPC = "${table.javaClassName}${env.rpcClassSuffix}.java";
	String FILENAME_RPC_DML_INPUT = "${table.javaClassName}${env.rpcDmlInputClassSuffix}.java";
	String FILENAME_RPC_GET_INPUT = "${table.javaClassName}${env.rpcGetInputClassSuffix}.java";
	String FILENAME_RPC_LIST_INPUT = "${table.javaClassName}${env.rpcListInputClassSuffix}.java";
	String FILENAME_RPC_GET_OUTPUT = "${table.javaClassName}${env.rpcGetOutputClassSuffix}.java";
	String FILENAME_RPC_LIST_OUTPUT = "${table.javaClassName}${env.rpcListOutputClassSuffix}.java";

	String FILENAME_FRONTEND_ROUTES = "routes.ts";
	String FILENAME_FRONTEND_LIST_PAGE = "List${table.javaClassName}.vue";
	String FILENAME_FRONTEND_ADD_PAGE = "Add${table.javaClassName}.vue";
	String FILENAME_FRONTEND_EDIT_PAGE = "Edit${table.javaClassName}.vue";

	String DIRNAME_EXAMPLE = "/";
	String DIRNAME_ENTITY = "${env.srcDir}/${table.javaPackageDir}/${env.basePackage.replace('.','/')}/${env.entityPackage.replace('.','/')}";
	String DIRNAME_MAPPER = "${env.srcDir}/${table.javaPackageDir}/${env.basePackage.replace('.','/')}/${env.mapperPackage.replace('.','/')}";
	String DIRNAME_SERVICE = "${env.srcDir}/${table.javaPackageDir}/${env.basePackage.replace('.','/')}/${env.servicePackage.replace('.','/')}";
	String DIRNAME_MAPPER_XML = "${env.resourceDir}/${env.mapperDir.replace('.','/')}";
	String DIRNAME_CONTROLLER = "${env.srcDir}/${table.javaPackageDir}/${env.facadePackage.replace('.','/')}/${env.controllerPackage.replace('.','/')}";
	String DIRNAME_RPC = "${env.srcDir}/${table.javaPackageDir}/${env.facadePackage.replace('.','/')}//${env.rpcPackage.replace('.','/')}";
	String DIRNAME_MODEL = "${env.srcDir}/${table.javaPackageDir}/${env.facadePackage.replace('.','/')}/${env.modelPackage.replace('.','/')}";

	String DIRNAME_FRONTEND = "${env.frontendDir}/${table.xmlName}";
	/**
	 * 模板路径，绝对路径或类路径的相对路径
	 */
	String path();

	/**
	 * 生成的文件名
	 */
	String filename();

	/**
	 * 生成文件的目录
	 */
	String dirname();


}
