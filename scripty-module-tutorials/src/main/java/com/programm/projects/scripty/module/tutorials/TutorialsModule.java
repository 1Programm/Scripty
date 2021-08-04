package com.programm.projects.scripty.module.tutorials;

import com.programm.projects.scripty.modules.api.Module;
import com.programm.projects.scripty.modules.api.SyCommandManager;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyModuleConfig;
import com.programm.projects.scripty.modules.api.ex.InvalidNameException;

public class TutorialsModule extends Module {

    @Override
    public void registerCommands(SyCommandManager commandManager) {
        try {
            commandManager.registerCommand("tut-modules", new CmdTutModules());
            commandManager.registerCommand("tut-dev", new CmdTutDev());
        }
        catch (InvalidNameException e){
            err().println("Error registering command: " + e.getMessage());
        }
    }

    @Override
    public void init(SyContext context, SyModuleConfig moduleConfig) {

    }
}
