package de.morphbit.thymeleaf.processor;

import org.thymeleaf.standard.processor.AbstractStandardFragmentInsertionTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

public class ComponentAttributeProcessor
        extends AbstractStandardFragmentInsertionTagProcessor {

	public static final int PRECEDENCE = 100;
	public static final String ATTR_NAME = "name";

	public ComponentAttributeProcessor(final String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, ATTR_NAME, PRECEDENCE, true);
	}

}
