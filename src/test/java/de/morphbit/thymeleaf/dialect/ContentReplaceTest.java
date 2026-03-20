package de.morphbit.thymeleaf.dialect;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.morphbit.thymeleaf.base.AbstractThymeleafComponentDialectTest;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;

public class ContentReplaceTest extends AbstractThymeleafComponentDialectTest {

	private static final String FILE_LINK_WITH_CONTENT = "link_with_content.html";
	private static final String FILE_LINK_WITH_CONTENT_AND_VARIABLE = "link_with_content_and_variable.html";

	@Test
	public void itShouldNotRenderNamespaceTagsAndReplaceContent() {
		String html = processThymeleafFile(FILE_LINK_WITH_CONTENT, new Context());

		assertNotNull(html);
		assertFalse(html.contains("tc:link"));
		assertFalse(html.contains("tc:content"));
		assertTrue(html.contains("<a href=\"http://www.test.com\">"));
		assertTrue(html.contains("<span>Test</span>"));
		assertFalse(html.contains("<span>&gt;&gt; </span>"));
	}

	@Test
	public void itShouldReplaceContentAndConsiderVariable() {
		String html = processThymeleafFile(FILE_LINK_WITH_CONTENT_AND_VARIABLE, new Context());

		assertNotNull(html);
		assertTrue(html.contains("<a href=\"http://www.test.com\">"));
		assertTrue(html.contains("<span>Test</span>"));
		assertTrue(html.contains("<span>&gt;&gt; </span>"));
	}
}
