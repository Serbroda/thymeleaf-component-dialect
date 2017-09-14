package de.morphbit.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.expression.Ids;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.standard.processor.AbstractStandardConditionalVisibilityTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

public class OnceAttributeTagProcessor
        extends AbstractStandardConditionalVisibilityTagProcessor {

	public static final int PRECEDENCE = 300;
	public static final String ATTR_NAME = "once";

	public OnceAttributeTagProcessor(String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, ATTR_NAME, PRECEDENCE);
	}

	@Override
	protected boolean isVisible(ITemplateContext context,
	        IProcessableElementTag tag, AttributeName attributeName,
	        String attributeValue) {
		Ids ids = new Ids(context);
		String id = ids.seq(attributeValue);
		return (attributeValue + "1").equals(id);
	}

}
