package io.polaris.bytecode.bytebuddy;

/**
 * @author Qt
 * @since Nov 03, 2025
 */
public class OriginClass {

	private String name;

	public OriginClass() {
	}

	public OriginClass(String name) {
		this.name = name;
	}

	private OriginClass(String name, int age) {
		this.name = name;
	}

	protected OriginClass(String name, int age, String sex) {
		this.name = name;
	}

	OriginClass(String name, int age, String sex, int height) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void originMethod() {
		System.out.println("originMethod");
	}

	public void originMethod(String name) {
		System.out.println("originMethod " + name);
	}

	public void originMethod(String name, int age) {
		System.out.println("originMethod " + name + " " + age);
	}

	public void originMethod(int age, String name) {
		System.out.println("originMethod " + age + " " + name);
	}

	public void originMethod(int age) {
		System.out.println("originMethod " + age);
	}

}
