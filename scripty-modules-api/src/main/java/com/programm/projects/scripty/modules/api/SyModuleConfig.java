package com.programm.projects.scripty.modules.api;

import java.util.List;

public interface SyModuleConfig {

    String name();
    String version();
    List<String> authors();
    String rootFolder();
    String rootPackage();
    List<String> files();
    String moduleEntry();

}
