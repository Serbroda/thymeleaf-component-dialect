package de.morphbit.thymeleaf.dialect;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;

import de.morphbit.thymeleaf.base.AbstractThymeleafComponentDialectTest;

public class SelfClosingComponentTest
        extends AbstractThymeleafComponentDialectTest {

	@Test
	public void itShouldRenderSelfClosingComponent() {
		String html = processThymeleafFile("self_closing_component.html",
		    new Context());

		assertNotNull(html);
		assertFalse(html.contains("tc:badge"));
		assertTrue(html.contains("class=\"badge\""));
		assertTrue(html.contains("Important"));
	}
}
