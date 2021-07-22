package com.programm.projects.scripty.module.test;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.ModuleFileConfig;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.Module;
import com.programm.projects.scripty.modules.api.ScriptyContext;
import com.programm.projects.scripty.modules.api.ex.InvalidNameException;

public class Test extends Module {

    @Override
    public void init(ScriptyContext context, ModuleFileConfig moduleConfig) {
        try {
            context.registerCommand("bla", this::run);
        }
        catch (InvalidNameException e){
            context.err().println("Error registering command: " + e.getMessage());
        }
    }

    public void run(ScriptyContext ctx, String name, Args args) throws CommandExecutionException {
        ctx.out().println("Ich bin ein toller command lel <(-.-\\<)");
    }
}
