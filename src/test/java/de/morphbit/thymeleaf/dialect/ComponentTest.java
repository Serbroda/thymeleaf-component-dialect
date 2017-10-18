package de.morphbit.thymeleaf.dialect;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.thymeleaf.context.Context;

import de.morphbit.thymeleaf.base.AbstractThymeleafComponentDialectTest;

public class ComponentTest extends AbstractThymeleafComponentDialectTest {

	private static final String FILE = "simple_content_replace_test.html";

	@Test
	public void test() {
		String html = processThymeleafFile(FILE, new Context());

		assertNotNull(html);
		assertTrue(!html.contains("tc:link"));
		assertTrue(!html.contains("tc:content"));
		assertTrue(html.contains("<a href=\"http://www.test.com\">"));
		assertTrue(html.contains("<span>Test</span>"));
	}
}
