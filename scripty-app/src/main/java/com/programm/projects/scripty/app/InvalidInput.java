package com.programm.projects.scripty.app;

import com.programm.projects.ioutils.args.analyzer.Args;
import com.programm.projects.ioutils.console.api.ICommand;
import com.programm.projects.ioutils.console.api.IContext;

public class InvalidInput implements ICommand {

    @Override
    public void run(IContext ctx, Args args) {
        ctx.out().println("{} is not a command!", args.size() == 0 ? "" : args.get(0));
    }
}
