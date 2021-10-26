package com.programm.projects.scripty.app.commands;

import com.programm.projects.scripty.module.api.IContext;
import com.programm.projects.scripty.module.api.commands.ICommand;
import com.programm.projects.scripty.module.api.events.Get;

public class HelpCommand implements ICommand {

    @Get
    private CommandManager manager;

    @Override
    public void run(IContext ctx, String... args) {
        manager.runCommandAs(this, SayHiCommand.class);
    }

}
