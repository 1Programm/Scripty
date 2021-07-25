package com.programm.projects.scripty.modules.api;

public interface SyWorkspace {

    /**
     * @return The scripty-home path.
     */
    String workspacePath();

    /**
     * @return The path of the executing user.
     */
    String userPath();

}
