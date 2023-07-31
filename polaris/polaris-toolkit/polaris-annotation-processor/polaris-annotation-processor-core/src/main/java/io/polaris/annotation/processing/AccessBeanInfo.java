package io.polaris.annotation.processing;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import lombok.Data;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Qt
 * @since 1.8
 */
@Data
public class AccessBeanInfo {
	private TypeElement element;
	private boolean accessFields = false;
	private boolean accessFluent = false;
	private boolean accessMap = false;
	private Set<String> excludeFieldSet;
	private Set<String> excludeSetterSet;
	private Set<String> excludeGetterSet;
	private TypeName beanTypeName;
	private ClassName beanClassName;
	private ClassName fluentClassName;
	private ClassName fieldsClassName;
	private ClassName mapClassName;
	private List<FieldInfo> fields = new ArrayList<>();

	public AccessBeanInfo(TypeElement element) {
		this.element = element;
		init();
	}

	private void init() {
		Access access = element.getAnnotation(Access.class);
		if (access == null) {
			return;
		}
		this.accessFields = access.fields();
		this.accessFluent = access.fluent();
		this.accessMap = access.map();

		excludeFieldSet = new HashSet<>();
		for (String s : access.excludeFields()) {
			excludeFieldSet.add(s);
		}
		excludeSetterSet = new HashSet<>();
		for (String s : access.excludeSetters()) {
			excludeSetterSet.add(s);
		}
		excludeGetterSet = new HashSet<>();
		for (String s : access.excludeGetters()) {
			excludeGetterSet.add(s);
		}
		String fluentSuffix = access.fluentSuffix();
		String fieldsSuffix = access.fieldsSuffix();
		String mapSuffix = access.mapSuffix();

		this.beanTypeName = TypeName.get(element.asType());
		this.beanClassName = ClassName.get(element);
		String simpleName =
			beanClassName.packageName().isEmpty() ? beanClassName.reflectionName()
				: beanClassName.reflectionName().substring(beanClassName.packageName().length() + 1);
		this.fluentClassName = ClassName.get(beanClassName.packageName(), simpleName + fluentSuffix);
		this.fieldsClassName = ClassName.get(beanClassName.packageName(), simpleName + fieldsSuffix);
		this.mapClassName = ClassName.get(beanClassName.packageName(), simpleName + mapSuffix);

		visitFieldElement(element);
	}

	private void visitFieldElement(TypeElement element) {
		Set<String> retrieved = new HashSet<>();
		while (!Object.class.getName().equals(element.toString())) {
			TypeName declaredTypeName = TypeName.get(element.asType());
			ClassName declaredClassName = ClassName.get(element);
			for (Element item : element.getEnclosedElements()) {
				if (item instanceof VariableElement) {
					VariableElement variableElement = (VariableElement) item;
					if (variableElement.getKind() == ElementKind.FIELD
						&& !variableElement.getModifiers().contains(Modifier.STATIC)) {
						String fieldName = variableElement.getSimpleName().toString();
						if (retrieved.contains(fieldName)) {
							continue;
						}
						retrieved.add(fieldName);
						TypeName typeName = TypeName.get(variableElement.asType());

						FieldInfo fieldInfo = new FieldInfo();
						fieldInfo.declaredTypeName = declaredTypeName;
						fieldInfo.declaredClassName = declaredClassName;
						fieldInfo.fieldName = fieldName;
						fieldInfo.typeName = typeName;
						fieldInfo.getterName = Utils.toGetterName(fieldName, typeName);
						fieldInfo.setterName = Utils.toSetterName(fieldName);


						if (excludeFieldSet.contains(fieldName)) {
							fieldInfo.accessField = false;
						} else {
							fieldInfo.accessField = variableElement.getAnnotation(Access.ExcludeField.class) == null;
						}

						if (excludeSetterSet.contains(fieldName)) {
							fieldInfo.accessSetter = false;
						} else {
							fieldInfo.accessSetter = variableElement.getAnnotation(Access.ExcludeSetter.class) == null;
						}

						if (excludeGetterSet.contains(fieldName)) {
							fieldInfo.accessGetter = false;
						} else {
							fieldInfo.accessGetter = variableElement.getAnnotation(Access.ExcludeGetter.class) == null;
						}

						fields.add(fieldInfo);
					}
				}
			}


			// super class
			TypeMirror typeMirror = element.getSuperclass();
			if (typeMirror instanceof NoType) {
				break;
			}
			element = (TypeElement) ((DeclaredType) typeMirror).asElement();
		}
	}

	@Data
	public static class FieldInfo {
		private TypeName declaredTypeName;
		private ClassName declaredClassName;
		private String fieldName;
		private TypeName typeName;
		private boolean accessGetter = true;
		private boolean accessSetter = true;
		private boolean accessField = true;
		private String setterName;
		private String getterName;
	}

}
