package de.morphbit.thymeleaf.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ThymeleafComponentTest {

	@Test
	public void itShouldStoreNameAndFragmentTemplate() {
		ThymeleafComponent component = new ThymeleafComponent("panel", "components/panel :: panel");

		assertEquals("panel", component.getName());
		assertEquals("components/panel :: panel", component.getFragmentTemplate());
	}

	@Test
	public void itShouldAllowSettingNameAndFragmentTemplate() {
		ThymeleafComponent component = new ThymeleafComponent("old", "old");

		component.setName("new-name");
		component.setFragmentTemplate("new/template :: fragment");

		assertEquals("new-name", component.getName());
		assertEquals("new/template :: fragment", component.getFragmentTemplate());
	}

	@Test
	public void itShouldHaveMeaningfulToString() {
		ThymeleafComponent component = new ThymeleafComponent("panel", "components/panel :: panel");

		String toString = component.toString();
		assertNotNull(toString);
		assertTrue(toString.contains("panel"));
		assertTrue(toString.contains("components/panel :: panel"));
	}
}
