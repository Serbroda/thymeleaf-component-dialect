package de.morphbit.thymeleaf.dialect;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;

import de.morphbit.thymeleaf.base.AbstractThymeleafComponentDialectTest;

public class MultipleComponentsTest
        extends AbstractThymeleafComponentDialectTest {

	@Test
	public void itShouldRenderMultipleComponentsOnSamePage() {
		String html = processThymeleafFile("multiple_components.html",
		    new Context());

		assertNotNull(html);
		assertFalse(html.contains("tc:alert"));
		assertFalse(html.contains("tc:card"));
		assertFalse(html.contains("tc:content"));

		// Two alert components
		assertTrue(html.contains("First"));
		assertTrue(html.contains("Second"));
		assertTrue(html.contains("<span>Content 1</span>"));
		assertTrue(html.contains("<span>Content 2</span>"));

		// One card component
		assertTrue(html.contains("My Card"));
		assertTrue(html.contains("<p>Card content</p>"));
	}

	@Test
	public void itShouldRenderCorrectAlertTypes() {
		String html = processThymeleafFile("multiple_components.html",
		    new Context());

		assertNotNull(html);
		assertTrue(html.contains("alert-success"));
		assertTrue(html.contains("alert-danger"));
	}

	@Test
	public void itShouldRenderCorrectNumberOfAlerts() {
		String html = processThymeleafFile("multiple_components.html",
		    new Context());

		assertNotNull(html);
		int alertCount = countMatches("class=\"alert", html);
		assertTrue(alertCount == 2,
		    "Expected 2 alerts but found " + alertCount);
	}

	private int countMatches(String text, String search) {
		int count = 0;
		Pattern pattern = Pattern.compile(Pattern.quote(text));
		Matcher matcher = pattern.matcher(search);
		while (matcher.find()) {
			count++;
		}
		return count;
	}
}
