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

package de.morphbit.thymeleaf.dialect;

import de.morphbit.thymeleaf.model.ThymeleafComponent;
import de.morphbit.thymeleaf.parser.IThymeleafComponentParser;
import de.morphbit.thymeleaf.processor.ComponentNamedElementProcessor;
import de.morphbit.thymeleaf.processor.OnceAttributeTagProcessor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

/**
 * A Thymeleaf dialect for creating reusable UI components, similar to React or
 * Vue components. Components are defined using standard {@code th:fragment}
 * attributes and used via the {@code tc:} namespace.
 *
 * <p>
 * Usage with Spring Boot:
 * </p>
 * 
 * <pre>{@code
 * @Bean
 * public ComponentDialect componentDialect() {
 * 	var dialect = new ComponentDialect();
 * 	dialect.addParser(new StandardThymeleafComponentParser("templates/", ".html", "components"));
 * 	return dialect;
 * }
 * }</pre>
 *
 * <p>
 * Components can also be registered manually:
 * </p>
 * 
 * <pre>{@code
 * var components = Set.of(new ThymeleafComponent("panel", "components/panel :: panel"));
 * var dialect = new ComponentDialect(components);
 * }</pre>
 *
 * @author Danny Rottstegge
 * @see ThymeleafComponent
 * @see IThymeleafComponentParser
 */
public class ComponentDialect extends AbstractProcessorDialect {

	/** The dialect name. */
	public static final String NAME = "Component Dialect";
	/** The namespace prefix used in templates ({@code tc}). */
	public static final String PREFIX = "tc";
	/** The dialect precedence (lower values are processed first). */
	public static final int PRECEDENCE = 1000;

	private final Set<ThymeleafComponent> components;
	private final List<IThymeleafComponentParser> parsers = new ArrayList<>();

	/**
	 * Creates a new dialect with no pre-registered components. Use
	 * {@link #addParser(IThymeleafComponentParser)} to register a parser for
	 * automatic component discovery.
	 */
	public ComponentDialect() {
		this(null);
	}

	/**
	 * Creates a new dialect with manually registered components.
	 *
	 * @param components
	 *            set of components to register, or {@code null}
	 */
	public ComponentDialect(Set<ThymeleafComponent> components) {
		super(NAME, PREFIX, PRECEDENCE);
		this.components = components;
	}

	/**
	 * Returns the dialect's processors. It will also add thymeleaf components
	 * processors parsed from added IThymeleafComponentParser
	 */
	@Override
	public Set<IProcessor> getProcessors(String dialectPrefix) {
		Set<IProcessor> processors = new HashSet<>();
		processors.add(new OnceAttributeTagProcessor(dialectPrefix));

		if (this.components != null) {
			for (ThymeleafComponent comp : this.components) {
				processors.add(new ComponentNamedElementProcessor(dialectPrefix, comp.name(), comp.fragmentTemplate()));
			}
		}

		for (ThymeleafComponent comp : parseComponents()) {
			processors.add(new ComponentNamedElementProcessor(dialectPrefix, comp.name(), comp.fragmentTemplate()));
		}

		return processors;
	}

	/**
	 * Get components from parsers
	 * 
	 * @return Thymeleaf components
	 */
	private Set<ThymeleafComponent> parseComponents() {
		Set<ThymeleafComponent> parsedComponents = new HashSet<>();
		for (IThymeleafComponentParser parser : this.parsers) {
			parsedComponents.addAll(parser.parse());
		}

		return parsedComponents;
	}

	/**
	 * Adds a parser that will discover and register components automatically.
	 * Multiple parsers can be added to scan different directories.
	 *
	 * @param parser
	 *            the component parser to add
	 * @see de.morphbit.thymeleaf.parser.StandardThymeleafComponentParser
	 */
	public void addParser(IThymeleafComponentParser parser) {
		this.parsers.add(parser);
	}
}
