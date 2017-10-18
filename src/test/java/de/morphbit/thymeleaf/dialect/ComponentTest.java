package de.morphbit.thymeleaf.dialect;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.thymeleaf.context.Context;

import de.morphbit.thymeleaf.base.AbstractThymeleafComponentDialectTest;

public class ComponentTest extends AbstractThymeleafComponentDialectTest {

	private static final String FILE = "link-component-test.html";

	@Test
	public void test() {
		String html = processThymeleafFile(FILE, new Context());
		System.out.println(html);
		assertNotNull(html);
	}
}
