package io.polaris.annotation.processing;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Qt
 * @since 1.8
 */
public abstract class BaseProcessor extends AbstractProcessor {

	public static final String PROPERTIES_PATH = "polaris-annotation-processor.properties";
	/** 用于在编译器打印消息的组件 */
	protected Messager messager;
	/** 语法树 */
	protected JavacTrees trees;
	/** 用来构造语法树节点 */
	protected TreeMaker treeMaker;
	/** 用于创建标识符的对象 */
	protected Names names;
	/** 用于将创建的类写入到文件 */
	protected Filer filer;
	protected Elements elements;
	protected Types types;
	protected Properties properties = new Properties();


	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		this.messager = processingEnv.getMessager();
		this.trees = JavacTrees.instance(processingEnv);
		Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
		this.treeMaker = TreeMaker.instance(context);
		this.names = Names.instance(context);
		this.filer = processingEnv.getFiler();
		this.elements = processingEnv.getElementUtils();
		this.types = processingEnv.getTypeUtils();
		super.init(processingEnv);
		try {
			FileObject fileObject = filer.getResource(StandardLocation.SOURCE_PATH, "", PROPERTIES_PATH);
			try (InputStream inputStream = fileObject.openInputStream();) {
				properties.load(inputStream);
			}
		} catch (Throwable ignore) {
		}
	}


}
