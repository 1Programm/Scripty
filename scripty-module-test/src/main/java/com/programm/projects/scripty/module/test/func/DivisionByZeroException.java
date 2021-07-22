package com.programm.projects.scripty.module.test.func;

public class DivisionByZeroException extends RuntimeException{

    public DivisionByZeroException() {
    }

    public DivisionByZeroException(String message) {
        super(message);
    }

    public DivisionByZeroException(String message, Throwable cause) {
        super(message, cause);
    }

    public DivisionByZeroException(Throwable cause) {
        super(cause);
    }
}
