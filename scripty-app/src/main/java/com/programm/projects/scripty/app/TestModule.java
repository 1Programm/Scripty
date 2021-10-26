package com.programm.projects.scripty.app;

import com.programm.projects.scripty.module.api.*;
import com.programm.projects.scripty.module.api.events.Get;
import com.programm.projects.scripty.module.api.events.PostSetup;
import com.programm.projects.scripty.module.api.events.PreSetup;

@Service
public class TestModule {

    @Get
    private IContext ctx;

    @PreSetup
    public void preSetup(){
        ctx.out().println("Start");
    }

    @PostSetup
    public void afterSetup(){
        ctx.out().println("");
    }

}
