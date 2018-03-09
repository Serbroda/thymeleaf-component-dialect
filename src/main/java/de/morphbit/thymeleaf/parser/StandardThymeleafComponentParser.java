/* 
 * Copyright 2017, Danny Rottstegge
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.morphbit.thymeleaf.parser;

import java.util.HashSet;
import java.util.Set;

import org.attoparser.dom.Element;
import org.thymeleaf.standard.StandardDialect;

import de.morphbit.thymeleaf.dialect.ComponentDialect;
import de.morphbit.thymeleaf.helper.ResourcePathFinder;
import de.morphbit.thymeleaf.model.ThymeleafComponent;

public class StandardThymeleafComponentParser extends AbstractElementParser
        implements IThymeleafComponentParser {

	protected static final String NAME_ATTRIBUTE = "selector";
	protected static final String FRAGMENT_ATTRIBUTE = "fragment";

	private final String directory;
	private final String templatePrefix;
	private final String templateSuffix;

	/**
	 * Constructor
	 * 
	 * @param templatePrefix
	 *            Template prefix (e.g. templates/)
	 * @param templateSuffix
	 *            Template suffix (e.g. .html)
	 * @param directory
	 *            Subdirectory of param templatePrefix to find components in
	 *            (e.g. components)
	 */
	public StandardThymeleafComponentParser(String templatePrefix,
	        String templateSuffix, String directory) {
		super(ComponentDialect.PREFIX);
		this.directory = directory;
		this.templatePrefix = templatePrefix;
		this.templateSuffix = templateSuffix;
	}

	@Override
	public Set<ThymeleafComponent> parse() {
		Set<ThymeleafComponent> components = new HashSet<>();

		for (String file : new ResourcePathFinder(templatePrefix + directory)
		    .findResourceFiles()) {
			for (Element element : parseElements(file)) {
				if (isThymeleafComponent(element)) {
					components.add(createComponent(element, file));
				}
			}
		}

		return components;
	}

	private ThymeleafComponent createComponent(Element element,
	        String htmlFile) {
		String templateFile = htmlFile;
		templateFile = templateFile.replaceAll("^" + this.templatePrefix, "");
		templateFile = templateFile
		    .replaceAll(this.templateSuffix.replace(".", "\\."), "");

		String frag = getDynamicAttributeValue(element, StandardDialect.PREFIX,
		    FRAGMENT_ATTRIBUTE);
		frag = frag.replaceAll("\\(.*\\)", "");

		String name = getDynamicAttributeValue(element, this.dialectPrefix,
		    NAME_ATTRIBUTE);
		if (name == null) {
			name = frag;
		}

		return new ThymeleafComponent(name, templateFile + " :: " + frag);
	}

	private boolean isThymeleafComponent(Element element) {
		return hasDynamicAttribute(element, StandardDialect.PREFIX,
		    FRAGMENT_ATTRIBUTE);
	}

	private boolean hasDynamicAttribute(Element element, String prefix,
	        String dynamicAttribute) {
		return element.hasAttribute("data-" + prefix + "-" + dynamicAttribute)
		        || element.hasAttribute(prefix + ":" + dynamicAttribute);
	}

	private String getDynamicAttributeValue(Element element, String prefix,
	        String dynamicAttribute) {
		String value = element
		    .getAttributeValue("data-" + prefix + "-" + dynamicAttribute);
		if (value != null) {
			return value;
		}
		return element.getAttributeValue(prefix + ":" + dynamicAttribute);
	}

}
