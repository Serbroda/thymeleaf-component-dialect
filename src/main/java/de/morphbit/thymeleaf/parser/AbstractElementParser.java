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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.attoparser.AbstractMarkupHandler;
import org.attoparser.MarkupParser;
import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;
import org.attoparser.dom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractElementParser extends AbstractMarkupHandler {

	private static final Logger LOG =
	        LoggerFactory.getLogger(AbstractElementParser.class);

	protected final String dialectPrefix;

	private List<Element> elements;
	private Element currentElement;

	public AbstractElementParser(String dialectPrefix) {
		this.dialectPrefix = dialectPrefix;
	}

	protected List<Element> parseElements(String file) {
		return parseElements(Thread.currentThread().getContextClassLoader()
		    .getResourceAsStream(file));
	}

	protected List<Element> parseElements(InputStream stream) {
		this.elements = new ArrayList<>();

		try (Reader reader = new InputStreamReader(stream)) {
			final ParseConfiguration config =
			        ParseConfiguration.htmlConfiguration();

			final ParseConfiguration autoCloseConfig =
			        ParseConfiguration.htmlConfiguration();
			autoCloseConfig.setElementBalancing(
			    ParseConfiguration.ElementBalancing.AUTO_OPEN_CLOSE);

			final MarkupParser htmlStandardParser = new MarkupParser(config);
			htmlStandardParser.parse(reader, this);

		} catch (IOException | ParseException e) {
			LOG.error("Error while parsing elements: {}", e);
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				LOG.error("Error while closing stream: {}", e);
			}
		}

		return this.elements;
	}

	@Override
	public void handleAttribute(char[] buffer, int nameOffset, int nameLen,
	        int nameLine, int nameCol, int operatorOffset, int operatorLen,
	        int operatorLine, int operatorCol, int valueContentOffset,
	        int valueContentLen, int valueOuterOffset, int valueOuterLen,
	        int valueLine, int valueCol) throws ParseException {
		String attributeName = new String(buffer, nameOffset, nameLen);
		String attributeValue =
		        new String(buffer, valueContentOffset, valueContentLen);
		if (currentElement != null) {
			currentElement.addAttribute(attributeName, attributeValue);
		}
	}

	@Override
	public void handleOpenElementStart(char[] buffer, int nameOffset,
	        int nameLen, int line, int col) throws ParseException {
		String attributeName = new String(buffer, nameOffset, nameLen);
		this.currentElement = new Element(attributeName);
		this.elements.add(currentElement);
	}

}
