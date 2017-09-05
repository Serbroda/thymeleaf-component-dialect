package de.morphbit.thymeleaf.processor.element;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

public class ComponentElementProcessor extends AbstractElementTagProcessor {

	private static final String TAG_NAME = "component";
	private static final int PRECEDENCE = 1000;

	public ComponentElementProcessor(final String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, TAG_NAME, true, null, false,
		    PRECEDENCE);
	}

	@Override
	protected void doProcess(ITemplateContext context,
	        IProcessableElementTag tag,
	        IElementTagStructureHandler structureHandler) {

		structureHandler.removeTags();
	}

}
