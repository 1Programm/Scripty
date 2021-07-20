package com.programm.projects.scripty.module.test;

import com.programm.projects.scripty.modules.api.Module;
import com.programm.projects.scripty.modules.api.ScriptyContext;

public class Test extends Module {

    @Override
    protected void onInit(ScriptyContext ctx) {
        ctx.out().println("Initializing Test");
    }
}
