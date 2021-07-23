package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyCommand;

import java.io.IOException;

public class CmdModulesAdd implements SyCommand {

    @Override
    public void run(SyContext ctx, String name, Args args) throws CommandExecutionException {
        String moduleName = args.size() == 0 ? null : args.get(0);

        if(moduleName == null){
            ctx.err().println("Invalid args. Expected 'modules-add [name] ([path])'");
            return;
        }

        String moduleDest = args.size() == 1 ? null : args.get(1);

        if(moduleDest == null){
            moduleDest = ctx.workspace().workspacePath() + "/modules/" + moduleName;
        }

        try {
            ((ScriptyWorkspace) ctx.workspace()).addModule(moduleName, moduleDest);
        }
        catch (IOException e){
            throw new CommandExecutionException("Could not add module [" + moduleName + "]: " + e.getMessage());
        }
    }
}
