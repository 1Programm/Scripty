package com.programm.projects.scripty.app.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ConfigFileLoader {

    private static List<String> readStringList(Properties properties, String key){
        List<String> list = new ArrayList<>();

        int i=0;
        String val;
        while((val = properties.getProperty(key + i)) != null) {
            list.add(val);
            i++;
        }

        return list;
    }

    public static ModuleConfigFile moduleConfigFileLoader(File file) throws IOException{
        Properties properties = new Properties();
        try (FileReader reader = new FileReader(file)) {
            properties.load(reader);
        }

        return moduleConfigFileLoader(properties);
    }

    public static ModuleConfigFile moduleConfigFileLoader(Properties properties){
        String name = properties.getProperty(ModuleConfigFile.KEY_NAME);
        String version = properties.getProperty(ModuleConfigFile.KEY_VERSION);
        List<String> authors = readStringList(properties, ModuleConfigFile.KEY_AUTHORS);
        String rootFolder = properties.getProperty(ModuleConfigFile.KEY_ROOT_FOLDER);
        String moduleEntry = properties.getProperty(ModuleConfigFile.KEY_MODULE_ENTRY);
        List<String> files = readStringList(properties, ModuleConfigFile.KEY_FILES);

        return new ModuleConfigFile(name, version, authors, rootFolder, moduleEntry, files);
    }

    public static ModulesConfigFile modulesConfigFileLoader(File file) throws IOException{
        Properties properties = new Properties();
        try (FileReader reader = new FileReader(file)) {
            properties.load(reader);
        }

        return modulesConfigFileLoader(properties);
    }

    public static ModulesConfigFile modulesConfigFileLoader(Properties properties){
        Map<String, String> moduleNameToUrlMap = new HashMap<>();

        for(String key : properties.stringPropertyNames()){
            moduleNameToUrlMap.put(key, properties.getProperty(key));
        }

        return new ModulesConfigFile(moduleNameToUrlMap);
    }

    public static RepoConfigFile repoConfigFileLoader(File file) throws IOException{
        Properties properties = new Properties();
        try (FileReader reader = new FileReader(file)) {
            properties.load(reader);
        }

        return repoConfigFileLoader(properties);
    }

    public static RepoConfigFile repoConfigFileLoader(Properties properties){
        String name = properties.getProperty(RepoConfigFile.KEY_NAME);
        String description = properties.getProperty(RepoConfigFile.KEY_DESCRIPTION);
        List<String> moduleNames = readStringList(properties, RepoConfigFile.KEY_MODULE_NAMES);
        List<String> moduleUrls = readStringList(properties, RepoConfigFile.KEY_MODULE_URLS);

        return new RepoConfigFile(name, description, moduleNames, moduleUrls);
    }

    public static ReposConfigFile reposConfigFileLoader(File file) throws IOException{
        Properties properties = new Properties();
        try (FileReader reader = new FileReader(file)) {
            properties.load(reader);
        }

        return reposConfigFileLoader(properties);
    }

    public static ReposConfigFile reposConfigFileLoader(Properties properties){
        List<String> repoUrls = readStringList(properties, ReposConfigFile.KEY_REPO_URLS);

        return new ReposConfigFile(repoUrls);
    }

}
