package de.morphbit.thymeleaf.dialect;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.morphbit.thymeleaf.base.AbstractThymeleafComponentDialectTest;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;

public class EdgeCaseTest extends AbstractThymeleafComponentDialectTest {

	@Test
	public void itShouldRenderDeeplyNestedComponents() {
		String html = processThymeleafFile("recursive_components.html", new Context());

		assertNotNull(html);
		assertFalse(html.contains("tc:outer"));
		assertFalse(html.contains("tc:inner"));
		assertFalse(html.contains("tc:content"));
		assertTrue(html.contains("class=\"outer\""));
		assertTrue(html.contains("class=\"inner\""));
		assertTrue(html.contains("<span>Deeply nested</span>"));
	}

	@Test
	public void itShouldRenderEmptyComponentWithoutContent() {
		String html = processThymeleafFile("empty_component.html", new Context());

		assertNotNull(html);
		assertFalse(html.contains("tc:card"));
		assertTrue(html.contains("class=\"card\""));
	}

	@Test
	public void itShouldHandleEmptyNamedSlots() {
		String html = processThymeleafFile("empty_named_slots.html", new Context());

		assertNotNull(html);
		assertFalse(html.contains("tc:slot"));
		assertFalse(html.contains("tc:content"));
		assertTrue(html.contains("class=\"layout\""));
		assertTrue(html.contains("class=\"header\""));
		assertTrue(html.contains("class=\"footer\""));
	}

	@Test
	public void itShouldIgnoreUnmatchedSlotsAndRenderDefaultContent() {
		String html = processThymeleafFile("unmatched_slot.html", new Context());

		assertNotNull(html);
		assertFalse(html.contains("tc:slot"));
		// The unmatched slot content should not appear anywhere
		assertFalse(html.contains("This slot does not exist"));
		// Default body content should still render
		assertTrue(html.contains("<p>Default body content</p>"));
	}

	@Test
	public void itShouldThrowExceptionForNonExistentFragment() {
		assertThrows(TemplateInputException.class,
				() -> processThymeleafFile("nonexistent_template.html", new Context()));
	}
}
