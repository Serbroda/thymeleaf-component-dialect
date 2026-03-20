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

import de.morphbit.thymeleaf.model.ThymeleafComponent;
import java.util.Set;

/**
 * Interface for component parsers that discover {@link ThymeleafComponent}s
 * from template files. Implementations are registered via
 * {@link de.morphbit.thymeleaf.dialect.ComponentDialect#addParser(IThymeleafComponentParser)}.
 *
 * @see StandardThymeleafComponentParser
 */
public interface IThymeleafComponentParser {

	/**
	 * Scans template files and returns all discovered components.
	 *
	 * @return set of discovered components
	 */
	Set<ThymeleafComponent> parse();
}
