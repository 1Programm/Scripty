package com.programm.projects.scripty.app.files;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class ModuleConfigFile {

    static final String KEY_NAME = "name";
    static final String KEY_VERSION = "version";
    static final String KEY_AUTHORS = "authors.author";
    static final String KEY_ROOT_FOLDER = "root-folder";
    static final String KEY_MODULE_ENTRY = "module-entry";
    static final String KEY_FILES = "files.file";

    private final String name;
    private final String version;
    private final List<String> authors;
    private final String rootFolder;
    private final String moduleEntry;
    private final List<String> files;

}
