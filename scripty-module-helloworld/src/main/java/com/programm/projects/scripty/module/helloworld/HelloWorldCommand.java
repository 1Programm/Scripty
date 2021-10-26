package com.programm.projects.scripty.module.helloworld;

import com.programm.projects.scripty.module.api.Command;

@Command
public class HelloWorldCommand {

    @Command
    public void run(String... args){
        System.out.println("Hello World!");
    }

}
