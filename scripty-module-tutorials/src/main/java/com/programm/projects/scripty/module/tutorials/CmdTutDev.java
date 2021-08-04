package com.programm.projects.scripty.module.tutorials;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyIO;

public class CmdTutDev implements TutCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String commandName, Args args) throws CommandExecutionException {
        io.out().println("TODO");
    }

    @Override
    public void printHelp(IOutput out, String commandName) {
        out.println("TODO");
    }
}
