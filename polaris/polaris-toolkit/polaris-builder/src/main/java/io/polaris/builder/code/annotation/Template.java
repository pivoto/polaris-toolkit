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
	String VM_PATH_ENTITY = "/vm/java/Entity.java.vm";
	String VM_PATH_ENTITY_FLUENT_MYBATIS = "/vm/java/FluentMybatisEntity.java.vm";
	String VM_PATH_MAPPER = "/vm/java/Mapper.java.vm";
	String VM_PATH_SERVICE = "/vm/java/Service.java.vm";
	String VM_PATH_MAPPER_XML = "/vm/resources/Mapper.xml.vm";

	String FILENAME_EXAMPLE = "${table.javaClassName}.example.txt";
	String FILENAME_ENTITY = "${table.javaClassName}Entity.java";
	String FILENAME_MAPPER = "${table.javaClassName}Mapper.java";
	String FILENAME_SERVICE = "${table.javaClassName}Service.java";
	String FILENAME_MAPPER_XML = "${table.javaClassName}Mapper.xml";

	String DIRNAME_EXAMPLE = "/";
	String DIRNAME_ENTITY = "${env.srcDir}/${table.javaPackageDir}/${env.entityPackage}";
	String DIRNAME_MAPPER = "${env.srcDir}/${table.javaPackageDir}/${env.mapperPackage}";
	String DIRNAME_SERVICE = "${env.srcDir}/${table.javaPackageDir}/${env.servicePackage}";
	String DIRNAME_MAPPER_XML = "${env.resourceDir}/${env.mapperPackage}";

	String path();

	String filename();

	String dirname();


}
