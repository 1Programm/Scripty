package com.programm.projects.scripty.app.cmds;

import com.programm.projects.ioutils.args.analyzer.Args;
import com.programm.projects.ioutils.console.api.ICommand;
import com.programm.projects.ioutils.console.api.IContext;

public class StatusCmd implements ICommand {

    @Override
    public void run(IContext ctx, Args args) {
        ctx.out().print("Checking status");

        try {
            for(int i=0;i<3;i++) {
                Thread.sleep(1000);
                ctx.out().print(".");
            }
        }
        catch (InterruptedException ignored){}

        ctx.out().newLine();
        ctx.out().println("Status: ${green}(OK)");
    }
}
