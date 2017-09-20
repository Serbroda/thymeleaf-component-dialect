package de.morphbit.thymeleaf.processor;

import static java.util.Collections.singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IElementTag;
import org.thymeleaf.model.IModel;
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
		model.addModel(mergeModels(frag, base, REPLACE_CONTENT_TAG));

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
		for (final IAttribute attribute : tag.getAllAttributes()) {
			String completeName = attribute.getAttributeCompleteName();
			if (!isDynamicAttribute(completeName, StandardDialect.PREFIX)) {
				attMap.put(completeName, attribute.getValue());
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
			if (clazz == null || clazz.isInstance(event)) {
				if (event.toString().contains(search)) {
					return i;
				}
			}
		}
		return -1;
	}
}
