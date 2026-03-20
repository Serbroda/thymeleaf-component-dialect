package de.morphbit.thymeleaf.autoconfigure;

import de.morphbit.thymeleaf.dialect.ComponentDialect;
import de.morphbit.thymeleaf.parser.StandardThymeleafComponentParser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot auto-configuration for the Thymeleaf Component Dialect.
 *
 * <p>
 * Automatically registers a {@link ComponentDialect} bean with a
 * {@link StandardThymeleafComponentParser} that scans for component fragments
 * based on {@link ComponentDialectProperties}.
 * </p>
 *
 * <p>
 * This auto-configuration is only active when Thymeleaf is on the classpath. To
 * fully customize the dialect, define your own {@link ComponentDialect} bean
 * and this auto-configuration will back off.
 * </p>
 *
 * @see ComponentDialectProperties
 */
@AutoConfiguration
@ConditionalOnClass(name = "org.thymeleaf.spring6.SpringTemplateEngine")
@EnableConfigurationProperties(ComponentDialectProperties.class)
public class ComponentDialectAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ComponentDialect componentDialect(ComponentDialectProperties properties) {
		var dialect = new ComponentDialect();
		dialect.addParser(new StandardThymeleafComponentParser(properties.getTemplatePrefix(),
				properties.getTemplateSuffix(), properties.getComponentDirectory()));
		return dialect;
	}
}
