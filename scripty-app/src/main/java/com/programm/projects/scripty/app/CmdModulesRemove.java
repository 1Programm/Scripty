package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyContext;

import java.io.IOException;

class CmdModulesRemove implements SySysCommand {

    @Override
    public void run(SyContext ctx, String name, Args args) throws CommandExecutionException {
        String moduleName = args.size() == 0 ? null : args.get(0);

        if(moduleName == null){
            ctx.err().println("Invalid args. Expected 'modules-remove [name]'");
            return;
        }

        try {
            ((ScriptyWorkspace) ctx.workspace()).removeModule(moduleName);
        }
        catch (IOException e){
            throw new CommandExecutionException("Could not remove module [" + moduleName + "]: " + e.getMessage());
        }
    }

    @Override
    public void printHelp(IOutput out) {
        //TODO
        out.println("TODO");
    }

}
