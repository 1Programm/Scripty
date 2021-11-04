package com.programm.projects.scripty.app.commands;

public class CommandSetupException extends Exception {

    public CommandSetupException() {
    }

    public CommandSetupException(String message) {
        super(message);
    }

    public CommandSetupException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandSetupException(Throwable cause) {
        super(cause);
    }
}
