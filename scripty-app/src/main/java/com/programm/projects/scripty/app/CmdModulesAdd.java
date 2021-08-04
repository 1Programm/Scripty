package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.Module;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class CmdModulesAdd implements SySysCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String commandName, Args args) throws CommandExecutionException {
        ScriptyCoreContext context = (ScriptyCoreContext) ctx;

        String moduleName = args.size() == 0 ? null : args.get(0);

        if(moduleName == null){
            io.err().println("Invalid args. Expected '" + commandName + " [name] ([path])'");
            return;
        }

        String moduleDest = args.size() == 1 ? null : args.get(1);

        if(moduleDest == null){
            moduleDest = ctx.workspace().workspacePath() + "/modules/" + moduleName;
        }

        try {
            boolean success = context.workspace.addModule(moduleName, moduleDest, false);
            if(!success) return;

            Module module = context.workspace.loadSingleModule(context.modulesManager, moduleName);

            ScriptyCommandManager commandManager = new ScriptyCommandManager();
            module.registerCommands(commandManager);


            List<String> cmdNamesNew = new ArrayList<>(commandManager.commandMap.keySet());
            cmdNamesNew.sort(String::compareToIgnoreCase);

            if(cmdNamesNew.size() != 0) {
                io.out().newLine();
                io.out().println("### New from [" + moduleName + "]:");

                io.out().newLine();
                io.out().println("# Commands");
                for (String cmdName : cmdNamesNew) {
                    io.out().println("=> New command: %20>([" + cmdName + "])");
                }
            }
        }
        catch (IOException e){
            throw new CommandExecutionException("Could not add module [" + moduleName + "]: " + e.getMessage());
        }
    }

    @Override
    public void printHelp(IOutput out, String commandName) {
        out.println("--- Command [" + commandName + "] ---");
        out.println("A command to add and download a module specified by a name which will be looked up in the specified repos.");
        out.newLine();

        out.println("# Usage:");
        out.println("|");
        out.println("| " + commandName + " [name] ([dest])  -> Searches in [sy-repositories] for the module [name] and addes the module at the [dest] - path. (If no [dest] is specified it will install the module at [scripty-home]/modules/[name]/)");
    }
}
