package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.*;

public class CmdHelp implements SySysCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String name, Args args) throws CommandExecutionException {
        if(args.size() == 0){
            printHelp(io.out(), name);
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

        info.printHelp(io.out(), commandName);
    }

    @Override
    public void printHelp(IOutput out, String commandName) {
        out.println("--- Scripty Help ---");
        out.println("Welcome to scripty ;D");
        out.println("To list all available commands use the command [commands-list]!");
        out.newLine();

        out.println("# Help usage:");
        out.println("|");
        out.println("| help           -> Prints this help page.");
        out.println("| help [command] -> Prints info about a specific command if that command provides help.");
    }
}
