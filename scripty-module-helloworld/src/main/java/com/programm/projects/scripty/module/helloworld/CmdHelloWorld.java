package com.programm.projects.scripty.module.helloworld;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.modules.api.SyCommand;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyIO;

public class CmdHelloWorld implements SyCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String commandName, Args args) {
        io.out().println("Hello World!");
    }
}
