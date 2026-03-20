package de.morphbit.thymeleaf.dialect;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;

import de.morphbit.thymeleaf.base.AbstractThymeleafComponentDialectTest;

public class NamedSlotTest extends AbstractThymeleafComponentDialectTest {

	@Test
	public void itShouldDistributeContentToNamedSlots() {
		String html =
		        processThymeleafFile("named_slots.html", new Context());

		assertNotNull(html);

		// No tc: tags should remain in output
		assertFalse(html.contains("tc:page-layout"));
		assertFalse(html.contains("tc:content"));
		assertFalse(html.contains("tc:slot"));

		// Layout structure should be present
		assertTrue(html.contains("class=\"layout\""));
		assertTrue(html.contains("class=\"header\""));
		assertTrue(html.contains("class=\"body\""));
		assertTrue(html.contains("class=\"footer\""));

		// Named slot "header" should be in the header section
		assertTrue(html.contains("<h1>My Page Title</h1>"));

		// Named slot "footer" should be in the footer section
		assertTrue(html.contains("<span>Copyright 2026</span>"));

		// Default content should be in the body section
		assertTrue(html.contains("<p>Main content here</p>"));
	}

	@Test
	public void itShouldPlaceNamedSlotsInCorrectSections() {
		String html =
		        processThymeleafFile("named_slots.html", new Context());

		assertNotNull(html);

		// Verify header content is within header section
		int headerSectionStart = html.indexOf("class=\"header\"");
		int bodySectionStart = html.indexOf("class=\"body\"");
		int footerSectionStart = html.indexOf("class=\"footer\"");
		int headerContentPos = html.indexOf("My Page Title");
		int bodyContentPos = html.indexOf("Main content here");
		int footerContentPos = html.indexOf("Copyright 2026");

		assertTrue(headerContentPos > headerSectionStart
		        && headerContentPos < bodySectionStart,
		    "Header content should be within header section");
		assertTrue(bodyContentPos > bodySectionStart
		        && bodyContentPos < footerSectionStart,
		    "Body content should be within body section");
		assertTrue(footerContentPos > footerSectionStart,
		    "Footer content should be within footer section");
	}

	@Test
	public void itShouldOverrideDefaultContentWithNamedSlot() {
		String html = processThymeleafFile(
		    "named_slots_default_content.html", new Context());

		assertNotNull(html);

		// Named slot "header" should override the default
		assertTrue(html.contains("<b>Custom Header</b>"));
		assertFalse(html.contains("Default Header"));

		// Default slot should contain custom body content
		assertTrue(html.contains("<p>Custom body content</p>"));
		assertFalse(html.contains("Default Body"));
	}

	@Test
	public void itShouldUseFallbackContentWhenNoSlotProvided() {
		String html = processThymeleafFile("named_slots_fallback.html",
		    new Context());

		assertNotNull(html);

		// No slots provided, so default content should be used
		assertTrue(html.contains("Default Header"));
		assertTrue(html.contains("Default Body"));
	}
}
