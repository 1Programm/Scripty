package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.modules.api.*;

import java.io.IOException;

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

    public void initRun(String commandName, Args args, ModuleIO moduleIO) throws CommandExecutionException{
        SySysCommand sysCommand = commandManager.systemCommands.get(commandName);
        SyCommand command;

        //First register commands - then init modules if commandName does not specify a system command!!!
        try {
            workspace.loadModules(modulesManager);
            modulesManager.initRegisterCommands(commandManager);
        }
        catch (IOException e){
            io.err().println("Error while initializing modules: " + e.getMessage());
        }

        //Run possible system command without initializing modules.
        if(sysCommand != null){
            sysCommand.run(this, io, commandName, args);
            return;
        }

        modulesManager.initModules(this, moduleIO);

        command = commandManager.commandMap.get(commandName);

        if(command == null){
            io.err().println("No such command: [" + command + "].");
            return;
        }

        command.run(this, io, commandName, args);
    }

    @Override
    public void run(String command, Args args) throws CommandExecutionException {
        SySysCommand sysCmd = commandManager.systemCommands.get(command);

        if(sysCmd != null){
            sysCmd.run(this, io, command, args);
            return;
        }

        SyCommand cmd = commandManager.commandMap.get(command);

        if(cmd == null){
            io.err().println("No such command: [" + command + "].");
            return;
        }

        cmd.run(this, io, command, args);
    }
}
