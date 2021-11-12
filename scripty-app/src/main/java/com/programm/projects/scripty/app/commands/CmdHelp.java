package com.programm.projects.scripty.app.commands;

import com.programm.projects.plugz.magic.Get;
import com.programm.projects.scripty.module.api.Command;
import com.programm.projects.scripty.module.api.Help;
import com.programm.projects.scripty.module.api.IContext;

@Command
public class CmdHelp {

    @Get
    private IContext ctx;

    @Get
    private CommandManager commands;

    @Command
    public void run(String name, String input) {
        if(input.equals("")){
            printGeneralHelp(name);
        }
        else{
            try {
                commands.runHelp(input);
            }
            catch (NullPointerException | CommandExecutionException e){
                ctx.err().println(e.getMessage());
            }
        }
    }

    private void printGeneralHelp(String name){
        ctx.out().println("|%18|({})|", name);
        ctx.out().println("%20<[-]()");
        ctx.out().println("Welcome to scripty ;D");
        ctx.out().newLine();
        ctx.out().println("To list all available commands use the command [commands-list]!");
        ctx.out().newLine();
    }

    @Help
    public void printHelpHelp(String name){
        ctx.out().println("# Help usage:");
        ctx.out().println("|");
        ctx.out().println("| help           -> Prints the general help page.");
        ctx.out().println("| help [command] -> Prints info about a specific command if that command provides help.");
    }


}
