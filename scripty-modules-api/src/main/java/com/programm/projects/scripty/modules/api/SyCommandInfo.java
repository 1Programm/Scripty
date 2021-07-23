package com.programm.projects.scripty.modules.api;

import com.programm.projects.scripty.core.IOutput;

public interface SyCommandInfo {

    String type();

    void printHelp(IOutput out);

}
