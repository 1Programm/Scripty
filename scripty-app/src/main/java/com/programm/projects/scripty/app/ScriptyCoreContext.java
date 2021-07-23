package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.modules.api.*;

class ScriptyCoreContext implements SyContext {

    private final SyIO io;
    final ScriptyWorkspace workspace;
    final ScriptyModulesManager modulesManager;
    final ScriptyCommandManager commandManager;

    public ScriptyCoreContext(SyIO io, ScriptyWorkspace workspace, ScriptyModulesManager modulesManager, ScriptyCommandManager commandManager) {
        this.io = io;
        this.workspace = workspace;
        this.modulesManager = modulesManager;
        this.commandManager = commandManager;
    }

    @Override
    public SyWorkspace workspace() {
        return workspace;
    }

    @Override
    public void run(String command, Args args) throws CommandExecutionException {
        SyCommand cmd = commandManager.commandMap.get(command);

        if(cmd == null){
            io.err().println("No such command: [" + command + "].");
            return;
        }

        cmd.run(this, io, command, args);
    }
}
