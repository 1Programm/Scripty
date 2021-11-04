package com.programm.projects.scripty.module.helloworld;

import com.programm.projects.scripty.module.api.IContext;
import com.programm.projects.scripty.module.api.Service;
import com.programm.projects.scripty.module.api.commands.ICommand;
import com.programm.projects.scripty.module.api.events.Get;
import com.programm.projects.scripty.module.api.events.PostSetup;
import com.programm.projects.scripty.module.api.events.PreSetup;

@Service
public class HelloWorldService {

    @Get
    private IContext ctx;

    @Get("hello-world")
    private ICommand hello;

    @Get
    private HelloWorldCommand cmd;

    @PreSetup
    public void preInit(){
        ctx.log().println("Pre: {}", hello);
    }

    @PostSetup
    public void init(){
        ctx.log().println("Post: {}", hello.name());
        hello.run();
        cmd.run(ctx);
    }

}
