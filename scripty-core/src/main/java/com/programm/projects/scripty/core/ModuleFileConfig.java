package com.programm.projects.scripty.core;

import java.util.List;

public class ModuleFileConfig {

    private final String name;
    private final String version;
    private final List<String> authors;

    private final String modulePackage;
    private final List<String> moduleFiles;
    private final String moduleEntry;

    public ModuleFileConfig(String name, String version, List<String> authors, String modulePackage, List<String> moduleFiles, String moduleEntry) {
        this.name = name;
        this.version = version;
        this.authors = authors;
        this.modulePackage = modulePackage;
        this.moduleFiles = moduleFiles;
        this.moduleEntry = moduleEntry;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getModulePackage() {
        return modulePackage;
    }

    public String getModulePackagePath(){
        return modulePackage.replaceAll("\\.", "/");
    }

    public List<String> getModuleFiles() {
        return moduleFiles;
    }

    public String getModuleEntry() {
        return moduleEntry;
    }
}
