package de.morphbit.thymeleaf.processor;

import java.util.Map;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import de.morphbit.thymeleaf.helper.FragmentHelper;

public class ComponentNamedElementProcessor extends AbstractDefaultElementModelProcessor {

	private static final int PRECEDENCE = 75;

	protected static final String THYMELEAF_FRAGMENT_PREFIX = "th";
	protected static final String THYMELEAF_FRAGMENT_ATTRIBUTE = "fragment";
	protected static final String REPLACE_CONTENT_TAG = "tc:content";

	private final String fragmentName;

	public ComponentNamedElementProcessor(final String dialectPrefix, final String tagName,
	        final String fragmentName) {
		super(TemplateMode.HTML, "", dialectPrefix + "-" + tagName, true, null,
		    false, PRECEDENCE);
		this.fragmentName = fragmentName;
	}

	@Override
	protected void doProcess(ITemplateContext context, IModel model,
	        IElementModelStructureHandler structureHandler) {

		IProcessableElementTag tag = processElementTag(context, model);
		Map<String, String> attrMap = processAttribute(context, tag);

		String param = attrMap.get("param");

		IModel base = model.cloneModel();
		base.remove(0);
		base.remove(base.size() - 1);

		IModel frag = FragmentHelper.getFragmentModel(context,
		    fragmentName + (param == null ? "" : "(" + param + ")"),
		    structureHandler, THYMELEAF_FRAGMENT_PREFIX,
		    THYMELEAF_FRAGMENT_ATTRIBUTE);

		model.reset();
		model.addModel(mergeModel(frag, base));

	}

	private IModel mergeModel(IModel fragment, IModel body) {
		IModel mergedModel = insert(fragment, body);
		mergedModel = remove(mergedModel);
		mergedModel = remove(mergedModel);
		return mergedModel;
	}

	private IModel insert(IModel fragment, IModel body) {
		IModel mergedModel = fragment.cloneModel();
		int size = mergedModel.size();
		ITemplateEvent event = null;
		for (int i = 0; i < size; i++) {
			event = mergedModel.get(i);
			if (event instanceof IOpenElementTag) {
				if (event.toString().contains(REPLACE_CONTENT_TAG)) {
					mergedModel.insertModel(i, body);
					break;
				}
			}
		}
		return mergedModel;
	}

	private IModel remove(IModel fragment) {
		IModel mergedModel = fragment.cloneModel();
		int size = mergedModel.size();
		ITemplateEvent event = null;
		for (int i = 0; i < size; i++) {
			event = mergedModel.get(i);
			if (event instanceof IOpenElementTag
			        || event instanceof ICloseElementTag) {
				if (event.toString().contains(REPLACE_CONTENT_TAG)) {
					mergedModel.remove(i);
					break;
				}
			}
		}
		return mergedModel;
	}

}
