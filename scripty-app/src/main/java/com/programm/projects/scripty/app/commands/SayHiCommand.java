package com.programm.projects.scripty.app.commands;

import com.programm.projects.scripty.module.api.Command;
import com.programm.projects.scripty.module.api.IContext;
import com.programm.projects.scripty.module.api.events.Get;

@Command
public class SayHiCommand {

    @Command
    public void run(@Get IContext ctx, String... args) {

    }
}
