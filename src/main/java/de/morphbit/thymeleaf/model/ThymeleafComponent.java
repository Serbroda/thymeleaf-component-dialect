/*
 * Copyright 2017, Danny Rottstegge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.morphbit.thymeleaf.model;

/**
 * Represents a reusable Thymeleaf component that maps a custom tag name to a
 * Thymeleaf fragment template.
 *
 * <p>
 * Example: A component with name {@code "panel"} and fragment template
 * {@code "components/panel :: panel"} will be rendered when the tag
 * {@code <tc:panel>} is used in a template.
 * </p>
 *
 * @param name
 *            the component tag name used in templates (e.g. {@code "panel"})
 * @param fragmentTemplate
 *            the Thymeleaf fragment reference without parameters (e.g.
 *            {@code "components/panel :: panel"})
 */
public record ThymeleafComponent(String name, String fragmentTemplate) {
}
