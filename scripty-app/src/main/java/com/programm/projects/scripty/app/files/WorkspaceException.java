package com.programm.projects.scripty.app.files;

public class WorkspaceException extends Exception {

    public WorkspaceException() {
    }

    public WorkspaceException(String message) {
        super(message);
    }

    public WorkspaceException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkspaceException(Throwable cause) {
        super(cause);
    }
}
