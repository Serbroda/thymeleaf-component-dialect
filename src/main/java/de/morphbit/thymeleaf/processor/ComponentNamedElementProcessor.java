package de.morphbit.thymeleaf.processor;

import java.util.Map;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import de.morphbit.thymeleaf.helper.FragmentHelper;

public class ComponentNamedElementProcessor extends AbstractComponentElementProcessor {

	private static final int PRECEDENCE = 75;

	protected static final String THYMELEAF_FRAGMENT_PREFIX = "th";
	protected static final String THYMELEAF_FRAGMENT_ATTRIBUTE = "fragment";
	protected static final String REPLACE_CONTENT_TAG = "tc:content";

	private final String fragmentName;

	public ComponentNamedElementProcessor(final String dialectPrefix, final String tagName, final String fragmentName) {
		super(TemplateMode.HTML, dialectPrefix, tagName, true, null, false, PRECEDENCE);
		this.fragmentName = fragmentName;
	}

	@Override
	protected void doProcess(ITemplateContext context, IModel model, IElementModelStructureHandler structureHandler) {

		IProcessableElementTag tag = processElementTag(context, model);
		Map<String, String> attrMap = processAttribute(context, tag);

		String param = attrMap.get("params");

		IModel base = model.cloneModel();
		base.remove(0);
		
		if(base.size() > 1) {
			base.remove(base.size() - 1);
		}

		IModel frag = FragmentHelper.getFragmentModel(context, fragmentName + (param == null ? "" : "(" + param + ")"),
				structureHandler, THYMELEAF_FRAGMENT_PREFIX, THYMELEAF_FRAGMENT_ATTRIBUTE);

		model.reset();
		model.addModel(mergeModel(frag, base, REPLACE_CONTENT_TAG));
	}

}
