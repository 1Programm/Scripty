package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyIO;

import java.io.IOException;
import java.util.List;

public class CmdReposList implements SySysCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String commandName, Args args) throws CommandExecutionException {
        ScriptyCoreContext context = (ScriptyCoreContext) ctx;

        try {
            List<String> repos = context.workspace.listRepositories();

            if(repos.size() == 0){
                io.out().println("No repositories defined. Try 'sy repos-add [url]' to add a repository.");
                return;
            }

            io.out().println("# Repositories:");
            for(String repo : repos){
                io.out().println("| " + repo);
            }
        }
        catch (IOException e){
            throw new CommandExecutionException(e.getMessage(), e);
        }
    }

    @Override
    public void printHelp(IOutput out) {
        out.println("TODO");
        //TODO: help for repos-list
    }
}
