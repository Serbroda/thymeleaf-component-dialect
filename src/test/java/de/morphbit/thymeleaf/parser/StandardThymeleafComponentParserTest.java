package de.morphbit.thymeleaf.parser;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import de.morphbit.thymeleaf.model.ThymeleafComponent;

public class StandardThymeleafComponentParserTest {

	@Test
	public void testParser() {
		final StandardThymeleafComponentParser parser =
		        new StandardThymeleafComponentParser("templates/", ".html",
		            "components");
		Set<ThymeleafComponent> components = parser.parse();
		assertTrue(!components.isEmpty());
		assertTrue(containsComponent(components, "link"));
		assertTrue(containsComponent(components, "link-named"));
		assertTrue(notContainsComponent(components, "link2"));
		assertTrue(notContainsComponent(components, "no_component"));
		assertTrue(notContainsComponent(components, "link_component"));
	}

	private boolean containsComponent(final Set<ThymeleafComponent> components,
	        final String name) {
		for (ThymeleafComponent component : components) {
			if (component.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	private boolean notContainsComponent(
	        final Set<ThymeleafComponent> components, final String name) {
		return !containsComponent(components, name);
	}
}
