package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.*;

import java.util.ArrayList;
import java.util.List;

class CmdCommandsList implements SySysCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String commandName, Args args) throws CommandExecutionException {
        ScriptyCoreContext context = (ScriptyCoreContext) ctx;

        String type = null;
        String pattern = null;
        boolean filterBuiltins = false;

        for(int i=0;i<args.size();i++){
            if(args.get(i).startsWith("-")) {
                if (args.get(i).equals("-t") || args.get(i).equals("--type")) {
                    if (i + 1 >= args.size()) {
                        throw new CommandExecutionException("No type specified after optional '" + args.get(i) + "'!");
                    }

                    type = args.get(i + 1);
                    i++;
                }
                else if(args.get(i).equals("-c") || args.get(i).equals("--custom")){
                    filterBuiltins = true;
                }
                else {
                    throw new CommandExecutionException("Invalid optional '" + args.get(i) + "'. Try 'help " + commandName + "' for more help!");
                }
            }
            else {
                if(pattern == null){
                    pattern = args.get(i);
                }
                else {
                    throw new CommandExecutionException("Invalid arguments. Try 'help " + commandName + "' for more help!");
                }
            }
        }





        List<String> commands = new ArrayList<>(context.commandManager.commandMap.keySet());

        if(filterBuiltins || pattern != null || type != null){
            for(int i=0;i<commands.size();i++){
                String cmdName = commands.get(i);
                SyCommand cmd = null;
                SyCommandInfo info = null;

                if(filterBuiltins){
                    cmd = context.commandManager.commandMap.get(cmdName);
                    info = cmd.info();

                    if(info != null){
                        String cmdType = info.type();
                        if(cmdType.equals("built-in")){
                            commands.remove(i);
                            i--;
                        }
                    }
                }

                if(pattern != null) {
                    if (!cmdName.matches(pattern)) {
                        commands.remove(i);
                        i--;
                        continue;
                    }
                }

                if(type != null){
                    if(cmd == null) {
                        cmd = context.commandManager.commandMap.get(cmdName);
                        info = cmd.info();
                    }

                    if(info != null){
                        String cmdType = info.type();
                        if(!cmdType.matches(type)){
                            commands.remove(i);
                            i--;
                        }
                    }
                }
            }
        }

        if(commands.isEmpty()){
            io.out().println("No commands found.");
            return;
        }

        commands.sort(String::compareToIgnoreCase);

        io.out().println("# Commands:");
        for(String command : commands){
            SyCommand cmd = context.commandManager.commandMap.get(command);
            SyCommandInfo info = cmd.info();

            if(info == null){
                io.out().println("| " + command);
            }
            else {
                String cmdType = info.type();
                cmdType = cmdType.replace(' ', '_');

                if(cmdType.equals("built-in")){
                    if(!(cmd instanceof SySysCommand)){
                        cmdType = "\"built-in\"";
                    }
                }

                io.out().println("| %20<(" + command + ") [" + cmdType + "]");
            }
        }
    }

    @Override
    public void printHelp(IOutput out, String commandName) {
        out.println("--- Command [" + commandName + "] ---");
        out.println("A command to list all available commands.");
        out.newLine();

        out.println("# Usage:");
        out.println("|");
        out.println("| commands-list (options)              -> Lists all commands and their command-types.");
        out.println("| commands-list (options) [pattern]    -> Lists all commands which match the specified [pattern] - regex.");
        out.newLine();

        out.println("# Options:");
        out.println("|");
        out.println("| -t [type]        -> Filters out all commands which do not match the specified [type] - regex.");
        out.println("| --type [type]");
        out.println("| -c               -> Filters out all 'built-in' commands.");
        out.println("| --custom");

    }
}
