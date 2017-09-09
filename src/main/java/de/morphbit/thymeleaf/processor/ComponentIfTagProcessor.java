package de.morphbit.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.processor.AbstractStandardConditionalVisibilityTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EvaluationUtils;

public class ComponentIfTagProcessor extends AbstractStandardConditionalVisibilityTagProcessor {

	private static final int PRECEDENCE = 65;
	public static final String ATTR_NAME = "if";
	
	public ComponentIfTagProcessor(String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, ATTR_NAME, PRECEDENCE);
	}

	@Override
	protected boolean isVisible(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
			String attributeValue) {
		final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());

        final IStandardExpression expression =
                expressionParser.parseExpression(context, attributeValue);
        final Object value = expression.execute(context);

        return EvaluationUtils.evaluateAsBoolean(value);
	}

}
