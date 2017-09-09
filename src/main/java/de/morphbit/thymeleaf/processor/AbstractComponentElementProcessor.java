package de.morphbit.thymeleaf.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.processor.element.AbstractElementModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.NoOpToken;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.processor.StandardDefaultAttributesTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;

import de.morphbit.thymeleaf.helper.WithHelper;

public abstract class AbstractComponentElementProcessor extends AbstractElementModelProcessor {

	private static final String DYNAMIC_ATT_PREFIX = "th:";

	protected static final String THYMELEAF_FRAGMENT_PREFIX = "th";
	protected static final String THYMELEAF_FRAGMENT_ATTRIBUTE = "fragment";
	protected static final String REPLACE_CONTENT_TAG = "tc:content";

	public AbstractComponentElementProcessor(String dialectPrefix, String elementName, int precedence) {
		super(TemplateMode.HTML, dialectPrefix, elementName, true, null, false, precedence);
	}

	protected IProcessableElementTag processElementTag(ITemplateContext context, IModel model) {
		ITemplateEvent firstEvent = model.get(0);
		String fixedFirstEvent = normalizeTag(firstEvent.toString());

		for (IProcessableElementTag tag : context.getElementStack()) {
			String fixedTag = normalizeTag(tag.toString());

			if (fixedTag.equals(fixedFirstEvent)) {
				return tag;
			}
		}
		return null;
	}

	protected void processVariables(Map<String, String> attrMap, ITemplateContext context,
			IElementModelStructureHandler structureHandler) {
		processVariables(attrMap, context, structureHandler, new HashSet<>());
	}

	protected void processVariables(Map<String, String> attrMap, ITemplateContext context,
			IElementModelStructureHandler structureHandler, Set<String> excludeAttr) {
		for (java.util.Map.Entry<String, String> entry : attrMap.entrySet()) {
			if (excludeAttr.contains(entry.getKey()) || entry.getKey().startsWith(this.getDialectPrefix() + ":")) {
				continue;
			}
			WithHelper.processWith(context, entry.getKey() + "=" + entry.getValue(), structureHandler);
		}
	}

	private String normalizeTag(final String tag) {
		String normalized = tag.trim().toLowerCase();
		normalized = normalized.replaceAll("[^<]" + this.getDialectPrefix() + ":[\\d|\\w]*=\"[^\"]*\"", "");
		normalized = normalized.replaceAll("\\n", "");
		normalized = normalized.replaceAll("\\s", "");
		return normalized;
	}

	protected Map<String, String> processAttribute(final ITemplateContext context, IProcessableElementTag tag) {
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

	protected IModel mergeModel(IModel fragment, IModel body, final String replaceTag) {
		IModel mergedModel = insert(fragment, body, replaceTag);
		mergedModel = remove(mergedModel, replaceTag);
		mergedModel = remove(mergedModel, replaceTag);
		return mergedModel;
	}

	private IModel insert(IModel fragment, IModel body, final String replaceTag) {
		IModel mergedModel = fragment.cloneModel();
		int size = mergedModel.size();
		ITemplateEvent event = null;
		for (int i = 0; i < size; i++) {
			event = mergedModel.get(i);
			if (event instanceof IOpenElementTag) {
				if (event.toString().contains(replaceTag)) {
					mergedModel.insertModel(i, body);
					break;
				}
			}
		}
		return mergedModel;
	}

	private IModel remove(IModel fragment, final String replaceTag) {
		IModel mergedModel = fragment.cloneModel();
		int size = mergedModel.size();
		ITemplateEvent event = null;
		for (int i = 0; i < size; i++) {
			event = mergedModel.get(i);
			if (event instanceof IOpenElementTag || event instanceof ICloseElementTag) {
				if (event.toString().contains(replaceTag)) {
					mergedModel.remove(i);
					break;
				}
			}
		}
		return mergedModel;
	}

	private void processDefaultAttribute(final ITemplateContext context, final IProcessableElementTag tag,
			final IAttribute attribute, Map<String, String> attMap) {

		try {

			final String attributeValue = EscapedAttributeUtils.unescapeAttribute(context.getTemplateMode(),
					attribute.getValue());

			final String newAttributeName = attribute.getAttributeCompleteName().substring(3);

			if (newAttributeName.trim().isEmpty()) {
				return;
			}

			/*
			 * Obtain the parser
			 */
			final IStandardExpressionParser expressionParser = StandardExpressions
					.getExpressionParser(context.getConfiguration());

			/*
			 * Execute the expression, handling nulls in a way consistent with
			 * the rest of the Standard Dialect
			 */
			final Object expressionResult;
			if (attributeValue != null) {

				final IStandardExpression expression = expressionParser.parseExpression(context, attributeValue);

				if (expression != null) {
					if (expression instanceof FragmentExpression) {
						// This is merely a FragmentExpression (not complex, not
						// combined with anything), so we can apply a shortcut
						// so that we don't require a "null" result for this
						// expression if the template does not exist. That will
						// save a call to resource.exists() which might be
						// costly.

						final FragmentExpression.ExecutedFragmentExpression executedFragmentExpression = FragmentExpression
								.createExecutedFragmentExpression(context, (FragmentExpression) expression,
										StandardExpressionExecutionContext.NORMAL);

						expressionResult = FragmentExpression.resolveExecutedFragmentExpression(context,
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

			final String newAttributeValue = Objects.toString(expressionResult, null);

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
			throw new TemplateProcessingException("Error during execution of processor '"
					+ StandardDefaultAttributesTagProcessor.class.getName() + "'", tag.getTemplateName(),
					attribute.getLine(), attribute.getCol(), e);
		}

	}

}
