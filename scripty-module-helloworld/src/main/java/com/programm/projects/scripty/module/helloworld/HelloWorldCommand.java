package com.programm.projects.scripty.module.helloworld;

import com.programm.projects.plugz.magic.Get;
import com.programm.projects.scripty.module.api.Command;
import com.programm.projects.scripty.module.api.IContext;

@Command
public class HelloWorldCommand {

    @Get
    private IContext ctx;

    @Command
    public void run(String name, String input){
        ctx.out().println("Hello World!");
    }

}
