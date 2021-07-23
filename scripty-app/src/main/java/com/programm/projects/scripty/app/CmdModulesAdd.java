package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class CmdModulesAdd implements SySysCommand {

    @Override
    public void run(SyContext ctx, String name, Args args) throws CommandExecutionException {
        ScriptyCoreContext context = (ScriptyCoreContext) ctx;

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
            List<String> cmdNamesOld = new ArrayList<>(context.commandMap.keySet());

            ((ScriptyWorkspace) ctx.workspace()).addModule(moduleName, moduleDest);

            List<String> cmdNamesNew = new ArrayList<>(context.commandMap.keySet());

            cmdNamesNew.removeAll(cmdNamesOld);
            cmdNamesNew.sort(String::compareToIgnoreCase);

            for(String cmdName : cmdNamesNew){
                ctx.out().println("=> Added new command: [" + cmdName + "].");
            }
        }
        catch (IOException e){
            throw new CommandExecutionException("Could not add module [" + moduleName + "]: " + e.getMessage());
        }
    }

    @Override
    public void printHelp(IOutput out) {
        //TODO
        out.println("TODO");
    }
}
