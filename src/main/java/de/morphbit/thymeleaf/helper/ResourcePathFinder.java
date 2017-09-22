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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ResourcePathFinder {

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
	 * @param recursively
	 *            Search files recursively
	 * @return List of files as strings
	 */
	public List<String> findResourceFiles(boolean recursively) {
		return getResourceFiles(directory, new ArrayList<>(), recursively);
	}

	private List<String> getResourceFiles(String dir, List<String> files,
	        boolean recursively) {
		URL url = loader.getResource(dir);
		String path = url.getPath();
		for (File file : new File(path).listFiles()) {
			if (recursively && file.isDirectory()) {
				return getResourceFiles(concatPath(dir, file.getName()), files,
				    recursively);
			} else {
				files.add(concatPath(dir, file.getName()));
			}
		}
		return files;
	}

	private String concatPath(String path1, String path2) {
		if (path1 == null) {
			return path2;
		}
		return path1.replaceAll("[/|\\\\]*$", "") + "/"
		        + path2.replaceAll("^[/|\\\\]*", "");
	}
}
