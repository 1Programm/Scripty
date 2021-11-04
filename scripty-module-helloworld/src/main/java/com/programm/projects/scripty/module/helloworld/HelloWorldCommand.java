package com.programm.projects.scripty.module.helloworld;

import com.programm.projects.scripty.module.api.Command;
import com.programm.projects.scripty.module.api.IContext;
import com.programm.projects.scripty.module.api.events.Get;

@Command
public class HelloWorldCommand {

    @Command
    public void run(@Get IContext ctx, String... args){
        ctx.out().println("Hello World!");
    }

}
