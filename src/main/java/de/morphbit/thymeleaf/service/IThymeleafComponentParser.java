package de.morphbit.thymeleaf.service;

import java.util.Set;

import de.morphbit.thymeleaf.model.ThymeleafComponent;

public interface IThymeleafComponentParser {

	Set<ThymeleafComponent> parse();
}
