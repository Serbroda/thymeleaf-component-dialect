package de.morphbit.thymeleaf.processor;

import java.util.Map;
import java.util.Set;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.TemplateManager;
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

		TemplateManager manager =
		        new TemplateManager(context.getConfiguration());
		manager.parseString(null, self.toString(), 0, 0, TemplateMode.HTML,
		    false);

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

		final String templateName =
		        FragmentExpression.resolveTemplateName(fragment);
		final Set<String> markupSelectors =
		        FragmentExpression.resolveFragments(fragment);
		final Map<String, Object> nameFragmentParameters =
		        fragment.getFragmentParameters();

		if (nameFragmentParameters != null) {

			if (fragment.hasSyntheticParameters()) {
				// We cannot allow synthetic parameters because there is no
				// way to specify them at the template
				// engine execution!
				throw new IllegalArgumentException(
				    "Parameters in a view specification must be named (non-synthetic): '"
				            + nameAttr + "'");
			}

			// context.setVariables(nameFragmentParameters);

		}

		// Model
		IModel fragmentModel = FragmentHelper.getFragmentModel(context,
		    nameAttr, structureHandler, "th", "fragment");

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
