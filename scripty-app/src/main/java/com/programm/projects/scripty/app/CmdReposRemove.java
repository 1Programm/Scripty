package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyIO;

import java.io.IOException;

public class CmdReposRemove implements SySysCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String commandName, Args args) throws CommandExecutionException {
        ScriptyCoreContext context = (ScriptyCoreContext) ctx;

        if(args.size() == 0){
            io.err().println("Invalid args. Expected '" + commandName + " [url]'");
            return;
        }

        String url = args.get(0);

        try {
            context.workspace.removeRepository(url);
            io.out().println("Successfully removed [" + url + "]!");
        }
        catch (IOException e){
            throw new CommandExecutionException(e.getMessage(), e);
        }
    }

    @Override
    public void printHelp(IOutput out) {
        out.println("TODO");
        //TODO: help for repos-remove
    }
}
