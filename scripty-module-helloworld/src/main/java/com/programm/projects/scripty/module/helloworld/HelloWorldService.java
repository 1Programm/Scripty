package com.programm.projects.scripty.module.helloworld;

import com.programm.projects.plugz.magic.Get;
import com.programm.projects.plugz.magic.PostSetup;
import com.programm.projects.plugz.magic.PreSetup;
import com.programm.projects.plugz.magic.Service;
import com.programm.projects.scripty.module.api.IContext;

@Service
public class HelloWorldService {

    @Get
    private IContext ctx;

    @PreSetup
    public void preInit(){
        ctx.log().println("Pre Setup");
    }

    @PostSetup
    public void init(){
        ctx.log().println("Post Setup");
        ctx.run("hello-world bla");
    }

}
