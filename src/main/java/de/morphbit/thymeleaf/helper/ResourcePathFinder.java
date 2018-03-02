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

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.*;
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
        return getResourceFiles(directory,  recursively);
    }

    private List<String> getResourceFiles(String dir, boolean recursively)
    {
        List<String> files = new ArrayList<>();
        try{
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(loader);
            Resource[] resources = resolver.getResources("classpath*:/"+ dir + "/**/*.*");
            String basePath = loader.getResource(dir).getPath();
            for (Resource resource: resources){

                String pathRelativeToDir = dir + "/" + resource.getURL().getPath().replace(basePath + "/", "");
                files.add(pathRelativeToDir);
            }

        }catch (IOException ex){
            System.err.println("Could not process resource pattern.");
        }

        return files;
    }
}
