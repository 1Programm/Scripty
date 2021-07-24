package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyIO;

public class CmdModulesDev implements SySysCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String name, Args args) throws CommandExecutionException {
        io.out().print("What is your name: ");
        String answer = io.in().next();

        io.out().println("Your name is: " + answer);
    }

    @Override
    public void printHelp(IOutput out) {

    }
}
