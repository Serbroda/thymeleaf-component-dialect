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
}
