package de.morphbit.thymeleaf.dialect;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.morphbit.thymeleaf.base.AbstractThymeleafComponentDialectTest;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;

public class NestedComponentTest extends AbstractThymeleafComponentDialectTest {

	@Test
	public void itShouldRenderNestedComponents() {
		String html = processThymeleafFile("nested_components.html", new Context());

		assertNotNull(html);
		assertFalse(html.contains("tc:card"));
		assertFalse(html.contains("tc:alert"));
		assertFalse(html.contains("tc:content"));
		assertTrue(html.contains("class=\"card\""));
		assertTrue(html.contains("Outer Card"));
		assertTrue(html.contains("class=\"alert"));
		assertTrue(html.contains("Nested Alert"));
		assertTrue(html.contains("<span>Nested content</span>"));
	}
}
