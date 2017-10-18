package de.morphbit.thymeleaf.dialect;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.thymeleaf.context.Context;

import de.morphbit.thymeleaf.base.AbstractThymeleafComponentDialectTest;

public class OnceAttributeTest extends AbstractThymeleafComponentDialectTest {

	private static final String FILE = "once_test.html";

	@Test
	public void itShouldHaveMultipleComponentsButOneScript() {
		String html = processThymeleafFile(FILE, new Context());

		assertNotNull(html);
		assertTrue(countMatches(
		    "\\<button onclick\\=\\\"onceFunction\\(\\)\\\"\\>Once Button\\</button\\>",
		    html) > 1);
		assertTrue(countMatches("function onceFunction()", html) == 1);
	}

	private int countMatches(String regex, String search) {
		int count = 0;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(search);
		while (matcher.find()) {
			count++;
		}
		return count;
	}
}
