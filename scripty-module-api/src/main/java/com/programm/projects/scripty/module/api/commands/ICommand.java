package com.programm.projects.scripty.module.api.commands;

import com.programm.projects.scripty.module.api.IContext;

public interface ICommand {

    void run(IContext ctx, String... args);

}
