package de.morphbit.thymeleaf.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ThymeleafComponentTest {

	@Test
	public void itShouldStoreNameAndFragmentTemplate() {
		var component = new ThymeleafComponent("panel", "components/panel :: panel");

		assertEquals("panel", component.name());
		assertEquals("components/panel :: panel", component.fragmentTemplate());
	}

	@Test
	public void itShouldHaveMeaningfulToString() {
		var component = new ThymeleafComponent("panel", "components/panel :: panel");

		String toString = component.toString();
		assertNotNull(toString);
		assertTrue(toString.contains("panel"));
		assertTrue(toString.contains("components/panel :: panel"));
	}

	@Test
	public void itShouldImplementEqualsAndHashCode() {
		var a = new ThymeleafComponent("panel", "components/panel :: panel");
		var b = new ThymeleafComponent("panel", "components/panel :: panel");
		var c = new ThymeleafComponent("other", "components/other :: other");

		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertNotEquals(a, c);
	}
}
