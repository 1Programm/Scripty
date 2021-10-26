package com.programm.projects.scripty.app.commands;

import com.programm.projects.scripty.module.api.IContext;
import com.programm.projects.scripty.module.api.commands.CmdUse;
import com.programm.projects.scripty.module.api.commands.ICommand;

public class SayHiCommand implements ICommand {

    @Override
    public void run(IContext ctx, String... args) {

    }

    @CmdUse(HelpCommand.class)
    public String help(String... args){
        return "A simple command to say 'hi'.";
    }
}
