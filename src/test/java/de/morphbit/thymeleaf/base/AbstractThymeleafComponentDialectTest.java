package de.morphbit.thymeleaf.base;

import org.junit.BeforeClass;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import de.morphbit.thymeleaf.dialect.ComponentDialect;

public class AbstractThymeleafComponentDialectTest {

	private static TemplateEngine templateEngine;

	@BeforeClass
	public static void beforeClass() {
		setupThymeleaf();
	}

	private static void setupThymeleaf() {
		ClassLoaderTemplateResolver templateResolver =
		        new ClassLoaderTemplateResolver();
		templateResolver.setCacheable(false);
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setTemplateMode(TemplateMode.HTML);

		templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		final ComponentDialect dialect = new ComponentDialect();
		templateEngine.addDialect(dialect.getPrefix(), dialect);
	}
}
