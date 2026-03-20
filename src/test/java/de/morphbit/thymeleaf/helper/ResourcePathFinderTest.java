package de.morphbit.thymeleaf.helper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ResourcePathFinderTest {

	@Test
	public void itShouldFindResourceFiles() {
		ResourcePathFinder finder =
		        new ResourcePathFinder("templates/components");
		List<String> files = finder.findResourceFiles();

		assertFalse(files.isEmpty());
	}

	@Test
	public void itShouldFindHtmlFiles() {
		ResourcePathFinder finder =
		        new ResourcePathFinder("templates/components");
		List<String> files = finder.findResourceFiles();

		assertTrue(files.stream()
		    .anyMatch(f -> f.contains("link_component.html")));
		assertTrue(files.stream()
		    .anyMatch(f -> f.contains("once_component.html")));
		assertTrue(files.stream()
		    .anyMatch(f -> f.contains("valid_input.html")));
	}

	@Test
	public void itShouldReturnRelativePaths() {
		ResourcePathFinder finder =
		        new ResourcePathFinder("templates/components");
		List<String> files = finder.findResourceFiles();

		for (String file : files) {
			assertTrue(file.startsWith("templates/components/"),
			    "Path should start with base directory: " + file);
		}
	}

	@Test
	public void itShouldReturnEmptyListForNonExistentDirectory() {
		ResourcePathFinder finder =
		        new ResourcePathFinder("nonexistent/directory");
		List<String> files = finder.findResourceFiles();

		assertTrue(files.isEmpty());
	}
}
