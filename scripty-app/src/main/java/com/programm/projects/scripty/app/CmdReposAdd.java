package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyIO;

import java.io.IOException;

public class CmdReposAdd implements SySysCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String commandName, Args args) throws CommandExecutionException {
        ScriptyCoreContext context = (ScriptyCoreContext) ctx;

        if(args.size() == 0){
            io.err().println("Invalid args. Expected '" + commandName + " [url]'");
            return;
        }

        String url = args.get(0);

        try {
            context.workspace.addRepository(url);
            io.out().println("Successfully added [" + url + "]!");
        }
        catch (IOException e){
            throw new CommandExecutionException(e.getMessage(), e);
        }
    }

    @Override
    public void printHelp(IOutput out, String commandName) {
        out.println("--- Command [" + commandName + "] ---");
        out.println("A command to add a repository url to local repositories.");
        out.newLine();

        out.println("# Usage:");
        out.println("|");
        out.println("| " + commandName + " [url]    -> Add a new repository for discovery from [url].");
    }
}
