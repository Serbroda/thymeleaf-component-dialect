package de.morphbit.thymeleaf.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.morphbit.thymeleaf.model.ThymeleafComponent;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class StandardThymeleafComponentParserTest {

	@Test
	public void testParser() {
		final StandardThymeleafComponentParser parser = new StandardThymeleafComponentParser("templates/", ".html",
				"components");
		Set<ThymeleafComponent> components = parser.parse();
		assertFalse(components.isEmpty());
		assertTrue(containsComponent(components, "link"));
		assertTrue(containsComponent(components, "link-named"));
		assertFalse(containsComponent(components, "link2"));
		assertFalse(containsComponent(components, "no_component"));
		assertFalse(containsComponent(components, "link_component"));
	}

	private boolean containsComponent(final Set<ThymeleafComponent> components, final String name) {
		return components.stream().anyMatch(c -> c.getName().equalsIgnoreCase(name));
	}
}
