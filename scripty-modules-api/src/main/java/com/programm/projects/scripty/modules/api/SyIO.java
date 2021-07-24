package com.programm.projects.scripty.modules.api;

import com.programm.projects.scripty.core.IInput;
import com.programm.projects.scripty.core.IOutput;

public interface SyIO {

    /**
     * @return The standard console output which can always be seen.
     */
    IOutput out();

    /**
     * @return The logging output which can only be seen when the -i / --info flag was given to scripty
     */
    IOutput log();

    /**
     * @return The error logging. Use to print error messages when the error itself is nothing 'internal'. Otherwise throw Exceptions.
     */
    IOutput err();

    /**
     * @return The input handle. Use to get access to user input.
     */
    IInput in();

}
