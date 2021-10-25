package com.programm.projects.scripty.app.files;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class RepoConfigFile {

    static final String KEY_NAME = "name";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_MODULE_NAMES = "module.name";
    static final String KEY_MODULE_URLS = "module.url";

    private final String name;
    private final String description;
    private final List<String> moduleNames;
    private final List<String> moduleUrls;

}
