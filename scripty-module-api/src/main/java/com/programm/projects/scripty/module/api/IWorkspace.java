package com.programm.projects.scripty.module.api;

public interface IWorkspace {

    /**
     * @return A global store where all commands and services can access it.
     */
    IStore globalStore();

    /**
     * Checks if a store with a name exists.
     * @param name the name of the store
     * @return true if there exists a store with the specified name
     */
    boolean exists(String name);

    /**
     * Creates and saves a new store with the specified name or get it if there already exists a store with that name.
     * @return A store object which can access and save private values to the caller.
     */
    IStore store(String name) throws StoreException;

}
