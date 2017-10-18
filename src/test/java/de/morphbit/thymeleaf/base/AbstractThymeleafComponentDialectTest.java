package de.morphbit.thymeleaf.base;

import org.junit.BeforeClass;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import de.morphbit.thymeleaf.dialect.ComponentDialect;
import de.morphbit.thymeleaf.parser.StandardThymeleafComponentParser;

public abstract class AbstractThymeleafComponentDialectTest {

	private static final String BASE_PATH = "templates";

	private static TemplateEngine templateEngine;

	@BeforeClass
	public static void beforeClass() {
		setupThymeleaf();
	}

	protected String processThymeleafFile(String fileName, Context context) {
		return templateEngine.process(BASE_PATH + "/" + fileName, context);
	}

	private static void setupThymeleaf() {
		ClassLoaderTemplateResolver templateResolver =
		        new ClassLoaderTemplateResolver();
		templateResolver.setCacheable(false);
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setSuffix(".html");

		templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		final ComponentDialect dialect = new ComponentDialect();
		final StandardThymeleafComponentParser parser =
		        new StandardThymeleafComponentParser("", ".html",
		            "templates/components");
		dialect.addParser(parser);
		templateEngine.addDialect(dialect.getPrefix(), dialect);
	}
}
