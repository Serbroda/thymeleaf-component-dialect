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

package de.morphbit.thymeleaf.processor;

import static java.util.Collections.singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IElementTag;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.model.IText;
import org.thymeleaf.processor.element.AbstractElementModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templatemode.TemplateMode;

import de.morphbit.thymeleaf.helper.FragmentHelper;
import de.morphbit.thymeleaf.helper.WithHelper;

public class ComponentNamedElementProcessor
        extends AbstractElementModelProcessor {

	private static final String FRAGMENT_ATTRIBUTE = "fragment";
	private static final String CONTENT_TAG = "tc:content";
	private static final String SLOT_TAG = "tc:slot";
	private static final String NAME_ATTR = "name";

	private static final int PRECEDENCE = 350;

	private final Set<String> excludeAttributes = singleton("tc:constructor");
	private final String fragmentName;

	/**
	 * Constructor
	 * 
	 * @param dialectPrefix
	 *            Dialect prefix (tc)
	 * @param tagName
	 *            Tag name to search for (e.g. panel)
	 * @param fragmentName
	 *            Fragment to search for
	 */
	public ComponentNamedElementProcessor(final String dialectPrefix,
	        final String tagName, final String fragmentName) {
		super(TemplateMode.HTML, dialectPrefix, tagName, true, null, false,
		    PRECEDENCE);
		this.fragmentName = fragmentName;
	}

	@Override
	protected void doProcess(ITemplateContext context, IModel model,
	        IElementModelStructureHandler structureHandler) {
		IProcessableElementTag tag = processElementTag(context, model);
		Map<String, String> attributes = processAttribute(tag);

		String constructorParams = attributes.get("tc:constructor");

		IModel componentModel = model.cloneModel();
		componentModel.remove(0);

		if (componentModel.size() > 1) {
			componentModel.remove(componentModel.size() - 1);
		}

		IModel fragmentModel = FragmentHelper.getFragmentModel(context,
		    fragmentName + (constructorParams == null ? "" : "(" + constructorParams + ")"),
		    structureHandler, StandardDialect.PREFIX, FRAGMENT_ATTRIBUTE);

		model.reset();

		IModel replacedFragmentModel = replaceAllAttributeValues(attributes, context, fragmentModel);

		IModelFactory modelFactory = context.getModelFactory();
		Map<String, IModel> namedSlots = new HashMap<>();
		IModel defaultContent = modelFactory.createModel();
		extractSlots(componentModel, namedSlots, defaultContent, modelFactory);

		model.addModel(mergeSlots(replacedFragmentModel, namedSlots,
		    defaultContent, modelFactory));

		processVariables(attributes, context, structureHandler, excludeAttributes);
	}

	private IProcessableElementTag processElementTag(ITemplateContext context,
	        IModel model) {
		ITemplateEvent firstEvent = model.get(0);
		for (IProcessableElementTag tag : context.getElementStack()) {
			if (locationMatches(firstEvent, tag)) {
				return tag;
			}
		}
		return null;
	}

	private boolean locationMatches(ITemplateEvent a, ITemplateEvent b) {
		return Objects.equals(a.getTemplateName(), b.getTemplateName())
		        && Objects.equals(a.getLine(), b.getLine())
		        && Objects.equals(a.getCol(), b.getCol());
	}

	private void processVariables(Map<String, String> attributes,
	        ITemplateContext context,
	        IElementModelStructureHandler structureHandler,
	        Set<String> excludeAttr) {
		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			if (excludeAttr.contains(entry.getKey()) || isDynamicAttribute(
			    entry.getKey(), this.getDialectPrefix())) {
				continue;
			}
			String attributeValue = entry.getValue();
			if (attributeValue == null) {
				attributeValue = "${true}";
			}
			WithHelper.processWith(context, entry.getKey() + "=" + attributeValue,
			    structureHandler, false);
		}
	}

	private Map<String, String> processAttribute(IProcessableElementTag tag) {
		Map<String, String> attributes = new HashMap<>();
		if (tag != null) {
			for (final IAttribute attribute : tag.getAllAttributes()) {
				String completeName = attribute.getAttributeCompleteName();
				if (!isDynamicAttribute(completeName, StandardDialect.PREFIX)) {
					attributes.put(completeName, attribute.getValue());
				}
			}
		}

		return attributes;
	}

	private boolean isDynamicAttribute(String attribute, String prefix) {
		return attribute.startsWith(prefix + ":")
		        || attribute.startsWith("data-" + prefix + "-");
	}

	private void extractSlots(IModel model, Map<String, IModel> namedSlots,
	        IModel defaultContent, IModelFactory modelFactory) {
		int i = 0;
		int size = model.size();

		while (i < size) {
			ITemplateEvent event = model.get(i);
			String elementName = getElementName(event);

			if (SLOT_TAG.equals(elementName)
			        && event instanceof IOpenElementTag) {
				String slotName = ((IProcessableElementTag) event)
				    .getAttributeValue(NAME_ATTR);
				IModel slotContent = modelFactory.createModel();
				i++;
				int depth = 1;
				while (i < size) {
					event = model.get(i);
					elementName = getElementName(event);
					if (SLOT_TAG.equals(elementName)
					        && event instanceof IOpenElementTag) {
						depth++;
					} else if (SLOT_TAG.equals(elementName)
					        && event instanceof ICloseElementTag) {
						depth--;
						if (depth == 0) {
							i++;
							break;
						}
					}
					slotContent.add(event);
					i++;
				}
				if (slotName != null && !slotName.isEmpty()) {
					namedSlots.put(slotName, slotContent);
				} else {
					for (int j = 0; j < slotContent.size(); j++) {
						defaultContent.add(slotContent.get(j));
					}
				}
			} else {
				defaultContent.add(event);
				i++;
			}
		}
	}

	private IModel mergeSlots(IModel fragmentModel,
	        Map<String, IModel> namedSlots, IModel defaultContent,
	        IModelFactory modelFactory) {
		IModel result = modelFactory.createModel();
		int i = 0;
		int size = fragmentModel.size();

		while (i < size) {
			ITemplateEvent event = fragmentModel.get(i);
			String elementName = getElementName(event);

			if (CONTENT_TAG.equals(elementName)) {
				String contentName = null;
				if (event instanceof IProcessableElementTag) {
					contentName = ((IProcessableElementTag) event)
					    .getAttributeValue(NAME_ATTR);
				}

				if (event instanceof IStandaloneElementTag) {
					IModel slotContent = resolveSlotContent(contentName,
					    namedSlots, defaultContent, null);
					if (slotContent != null) {
						result.addModel(slotContent);
					}
					i++;
				} else if (event instanceof IOpenElementTag) {
					IModel fallbackContent = modelFactory.createModel();
					i++;
					int depth = 1;
					while (i < size) {
						event = fragmentModel.get(i);
						elementName = getElementName(event);
						if (CONTENT_TAG.equals(elementName)
						        && event instanceof IOpenElementTag) {
							depth++;
						} else if (CONTENT_TAG.equals(elementName)
						        && event instanceof ICloseElementTag) {
							depth--;
							if (depth == 0) {
								i++;
								break;
							}
						}
						fallbackContent.add(event);
						i++;
					}
					IModel slotContent = resolveSlotContent(contentName,
					    namedSlots, defaultContent, fallbackContent);
					if (slotContent != null) {
						result.addModel(slotContent);
					}
				} else {
					i++;
				}
			} else if (event instanceof ITemplateStart
				        || event instanceof ITemplateEnd) {
				i++;
			} else {
				result.add(event);
				i++;
			}
		}

		return result;
	}

	private IModel resolveSlotContent(String contentName,
	        Map<String, IModel> namedSlots, IModel defaultContent,
	        IModel fallbackContent) {
		if (contentName != null && !contentName.isEmpty()) {
			IModel slotContent = namedSlots.get(contentName);
			if (slotContent != null && !isEmptyOrWhitespace(slotContent)) {
				return slotContent;
			}
			return fallbackContent;
		} else {
			if (defaultContent != null
			        && !isEmptyOrWhitespace(defaultContent)) {
				return defaultContent;
			}
			return fallbackContent;
		}
	}

	private boolean isEmptyOrWhitespace(IModel model) {
		for (int i = 0; i < model.size(); i++) {
			ITemplateEvent event = model.get(i);
			if (event instanceof IText) {
				if (!((IText) event).getText().trim().isEmpty()) {
					return false;
				}
			} else if (event instanceof IElementTag) {
				return false;
			}
		}
		return true;
	}

	private String getElementName(ITemplateEvent event) {
		if (event instanceof IElementTag) {
			return ((IElementTag) event).getElementCompleteName();
		}
		return null;
	}

	private IModel replaceAllAttributeValues(Map<String, String> attributes,
	        ITemplateContext context, IModel model) {
		Map<String, String> replaceAttributes = findAllAttributesStartsWith(
		    attributes, super.getDialectPrefix(), "repl-", true);

		if (replaceAttributes.isEmpty()) {
			return model;
		}
		IModel clonedModel = model.cloneModel();
		int size = model.size();
		for (int i = 0; i < size; i++) {
			ITemplateEvent replacedEvent = replaceAttributeValue(context,
			    clonedModel.get(i), replaceAttributes);
			if (replacedEvent != null) {
				clonedModel.replace(i, replacedEvent);
			}

		}
		return clonedModel;
	}

	private ITemplateEvent replaceAttributeValue(ITemplateContext context,
	        ITemplateEvent model, Map<String, String> replaceValueMap) {
		IProcessableElementTag firstEvent = null;
		if (!replaceValueMap.isEmpty()
		        && model instanceof IProcessableElementTag) {
			final IModelFactory modelFactory = context.getModelFactory();

			firstEvent = (IProcessableElementTag) model;
			for (Map.Entry<String, String> entry : firstEvent.getAttributeMap()
			    .entrySet()) {
				String oldAttrValue = entry.getValue();
				String replacePart = getReplaceAttributePart(oldAttrValue);
				if (replacePart != null
				        && replaceValueMap.containsKey(replacePart)) {
					String newStringValue =
					        oldAttrValue.replace("?[" + replacePart + "]",
					            replaceValueMap.get(replacePart));
					firstEvent = modelFactory.replaceAttribute(firstEvent,
					    AttributeNames.forTextName(entry.getKey()),
					    entry.getKey(), newStringValue);
				}
			}
		}
		return firstEvent;
	}

	private Map<String, String> findAllAttributesStartsWith(
	        final Map<String, String> attributes, final String prefix,
	        final String attributeName, boolean removeStart) {
		Map<String, String> matchingAttributes = new HashMap<>();
		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (key.startsWith(prefix + ":" + attributeName)
			        || key.startsWith("data-" + prefix + "-" + attributeName)) {
				if (removeStart) {
					key = key.replaceAll("^" + prefix + ":" + attributeName,
					    "");
					key = key.replaceAll(
					    "^data-" + prefix + "-" + attributeName, "");
				}
				matchingAttributes.put(key, value);
			}
		}
		return matchingAttributes;
	}

	private String getReplaceAttributePart(String attributeValue) {
		Pattern pattern = Pattern.compile(".*\\?\\[([\\w|\\d|.|\\-|_]*)\\].*");
		Matcher matcher = pattern.matcher(attributeValue);
		while (matcher.find()) {
			if (matcher.group(1) != null && !matcher.group(1).isEmpty()) {
				return matcher.group(1);
			}
		}
		return null;
	}
}
