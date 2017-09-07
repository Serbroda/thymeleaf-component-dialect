package de.morphbit.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.processor.element.AbstractElementModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

import de.morphbit.thymeleaf.helper.FragmentHelper;

public class ComponentElementProcessor extends AbstractElementModelProcessor {

	private static final String TAG_NAME = "component";
	private static final int PRECEDENCE = 1000;

	public ComponentElementProcessor(final String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, TAG_NAME, true, null, false,
		    PRECEDENCE);
	}

	// https://github.com/mhlx/mblog/blob/master/src/main/java/me/qyh/blog/web/template/thymeleaf/ThymeleafRenderExecutor.java
	@Override
	protected void doProcess(ITemplateContext context, IModel model,
	        IElementModelStructureHandler structureHandler) {

		IProcessableElementTag self = getSelfElement(context, model);
		String nameAttr = self.getAttributeValue("name");

		final IStandardExpressionParser parser = StandardExpressions
		    .getExpressionParser(context.getConfiguration());

		final FragmentExpression fragmentExpression;
		try {
			fragmentExpression = (FragmentExpression) parser
			    .parseExpression(context, "~{" + nameAttr + "}");
		} catch (final TemplateProcessingException e) {
			throw new IllegalArgumentException(
			    "Invalid template name specification: '" + nameAttr + "'", e);
		}

		final FragmentExpression.ExecutedFragmentExpression fragment =
		        FragmentExpression.createExecutedFragmentExpression(context,
		            fragmentExpression,
		            StandardExpressionExecutionContext.NORMAL);

		IModel fragmentModel = FragmentHelper.getFragmentModel(context,
		    nameAttr, structureHandler, "th", "fragment");

		FragmentExpression ex =
		        FragmentExpression.parseFragmentExpression(nameAttr);
		FragmentExpression.createExecutedFragmentExpression(context, null,
		    null);

		System.out.println(fragmentModel);
		structureHandler.setTemplateData(null);
	}

	private IProcessableElementTag getSelfElement(ITemplateContext context,
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

	private String dummyStructure() {
		return "<div> " + "    <h1>Header 1</h1> "
		        + "    <tc:component></tc:component> "
		        + "    <span>Some span</span> " + "</div> ";
	}

	private int findPos(IModel model, String fragment) {
		for (int i = 0; i < model.size(); i++) {
			if (model.get(i).toString().trim()
			    .equalsIgnoreCase("<tc:component/>")) {
				return i;
			}
		}
		return -1;
	}

}
