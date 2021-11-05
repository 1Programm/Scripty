package com.programm.projects.scripty.app.commands;

import com.programm.projects.plugz.magic.Get;
import com.programm.projects.scripty.module.api.Command;
import com.programm.projects.scripty.module.api.IContext;

import java.util.List;

@Command("commands-list")
public class CmdCommandsList {

    @Get
    private IContext ctx;

    @Get
    private CommandManager commands;

    @Command
    public void run(String name, String input){
        List<String> commandNames = commands.getCommandNames();

        for(String cmdName : commandNames){
            ctx.out().println("> " + cmdName);
        }
    }

}
