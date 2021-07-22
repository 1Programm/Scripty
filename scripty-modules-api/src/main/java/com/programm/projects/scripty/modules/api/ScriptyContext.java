package com.programm.projects.scripty.modules.api;

import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.ex.InvalidNameException;

public interface ScriptyContext {

    /**
     * @return The standard console output which can always be seen.
     */
    IOutput out();

    /**
     * @return The logging output which can only be seen when the -i / --info flag was given to scripty
     */
    IOutput log();

    /**
     * @return The error logging. Use to print error messages when the error itself is nothing 'internat'. Otherwise throw Exceptions.
     */
    IOutput err();


    /**
     * Method to register a command.
     * @param name The name with which the command should be called.
     * @param command The actual command.
     * @throws InvalidNameException when [name] is already bound to a command or is in an invalid format (No special characters like: '%', '$', ...).
     * @throws NullPointerException when [name] or [command] is null;
     */
    void registerCommand(String name, SyCommand command) throws InvalidNameException;

}
