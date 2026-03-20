package de.morphbit.thymeleaf.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Thymeleaf Component Dialect.
 *
 * <p>
 * Example {@code application.yml}:
 * </p>
 * 
 * <pre>{@code
 * thymeleaf-component-dialect:
 *   template-prefix: templates/
 *   template-suffix: .html
 *   component-directory: components
 * }</pre>
 */
@ConfigurationProperties(prefix = "thymeleaf-component-dialect")
public class ComponentDialectProperties {

	private String templatePrefix = "templates/";
	private String templateSuffix = ".html";
	private String componentDirectory = "components";

	public String getTemplatePrefix() {
		return templatePrefix;
	}

	public void setTemplatePrefix(String templatePrefix) {
		this.templatePrefix = templatePrefix;
	}

	public String getTemplateSuffix() {
		return templateSuffix;
	}

	public void setTemplateSuffix(String templateSuffix) {
		this.templateSuffix = templateSuffix;
	}

	public String getComponentDirectory() {
		return componentDirectory;
	}

	public void setComponentDirectory(String componentDirectory) {
		this.componentDirectory = componentDirectory;
	}
}
