package de.morphbit.thymeleaf.helper;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ResourcePathFinder {

	private final String directory;
	private final ClassLoader loader;

	public ResourcePathFinder(String directory) {
		this.directory = directory;
		this.loader = Thread.currentThread().getContextClassLoader();
	}

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
