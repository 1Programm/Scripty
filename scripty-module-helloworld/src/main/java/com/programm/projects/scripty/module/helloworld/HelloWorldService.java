package com.programm.projects.scripty.module.helloworld;

import com.programm.projects.scripty.module.api.IContext;
import com.programm.projects.scripty.module.api.Service;
import com.programm.projects.scripty.module.api.events.Get;
import com.programm.projects.scripty.module.api.events.PostSetup;

@Service
public class HelloWorldService {

    @Get
    private IContext ctx;

    @PostSetup
    public void init(){
        ctx.log().println("Init from [hello-world] module!");
    }

}
