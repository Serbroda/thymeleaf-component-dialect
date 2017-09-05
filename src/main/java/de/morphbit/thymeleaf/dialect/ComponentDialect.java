package de.morphbit.thymeleaf.dialect;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import de.morphbit.thymeleaf.processor.element.ComponentAttributeProcessor;
import de.morphbit.thymeleaf.processor.element.ComponentElementProcessor;

public class ComponentDialect extends AbstractProcessorDialect {

	public static final String NAME = "Component Dialect";

	public ComponentDialect() {
		super(NAME, "tc", StandardDialect.PROCESSOR_PRECEDENCE);
	}

	@Override
	public Set<IProcessor> getProcessors(String dialectPrefix) {
		Set<IProcessor> processors = new HashSet<>();
		processors.add(new ComponentElementProcessor(dialectPrefix));
		processors.add(new ComponentAttributeProcessor(dialectPrefix));
		return processors;
	}

}
