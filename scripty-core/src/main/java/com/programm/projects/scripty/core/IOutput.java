package com.programm.projects.scripty.core;

public interface IOutput {

    void print(String s);
    void newLine();

    void enable(boolean enable);

    default void println(String s){
        print(s);
        newLine();
    }

}
