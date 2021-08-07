package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.*;
import com.programm.projects.scripty.modules.api.Module;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CmdInfo implements SySysCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String commandName, Args args) throws CommandExecutionException {
        ScriptyCoreContext context = (ScriptyCoreContext)ctx;

        boolean allInfo = false;

        if(args.size() == 1){
            if(args.get(0).equals("-a") || args.get(0).equals("--all")){
                allInfo = true;
            }
            else {
                throw new CommandExecutionException("Invalid argument. Try 'help " + commandName + "' for more info!");
            }
        }
        else if(args.size() != 0) {
            throw new CommandExecutionException("Invalid number of arguments. Try 'help " + commandName + "' for more info!");
        }

        io.out().println("%30|[-]( Scripty Info )");
        io.out().println("- %10<(Version:) " + ctx.workspace().scriptyVersion());

        List<String> repos = context.workspace.repositoryUrls;
        io.out().println("- %10<(Repos:) " + repos.size());
        if(allInfo){
            try {
                Map<String, Map<String, String>> repoModulesMap = context.workspace._collectRepoModulesMap();
                for(String repoName : repoModulesMap.keySet()){
                    Map<String, String> modulesMap = repoModulesMap.get(repoName);
                    io.out().println("  | - " + repoName + " (" + modulesMap.size() + " Modules)");
                    for(String moduleName : modulesMap.keySet()){
                        io.out().println("      | - %20<(" + moduleName + ":) " + modulesMap.get(moduleName));
                    }
                }
            } catch (IOException e) {
                throw new CommandExecutionException(e.getMessage(), e);
            }
        }

        Map<String, Module> moduleConfigs = context.modulesManager.moduleMap;
        io.out().println("- %10<(Modules:) " + moduleConfigs.size());
        if(allInfo){
            for(String moduleName : moduleConfigs.keySet()){
                io.out().println("  | - " + moduleName);
            }
        }


        Map<String, SyCommand> cmdMap = context.commandManager.commandMap;
        int customCommands = 0;
        for(SyCommand cmd : cmdMap.values()){
            SyCommandInfo info = cmd.info();

            if(info == null || !info.type().equals("built-in")){
                customCommands++;
            }
        }

        io.out().println("- %10<(Commands:) %5<(" + cmdMap.size() + ") (Custom: " + customCommands + ")");
        if(allInfo){
            for(String cmdName : cmdMap.keySet()){
                SyCommandInfo _info = cmdMap.get(cmdName).info();
                String info = _info == null ? "" : " [" + _info.type() + "]";
                io.out().println("  | - %20<(" + cmdName + ") " + info);
            }
        }
    }

    @Override
    public void printHelp(IOutput out, String commandName) {
        out.println("--- Command [" + commandName + "] ---");
        out.println("A command to print info about scripty.");
        out.newLine();

        out.println("# Usage:");
        out.println("|");
        out.println("| " + commandName + " (options)        -> Prints info.");
        out.newLine();

        out.println("# Options:");
        out.println("|");
        out.println("| -a       -> Prints more detailed information.");
        out.println("| --all");
    }
}
