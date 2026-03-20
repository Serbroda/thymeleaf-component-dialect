package de.morphbit.thymeleaf.dialect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.morphbit.thymeleaf.base.AbstractThymeleafComponentDialectTest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;

public class OnceAttributeTest extends AbstractThymeleafComponentDialectTest {

	private static final String FILE = "once_test.html";

	@Test
	public void itShouldHaveMultipleComponentsButOneScript() {
		String html = processThymeleafFile(FILE, new Context());

		assertNotNull(html);
		assertTrue(countMatches("\\<button onclick\\=\\\"onceFunction\\(\\)\\\"\\>Once Button\\</button\\>", html) > 1);
		assertEquals(1, countMatches("function onceFunction()", html));
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
