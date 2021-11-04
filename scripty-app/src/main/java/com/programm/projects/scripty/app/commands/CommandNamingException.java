package com.programm.projects.scripty.app.commands;

public class CommandNamingException extends Exception {

    public CommandNamingException() {
    }

    public CommandNamingException(String message) {
        super(message);
    }

    public CommandNamingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandNamingException(Throwable cause) {
        super(cause);
    }
}
