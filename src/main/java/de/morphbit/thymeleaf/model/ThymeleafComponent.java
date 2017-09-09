package de.morphbit.thymeleaf.model;

public class ThymeleafComponent {

	private String name;
	private String fragmentTemplate;

	public ThymeleafComponent(String name, String fragmentTemplate) {
		this.name = name;
		this.fragmentTemplate = fragmentTemplate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFragmentTemplate() {
		return fragmentTemplate;
	}

	public void setFragmentTemplate(String fragmentTemplate) {
		this.fragmentTemplate = fragmentTemplate;
	}

	@Override
	public String toString() {
		return "Component [name=" + name + ", fragmentTemplate=" + fragmentTemplate + "]";
	}

}
