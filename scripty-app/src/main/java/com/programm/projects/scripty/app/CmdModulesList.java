package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyIO;
import com.programm.projects.scripty.modules.api.SyModuleConfig;

import java.io.IOException;
import java.util.Map;

class CmdModulesList implements SySysCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String commandName, Args args) throws CommandExecutionException {
        ScriptyCoreContext context = (ScriptyCoreContext) ctx;

        Map<String, String> modules;
        try {
            modules = context.workspace.listModules();
        } catch (IOException e) {
            throw new CommandExecutionException("Could not list modules: " + e.getMessage());
        }

        if(modules.size() == 0){
            io.out().println("No modules installed. Try 'sy modules-add [name]' to add a module.");
            return;
        }

        io.out().println("# Modules:");
        for(String moduleName : modules.keySet()){
            SyModuleConfig config = context.modulesManager.getConfig(moduleName);

            if(config == null){
                io.err().println("Searching for module [" + moduleName + "] but no config could be found under same name. Reason could be an inconsistent naming (The name of the module in its config file and the name of the module in the repo file could differ!)");
                continue;
            }

            io.out().println("| " + moduleName + " - " + config.version() + " - " + modules.get(moduleName));
        }
    }

    @Override
    public void printHelp(IOutput out) {
        //TODO
        out.println("TODO");
    }

}
