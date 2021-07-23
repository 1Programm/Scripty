package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyCommand;
import com.programm.projects.scripty.modules.api.SyContext;

import java.io.IOException;

public class CmdModulesRemove implements SyCommand {

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
}
