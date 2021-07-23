package com.programm.projects.scripty.modules.api;

import com.programm.projects.scripty.core.Args;

public interface SyCommand {

    /**
     * Method to execute this command.
     * @param ctx the context to access scripty functionality
     * @param name the name with which this command was called.
     * @param args the arguments given to this command when executing.
     * @throws CommandExecutionException when invalid arguments were given or some other failure happened.
     */
    void run(SyContext ctx, String name, Args args) throws CommandExecutionException;

}
