package com.programm.projects.scripty.module.tutorials;

import com.programm.projects.scripty.modules.api.SyCommand;
import com.programm.projects.scripty.modules.api.SyCommandInfo;

public interface TutCommand extends SyCommand, SyCommandInfo {

    @Override
    default SyCommandInfo info() {
        return this;
    }

    @Override
    default String type() {
        return "tutorial";
    }
}
