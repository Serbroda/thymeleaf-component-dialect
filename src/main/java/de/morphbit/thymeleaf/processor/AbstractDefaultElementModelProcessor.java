package de.morphbit.thymeleaf.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.processor.element.AbstractElementModelProcessor;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.NoOpToken;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.processor.StandardDefaultAttributesTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;

public abstract class AbstractDefaultElementModelProcessor
        extends AbstractElementModelProcessor {

	private static final String DYNAMIC_ATT_PREFIX = "th:";

	public AbstractDefaultElementModelProcessor(TemplateMode templateMode,
	        String dialectPrefix, String elementName, boolean prefixElementName,
	        String attributeName, boolean prefixAttributeName, int precedence) {
		super(templateMode, dialectPrefix, elementName, prefixElementName,
		    attributeName, prefixAttributeName, precedence);
	}

	protected String processModelAsString(IModel model) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < model.size(); i++) {
			builder.append(model.get(i));
		}
		return builder.toString();
	}

	protected IProcessableElementTag processElementTag(ITemplateContext context,
	        IModel model) {
		ITemplateEvent firstEvent = model.get(0);
		for (IProcessableElementTag tag : context.getElementStack()) {
			if (tag.toString().trim()
			    .equalsIgnoreCase(firstEvent.toString().trim())) {
				return tag;
			}
		}
		return null;
	}

	protected Map<String, String> processAttribute(
	        final ITemplateContext context, IProcessableElementTag tag) {
		Map<String, String> attMap = new HashMap<>();
		for (final IAttribute attribute : tag.getAllAttributes()) {
			String completeName = attribute.getAttributeCompleteName();
			if (completeName.startsWith(DYNAMIC_ATT_PREFIX)) {
				processDefaultAttribute(context, tag, attribute, attMap);
			} else {
				attMap.put(completeName, attribute.getValue());
			}
		}

		return attMap;
	}

	private void processDefaultAttribute(final ITemplateContext context,
	        final IProcessableElementTag tag, final IAttribute attribute,
	        Map<String, String> attMap) {

		try {

			final String attributeValue =
			        EscapedAttributeUtils.unescapeAttribute(
			            context.getTemplateMode(), attribute.getValue());

			final String newAttributeName =
			        attribute.getAttributeCompleteName().substring(3);

			if (newAttributeName.trim().isEmpty()) {
				return;
			}

			/*
			 * Obtain the parser
			 */
			final IStandardExpressionParser expressionParser =
			        StandardExpressions
			            .getExpressionParser(context.getConfiguration());

			/*
			 * Execute the expression, handling nulls in a way consistent with
			 * the rest of the Standard Dialect
			 */
			final Object expressionResult;
			if (attributeValue != null) {

				final IStandardExpression expression = expressionParser
				    .parseExpression(context, attributeValue);

				if (expression != null) {
					if (expression instanceof FragmentExpression) {
						// This is merely a FragmentExpression (not complex, not
						// combined with anything), so we can apply a shortcut
						// so that we don't require a "null" result for this
						// expression if the template does not exist. That will
						// save a call to resource.exists() which might be
						// costly.

						final FragmentExpression.ExecutedFragmentExpression executedFragmentExpression =
						        FragmentExpression
						            .createExecutedFragmentExpression(context,
						                (FragmentExpression) expression,
						                StandardExpressionExecutionContext.NORMAL);

						expressionResult = FragmentExpression
						    .resolveExecutedFragmentExpression(context,
						        executedFragmentExpression, true);
					} else {
						expressionResult = expression.execute(context);
					}
				} else {
					expressionResult = null;
				}

			} else {
				expressionResult = null;
			}

			/*
			 * If the result of this expression is NO-OP, there is nothing to
			 * execute
			 */
			if (expressionResult == NoOpToken.VALUE) {
				return;
			}

			final String newAttributeValue =
			        Objects.toString(expressionResult, null);

			/*
			 * Set the new value, removing the attribute completely if the
			 * expression evaluated to null
			 */
			if (newAttributeValue == null || newAttributeValue.length() == 0) {
				return;
			} else {
				attMap.put(newAttributeName, newAttributeValue);
			}

		} catch (final TemplateProcessingException e) {
			// This is a nice moment to check whether the execution raised an
			// error and, if so, add location information
			// Note this is similar to what is done at the superclass
			// AbstractElementTagProcessor, but we can be more
			// specific because we know exactly what attribute was being
			// executed and caused the error
			if (!e.hasTemplateName()) {
				e.setTemplateName(tag.getTemplateName());
			}
			if (!e.hasLineAndCol()) {
				e.setLineAndCol(attribute.getLine(), attribute.getCol());
			}
			throw e;
		} catch (final Exception e) {
			throw new TemplateProcessingException(
			    "Error during execution of processor '"
			            + StandardDefaultAttributesTagProcessor.class.getName()
			            + "'",
			    tag.getTemplateName(), attribute.getLine(), attribute.getCol(),
			    e);
		}

	}

}
