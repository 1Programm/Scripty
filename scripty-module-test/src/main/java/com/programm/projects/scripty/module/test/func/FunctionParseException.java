package com.programm.projects.scripty.module.test.func;

public class FunctionParseException extends RuntimeException{

    public FunctionParseException() {
    }

    public FunctionParseException(String message) {
        super(message);
    }

    public FunctionParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public FunctionParseException(Throwable cause) {
        super(cause);
    }
}
