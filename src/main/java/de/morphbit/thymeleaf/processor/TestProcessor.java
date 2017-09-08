package de.morphbit.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import de.morphbit.thymeleaf.helper.FragmentHelper;

public class TestProcessor extends AbstractDefaultElementModelProcessor {

	private static final int PRECEDENCE = 75;

	private static final String THYMELEAF_FRAGMENT_PREFIX = "th";
	private static final String THYMELEAF_FRAGMENT_ATTRIBUTE = "fragment";

	private static final String REPLACE_CONTENT_TAG = "tc:content";

	private final String fragmentName;

	public TestProcessor(final String dialectPrefix, final String tagName,
	        final String fragmentName) {
		super(TemplateMode.HTML, "", tagName, true, null, false, PRECEDENCE);
		this.fragmentName = fragmentName;
	}

	@Override
	protected void doProcess(ITemplateContext context, IModel model,
	        IElementModelStructureHandler structureHandler) {

		IModel base = model.cloneModel();
		base.remove(0);
		base.remove(base.size() - 1);

		IModel frag = FragmentHelper.getFragmentModel(context, fragmentName,
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
