package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.ex.InvalidNameException;

import java.io.IOException;

public class Scripty {

    private final ScriptyIO io;
    private final ScriptyWorkspace workspace;
    private final ScriptyModulesManager modulesManager;
    private final ScriptyCommandManager commandManager;
    private final ScriptyCoreContext context;

    public Scripty() {
        this.io = new ScriptyIO();
        this.workspace = new ScriptyWorkspace(io);
        this.modulesManager = new ScriptyModulesManager(io);
        this.commandManager = new ScriptyCommandManager();
        this.context = new ScriptyCoreContext(io, workspace, modulesManager, commandManager);

        try {
            commandManager.registerCommand("help", new CmdHelp());
            commandManager.registerCommand("modules-add", new CmdModulesAdd());
            commandManager.registerCommand("modules-remove", new CmdModulesRemove());
            commandManager.registerCommand("modules-list", new CmdModulesList());
            commandManager.registerCommand("commands-list", new CmdCommandsList());
        }
        catch (InvalidNameException e){
            throw new IllegalStateException("This should not happen.", e);
        }
    }

    public void init(String workspacePath){
        io.log().println("Initializing Workspace ...");

        try {
            workspace.setupWorkspace(workspacePath);
        }
        catch (Exception e){
            io.err().println("Error while initializing workspace: " + e.getMessage());
            System.exit(-1);
        }
    }

    public void run(String command, Args args){
        int index_info = args.indexOf("-i");

        if(index_info == -1){
            index_info = args.indexOf("--info");
        }

        if(index_info != -1){
            io.enableLog();
            args = args.removed(index_info);
        }

        try {
            workspace.loadModules(modulesManager);
            modulesManager.initRegisterCommands(commandManager);
            modulesManager.initModules(context);
        }
        catch (IOException e){
            io.err().println("Error while running command [" + command + "]: " + e.getMessage());
        }

        try {
            context.run(command, args);
        }
        catch (CommandExecutionException e){
            io.err().println("[" + command + "]: " + e.getMessage());
        }
    }

}
