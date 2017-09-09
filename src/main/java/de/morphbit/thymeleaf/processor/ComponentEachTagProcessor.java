package de.morphbit.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.Each;
import org.thymeleaf.standard.expression.EachUtils;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;

public class ComponentEachTagProcessor extends AbstractAttributeTagProcessor {

	public static final int PRECEDENCE = 70;
	public static final String ATTR_NAME = "each";

	public ComponentEachTagProcessor(final String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, null, false, ATTR_NAME, true, PRECEDENCE, true);
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
			String attributeValue, IElementTagStructureHandler structureHandler) {
		final Each each = EachUtils.parseEach(context, attributeValue);

        final IStandardExpression iterVarExpr = each.getIterVar();
        final Object iterVarValue = iterVarExpr.execute(context);

        final IStandardExpression statusVarExpr = each.getStatusVar();
        final Object statusVarValue;
        if (statusVarExpr != null) {
            statusVarValue = statusVarExpr.execute(context);
        } else {
            statusVarValue = null; // Will provoke the default behaviour: iterVarValue + 'Stat'
        }

        final IStandardExpression iterableExpr = each.getIterable();
        final Object iteratedValue = iterableExpr.execute(context);

        final String iterVarName = (iterVarValue == null? null : iterVarValue.toString());
        if (StringUtils.isEmptyOrWhitespace(iterVarName)) {
            throw new TemplateProcessingException(
                    "Iteration variable name expression evaluated as null: \"" + iterVarExpr + "\"");
        }

        final String statusVarName = (statusVarValue == null? null : statusVarValue.toString());
        if (statusVarExpr != null && StringUtils.isEmptyOrWhitespace(statusVarName)) {
            throw new TemplateProcessingException(
                    "Status variable name expression evaluated as null or empty: \"" + statusVarExpr + "\"");
        }

        structureHandler.iterateElement(iterVarName, statusVarName, iteratedValue);
	}

}
