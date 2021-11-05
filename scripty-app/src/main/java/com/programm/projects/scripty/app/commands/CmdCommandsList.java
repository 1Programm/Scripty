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

        Counter maxLength = new Counter();
        maxLength.i = commandNames.get(0).length(); //If array has only 1 element it will not go into sort

        commandNames.sort((n1, n2) -> {
            maxLength.i = Math.max(maxLength.i, n1.length());
            return n1.compareTo(n2);
        });

        ctx.out().println("| %{}<(${yellow}(Commands):)   |", maxLength.i);
        ctx.out().println("----%{}<[-]()--", maxLength.i);

        for(String cmdName : commandNames){
            ctx.out().println("| > %{}<({}) |", maxLength.i, cmdName);
        }
    }

}
