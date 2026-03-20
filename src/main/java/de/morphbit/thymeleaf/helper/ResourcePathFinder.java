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

package de.morphbit.thymeleaf.helper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourcePathFinder {

	private static final Logger LOG =
	        LoggerFactory.getLogger(ResourcePathFinder.class);

	private final String directory;
	private final ClassLoader loader;

	/**
	 * Constructor
	 *
	 * @param directory
	 *            Base directory to search resource files (e.g.
	 *            templates/components)
	 */
	public ResourcePathFinder(String directory) {
		this.directory = directory;
		this.loader = Thread.currentThread().getContextClassLoader();
	}

	/**
	 * Searches for resource files
	 *
	 * Search files recursively
	 *
	 * @return List of files as strings
	 */
	public List<String> findResourceFiles() {
		return getResourceFiles(directory);
	}

	private List<String> getResourceFiles(String dir) {
		List<String> files = new ArrayList<>();
		try {
			Enumeration<URL> resources = loader.getResources(dir);
			while (resources.hasMoreElements()) {
				URL resourceUrl = resources.nextElement();
				URI uri = resourceUrl.toURI();

				Path path;
				if ("jar".equals(uri.getScheme())) {
					FileSystem fileSystem;
					try {
						fileSystem = FileSystems.newFileSystem(uri,
						    Collections.emptyMap());
					} catch (FileSystemAlreadyExistsException e) {
						fileSystem = FileSystems.getFileSystem(uri);
					}
					path = fileSystem.getPath(dir);
				} else {
					path = Paths.get(uri);
				}

				try (Stream<Path> walk = Files.walk(path)) {
					walk.filter(Files::isRegularFile).forEach(p -> {
						String relativePath =
						    dir + "/" + path.relativize(p).toString();
						files.add(relativePath);
					});
				}
			}
		} catch (IOException | URISyntaxException ex) {
			LOG.error("Could not process resource pattern. {}", ex.getMessage(),
			    ex);
		}

		return files;
	}
}
