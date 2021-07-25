package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyIO;

import java.io.IOException;
import java.util.Map;

class CmdModulesUpdate implements SySysCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String commandName, Args args) throws CommandExecutionException {
        ScriptyCoreContext context = (ScriptyCoreContext) ctx;

        if(args.size() == 0){
            Map<String, String> modules;
            try {
                modules = context.workspace.listModules();
            }
            catch (IOException e){
                throw new CommandExecutionException("Failed to read modules: " + e.getMessage());
            }

            for(String moduleName : modules.keySet()){
                updateModule(context, moduleName);
            }
        }
        else {
            String moduleToUpdate = args.get(0);
            updateModule(context, moduleToUpdate);
        }
    }

    private void updateModule(ScriptyCoreContext ctx, String name) throws CommandExecutionException {
        try {
            ctx.workspace.updateModule(name);
        }
        catch (IOException e){
            throw new CommandExecutionException("Failed to update module [" + name + "]: " + e.getMessage());
        }
    }

    @Override
    public void printHelp(IOutput out) {
        //TODO
        out.println("TODO");
    }
}
