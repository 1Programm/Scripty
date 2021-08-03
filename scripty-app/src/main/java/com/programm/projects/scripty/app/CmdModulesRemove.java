package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyIO;

import java.io.IOException;

class CmdModulesRemove implements SySysCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String commandName, Args args) throws CommandExecutionException {
        String moduleName = args.size() == 0 ? null : args.get(0);

        if(moduleName == null){
            io.err().println("Invalid args. Expected '" + commandName + " [name]'");
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
    public void printHelp(IOutput out, String commandName) {
        out.println("--- Command [" + commandName + "] ---");
        out.println("A command to remove an installed module.");
        out.newLine();

        out.println("# Usage:");
        out.println("|");
        out.println("| " + commandName + " [name]   -> Removes the module [name].");
    }

}
