package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyContext;

class CmdModulesUpdate implements SySysCommand {

    @Override
    public void run(SyContext ctx, String name, Args args) throws CommandExecutionException {

    }

    @Override
    public void printHelp(IOutput out) {
        //TODO
        out.println("TODO");
    }
}
