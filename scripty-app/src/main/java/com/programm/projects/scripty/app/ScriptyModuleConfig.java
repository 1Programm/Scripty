package com.programm.projects.scripty.app;

import com.programm.projects.scripty.modules.api.SyModuleConfig;

import java.util.List;

public class ScriptyModuleConfig implements SyModuleConfig {

    String name;
    String version;
    List<String> authors;

    String rootFolder;
    String rootPackage;
    final List<String> files;
    String moduleEntry;

    public ScriptyModuleConfig(String name, String version, List<String> authors, String rootFolder, String rootPackage, List<String> files, String moduleEntry) {
        this.name = name;
        this.version = version;
        this.authors = authors;
        this.rootFolder = rootFolder;
        this.rootPackage = rootPackage;
        this.files = files;
        this.moduleEntry = moduleEntry;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public List<String> authors() {
        return authors;
    }

    @Override
    public String rootFolder() {
        return rootFolder;
    }

    @Override
    public String rootPackage() {
        return rootPackage;
    }

    @Override
    public List<String> files() {
        return files;
    }

    @Override
    public String moduleEntry() {
        return moduleEntry;
    }
}
