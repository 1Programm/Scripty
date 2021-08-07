package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.ex.InvalidNameException;

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
            commandManager.registerCommand("info", new CmdInfo());
            commandManager.registerCommand("repos-list", new CmdReposList());
            commandManager.registerCommand("repos-add", new CmdReposAdd());
            commandManager.registerCommand("repos-remove", new CmdReposRemove());
            commandManager.registerCommand("modules-add", new CmdModulesAdd());
            commandManager.registerCommand("modules-remove", new CmdModulesRemove());
            commandManager.registerCommand("modules-update", new CmdModulesUpdate());
            commandManager.registerCommand("modules-list", new CmdModulesList());
            commandManager.registerCommand("modules-dev", new CmdModulesDev());
            commandManager.registerCommand("commands-list", new CmdCommandsList());
        }
        catch (InvalidNameException e){
            throw new IllegalStateException("This should not happen.", e);
        }
    }

    public void init(String installationPath, String workspacePath, String userPath){
        try {
            workspace.setupWorkspace(installationPath, workspacePath, userPath);
        }
        catch (Exception e){
            io.err().println("Error while initializing workspace: " + e.getMessage());
            System.exit(-1);
        }
    }

    public void run(Args args){
        int indexNextCommand = -1;
        boolean infoEnabled = false;

        for(int i=0;i<args.size();i++){
            String arg = args.get(i);

            if(!arg.startsWith("-")){
                indexNextCommand = i;
                break;
            }
            else if(arg.equals("-i") || arg.equals("--info")){
                infoEnabled = true;
            }
            // Other Scripty optionals here in future
        }

        if(indexNextCommand == -1){
            System.out.println("No command specified!");
            System.exit(-1);
        }

        String command = args.get(indexNextCommand);
        Args commandArgs = args.sub(indexNextCommand + 1);






        ModuleIO moduleIO = new ModuleIO();

        if(infoEnabled){
            io.enableLog();
            moduleIO.enableLog();
        }

        try {
            context.initRun(command, commandArgs, moduleIO);
        }
        catch (CommandExecutionException e){
            io.err().println("[" + command + "]: " + e.getMessage());
        }
    }

}
