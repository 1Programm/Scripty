package com.programm.projects.scripty.module.helloworld;

import com.programm.projects.scripty.modules.api.Module;
import com.programm.projects.scripty.modules.api.SyCommandManager;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyModuleConfig;
import com.programm.projects.scripty.modules.api.ex.InvalidNameException;

public class HelloWorldModule extends Module {

    @Override
    public void registerCommands(SyCommandManager commandManager) {
        try {
            commandManager.registerCommand("hello-world", new CmdHelloWorld());
        }
        catch (InvalidNameException e){
            err().println("Error registering command: " + e.getMessage());
        }
    }

    @Override
    public void init(SyContext context, SyModuleConfig moduleConfig) {
        log().println("Greetings from the [hello-world] - module.");
    }
}
