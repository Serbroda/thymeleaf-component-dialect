package de.morphbit.thymeleaf.dialect;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import de.morphbit.thymeleaf.model.ThymeleafComponent;
import de.morphbit.thymeleaf.processor.ComponentNamedElementProcessor;
import de.morphbit.thymeleaf.processor.OnceAttributeTagProcessor;

public class ComponentDialect extends AbstractProcessorDialect {

	public static final String NAME = "Component Dialect";
	public static final String PREFIX = "tc";
	public static final int PRECEDENCE = 1000;

	private final Set<ThymeleafComponent> components;

	public ComponentDialect() {
		this(null);
	}

	public ComponentDialect(Set<ThymeleafComponent> components) {
		super(NAME, PREFIX, PRECEDENCE);
		this.components = components;
	}

	@Override
	public Set<IProcessor> getProcessors(String dialectPrefix) {
		Set<IProcessor> processors = new HashSet<>();
		processors.add(new OnceAttributeTagProcessor(dialectPrefix));

		if (this.components != null) {
			for (ThymeleafComponent comp : this.components) {
				processors.add(new ComponentNamedElementProcessor(dialectPrefix,
				    comp.getName(), comp.getFragmentTemplate()));
			}
		}
		return processors;
	}

}
