package com.programm.projects.scripty.app.files;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public class ModulesConfigFile {

    private final Map<String, String> moduleNameToUrlMap;

}
