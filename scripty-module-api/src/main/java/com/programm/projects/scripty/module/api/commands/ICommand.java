package com.programm.projects.scripty.module.api.commands;

public interface ICommand {

    String name();
    void run(String... args);

}
