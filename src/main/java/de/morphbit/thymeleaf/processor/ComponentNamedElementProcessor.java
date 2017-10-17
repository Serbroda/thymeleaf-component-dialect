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
import org.thymeleaf.model.IElementTag;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.processor.element.AbstractElementModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templatemode.TemplateMode;

import de.morphbit.thymeleaf.helper.FragmentHelper;
import de.morphbit.thymeleaf.helper.WithHelper;

public class ComponentNamedElementProcessor
        extends AbstractElementModelProcessor {

	private static final String FRAGMENT_ATTRIBUTE = "fragment";
	private static final String REPLACE_CONTENT_TAG = "tc:content";

	private static final int PRECEDENCE = 350;

	private final Set<String> excludeAttributes = singleton("params");
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
		Map<String, String> attrMap = processAttribute(tag);

		String param = attrMap.get("params");

		IModel base = model.cloneModel();
		base.remove(0);

		if (base.size() > 1) {
			base.remove(base.size() - 1);
		}

		IModel frag = FragmentHelper.getFragmentModel(context,
		    fragmentName + (param == null ? "" : "(" + param + ")"),
		    structureHandler, StandardDialect.PREFIX, FRAGMENT_ATTRIBUTE);

		model.reset();

		IModel replaced = replaceAllAttributeValues(attrMap, context, frag);
		model.addModel(mergeModels(replaced, base, REPLACE_CONTENT_TAG));

		processVariables(attrMap, context, structureHandler, excludeAttributes);
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

	private void processVariables(Map<String, String> attrMap,
	        ITemplateContext context,
	        IElementModelStructureHandler structureHandler,
	        Set<String> excludeAttr) {
		for (Map.Entry<String, String> entry : attrMap.entrySet()) {
			if (excludeAttr.contains(entry.getKey()) || isDynamicAttribute(
			    entry.getKey(), this.getDialectPrefix())) {
				continue;
			}
			String val = entry.getValue();
			if (val == null) {
				val = "${true}";
			}
			WithHelper.processWith(context, entry.getKey() + "=" + val,
			    structureHandler);
		}
	}

	private Map<String, String> processAttribute(IProcessableElementTag tag) {
		Map<String, String> attMap = new HashMap<>();
		if (tag != null) {
			for (final IAttribute attribute : tag.getAllAttributes()) {
				String completeName = attribute.getAttributeCompleteName();
				if (!isDynamicAttribute(completeName, StandardDialect.PREFIX)) {
					attMap.put(completeName, attribute.getValue());
				}
			}
		}

		return attMap;
	}

	private boolean isDynamicAttribute(String attribute, String prefix) {
		return attribute.startsWith(prefix + ":")
		        || attribute.startsWith("data-" + prefix + "-");
	}

	private IModel mergeModels(IModel base, IModel insert, String replaceTag) {
		IModel mergedModel = insertModel(base, insert, replaceTag);
		mergedModel = removeTag(mergedModel, replaceTag);
		mergedModel = removeTag(mergedModel, replaceTag);
		return mergedModel;
	}

	private IModel insertModel(IModel base, IModel insert, String replaceTag) {
		IModel clonedModel = base.cloneModel();
		int index = findTag(base, replaceTag, IElementTag.class);
		if (index > -1) {
			clonedModel.insertModel(index, insert);
		}
		return clonedModel;
	}

	private IModel removeTag(IModel model, final String tag) {
		IModel clonedModel = model.cloneModel();
		int index = findTag(model, tag, IElementTag.class);
		if (index > -1) {
			clonedModel.remove(index);
		}
		return clonedModel;
	}

	private int findTag(IModel model, final String search, Class<?> clazz) {
		int size = model.size();
		ITemplateEvent event = null;
		for (int i = 0; i < size; i++) {
			event = model.get(i);
			if ((clazz == null || clazz.isInstance(event))
			        && event.toString().contains(search)) {
				return i;
			}
		}
		return -1;
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
					        oldAttrValue.replace("--:" + replacePart + ":--",
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
		Pattern pattern = Pattern.compile(".*--:(.*):--.*");
		Matcher matcher = pattern.matcher(attributeValue);
		while (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
}
