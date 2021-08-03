package com.programm.projects.scripty.app;

import com.programm.projects.scripty.modules.api.SyCommand;
import com.programm.projects.scripty.modules.api.SyCommandInfo;

interface SySysCommand extends SyCommand, SyCommandInfo {

    @Override
    default SyCommandInfo info() {
        return this;
    }

    @Override
    default String type() {
        return "built-in";
    }
}
