package com.programm.projects.scripty.modules.api;

import com.programm.projects.scripty.core.Args;

public interface SyContext {

    /**
     * @return The workspace which handles File Management
     */
    SyWorkspace workspace();

    /**
     * Method to run some command.
     * @param command The name of the command.
     * @param args The arguments which should be processed.
     * @throws CommandExecutionException when there are invalid commands or some other 'internal' error happened.
     */
    void run(String command, Args args) throws CommandExecutionException;

    default void run(String command, String... args) throws CommandExecutionException{
        run(command, new Args(args));
    }

}
