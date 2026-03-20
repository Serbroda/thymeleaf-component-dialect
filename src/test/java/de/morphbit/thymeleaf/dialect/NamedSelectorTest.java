package de.morphbit.thymeleaf.dialect;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;

import de.morphbit.thymeleaf.base.AbstractThymeleafComponentDialectTest;

public class NamedSelectorTest extends AbstractThymeleafComponentDialectTest {

	@Test
	public void itShouldResolveComponentByNamedSelector() {
		String html =
		        processThymeleafFile("named_selector.html", new Context());

		assertNotNull(html);
		assertFalse(html.contains("tc:link-named"));
		assertFalse(html.contains("tc:content"));
		assertTrue(html.contains("<a href=\"http://www.example.com\">"));
		assertTrue(html.contains("<span>Named Link</span>"));
	}
}
