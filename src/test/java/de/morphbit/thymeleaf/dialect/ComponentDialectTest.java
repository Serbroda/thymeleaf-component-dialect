package de.morphbit.thymeleaf.dialect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import de.morphbit.thymeleaf.model.ThymeleafComponent;

public class ComponentDialectTest {

	@Test
	public void itShouldHaveCorrectPrefixAndName() {
		ComponentDialect dialect = new ComponentDialect();
		assertEquals("tc", dialect.getPrefix());
		assertEquals("Component Dialect", dialect.getName());
	}

	@Test
	public void itShouldRegisterManualComponents() {
		Set<ThymeleafComponent> components = new HashSet<>();
		components.add(
		    new ThymeleafComponent("my-comp", "templates/test :: fragment"));

		ComponentDialect dialect = new ComponentDialect(components);
		Set<IProcessor> processors = dialect.getProcessors("tc");

		// OnceAttributeTagProcessor + 1 manual component
		assertTrue(processors.size() >= 2);
	}

	@Test
	public void itShouldWorkWithManuallyRegisteredComponent() {
		ClassLoaderTemplateResolver templateResolver =
		        new ClassLoaderTemplateResolver();
		templateResolver.setCacheable(false);
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setSuffix(".html");

		TemplateEngine engine = new TemplateEngine();
		engine.setTemplateResolver(templateResolver);

		Set<ThymeleafComponent> components = new HashSet<>();
		components.add(new ThymeleafComponent("link",
		    "templates/components/link_component :: link"));

		ComponentDialect dialect = new ComponentDialect(components);
		engine.addDialect(dialect.getPrefix(), dialect);

		String html = engine.process("templates/link_with_content",
		    new Context());

		assertNotNull(html);
		assertFalse(html.contains("tc:link"));
		assertTrue(html.contains("<a href=\"http://www.test.com\">"));
	}

	@Test
	public void itShouldReturnOnceProcessorWithEmptyDialect() {
		ComponentDialect dialect = new ComponentDialect();
		Set<IProcessor> processors = dialect.getProcessors("tc");

		assertFalse(processors.isEmpty());
		assertEquals(1, processors.size());
	}
}
