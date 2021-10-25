package com.programm.projects.scripty.app.modules;

import com.programm.projects.scripty.module.api.*;
import com.programm.projects.scripty.module.api.events.Get;
import com.programm.projects.scripty.module.api.events.OnMessage;
import com.programm.projects.scripty.module.api.events.PostSetup;
import com.programm.projects.scripty.module.api.events.PreSetup;

public class TestModule implements SyModule {

    @Get
    private SyContext ctx;

    @PreSetup
    public void start(){
        System.out.println("Start");
    }

    @Override
    public void setup(SyContext ctx) {
        System.out.println("Setup!");
    }

    @PostSetup
    public void afterSetup(){
        System.out.println();
    }

    @OnMessage
    public void onMessage(SyMessage msg){
        System.out.println(msg);
    }
}
