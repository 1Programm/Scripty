package com.programm.projects.scripty.modules.api;

import com.programm.projects.scripty.modules.api.ex.InvalidNameException;

public interface SyCommandManager {

    /**
     * Method to register a command.
     * @param name The name with which the command should be called.
     * @param command The actual command.
     * @throws InvalidNameException when [name] is already bound to a command or is in an invalid format (No special characters like: '%', '$', ...).
     * @throws NullPointerException when [name] or [command] is null;
     */
    void registerCommand(String name, SyCommand command) throws InvalidNameException;

}
