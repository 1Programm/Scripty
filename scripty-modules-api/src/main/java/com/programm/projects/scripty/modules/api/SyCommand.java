package com.programm.projects.scripty.modules.api;

import com.programm.projects.scripty.core.Args;

public interface SyCommand {

    /**
     * Method to execute this command.
     * @param ctx the context to access scripty functionality
     * @param io the in - and output manager
     * @param name the name with which this command was called.
     * @param args the arguments given to this command when executing.
     * @throws CommandExecutionException when invalid arguments were given or some other failure happened.
     */
    void run(SyContext ctx, SyIO io, String name, Args args) throws CommandExecutionException;

    /**
     * Method to collect information about a command.
     * @return The Command-Information-Object.
     */
    default SyCommandInfo info(){
        return null;
    }

}
