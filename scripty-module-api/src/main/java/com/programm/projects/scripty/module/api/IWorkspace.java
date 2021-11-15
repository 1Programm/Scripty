package com.programm.projects.scripty.module.api;

public interface IWorkspace {

    /**
     * @return A global store where all commands and services can access it.
     */
    IStore globalStore();

    /**
     * @return A store object which can access and save private values to the caller.
     */
    IStore privateStore();

}
