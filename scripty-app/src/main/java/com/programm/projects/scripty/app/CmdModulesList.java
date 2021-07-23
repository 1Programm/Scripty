package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyCommand;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyIO;

import java.io.IOException;
import java.util.Map;

class CmdModulesList implements SySysCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String name, Args args) throws CommandExecutionException {
        ScriptyWorkspace workspace = (ScriptyWorkspace) ctx.workspace();

        Map<String, String> modules;
        try {
            modules = workspace.listModules();
        } catch (IOException e) {
            throw new CommandExecutionException("Could not list modules: " + e.getMessage());
        }

        if(modules.size() == 0){
            io.out().println("No modules installed. Try 'sy modules-add [name]' to add a module.");
            return;
        }

        io.out().println("# Modules:");
        for(String moduleName : modules.keySet()){
            io.out().println("| " + moduleName + " - " + modules.get(moduleName));
        }
    }

    @Override
    public void printHelp(IOutput out) {
        //TODO
        out.println("TODO");
    }

}
