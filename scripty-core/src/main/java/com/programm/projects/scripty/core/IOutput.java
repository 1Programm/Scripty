package com.programm.projects.scripty.core;

public interface IOutput {

    void print(String s);
    void newLine();

    boolean enabled();

    default void println(String s){
        print(s);
        newLine();
    }

}
