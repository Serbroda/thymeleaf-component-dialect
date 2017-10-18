package de.morphbit.thymeleaf.dialect;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Locale;

import org.junit.Test;
import org.thymeleaf.context.Context;

import de.morphbit.thymeleaf.base.AbstractThymeleafComponentDialectTest;

public class AttributeReplaceTest
        extends AbstractThymeleafComponentDialectTest {

	private static final String FILE = "repl_test.html";

	public static class User {

		private String name;

		public User(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	@Test
	public void itShouldReplaceAllReplAttributes() {
		String html = processThymeleafFile(FILE, new Context(Locale.ENGLISH,
		    Collections.singletonMap("user", new User("John"))));

		assertNotNull(html);
		assertTrue(!html.matches(".*\\?\\[([\\w|\\d|.|\\-|_]*)\\].*"));
	}

}
