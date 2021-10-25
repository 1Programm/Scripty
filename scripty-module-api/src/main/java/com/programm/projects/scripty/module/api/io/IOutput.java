package com.programm.projects.scripty.module.api.io;

public interface IOutput {

    void print(String s, Object... args);
    void newLine();

    default void println(String s, Object... args){
        print(s, args);
        newLine();
    }

}
