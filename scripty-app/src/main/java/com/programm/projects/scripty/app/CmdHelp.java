package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.*;

public class CmdHelp implements SySysCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String name, Args args) throws CommandExecutionException {
        if(args.size() == 0){
            printHelp(io.out());
            return;
        }

        ScriptyCoreContext context = (ScriptyCoreContext) ctx;

        String commandName = args.get(0);
        SyCommand command;

        command = context.commandManager.systemCommands.get(commandName);

        if(command == null) {
            command = context.commandManager.commandMap.get(commandName);
        }

        if(command == null){
            throw new CommandExecutionException("Command [" + commandName + "] not found.");
        }

        SyCommandInfo info = command.info();

        if(info == null){
            throw new CommandExecutionException("Command [" + commandName + "] does not provide help.");
        }

        info.printHelp(io.out());
    }

    @Override
    public void printHelp(IOutput out) {
        out.println("--- Scripty Help ---");
        out.newLine();

        out.println("# Usage:");
        out.println("|");
        out.println("| help           -> Prints this help page.");
        out.println("| help [command] -> Prints info about a specific command if that command provides help.");
    }
}
